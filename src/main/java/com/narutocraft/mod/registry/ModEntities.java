package com.narutocraft.mod.registry;

import com.narutocraft.mod.NarutoCraft;
import com.narutocraft.mod.entity.RasenshurikenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NarutoCraft.MODID);
    public static final RegistryObject<EntityType<RasenshurikenEntity>> RASENSHURIKEN = ENTITIES.register("rasenshuriken",
            () -> EntityType.Builder.<RasenshurikenEntity>of(RasenshurikenEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f).clientTrackingRange(8).updateInterval(2).build("rasenshuriken"));
}
