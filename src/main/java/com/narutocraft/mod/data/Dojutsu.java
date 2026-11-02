package com.narutocraft.mod.data;

public enum Dojutsu {
    SHARINGAN("Sharingan", 0.05f),
    MANGEKYOU_SASUKE("Mangekyou Sharingan (Sasuke)", 0.15f),
    MANGEKYOU_OBITO("Mangekyou Sharingan (Obito)", 0.15f),
    MANGEKYOU_MADARA("Mangekyou Sharingan (Madara)", 0.18f),
    MANGEKYOU_ITACHI("Mangekyou Sharingan (Itachi)", 0.16f),
    MANGEKYOU_SHISUI("Mangekyou Sharingan (Shisui)", 0.16f),
    RINNEGAN("Rinnegan", 0.25f),
    RINNESHARINGAN("Rinne Sharingan (Sasuke)", 0.30f);

    public final String display;
    /** Bônus base de dano (multiplicador) concedido pelo dojutsu. */
    public final float power;
    Dojutsu(String display, float power) { this.display = display; this.power = power; }
}
