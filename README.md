# NarutoCraft (Forge 1.20.1) — Fase 1

## Como compilar (precisa de internet + JDK 17)
1. Baixe o MDK oficial do Forge 1.20.1 (47.3.0) em files.minecraftforge.net e extraia numa pasta.
2. Copie `src/` e substitua `build.gradle`, `settings.gradle`, `gradle.properties` por estes (o MDK traz o `gradlew`).
3. `./gradlew build` → jar em `build/libs/`. Para testar: `./gradlew runClient`.

## O que a Fase 1 faz
- Livro/Pergaminho Shinobi: você nasce com ele (login e respawn). Botão direito ou tecla K abre o painel.
- Atributos (SP): Taijutsu, Ninjutsu, Donjutsu, Chakra, Vida, Inteligência (nível máx 100, custo 10 + 5×nível).
- SP/JP ao matar: SP = vida máx × 0,5 (mob) ou × 1,5 (ninja/jogador); JP = 40% do SP em ninjas, vida/10 em mobs.
- Jutsus (JP p/ aprender, tecla R usa o equipado): Rasengan, Ragenshuriken (projétil em área), Modo Sábio (buff com dreno de chakra).
- Dojutsus: Sharingan, 5 Mangekyous (Sasuke/Obito/Madara/Itachi/Shisui), Rinnegan, Rinne Sharingan. No criativo o botão "Desbloquear" libera na hora; no survival só equipa o que já foi desbloqueado. Cada um dá bônus de dano e visão noturna.
- `Kage.java`: lista de Kages (vivos e mortos) das 4 vilas — dados prontos para a Fase 2.

## Fase 2 (não implementada)
Entidades/NPCs dos Kages, vilas (estruturas), Edo Tensei, habilidades únicas de cada dojutsu (Susanoo, Amaterasu, Kamui...), modelos/texturas próprios.
