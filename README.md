# Jump-and-Run

Ein einfaches Paper-Plugin für eine kleine Jump-and-Run-Lobby-Arena.

## Funktionen
- Befehl: /jump
- Spieler wird zu einer einfachen Jump-and-Run-Strecke teleportiert
- Aktive Blockstrecke mit spielbaren Sprüngen
- Glasblöcke als Sprungziele mit Partikeln
- HUD über der Hotbar mit:
  - aktuellen Sprüngen
  - Highscore

## Voraussetzungen
- Java 21
- Paper Server
- Gradle

## Bauen
```bash
gradle build
```

Die fertige Plugin-Datei befindet sich danach in:
```text
build/libs/jump-and-run-26.1.2.jar
```

## Installieren
1. Die JAR-Datei in den Plugins-Ordner deines Paper-Servers kopieren.
2. Den Server starten.
3. Mit /jump die Arena starten.

## Konfiguration
Die Startposition kann in der Datei `src/main/resources/config.yml` angepasst werden.

## Projektstruktur
- `src/main/java/...` – Plugin-Logik und Commands
- `src/main/resources/` – plugin.yml, config.yml
- `src/test/java/...` – einfache Tests

## Hinweis
Dieses Plugin ist ein MVP und kann später um weitere Arenen, Timer oder Bestzeiten erweitert werden.
