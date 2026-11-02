package com.narutocraft.mod.event;

import com.narutocraft.mod.data.*;
import com.narutocraft.mod.entity.RasenshurikenEntity;
import com.narutocraft.mod.net.Net;
import com.narutocraft.mod.net.ActionPacket;
import com.narutocraft.mod.net.SyncPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class ServerLogic {
    private static final UUID HEALTH_ID = UUID.fromString("5c1f0a10-0000-4a00-8000-000000000001");
    private static final UUID ATTACK_ID = UUID.fromString("5c1f0a10-0000-4a00-8000-000000000002");
    private static final UUID SAGE_ATK_ID = UUID.fromString("5c1f0a10-0000-4a00-8000-000000000003");
    private static final UUID SAGE_SPD_ID = UUID.fromString("5c1f0a10-0000-4a00-8000-000000000004");

    public static NinjaData data(ServerPlayer p) {
        return p.getCapability(NinjaProvider.NINJA).orElseThrow(() -> new IllegalStateException("sem dados ninja"));
    }

    public static void sync(ServerPlayer p) { Net.sendToClient(p, new SyncPacket(data(p).save())); }

    private static void setMod(ServerPlayer p, Attribute a, UUID id, String name, double v, AttributeModifier.Operation op) {
        AttributeInstance inst = p.getAttribute(a);
        if (inst == null) return;
        inst.removeModifier(id);
        if (v != 0) inst.addTransientModifier(new AttributeModifier(id, name, v, op));
    }

    /** Reaplica os modificadores de atributo derivados dos níveis. */
    public static void applyStats(ServerPlayer p, NinjaData d) {
        setMod(p, Attributes.MAX_HEALTH, HEALTH_ID, "narutocraft_vida", d.level(Stat.VIDA) * 2.0, AttributeModifier.Operation.ADDITION);
        setMod(p, Attributes.ATTACK_DAMAGE, ATTACK_ID, "narutocraft_taijutsu", d.level(Stat.TAIJUTSU) * 0.1, AttributeModifier.Operation.ADDITION);
        double ninj = d.sage ? 1 : 0;
        setMod(p, Attributes.ATTACK_DAMAGE, SAGE_ATK_ID, "narutocraft_sage_atk", ninj * (4.0 + d.level(Stat.NINJUTSU) * 0.1), AttributeModifier.Operation.ADDITION);
        setMod(p, Attributes.MOVEMENT_SPEED, SAGE_SPD_ID, "narutocraft_sage_spd", ninj * 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL);
        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
    }

    public static void handleAction(ServerPlayer p, int type, int arg) {
        NinjaData d = data(p);
        switch (type) {
            case ActionPacket.UPGRADE_STAT -> {
                if (arg < 0 || arg >= Stat.values().length) return;
                Stat s = Stat.values()[arg];
                int cost = d.upgradeCost(s);
                if (d.level(s) >= Stat.MAX_LEVEL || d.sp < cost) return;
                d.sp -= cost; d.stats[arg]++;
                applyStats(p, d);
            }
            case ActionPacket.LEARN_JUTSU -> {
                if (arg < 0 || arg >= Jutsu.values().length) return;
                Jutsu j = Jutsu.values()[arg];
                if (d.has(j) || d.jp < j.jpCost || d.level(Stat.NINJUTSU) < j.reqNinjutsu) return;
                d.jp -= j.jpCost; d.jutsuMask |= 1 << arg;
                if (d.selected < 0) d.selected = arg;
            }
            case ActionPacket.SELECT_JUTSU -> {
                if (arg >= 0 && arg < Jutsu.values().length && d.has(Jutsu.values()[arg])) d.selected = arg;
            }
            case ActionPacket.CAST -> cast(p, d);
            case ActionPacket.DOJUTSU -> {
                if (arg < 0 || arg >= Dojutsu.values().length) return;
                if (p.isCreative()) d.dojutsuMask |= 1 << arg;        // só no criativo desbloqueia direto
                if (!d.has(Dojutsu.values()[arg])) return;
                d.dojutsuActive = arg;
            }
            case ActionPacket.DOJUTSU_NONE -> d.dojutsuActive = -1;
            default -> { return; }
        }
        sync(p);
    }

    public static void stopSage(ServerPlayer p, NinjaData d) {
        d.sage = false;
        applyStats(p, d);
        p.displayClientMessage(Component.literal("Modo Sábio encerrado"), true);
    }

    private static void cast(ServerPlayer p, NinjaData d) {
        if (d.selected < 0) { p.displayClientMessage(Component.literal("Nenhum jutsu equipado"), true); return; }
        Jutsu j = Jutsu.values()[d.selected];
        if (!d.has(j)) return;
        if (j == Jutsu.SAGE_MODE && d.sage) { stopSage(p, d); return; }
        if (d.cooldown[j.ordinal()] > 0) { p.displayClientMessage(Component.literal(j.display + " em recarga"), true); return; }
        float cost = d.chakraCost(j);
        if (d.chakra < cost) { p.displayClientMessage(Component.literal("Chakra insuficiente"), true); return; }
        d.chakra -= cost;
        d.cooldown[j.ordinal()] = j.cooldown;
        switch (j) {
            case RASENGAN -> {
                d.rasenganTicks = 100;
                p.displayClientMessage(Component.literal("Rasengan pronto! Acerte um inimigo."), true);
            }
            case RASENSHURIKEN -> {
                RasenshurikenEntity e = new RasenshurikenEntity(p.level(), p);
                e.setDamage(d.jutsuDamage(j));
                e.shootFromRotation(p, p.getXRot(), p.getYRot(), 0f, 1.6f, 0f);
                p.level().addFreshEntity(e);
            }
            case SAGE_MODE -> {
                d.sage = true;
                applyStats(p, d);
                p.displayClientMessage(Component.literal("Modo Sábio ativado"), true);
            }
        }
        sync(p);
    }
}
