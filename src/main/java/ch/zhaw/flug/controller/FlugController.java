package ch.zhaw.flug.controller;

import ch.zhaw.flug.model.Flug;
import ch.zhaw.flug.model.Passagier;
import ch.zhaw.flug.service.BuchungService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST-Controller fuer Flug- und Buchungsoperationen.
 * Stellt HTTP-Endpunkte bereit und gibt JSON-Antworten zurueck.
 */
@RestController
@RequestMapping("/fluege")
public class FlugController {

    private final BuchungService buchungService;

    public FlugController(BuchungService buchungService) {
        this.buchungService = buchungService;
    }

    /**
     * GET /fluege - Alle Fluege anzeigen.
     */
    @GetMapping
    public List<Flug> alleFluege() {
        return buchungService.alleFluege();
    }

    /**
     * GET /fluege/{id} - Einen Flug nach ID suchen.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Flug> flugSuchen(@PathVariable Long id) {
        try {
            Flug flug = buchungService.flugSuchen(id);
            return ResponseEntity.ok(flug);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * POST /fluege - Neuen Flug erstellen.
     * Erwartet JSON: {"ziel": "Paris", "abflug": "2026-12-01T10:00", "maxKapazitaet": 5}
     */
    @PostMapping
    public ResponseEntity<Flug> flugErstellen(@RequestBody Map<String, Object> body) {
        String ziel = (String) body.get("ziel");
        String abflugStr = (String) body.get("abflug");
        int maxKapazitaet = ((Number) body.get("maxKapazitaet")).intValue();

        LocalDateTime abflug = LocalDateTime.parse(abflugStr);
        Flug flug = buchungService.flugErstellen(ziel, abflug, maxKapazitaet);

        return ResponseEntity.status(HttpStatus.CREATED).body(flug);
    }

    /**
     * POST /fluege/{id}/buchen - Passagier einbuchen.
     * Erwartet JSON: {"name": "Max Mustermann", "alter": 30}
     */
    @PostMapping("/{id}/buchen")
    public ResponseEntity<?> passagierEinbuchen(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            int alter = ((Number) body.get("alter")).intValue();

            Passagier passagier = buchungService.passagierEinbuchen(id, name, alter);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Passagier " + name + " erfolgreich eingebucht",
                    "passagier", passagier
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /fluege/{id}/ausbuchen - Passagier ausgebuchen.
     * Erwartet JSON: {"name": "Max Mustermann", "alter": 30}
     */
    @DeleteMapping("/{id}/ausbuchen")
    public ResponseEntity<?> passagierAusbuchen(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            int alter = ((Number) body.get("alter")).intValue();

            buchungService.passagierAusbuchen(id, name, alter);
            return ResponseEntity.ok(Map.of("message", "Passagier " + name + " erfolgreich ausgebucht"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /fluege/{id}/passagiere - Alle Passagiere eines Flugs anzeigen.
     */
    @GetMapping("/{id}/passagiere")
    public ResponseEntity<?> allePassagiere(@PathVariable Long id) {
        try {
            List<Passagier> passagiere = buchungService.allePassagiere(id);
            return ResponseEntity.ok(passagiere);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
