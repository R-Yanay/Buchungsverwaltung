package ch.zhaw.flug.repository;

import ch.zhaw.flug.model.Passagier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository fuer Passagier-Entitaeten.
 * Spring Data JPA generiert die Standardmethoden automatisch.
 */
public interface PassagierRepository extends JpaRepository<Passagier, Long> {

    /**
     * Findet alle Passagiere eines Flugs.
     */
    List<Passagier> findByFlugId(Long flugId);

    /**
     * Loescht einen Passagier nach Name und Alter in einem Flug.
     */
    @Modifying
    @Query("DELETE FROM Passagier p WHERE p.name = :name AND p.alter = :alter AND p.flug.id = :flugId")
    int deleteByNameAndAlterAndFlugId(@Param("name") String name,
                                      @Param("alter") int alter,
                                      @Param("flugId") Long flugId);
}
