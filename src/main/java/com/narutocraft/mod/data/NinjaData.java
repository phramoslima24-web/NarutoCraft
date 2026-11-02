package com.narutocraft.mod.data;

import net.minecraft.nbt.CompoundTag;

public class NinjaData {
    public int sp, jp;
    public final int[] stats = new int[Stat.values().length];
    public float chakra = 100f;
    public int dojutsuMask, dojutsuActive = -1;
    public int jutsuMask, selected = -1;
    public boolean sage, bookGiven;
    // transitórios (não salvos)
    public int rasenganTicks;
    public final int[] cooldown = new int[Jutsu.values().length];

    public int level(Stat s) { return stats[s.ordinal()]; }
    public int upgradeCost(Stat s) { return 10 + 5 * level(s); }
    public float maxChakra() { return 100f + 15f * level(Stat.CHAKRA); }
    public boolean has(Jutsu j) { return (jutsuMask & (1 << j.ordinal())) != 0; }
    public boolean has(Dojutsu d) { return (dojutsuMask & (1 << d.ordinal())) != 0; }
    public Dojutsu activeDojutsu() { return dojutsuActive < 0 ? null : Dojutsu.values()[dojutsuActive]; }

    public float chakraCost(Jutsu j) {
        return j.chakraCost * (1f - Math.min(0.5f, level(Stat.INTELIGENCIA) * 0.005f));
    }
    public float jutsuDamage(Jutsu j) { return j.damage * (1f + level(Stat.NINJUTSU) * 0.04f); }
    public float dojutsuBonus() {
        Dojutsu d = activeDojutsu();
        return d == null ? 0f : d.power * (1f + level(Stat.DONJUTSU) * 0.02f);
    }

    public CompoundTag save() {
        CompoundTag t = new CompoundTag();
        t.putInt("sp", sp); t.putInt("jp", jp); t.putFloat("chakra", chakra);
        t.putIntArray("stats", stats);
        t.putInt("dMask", dojutsuMask); t.putInt("dActive", dojutsuActive);
        t.putInt("jMask", jutsuMask); t.putInt("sel", selected);
        t.putBoolean("sage", sage); t.putBoolean("book", bookGiven);
        return t;
    }

    public void load(CompoundTag t) {
        sp = t.getInt("sp"); jp = t.getInt("jp");
        chakra = t.contains("chakra") ? t.getFloat("chakra") : 100f;
        int[] s = t.getIntArray("stats");
        for (int i = 0; i < stats.length && i < s.length; i++) stats[i] = s[i];
        dojutsuMask = t.getInt("dMask"); dojutsuActive = t.contains("dActive") ? t.getInt("dActive") : -1;
        jutsuMask = t.getInt("jMask"); selected = t.contains("sel") ? t.getInt("sel") : -1;
        sage = t.getBoolean("sage"); bookGiven = t.getBoolean("book");
    }

    public void copyFrom(NinjaData o) { load(o.save()); sage = false; }
}
