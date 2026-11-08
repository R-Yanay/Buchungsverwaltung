# Buchungsverwaltung – Spring Boot Flugbuchungs-API

Eine REST-API zur Verwaltung von Flügen und Passagieren, gebaut mit Spring Boot.
Das Projekt dient als Lernprojekt für Spring Boot-Grundlagen: Controller, Service,
Entity, Repository und H2-Datenbank.

## Features

- Flüge erstellen, lesen und nach ID suchen
- Passagiere ein- und ausgebuchen
- Passagierliste pro Flug anzeigen
- Kapazitätsprüfung – keine Überbuchung möglich
- H2-Datenbank im Speicher – keine Installation nötig
- JSON-Antworten für alle Endpunkte

## Technischer Stack

| Technologie | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.2 |
| Spring Data JPA | (inklusive) |
| H2 Database | (inklusive) |
| Build-System | Maven |
| IDE | IntelliJ IDEA |

## Projektstruktur

```
src/main/java/ch/zhaw/flug/
├── BuchungsverwaltungApplication.java  # Spring Boot Einstiegspunkt
├── model/
│   ├── Flug.java                       # Entity: Ziel, Abflug, Kapazität
│   └── Passagier.java                  # Entity: Name, Alter, Flug-Relation
├── repository/
│   ├── FlugRepository.java             # Datenbankzugriff für Flüge
│   └── PassagierRepository.java        # Datenbankzugriff für Passagiere
├── service/
│   └── BuchungService.java             # Geschäftslogik: einbuchen, ausgebuchen
└── controller/
    └── FlugController.java             # HTTP-Endpunkte: GET, POST, DELETE
```

## API-Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| GET | `/fluege` | Alle Flüge anzeigen |
| GET | `/fluege/{id}` | Einen Flug nach ID suchen |
| POST | `/fluege` | Neuen Flug erstellen |
| POST | `/fluege/{id}/buchen` | Passagier einbuchen |
| DELETE | `/fluege/{id}/ausbuchen` | Passagier ausgebuchen |
| GET | `/fluege/{id}/passagiere` | Alle Passagiere eines Flugs |

## Starten der App

### Voraussetzungen

- Java 25 (oder höher) installiert
- Maven installiert
- IntelliJ IDEA (empfohlen) oder eine andere Java-IDE

### Über Maven

```bash
mvn spring-boot:run
```

Der Server startet auf `http://localhost:8080`.

### Über IntelliJ IDEA

1. Projekt in IntelliJ IDEA öffnen
2. `BuchungsverwaltungApplication.java` öffnen
3. Auf Run klicken oder `Shift+F10` drücken

## API-Beispiele

### Flug erstellen

```bash
curl -X POST http://localhost:8080/fluege \
  -H "Content-Type: application/json" \
  -d '{"ziel": "Paris", "abflug": "2026-12-01T10:00", "maxKapazitaet": 5}'
```

### Passagier einbuchen

```bash
curl -X POST http://localhost:8080/fluege/1/buchen \
  -H "Content-Type: application/json" \
  -d '{"name": "Max Mustermann", "alter": 30}'
```

### Alle Passagiere anzeigen

```bash
curl http://localhost:8080/fluege/1/passagiere
```

## Tests

Das Projekt enthält **21 JUnit-Tests**, die zwei Ebenen der Anwendung abdecken.

### Tests ausführen

```bash
mvn test
```

### Service-Tests (11 Tests) – `BuchungServiceTest.java`

Startet eine echte **H2-Testdatenbank** und prüft die gesamte Kette: **Service → Repository → Datenbank**.

| Test | Was wird geprüft? |
|---|---|
| `flugErstellen_speichertFlug_undGibtIddZurueck` | Ein neuer Flug wird gespeichert und bekommt eine ID |
| `alleFluege_gibtAlleFluegeZurueck` | Alle Flüge werden als Liste zurückgegeben |
| `flugSuchen_gibtFlugZurueck` | Ein Flug wird nach ID gefunden |
| `flugSuchen_wennNichtVorhanden_wirftException` | Suche nach nicht-existentem Flug wirft Fehler |
| `passagierEinbuchen_fuegtPassagierHinzu` | Passagier wird erfolgreich eingebucht |
| `passagierEinbuchen_wennVoll_wirftException` | **Kapazitätsgrenze** wird eingehalten – kein Überbuchen |
| `passagierEinbuchen_wennFlugNichtVorhanden_wirftException` | Einbuchen in nicht-existenten Flug wirft Fehler |
| `passagierAusbuchen_entferntPassagier` | Passagier wird erfolgreich ausgebucht |
| `passagierAusbuchen_wennNichtGebucht_wirftException` | Ausbuchen nicht-gebuchter Passagier wirft Fehler |
| `allePassagiere_gibtPassagierListeZurueck` | Passagierliste wird korrekt zurückgegeben |
| `allePassagiere_wennFlugNichtVorhanden_wirftException` | Passagiere von nicht-existentem Flug wirft Fehler |

