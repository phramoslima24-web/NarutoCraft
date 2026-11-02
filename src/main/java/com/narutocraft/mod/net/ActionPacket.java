package com.narutocraft.mod.net;

import com.narutocraft.mod.event.ServerLogic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Pacote cliente→servidor único; o servidor valida tudo. */
public class ActionPacket {
    public static final int UPGRADE_STAT = 0, LEARN_JUTSU = 1, SELECT_JUTSU = 2, CAST = 3, DOJUTSU = 4, DOJUTSU_NONE = 5;
    public final int type, arg;
    public ActionPacket(int type, int arg) { this.type = type; this.arg = arg; }
    public static void encode(ActionPacket m, FriendlyByteBuf b) { b.writeVarInt(m.type); b.writeVarInt(m.arg); }
    public static ActionPacket decode(FriendlyByteBuf b) { return new ActionPacket(b.readVarInt(), b.readVarInt()); }
    public static void handle(ActionPacket m, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer p = ctx.get().getSender();
        ctx.get().enqueueWork(() -> { if (p != null) ServerLogic.handleAction(p, m.type, m.arg); });
        ctx.get().setPacketHandled(true);
    }
}
