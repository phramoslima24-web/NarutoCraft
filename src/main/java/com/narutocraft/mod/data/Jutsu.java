package com.narutocraft.mod.data;

/** Quanto melhor o jutsu: mais dano, mais chakra e mais JP para aprender. */
public enum Jutsu {
    //            nome            JP   ninjutsu chakra dano  cooldown(ticks)
    RASENGAN("Rasengan",          20,   5,  25, 14f,  60),
    RASENSHURIKEN("Ragenshuriken",150, 20,  80, 30f, 200),
    SAGE_MODE("Modo Sábio",      300, 30,  60,  0f, 100);   // buff: custo inicial + dreno por segundo

    public final String display;
    public final int jpCost, reqNinjutsu, chakraCost, cooldown;
    public final float damage;

    Jutsu(String display, int jpCost, int reqNinjutsu, int chakraCost, float damage, int cooldown) {
        this.display = display; this.jpCost = jpCost; this.reqNinjutsu = reqNinjutsu;
        this.chakraCost = chakraCost; this.damage = damage; this.cooldown = cooldown;
    }
}