### Controller-Tests (10 Tests) – `FlugControllerTest.java`

Simuliert **HTTP-Anfragen** ohne echten Server. Der Service wird **gemockt** – nur der Controller wird getestet.

| Test | Was wird geprüft? |
|---|---|
| `fluegeAnzeigen_gibtLeereListe` | GET `/fluege` gibt leere Liste `[]` zurück |
| `fluegeAnzeigen_gibtFlugListe` | GET `/fluege` gibt Flug-Liste mit korrekten Werten |
| `flugErstellen_gibtNeuenFlug` | POST `/fluege` erstellt Flug mit Status **201 Created** |
| `passagierEinbuchen_gibtErfolg` | POST `/fluege/1/buchen` gibt Erfolgsmeldung |
| `passagierEinbuchen_wennFlugNichtVorhanden_gibt400` | Einbuchen bei fehlendem Flug gibt **400 Bad Request** |
| `passagierEinbuchen_wennVoll_gibt400` | Einbuchen bei voller Kapazität gibt **400 Bad Request** |
| `passagierAusbuchen_gibtErfolg` | DELETE `/fluege/1/ausbuchen` gibt Erfolgsmeldung |
| `passagierAusbuchen_wennNichtGebucht_gibt400` | Ausbuchen nicht-gebuchter Passagier gibt **400 Bad Request** |
| `passagiereAnzeigen_gibtListe` | GET `/fluege/1/passagiere` gibt Passagier-Liste |
| `passagiereAnzeigen_wennFlugNichtVorhanden_gibt404` | Passagiere bei fehlendem Flug gibt **404 Not Found** |

### Unterschied zu Postman

| Postman | JUnit |
|---|---|
| Manuell – du klickst auf Send | Automatisch – `mvn test` läuft alles durch |
| Gut zum Entdecken und Debuggen | Gut zum Sicherstellen, dass nichts kaputt geht |
| Anfragen müssen gespeichert werden | Tests sind Teil des Projekts in Git |
| Nur du kannst sie ausführen | Jeder im Team kann sie ausführen |

**Kurz: Postman ist zum manuellen Testen, JUnit ist zur Automatisierung.**

## API testen mit Postman

Die Postman-Requests liegen im Projekt unter `postman/collections/Buchungsverwaltung API-1/` als YAML-Dateien.
Diese werden automatisch mit deinem Postman-Workspace synchronisiert.

Um die API in Postman zu testen:

1. Postman öffnen
2. Den Local File-based Workspace aus diesem Projekt öffnen
3. Die Collection `Buchungsverwaltung API` erscheint links mit 4 Requests:
   - Flug erstellen (POST)
   - Alle Flüge anzeigen (GET)
   - Passagier einbuchen (POST)
   - Passagiere anzeigen (GET)
4. Server starten und die Requests mit **Send** ausführen

## Was ich gelernt habe

Bei diesem Projekt habe ich Spring Boot kennengelernt und verstanden, wie man eine REST-API mit Java baut. Im Gegensatz zu JavaFX (Desktop-GUI) läuft eine Spring Boot-App auf einem Server und antwortet auf HTTP-Anfragen mit JSON. Die wichtigsten Konzepte waren `@RestController` für Endpunkte, `@Service` für Geschäftslogik, `@Entity` und `@Repository` für den Datenbankzugriff mit Spring Data JPA. Die H2-Datenbank läuft automatisch im Speicher – perfekt zum Lernen ohne Installation.

Zusätzlich habe ich gelernt, wie man die API mit **Postman** manuell testet und mit **JUnit-Tests** automatisch prüft. Die 21 Tests decken alle Endpunkte und Fehlerfälle ab und laufen bei jedem Build mit `mvn test`.

## Lizenz

Dieses Projekt wurde als Übungprojekt erstellt und dient Demonstrationszwecken.
