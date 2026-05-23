# 🗳️ Democratic Mod — Guide d'installation complet

## C'est quoi ce mod ?

Ce mod empêche un joueur de "casser" la survie en utilisant des commandes puissantes sans l'accord du groupe.

**Comment ça marche :**
- Un joueur tape `/gamemode creative` (ou F3+F4, `/give`, `/op`, etc.)
- → Au lieu de s'exécuter, un vote est lancé pour TOUS les autres joueurs
- → Chaque joueur doit taper `/voteyes` ✅ ou `/voteno` ❌
- → La commande ne s'exécute **que si TOUT LE MONDE vote oui**
- → Si quelqu'un vote non OU si 30 secondes s'écoulent → annulé

---

## 📋 Prérequis

Avant d'installer le mod, il faut :
1. **Java 17** installé sur l'ordinateur qui héberge le serveur
2. **Fabric Loader** (pas Forge !)
3. **Fabric API** (un mod nécessaire pour que les mods Fabric fonctionnent)

---

## 🛠️ Étape 1 — Installer Fabric sur le serveur

1. Aller sur https://fabricmc.net/use/installer/
2. Cliquer sur **"Download installer (Universal/.JAR)"**
3. Lancer le fichier téléchargé
4. Choisir l'onglet **"Serveur"**
5. Sélectionner la version Minecraft (ex: 1.20.1)
6. Cliquer **Installer**
7. Un fichier `fabric-server-launch.jar` est créé → c'est ce fichier qui lance le serveur

---

## 🛠️ Étape 2 — Installer Fabric API

1. Aller sur https://modrinth.com/mod/fabric-api
2. Télécharger la version correspondant à ta version Minecraft
3. Mettre le fichier `.jar` dans le dossier `mods/` du serveur
   (créer le dossier s'il n'existe pas)

---

## 🛠️ Étape 3 — Compiler le Democratic Mod

### Option A — Compiler soi-même (nécessite Java 17 + Git)

```bash
# Cloner/copier les sources dans un dossier
cd democratic-mod

# Compiler
./gradlew build   # Linux/Mac
gradlew.bat build  # Windows
```

Le fichier `.jar` sera dans `build/libs/democratic-mod-1.0.0.jar`

### Option B — Demander à quelqu'un de compiler pour vous

Envoyer le dossier `democratic-mod/` à quelqu'un qui a Java + Gradle installé.
Il fait `./gradlew build` et vous donne le `.jar` résultant.

---

## 🛠️ Étape 4 — Installer le mod

1. Copier `democratic-mod-1.0.0.jar` dans le dossier `mods/` du serveur
2. S'assurer que `fabric-api-xxx.jar` est aussi dans `mods/`
3. Démarrer le serveur avec `fabric-server-launch.jar`

---

## 🎮 Commandes disponibles en jeu

| Commande | Description |
|----------|-------------|
| `/voteyes` | Voter OUI pour la commande en cours |
| `/voteno` | Voter NON (annule immédiatement) |
| `/votestatus` | Voir les votes en cours |

---

## 🔒 Commandes protégées par un vote

- `/gamemode` / `/gm` (et F3+F4)
- `/give`
- `/op` / `/deop`
- `/weather`
- `/time set`
- `/difficulty`
- `/tp` / `/teleport`
- `/kill`
- `/ban` / `/kick`
- `/stop` (arrêter le serveur)
- `/fill`, `/clone`, `/setblock`
- `/effect`, `/enchant`
- `/clear`

---

## ❓ FAQ

**Q : Et si le joueur est seul sur le serveur ?**
→ La commande s'exécute directement, pas besoin de voter.

**Q : L'initiateur peut-il voter pour sa propre commande ?**
→ Non, seuls les autres joueurs votent.

**Q : Que se passe-t-il si personne ne vote en 30s ?**
→ La commande est annulée automatiquement.

**Q : Un joueur peut-il voter deux fois ?**
→ Non, un seul vote par joueur par demande.

**Q : La console du serveur est-elle aussi limitée ?**
→ Non, seuls les joueurs en jeu sont limités. La console reste libre.

---

## 🆘 Problèmes fréquents

**Le mod ne se charge pas**
→ Vérifier que Fabric API est bien dans `mods/`
→ Vérifier que la version du mod correspond à la version Minecraft

**Les commandes ne sont pas interceptées**
→ Vérifier que le joueur est bien OP (le mod n'intercepte que les commandes OP)
→ Relancer le serveur proprement

**Erreur "mixin failed"**
→ Incompatibilité avec un autre mod, signaler le nom du mod conflictuel
