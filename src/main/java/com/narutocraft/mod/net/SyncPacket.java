package com.narutocraft.mod.net;

import com.narutocraft.mod.client.ClientHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPacket {
    private final CompoundTag tag;
    public SyncPacket(CompoundTag tag) { this.tag = tag; }
    public static void encode(SyncPacket m, FriendlyByteBuf b) { b.writeNbt(m.tag); }
    public static SyncPacket decode(FriendlyByteBuf b) { return new SyncPacket(b.readNbt()); }
    public static void handle(SyncPacket m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.sync(m.tag)));
        ctx.get().setPacketHandled(true);
    }
}
