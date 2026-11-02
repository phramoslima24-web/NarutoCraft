package com.narutocraft.mod.data;

/** Dados para a próxima fase (NPCs/chefes kage e Edo Tensei). Ainda não spawnam. */
public enum Kage {
    HASHIRAMA(Village.KONOHA, "Hashirama Senju (1º Hokage)", true),
    TOBIRAMA(Village.KONOHA, "Tobirama Senju (2º Hokage)", true),
    HIRUZEN(Village.KONOHA, "Hiruzen Sarutobi (3º Hokage)", true),
    MINATO(Village.KONOHA, "Minato Namikaze (4º Hokage)", true),
    TSUNADE(Village.KONOHA, "Tsunade (5ª Hokage)", false),
    DANZO(Village.KONOHA, "Danzo Shimura (6º Hokage)", true),
    KAKASHI(Village.KONOHA, "Kakashi Hatake (6º Hokage)", false),
    NARUTO(Village.KONOHA, "Naruto Uzumaki (7º Hokage)", false),
    GAARA(Village.SUNA, "Gaara (5º Kazekage)", false),
    RASA(Village.SUNA, "Rasa (4º Kazekage)", true),
    ONOKI(Village.IWA, "Onoki (3º Tsuchikage)", false),
    MU(Village.IWA, "Mu (2º Tsuchikage)", true),
    KUROTSUCHI(Village.IWA, "Kurotsuchi (5ª Tsuchikage)", false),
    A_RAIKAGE(Village.KUMO, "A (4º Raikage)", false),
    A3_RAIKAGE(Village.KUMO, "3º Raikage", true),
    A2_RAIKAGE(Village.KUMO, "2º Raikage", true),
    DARUI(Village.KUMO, "Darui (5º Raikage)", false);

    public final Village village;
    public final String display;
    /** true = morto na obra original; só aparece via Edo Tensei. */
    public final boolean deceased;
    Kage(Village v, String d, boolean dead) { village = v; display = d; deceased = dead; }

    public enum Village {
        KONOHA("Vila da Folha"), SUNA("Vila da Areia"), IWA("Vila da Pedra"), KUMO("Vila da Nuvem");
        public final String display;
        Village(String d) { display = d; }
    }
}
