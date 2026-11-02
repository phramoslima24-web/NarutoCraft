package com.narutocraft.mod.entity;

import com.narutocraft.mod.registry.ModEntities;
import com.narutocraft.mod.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class RasenshurikenEntity extends ThrowableItemProjectile {
    private float damage = 30f;

    public RasenshurikenEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) { super(type, level); }
    public RasenshurikenEntity(Level level, LivingEntity owner) { super(ModEntities.RASENSHURIKEN.get(), owner, level); }

    public void setDamage(float d) { damage = d; }

    @Override protected Item getDefaultItem() { return ModItems.RASENSHURIKEN_PROJECTILE.get(); }
    @Override protected float getGravity() { return 0f; }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 3, 0.2, 0.2, 0.2, 0.01);
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 2, 0.3, 0.3, 0.3, 0.02);
            if (tickCount > 80) discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!(level() instanceof ServerLevel sl)) return;
        for (LivingEntity t : sl.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(4.0),
                e -> e != getOwner() && e.isAlive())) {
            t.hurt(damageSources().indirectMagic(this, getOwner()), damage);
        }
        sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        sl.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 40, 1.5, 1.0, 1.5, 0.1);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 1.3f);
        discard();
    }
}
