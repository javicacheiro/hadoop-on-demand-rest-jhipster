package es.cesga.hadoop.repository;

import es.cesga.hadoop.domain.Key;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Key entity.
 */
public interface KeyRepository extends JpaRepository<Key,Long> {

    @Query("select key from Key key where key.user.login = ?#{principal.username}")
    List<Key> findAllForCurrentUser();

}
