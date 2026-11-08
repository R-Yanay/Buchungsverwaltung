package ch.zhaw.flug.model;

import jakarta.persistence.*;

/**
 * Entity fuer einen Passagier.
 * Speichert Name und Alter und hat eine Many-to-One-Relation zu Flug.
 */
@Entity
@Table(name = "passagiere")
public class Passagier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int alter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flug_id", nullable = false)
    private Flug flug;

    public Passagier() {
    }

    public Passagier(String name, int alter) {
        this.name = name;
        this.alter = alter;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlter() {
        return alter;
    }

    public void setAlter(int alter) {
        this.alter = alter;
    }

    public Flug getFlug() {
        return flug;
    }

    public void setFlug(Flug flug) {
        this.flug = flug;
    }
}
