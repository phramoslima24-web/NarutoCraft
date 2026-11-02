package com.narutocraft.mod.client;

import com.narutocraft.mod.data.NinjaData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

public class ClientHooks {
    public static final NinjaData DATA = new NinjaData();

    public static void sync(CompoundTag tag) {
        DATA.load(tag);
        if (Minecraft.getInstance().screen instanceof NinjaScreen s) s.refresh();
    }

    public static void openPanel() { Minecraft.getInstance().setScreen(new NinjaScreen()); }
}
