package com.narutocraft.mod.data;

public enum Stat {
    TAIJUTSU("Taijutsu", "+0.1 dano corpo a corpo por nível"),
    NINJUTSU("Ninjutsu", "+4% dano de jutsus por nível"),
    DONJUTSU("Donjutsu", "+2% poder dos dojutsus por nível"),
    CHAKRA("Chakra", "+15 chakra máximo por nível"),
    VIDA("Vida", "+2 vida máxima por nível"),
    INTELIGENCIA("Inteligência", "-0.5% custo de chakra por nível");

    public static final int MAX_LEVEL = 100;
    public final String display, desc;
    Stat(String display, String desc) { this.display = display; this.desc = desc; }
}
