package com.narutocraft.mod;

import com.narutocraft.mod.net.Net;
import com.narutocraft.mod.registry.ModEntities;
import com.narutocraft.mod.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(NarutoCraft.MODID)
public class NarutoCraft {
    public static final String MODID = "narutocraft";

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("narutocraft", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.narutocraft"))
                    .icon(() -> new ItemStack(ModItems.NINJA_BOOK.get()))
                    .displayItems((params, out) -> out.accept(ModItems.NINJA_BOOK.get()))
                    .build());

    public NarutoCraft() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        TABS.register(bus);
        bus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(Net::register));
    }
}
