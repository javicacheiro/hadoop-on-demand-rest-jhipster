package es.cesga.hadoop.repository;

import es.cesga.hadoop.domain.SshKey;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SshKey entity.
 */
public interface SshKeyRepository extends JpaRepository<SshKey,Long> {

}
