package com.narutocraft.mod.client;

import com.narutocraft.mod.data.*;
import com.narutocraft.mod.net.ActionPacket;
import com.narutocraft.mod.net.Net;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NinjaScreen extends Screen {
    private static final int W = 320, H = 250;
    private static final String[] TABS = {"Atributos", "Jutsus", "Dojutsus"};
    private int tab = 0, left, top;

    public NinjaScreen() { super(Component.literal("Painel Shinobi")); }

    @Override protected void init() { left = (width - W) / 2; top = (height - H) / 2; build(); }
    public void refresh() { build(); }
    @Override public boolean isPauseScreen() { return false; }

    private void send(int type, int arg) { Net.CH.sendToServer(new ActionPacket(type, arg)); }

    private void build() {
        clearWidgets();
        NinjaData d = ClientHooks.DATA;
        for (int i = 0; i < TABS.length; i++) {
            final int t = i;
            Button b = Button.builder(Component.literal(TABS[i]), x -> { tab = t; build(); })
                    .bounds(left + 8 + i * 102, top + 8, 98, 18).build();
            b.active = tab != i;
            addRenderableWidget(b);
        }
        boolean creative = minecraft != null && minecraft.player != null && minecraft.player.isCreative();
        switch (tab) {
            case 0 -> {
                for (Stat s : Stat.values()) {
                    int y = top + 52 + s.ordinal() * 28;
                    Button b = Button.builder(Component.literal("+ (" + d.upgradeCost(s) + " SP)"), x -> send(ActionPacket.UPGRADE_STAT, s.ordinal()))
                            .bounds(left + W - 98, y, 88, 18).build();
                    b.active = d.sp >= d.upgradeCost(s) && d.level(s) < Stat.MAX_LEVEL;
                    addRenderableWidget(b);
                }
            }
            case 1 -> {
                for (Jutsu j : Jutsu.values()) {
                    int y = top + 52 + j.ordinal() * 40;
                    boolean known = d.has(j);
                    String label = known ? (d.selected == j.ordinal() ? "Equipado" : "Equipar") : "Aprender (" + j.jpCost + " JP)";
                    Button b = Button.builder(Component.literal(label),
                            x -> send(known ? ActionPacket.SELECT_JUTSU : ActionPacket.LEARN_JUTSU, j.ordinal()))
                            .bounds(left + W - 118, y + 4, 108, 18).build();
                    b.active = known ? d.selected != j.ordinal() : (d.jp >= j.jpCost && d.level(Stat.NINJUTSU) >= j.reqNinjutsu);
                    addRenderableWidget(b);
                }
            }
            case 2 -> {
                for (Dojutsu dj : Dojutsu.values()) {
                    int y = top + 34 + dj.ordinal() * 20;
                    boolean has = d.has(dj);
                    String label = d.dojutsuActive == dj.ordinal() ? "Ativo" : has ? "Equipar" : creative ? "Desbloquear" : "Bloqueado";
                    Button b = Button.builder(Component.literal(label), x -> send(ActionPacket.DOJUTSU, dj.ordinal()))
                            .bounds(left + W - 98, y, 88, 18).build();
                    b.active = d.dojutsuActive != dj.ordinal() && (has || creative);
                    addRenderableWidget(b);
                }
                addRenderableWidget(Button.builder(Component.literal("Remover dojutsu"), x -> send(ActionPacket.DOJUTSU_NONE, 0))
                        .bounds(left + 10, top + H - 26, 110, 18).build());
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.fill(left - 2, top - 2, left + W + 2, top + H + 2, 0xFFB5651D);
        g.fill(left, top, left + W, top + H, 0xF01E1610);
        super.render(g, mx, my, pt);
        NinjaData d = ClientHooks.DATA;
        g.drawString(font, "SP: " + d.sp + "   JP: " + d.jp + "   Chakra: " + (int) d.chakra + "/" + (int) d.maxChakra(), left + 10, top + 32, 0xFFE0A030, false);
        switch (tab) {
            case 0 -> {
                for (Stat s : Stat.values()) {
                    int y = top + 52 + s.ordinal() * 28;
                    g.drawString(font, s.display + "  Nv " + d.level(s) + "/" + Stat.MAX_LEVEL, left + 10, y, 0xFFFFFFFF, false);
                    g.drawString(font, s.desc, left + 10, y + 11, 0xFF9A9A9A, false);
                }
            }
            case 1 -> {
                for (Jutsu j : Jutsu.values()) {
                    int y = top + 52 + j.ordinal() * 40;
                    int col = d.has(j) ? 0xFF55FF55 : 0xFFFFFFFF;
                    g.drawString(font, j.display, left + 10, y, col, false);
                    String info = (j.damage > 0 ? "Dano " + (int) d.jutsuDamage(j) + "  " : "Buff  ") + "Chakra " + (int) d.chakraCost(j) + "  Req. Ninjutsu " + j.reqNinjutsu;
                    g.drawString(font, info, left + 10, y + 11, 0xFF9A9A9A, false);
                }
                g.drawString(font, "Tecla R usa o jutsu equipado", left + 10, top + H - 16, 0xFF777777, false);
            }
            case 2 -> {
                for (Dojutsu dj : Dojutsu.values()) {
                    int y = top + 34 + dj.ordinal() * 20 + 5;
                    g.drawString(font, dj.display, left + 10, y, d.has(dj) ? 0xFFFF5555 : 0xFF888888, false);
                }
            }
        }
    }
}
