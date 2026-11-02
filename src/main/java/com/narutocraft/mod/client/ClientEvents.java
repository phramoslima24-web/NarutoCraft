package com.narutocraft.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.narutocraft.mod.NarutoCraft;
import com.narutocraft.mod.data.NinjaData;
import com.narutocraft.mod.net.ActionPacket;
import com.narutocraft.mod.net.Net;
import com.narutocraft.mod.registry.ModEntities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {
    public static final KeyMapping MENU = new KeyMapping("key.narutocraft.menu", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.narutocraft");
    public static final KeyMapping CAST = new KeyMapping("key.narutocraft.cast", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.narutocraft");

    @Mod.EventBusSubscriber(modid = NarutoCraft.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e) { e.register(MENU); e.register(CAST); }

        @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
            e.registerEntityRenderer(ModEntities.RASENSHURIKEN.get(), ctx -> new ThrownItemRenderer<>(ctx, 2.5f, true));
        }

        @SubscribeEvent public static void hud(RegisterGuiOverlaysEvent e) {
            e.registerAboveAll("chakra", (gui, g, pt, w, h) -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null || mc.options.hideGui) return;
                NinjaData d = ClientHooks.DATA;
                int x = w / 2 - 91, y = h - 52, bw = 182;
                g.fill(x - 1, y - 1, x + bw + 1, y + 5, 0xFF000000);
                g.fill(x, y, x + (int) (bw * Math.min(1f, d.chakra / d.maxChakra())), y + 4, 0xFF3FA9FF);
                String sel = d.selected >= 0 ? com.narutocraft.mod.data.Jutsu.values()[d.selected].display : "-";
                g.drawString(mc.font, "Chakra " + (int) d.chakra + "  |  " + sel + (d.sage ? "  [Sábio]" : ""), x, y - 10, 0xFF9FD8FF, true);
            });
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoCraft.MODID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) return;
            while (MENU.consumeClick()) ClientHooks.openPanel();
            while (CAST.consumeClick()) Net.CH.sendToServer(new ActionPacket(ActionPacket.CAST, 0));
        }
    }
}
