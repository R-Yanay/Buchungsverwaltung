package ch.zhaw.flug.service;

import ch.zhaw.flug.model.Flug;
import ch.zhaw.flug.model.Passagier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests fuer den BuchungService.
 * Verwendet eine echte H2-Testdatenbank, um die gesamte Kette
 * (Service -> Repository -> Datenbank) zu testen.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BuchungServiceTest {

    @Autowired
    private BuchungService buchungService;

    private Long flugId;

    @BeforeEach
    void setUp() {
        // Jede Testmethode startet mit einem frischen Flug
        Flug flug = buchungService.flugErstellen("Paris", LocalDateTime.of(2026, 12, 1, 10, 0), 3);
        flugId = flug.getId();
    }

    @Test
    void flugErstellen_speichertFlug_undGibtIddZurueck() {
        Flug flug = buchungService.flugErstellen("Berlin", LocalDateTime.of(2026, 11, 15, 8, 0), 10);

        assertNotNull(flug.getId());
        assertEquals("Berlin", flug.getZiel());
        assertEquals(10, flug.getMaxKapazitaet());
        assertEquals(0, flug.getAnzahlPassagiere());
    }

    @Test
    void alleFluege_gibtAlleFluegeZurueck() {
        buchungService.flugErstellen("Rom", LocalDateTime.of(2026, 12, 5, 14, 0), 5);

        List<Flug> alleFluege = buchungService.alleFluege();

        assertEquals(2, alleFluege.size());
    }

    @Test
    void flugSuchen_gibtFlugZurueck() {
        Flug flug = buchungService.flugSuchen(flugId);

        assertEquals("Paris", flug.getZiel());
    }

    @Test
    void flugSuchen_wennNichtVorhanden_wirftException() {
        assertThrows(RuntimeException.class, () -> {
            buchungService.flugSuchen(-999L);
        });
    }

    @Test
    void passagierEinbuchen_fuegtPassagierHinzu() {
        Passagier passagier = buchungService.passagierEinbuchen(flugId, "Max", 30);

        assertEquals("Max", passagier.getName());
        assertEquals(30, passagier.getAlter());

        List<Passagier> passagiere = buchungService.allePassagiere(flugId);
        assertEquals(1, passagiere.size());
    }

    @Test
    void passagierEinbuchen_wennVoll_wirftException() {
        // Flug hat Kapazitaet 3 — buche 3 Passagiere ein
        buchungService.passagierEinbuchen(flugId, "Max", 30);
        buchungService.passagierEinbuchen(flugId, "Anna", 25);
        buchungService.passagierEinbuchen(flugId, "Tom", 40);

        // Der 4. Passagier soll abgelehnt werden
        assertThrows(RuntimeException.class, () -> {
            buchungService.passagierEinbuchen(flugId, "Lisa", 22);
        });

        // Flug hat immer noch nur 3 Passagiere
        List<Passagier> passagiere = buchungService.allePassagiere(flugId);
        assertEquals(3, passagiere.size());
    }

    @Test
    void passagierEinbuchen_wennFlugNichtVorhanden_wirftException() {
        assertThrows(RuntimeException.class, () -> {
            buchungService.passagierEinbuchen(-999L, "Max", 30);
        });
    }

    @Test
    void passagierAusbuchen_entferntPassagier() {
        buchungService.passagierEinbuchen(flugId, "Max", 30);
        List<Passagier> vorher = buchungService.allePassagiere(flugId);
        assertEquals(1, vorher.size());

        buchungService.passagierAusbuchen(flugId, "Max", 30);

        List<Passagier> nachher = buchungService.allePassagiere(flugId);
        assertEquals(0, nachher.size());
    }

    @Test
    void passagierAusbuchen_wennNichtGebucht_wirftException() {
        assertThrows(RuntimeException.class, () -> {
            buchungService.passagierAusbuchen(flugId, "Unbekannt", 99);
        });
    }

    @Test
    void allePassagiere_gibtPassagierListeZurueck() {
        buchungService.passagierEinbuchen(flugId, "Max", 30);
        buchungService.passagierEinbuchen(flugId, "Anna", 25);

        List<Passagier> passagiere = buchungService.allePassagiere(flugId);

        assertEquals(2, passagiere.size());
    }

    @Test
    void allePassagiere_wennFlugNichtVorhanden_wirftException() {
        assertThrows(RuntimeException.class, () -> {
            buchungService.allePassagiere(-999L);
        });
    }
}
