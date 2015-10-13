package es.cesga.hadoop.repository;

import es.cesga.hadoop.domain.Ip;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ip entity.
 */
public interface IpRepository extends JpaRepository<Ip,Long> {
	
	@Query("select ip from Ip ip where ip.username = ?#{principal.username}")
	List<Ip> findAllForCurrentUser();
	
	@Query("select ip from Ip ip where ip.username = ?1")
	List<Ip> findAllForUser(String username);
	
	@Query("select ip from Ip ip where ip.username = ?1 and ip.enabled = true")
	List<Ip> findAllEnabledIPsForUser(String username);
	
	@Override
	@Query("select ip from Ip ip where ip.username = ?#{principal.username}")
	List<Ip> findAll();
	
}
