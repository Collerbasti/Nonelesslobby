# Noneless Lobby - NPC Integration

## 🎯 Übersicht
Das Noneless Lobby Plugin wurde mit einer vollständigen Citizens2 Integration erweitert, die automatisch 5-6 zufällige NPCs in der Nähe der Hauptlobby spawnt und sie herumlaufen lässt.

## 📋 Voraussetzungen

### Benötigte Plugins:
1. **Citizens2** - [Download von SpigotMC](https://www.spigotmc.org/resources/citizens.13811/)
   - Citizens2 muss vor dem NonelessLobby Plugin geladen werden
   - Das Plugin ist als "softdepend" konfiguriert

### Server-Anforderungen:
- Spigot/Paper 1.21.4 oder höher
- Java 17 oder höher

## 🔧 Installation

### 1. Citizens2 installieren
```bash
# Lade Citizens2 herunter und platziere es im plugins Ordner
# Starte den Server einmal, um Citizens2 zu initialisieren
```

### 2. Plugin kompilieren
```bash
# Im Projekt-Verzeichnis:
mvn clean package

# Das fertige Plugin findest du in target/NonelessLobby-3.0.jar
```

### 3. Plugin installieren
```bash
# Kopiere die JAR-Datei in den plugins Ordner
# Starte den Server neu
```

## 🎮 Features

### Automatische NPC-Verwaltung
- **5-6 zufällige NPCs** werden automatisch gespawnt
- **Zufällige Namen** aus einem vordefinierten Pool
- **Zufällige Skins** für Abwechslung
- **Intelligente Bewegung** - NPCs wandern in einem 20-Block-Radius um die Lobby
- **Spieler-Interaktion** - NPCs schauen Spieler an (8-Block-Reichweite)

### NPC-Verhalten
- **Wander-System**: NPCs laufen zufällig in der Lobby herum
- **Pause-System**: 3-7 Sekunden Verzögerung zwischen Bewegungen
- **Look-Close**: NPCs schauen Spieler automatisch an
- **Collision-Detection**: NPCs vermeiden Hindernisse

## 🎯 Commands

### Admin Commands
```bash
# Haupt-Command (benötigt nonelesslobby.admin Permission)
/lobbynpc <subcommand>

# Alternative Aliases
/npc <subcommand>
/npcs <subcommand>
```

### Verfügbare Subcommands:

#### `/lobbynpc spawn`
- Spawnt neue Lobby NPCs (5-6 Stück)
- Entfernt automatisch existierende NPCs

#### `/lobbynpc remove`
- Entfernt alle aktiven Lobby NPCs
- Zeigt Bestätigung mit Anzahl entfernter NPCs

#### `/lobbynpc reload`
- Lädt alle NPCs neu (entfernt alte und spawnt neue)
- Nützlich nach Konfigurationsänderungen

#### `/lobbynpc count`
- Zeigt die Anzahl aktiver Lobby NPCs an

#### `/lobbynpc setlobby`
- Setzt eine neue Lobby-Position an deiner aktuellen Position
- NPCs spawnen dann um diese neue Position herum
- Nur für Spieler verfügbar (nicht Console)

#### `/lobbynpc info`
- Zeigt Informationen über den NPC Manager
- Citizens2 Status und aktive NPCs

## ⚙️ Konfiguration

### Standard-Einstellungen
```java
// Anzahl NPCs: 5-6 (zufällig)
// Spawn-Radius: 30 Blöcke (15 Block Radius um Lobby)
// Wander-Radius: 20 Blöcke um Lobby
// Look-Range: 8 Blöcke
// Bewegungsdelay: 3-7 Sekunden
```

### Anpassung der Lobby-Position
```bash
# Gehe zur gewünschten Lobby-Position
# Führe folgenden Command aus:
/lobbynpc setlobby

# NPCs spawnen dann automatisch um diese Position
```

### Anpassung der NPC-Namen und Skins
Die Namen und Skins können in der `NPCManager.java` Datei angepasst werden:

```java
// In NPCManager.java Zeile ~30
private final String[] npcNames = {
    "Alex", "Steve", "Emma", "Liam", "Sophie", "Noah", 
    // Füge hier deine eigenen Namen hinzu
};

// In NPCManager.java Zeile ~37
private final String[] skins = {
    "Notch", "jeb_", "Dinnerbone", "Grumm",
    // Füge hier eigene Skin-Namen hinzu
};
```

## 🔍 Troubleshooting

### NPCs spawnen nicht
1. **Citizens2 prüfen:**
   ```bash
   /plugins
   # Stelle sicher, dass Citizens2 grün (aktiviert) ist
   ```

2. **Permissions prüfen:**
   ```bash
   # Stelle sicher, dass du nonelesslobby.admin Permission hast
   /lobbynpc info
   ```

3. **Manuell spawnen:**
   ```bash
   /lobbynpc spawn
   ```

### NPCs bewegen sich nicht
1. **Citizens2 neu laden:**
   ```bash
   /citizens reload
   ```

2. **NPCs neu laden:**
   ```bash
   /lobbynpc reload
   ```

### Fehler im Console-Log
```bash
# Suche nach diesen Fehlermeldungen:
[NonelessLobby] Citizens2 ist nicht verfügbar. NPCs werden nicht gespawnt.
[NonelessLobby] Welt 'world' wurde nicht gefunden!

# Lösung: Stelle sicher, dass Citizens2 installiert ist
# und die richtige Welt-Name in NPCManager.java konfiguriert ist
```

## 📝 Permissions

```yaml
# Admin Permissions (in plugin.yml definiert)
nonelesslobby.admin:
  description: Admin Permissions für NPC Management
  default: op
  children:
    nonelesslobby.admin: true
```

## 🔄 Updates und Wartung

### Plugin Update
1. Stoppe den Server
2. Ersetze die JAR-Datei
3. Starte den Server neu
4. NPCs werden automatisch neu gespawnt

### Citizens2 Update
1. Aktualisiere Citizens2
2. Führe `/lobbynpc reload` aus
3. NPCs werden mit neuen Citizens2 Features neu geladen

## 🎨 Anpassungsmöglichkeiten

### Mehr NPCs spawnen
In `NPCManager.java` Zeile ~77:
```java
// Ändere diese Zeile:
int npcCount = 5 + random.nextInt(2); // 5 oder 6 NPCs

// Zu beispielsweise:
int npcCount = 8 + random.nextInt(3); // 8-10 NPCs
```

### Andere Welt verwenden
In `NPCManager.java` Zeile ~67:
```java
// Ändere diese Zeile:
World world = Bukkit.getWorld("world");

// Zu deiner Lobby-Welt:
World world = Bukkit.getWorld("lobby");
```

### Spawn-Bereich anpassen
In `NPCManager.java` Zeile ~84-85:
```java
// Ändere diese Werte für größeren/kleineren Spawn-Bereich:
double offsetX = (random.nextDouble() - 0.5) * 30; // Aktuell: 30 Blöcke
double offsetZ = (random.nextDouble() - 0.5) * 30; // Ändere zu deinem Wert
```

## 📞 Support

Bei Problemen oder Fragen:
1. Überprüfe die Console-Logs
2. Teste mit `/lobbynpc info`
3. Stelle sicher, dass alle Permissions korrekt sind
4. Vergewissere dich, dass Citizens2 richtig installiert ist

---

**Entwickelt für Noneless Server**  
**Version: 3.0**  
**Compatible mit: Citizens2 2.0.35+, Spigot 1.21.4+**