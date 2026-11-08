package ch.zhaw.flug.repository;

import ch.zhaw.flug.model.Flug;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository fuer Flug-Entitaeten.
 * Spring Data JPA generiert die Methoden automatisch.
 */
public interface FlugRepository extends JpaRepository<Flug, Long> {
}
