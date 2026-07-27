# Plan für das Jump-and-Run-Lobby-Plugin

## Ziel
Ein Paper-Plugin mit dem Befehl /jump, das Spieler in eine einfache Jump-and-Run-Arena teleportiert, dort eine schrittweise Strecke erzeugt und die Sprünge bewusst machbar hält.

## Konkrete Anforderungen aus deiner Beschreibung
- Plugin-Version: 26.1.2
- Server-API: Paper
- Befehl: /jump
- Beim Start wird der Spieler an einen Startpunkt teleportiert
- Es werden immer drei Blöcke in einer Reihe erzeugt
- Der erste Block ist ein normaler Block aus Wolle
- Vor diesem Block wird ein weiterer Block in derselben Farbe als Glasblock mit Partikeln erzeugt
- Der Spieler muss auf diesen Glasblock springen
- Wenn der Glasblock berührt wird, soll er wieder zur Wolle-Farbe werden
- Der dritte Block wird dann ebenfalls zu Glas und ein neuer dritter Block wird erzeugt
- Die Sprünge müssen immer spielbar sein

## Spielprinzip für die erste Version
Die Arena soll als einfache lineare Strecke aufgebaut werden:
1. Startpunkt festlegen
2. Ein Segment besteht aus 3 Blöcken:
   - Block 1: Wolle-Block als Standfläche
   - Block 2: Glasblock mit Partikeln als Ziel für den Sprung
   - Block 3: vorbereiteter nächster Block, der später aktiviert wird
3. Sobald der Spieler den Glasblock berührt, wird:
   - der Glasblock in Wolle umgewandelt
   - der dritte Block aktiviert und zu Glas gemacht
   - ein neuer dritter Block erzeugt
4. Die Abstände werden so gewählt, dass die Sprünge realistisch und machbar bleiben

## Wichtig für die Spielbarkeit
- Keine zu großen Sprünge
- Abstand zwischen den Blöcken bewusst klein halten
- Höhe der Strecke kontrollieren
- Wenn nötig: kurze Laufstrecken oder kleine Plattformen ergänzen
- Spieler dürfen nicht in unlösbare Lücken fallen

## Technischer Stack
- Java
- Gradle
- Paper API
- YAML für Konfiguration
- Optional später: Scoreboard, Bestzeiten, Mehrere Arenen

## Projektstruktur
- src/main/java/de/yourname/jumpplugin/Main.java
- commands/JumpCommand.java
- arena/ArenaManager.java
- arena/BlockSequence.java
- listeners/PlayerMoveListener.java
- listeners/PlayerQuitListener.java
- config/ConfigManager.java
- utils/LocationUtil.java

## Umsetzungsplan

### Phase 1: Grundgerüst
- Paper-Plugin mit Gradle einrichten
- Main-Class mit onEnable/onDisable bauen
- /jump-Befehl registrieren
- Startpunkt und Arena-Spawnpunkt definieren

### Phase 2: Arena-Mechanik
- Beim /jump-Befehl den Spieler teleportieren
- Eine einfache Strecke erzeugen
- Drei Blöcke pro Segment platzieren
- Den aktuellen Glasblock als Sprungziel markieren
- Beim Betreten des Glasblocks den nächsten Zustand auslösen

### Phase 3: Zustandslogik
- Blockzustände verwalten: aktiv, abgeschlossen, neu erzeugt
- Wolle- und Glasblöcke sauber wechseln
- Partikel nur bei aktiven Glasblöcken anzeigen
- Fehler vermeiden, wenn der Spieler den Block nicht trifft

### Phase 4: Spielbarkeit und Feinschliff
- Sprungabstände testen und anpassen
- Sicherstellen, dass die Strecke nicht zu schwer wird
- Start- und Zielbereich sauber definieren
- Optional: Reset-Funktion bei Fall oder Neustart

### Phase 5: Erweiterungen später
- Mehrere Arenen
- Timer und Bestzeiten
- Scoreboard
- Admin-Befehle wie /jump reload

## MVP für den Start
- 1 Arena
- 1 Startpunkt
- 1 einfache Strecke mit machbaren Sprüngen
- 1 /jump-Befehl
- 1 einfache Block-Sequenz-Logik

## Zielzustand nach dem ersten Build
Ein Spieler kann mit /jump in die Arena springen, bekommt eine klare Strecke aus Blöcken, muss die Glasblöcke sicher betreten und durchläuft die Sequenz bis zum Ende.
