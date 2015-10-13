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
	
	@Query("select k from SshKey k where k.username = ?1")
	List<SshKey> findAllForUser(String username);

	@Query("select k from SshKey k where k.username = ?1 and k.enabled = true")
	List<SshKey> findAllEnabledKeysForUSer(String username);
}
