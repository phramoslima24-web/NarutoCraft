package com.narutocraft.mod.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class NinjaProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<NinjaData> NINJA = CapabilityManager.get(new CapabilityToken<>() {});

    private final NinjaData data = new NinjaData();
    private final LazyOptional<NinjaData> lazy = LazyOptional.of(() -> data);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == NINJA ? lazy.cast() : LazyOptional.empty();
    }
    @Override public CompoundTag serializeNBT() { return data.save(); }
    @Override public void deserializeNBT(CompoundTag nbt) { data.load(nbt); }
    public void invalidate() { lazy.invalidate(); }
}
