# Subnetting und Routing System

Dieses Programm ist ein System zum Routing in und zwischen Subnetzen. Es verwaltet beliebig viele Subnetze beliebiger Größe und findet aufgrund der Topologie den kürzesten Pfad zwischen beliebigen Systemen. Dadurch ermöglicht es den Austausch von Paketen.

## Funktionen

- **Verwaltung von Subnetzen**: Erstellen und Verwalten von beliebig vielen Subnetzen unterschiedlicher Größe
- **Systemverwaltung**: Hinzufügen und Entfernen von Computern und Routern
- **Verbindungsverwaltung**: Erstellen und Entfernen von Verbindungen zwischen Systemen
- **Pfadfindung**: Berechnung des kürzesten Pfades zwischen beliebigen Systemen
  - Intra-Subnet-Routing mit Dijkstra-Algorithmus
  - Inter-Subnet-Routing mit BGP-Tabellen
- **Paketaustausch**: Senden von Paketen zwischen Systemen über den berechneten Pfad

## Komponenten

- **Network**: Zentrale Datenstruktur zur Verwaltung von Subnetzen, Systemen und Verbindungen
- **Subnet**: Repräsentiert ein Subnetz mit CIDR-Notation und enthält Systeme
- **Systems**: Basisklasse für alle Netzwerksysteme
  - **Computer**: Endgerät in einem Subnetz
  - **Router**: Verbindet Subnetze und ermöglicht Inter-Subnet-Routing
- **Connection**: Verbindung zwischen zwei Systemen mit Gewichtung
- **PathFinder**: Implementiert Algorithmen zur Pfadfindung

## Verwendung

Das Programm wird über die Kommandozeile bedient. Folgende Befehle stehen zur Verfügung:

- `load network`: Lädt ein Netzwerk aus einer Datei
- `list`: Listet alle Subnetze auf
- `list range`: Zeigt den IP-Bereich eines Subnetzes an
- `list systems`: Listet alle Systeme in einem Subnetz auf
- `add computer`: Fügt einen Computer zu einem Subnetz hinzu
- `remove computer`: Entfernt einen Computer aus einem Subnetz
- `add connection`: Erstellt eine Verbindung zwischen zwei Systemen
- `remove connection`: Entfernt eine Verbindung zwischen zwei Systemen
- `send packet`: Sendet ein Paket von einem System zu einem anderen
- `quit`: Beendet das Programm

## Technische Details

Das System verwendet:
- Dijkstra-Algorithmus für Intra-Subnet-Routing
- BGP-Tabellen für Inter-Subnet-Routing
- Automatische Aktualisierung der Routing-Tabellen bei Änderungen der Netzwerktopologie

Alle Quelltextdateien befinden sich im Verzeichnis `src/`.
