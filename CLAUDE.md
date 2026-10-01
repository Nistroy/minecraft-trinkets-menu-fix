# minecraft-trinkets-menu-fix — instructions agents

Mod Fabric 1.21.1, serveur seul (client se répare via `applySyncPacket`). Serveur `Nistroy/minecraft-server`
(`MODS.md`). Public, GPL-3.0. Docs `.md` = notes denses pour agents, sauf `README.md`.

## But (nistroy 2026-10-01)
Bug pertes/dupes slots Trinkets depuis 2026-09-27. Enquête : `~/minecraft-tools/back-slot/ENQUETE.md`.

## Cause (Trinkets 3.10.0 + Aether 1.21.1-1.5.11, lu au décompilateur 2026-10-01)
- `AetherAccessoriesMenu extends InventoryMenu` → mixin Trinkets `PlayerScreenHandlerMixin.init` (RETURN `<init>`)
  → `trinkets$updateTrinketSlots(true)` → `LivingEntityTrinketComponent.update()` : nouvelles `TrinketInventory`
  (piles copiées).
- Seul l'écran en création est reconstruit ; `player.inventoryMenu` garde les `SurvivalTrinketSlot` sur les anciennes
  → retirer = dupe, poser = perte, jusqu'au relog/respawn.

## Carte
- `mixin/LivingEntityTrinketComponentMixin` — RETURN de `update()` : `trinkets$updateTrinketSlots(false)` sur
  `player.inventoryMenu` (null pendant construction du joueur → ignoré). `false` = pas de récursion. Appel imbriqué
  dans `updateTrinketSlots(true)` de l'écran du joueur = double reconstruction, sans effet.
- `depends.trinkets = 3.10.0` exact : mixin sur classe interne (`remap = false`) ; nouvelle version → relire `update()`.

## Tests — TDD obligatoire
- `JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew build runGameTest` (CI idem). Rouge d'abord.
- Résultat dans `build/run/gameTest/logs/latest.log` (`All N required tests passed`) ; sortie console filtrée par rtk.
- Repro sans Aether : `new InventoryMenu(player.getInventory(), true, player)` = ce que fait le `super(...)` d'Aether.
- Emplacement `chest/back` activé pour le joueur par `src/gametest/resources/data/trinkets/entities/`.

## Release
Tag `vX.Y.Z` = `version` de `gradle.properties` → workflow `release` → jar sur la release GitHub.
