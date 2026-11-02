package com.narutocraft.mod.event;

import com.narutocraft.mod.NarutoCraft;
import com.narutocraft.mod.data.*;
import com.narutocraft.mod.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NarutoCraft.MODID)
public class ServerEvents {
    public static final TagKey<EntityType<?>> NINJA_TAG =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(NarutoCraft.MODID, "ninja"));

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player && !e.getObject().getCapability(NinjaProvider.NINJA).isPresent()) {
            NinjaProvider prov = new NinjaProvider();
            e.addCapability(new ResourceLocation(NarutoCraft.MODID, "ninja"), prov);
            e.addListener(prov::invalidate);
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone e) {
        e.getOriginal().reviveCaps();
        e.getOriginal().getCapability(NinjaProvider.NINJA).ifPresent(old ->
                e.getEntity().getCapability(NinjaProvider.NINJA).ifPresent(nw -> nw.copyFrom(old)));
        e.getOriginal().invalidateCaps();
    }

    private static void giveBookIfMissing(ServerPlayer p, NinjaData d) {
        for (ItemStack s : p.getInventory().items) if (s.is(ModItems.NINJA_BOOK.get())) return;
        p.getInventory().add(new ItemStack(ModItems.NINJA_BOOK.get()));
        d.bookGiven = true;
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        NinjaData d = ServerLogic.data(p);
        d.sage = false;
        if (!d.bookGiven) giveBookIfMissing(p, d);
        ServerLogic.applyStats(p, d);
        ServerLogic.sync(p);
    }

    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        NinjaData d = ServerLogic.data(p);
        giveBookIfMissing(p, d);          // "você nasce com o livro"
        d.chakra = d.maxChakra();
        ServerLogic.applyStats(p, d);
        ServerLogic.sync(p);
    }

    /** SP/JP ao matar: ninjas dão mais; mais vida do alvo = mais recompensa. */
    @SubscribeEvent
    public static void death(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p) || e.getEntity() == p) return;
        LivingEntity v = e.getEntity();
        boolean ninja = v instanceof Player || v.getType().is(NINJA_TAG);
        float hp = v.getMaxHealth();
        int sp = Math.max(1, Math.round(hp * (ninja ? 1.5f : 0.5f)));
        int jp = ninja ? Math.max(1, Math.round(sp * 0.4f)) : Math.round(hp / 10f);
        NinjaData d = ServerLogic.data(p);
        d.sp += sp; d.jp += jp;
        p.displayClientMessage(net.minecraft.network.chat.Component.literal("+" + sp + " SP  +" + jp + " JP"), true);
        ServerLogic.sync(p);
    }

    @SubscribeEvent
    public static void hurt(LivingHurtEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p)) return;
        NinjaData d = ServerLogic.data(p);
        float amount = e.getAmount();
        boolean melee = e.getSource().getDirectEntity() == p;
        if (melee && d.rasenganTicks > 0) {
            amount += d.jutsuDamage(Jutsu.RASENGAN);
            d.rasenganTicks = 0;
            LivingEntity t = e.getEntity();
            t.knockback(1.8, p.getX() - t.getX(), p.getZ() - t.getZ());
            if (t.level() instanceof net.minecraft.server.level.ServerLevel sl)
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, t.getX(), t.getY() + 1, t.getZ(), 25, 0.5, 0.5, 0.5, 0.1);
        }
        e.setAmount(amount * (1f + d.dojutsuBonus()));
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p)) return;
        NinjaData d = ServerLogic.data(p);
        for (int i = 0; i < d.cooldown.length; i++) if (d.cooldown[i] > 0) d.cooldown[i]--;
        if (d.rasenganTicks > 0) d.rasenganTicks--;

        if (d.sage) {
            d.chakra -= Jutsu.SAGE_MODE.chakraCost / 15f / 20f;   // dreno contínuo
            if (p.tickCount % 5 == 0 && p.level() instanceof net.minecraft.server.level.ServerLevel sl)
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, p.getX(), p.getY() + 1, p.getZ(), 2, 0.3, 0.5, 0.3, 0.01);
            if (d.chakra <= 0) { d.chakra = 0; ServerLogic.stopSage(p, d); }
        }
        if (d.activeDojutsu() != null && p.tickCount % 100 == 0)
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false, false));

        if (p.tickCount % 20 == 0) {
            if (!d.sage) d.chakra = Math.min(d.maxChakra(), d.chakra + 1.5f + d.level(Stat.CHAKRA) * 0.1f);
            d.chakra = Math.min(d.chakra, d.maxChakra());
            ServerLogic.sync(p);
        }
    }
}
