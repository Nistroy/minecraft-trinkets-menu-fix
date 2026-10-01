# Correctif des emplacements Trinkets figés

Mod Fabric 1.21.1 pour le serveur de nistroy, avec Trinkets 3.10.0.

## Le problème

Quand un autre écran d'inventaire du joueur est créé (par exemple l'écran accessoires d'Aether, qui hérite de
l'inventaire vanilla), Trinkets recrée les inventaires de ses emplacements (sac, cape, colliers…) mais laisse
l'inventaire habituel du joueur branché sur les anciens. Jusqu'à la déconnexion ou la mort du joueur :

- retirer un objet d'un emplacement le duplique ;
- poser un objet dans un emplacement le fait disparaître.

## Le correctif

Après chaque recréation des inventaires, l'inventaire du joueur est rebranché sur les nouveaux. Il suffit côté
serveur ; il est sans effet de bord s'il est aussi installé côté client.

## Tests

```
./gradlew build runGameTest
```
