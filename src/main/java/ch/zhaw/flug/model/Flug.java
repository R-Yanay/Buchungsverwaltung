package ch.zhaw.flug.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity fuer einen Flug.
 * Speichert Ziel, Abflugzeit und maximale Kapazitaet.
 * Hat eine One-to-Many-Relation zu Passagier.
 */
@Entity
@Table(name = "fluege")
public class Flug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ziel;

    @Column(nullable = false)
    private LocalDateTime abflug;

    @Column(nullable = false)
    private int maxKapazitaet;

    @OneToMany(mappedBy = "flug", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Passagier> passagiere = new ArrayList<>();

    public Flug() {
    }

    public Flug(String ziel, LocalDateTime abflug, int maxKapazitaet) {
        this.ziel = ziel;
        this.abflug = abflug;
        this.maxKapazitaet = maxKapazitaet;
    }

    public Long getId() {
        return id;
    }

    public String getZiel() {
        return ziel;
    }

    public void setZiel(String ziel) {
        this.ziel = ziel;
    }

    public LocalDateTime getAbflug() {
        return abflug;
    }

    public void setAbflug(LocalDateTime abflug) {
        this.abflug = abflug;
    }

    public int getMaxKapazitaet() {
        return maxKapazitaet;
    }

    public void setMaxKapazitaet(int maxKapazitaet) {
        this.maxKapazitaet = maxKapazitaet;
    }

    public List<Passagier> getPassagiere() {
        return passagiere;
    }

    public void setPassagiere(List<Passagier> passagiere) {
        this.passagiere = passagiere;
    }

    /**
     * Fuegt einen Passagier zur Liste hinzu und setzt die bidirektionale Relation.
     */
    public void addPassagier(Passagier passagier) {
        passagiere.add(passagier);
        passagier.setFlug(this);
    }

    /**
     * Entfernt einen Passagier aus der Liste.
     */
    public void removePassagier(Passagier passagier) {
        passagiere.remove(passagier);
        passagier.setFlug(null);
    }

    /**
     * Gibt die aktuelle Anzahl der Passagiere zurueck.
     */
    public int getAnzahlPassagiere() {
        return passagiere.size();
    }
}
