package ch.zhaw.flug.service;

import ch.zhaw.flug.model.Flug;
import ch.zhaw.flug.model.Passagier;
import ch.zhaw.flug.repository.FlugRepository;
import ch.zhaw.flug.repository.PassagierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service fuer Buchungsoperationen.
 * Enthaelte die Geschaeftslogik: Flug erstellen, Passagier ein- und ausgebuchen.
 */
@Service
public class BuchungService {

    private final FlugRepository flugRepository;
    private final PassagierRepository passagierRepository;

    public BuchungService(FlugRepository flugRepository, PassagierRepository passagierRepository) {
        this.flugRepository = flugRepository;
        this.passagierRepository = passagierRepository;
    }

    /**
     * Erstellt einen neuen Flug.
     */
    public Flug flugErstellen(String ziel, LocalDateTime abflug, int maxKapazitaet) {
        Flug flug = new Flug(ziel, abflug, maxKapazitaet);
        return flugRepository.save(flug);
    }

    /**
     * Gibt alle Fluege zurueck.
     */
    public List<Flug> alleFluege() {
        return flugRepository.findAll();
    }

    /**
     * Sucht einen Flug nach ID.
     */
    public Flug flugSuchen(Long id) {
        return flugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flug mit ID " + id + " nicht gefunden"));
    }

    /**
     * Bucht einen Passagier in einem Flug ein.
     * Prueft, ob der Flug existiert und ob noch Platz ist.
     */
    @Transactional
    public Passagier passagierEinbuchen(Long flugId, String name, int alter) {
        Flug flug = flugSuchen(flugId);

        // Kapazitaet pruefen
        if (flug.getAnzahlPassagiere() >= flug.getMaxKapazitaet()) {
            throw new RuntimeException("Keine freien Plaetze mehr im Flug nach " + flug.getZiel());
        }

        Passagier passagier = new Passagier(name, alter);
        flug.addPassagier(passagier);
        flugRepository.save(flug);

        return passagier;
    }

    /**
     * Bucht einen Passagier aus einem Flug aus.
     * Prueft, ob der Passagier tatsaechlich gebucht ist.
     */
    @Transactional
    public void passagierAusbuchen(Long flugId, String name, int alter) {
        int geloescht = passagierRepository.deleteByNameAndAlterAndFlugId(name, alter, flugId);
        if (geloescht == 0) {
            throw new RuntimeException("Passagier " + name + " ist in diesem Flug nicht gebucht");
        }
    }

    /**
     * Gibt alle Passagiere eines Flugs zurueck.
     */
    public List<Passagier> allePassagiere(Long flugId) {
        flugSuchen(flugId); // Prueft, ob Flug existiert
        return passagierRepository.findByFlugId(flugId);
    }
}
