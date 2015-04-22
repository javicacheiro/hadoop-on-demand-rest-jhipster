package es.cesga.hadoop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.cesga.hadoop.domain.SshKey;

/**
 * Spring Data JPA repository for the SshKey entity.
 */
public interface SshKeyRepository extends JpaRepository<SshKey,Long> {

	@Query("select k from SshKey k where k.username = ?#{principal.username}")
	List<SshKey> findAllForCurrentUser();
}
