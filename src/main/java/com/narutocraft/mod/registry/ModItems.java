package com.narutocraft.mod.registry;

import com.narutocraft.mod.NarutoCraft;
import com.narutocraft.mod.item.NinjaBookItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NarutoCraft.MODID);
    public static final RegistryObject<Item> NINJA_BOOK = ITEMS.register("ninja_book",
            () -> new NinjaBookItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RASENSHURIKEN_PROJECTILE = ITEMS.register("rasenshuriken_projectile",
            () -> new Item(new Item.Properties()));
}
