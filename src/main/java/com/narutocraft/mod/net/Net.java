package com.narutocraft.mod.net;

import com.narutocraft.mod.NarutoCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Net {
    private static final String V = "1";
    public static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NarutoCraft.MODID, "main"), () -> V, V::equals, V::equals);

    public static void register() {
        int id = 0;
        CH.registerMessage(id++, ActionPacket.class, ActionPacket::encode, ActionPacket::decode, ActionPacket::handle);
        CH.registerMessage(id++, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle);
    }

    public static void sendToClient(ServerPlayer p, SyncPacket msg) {
        CH.send(PacketDistributor.PLAYER.with(() -> p), msg);
    }
}
