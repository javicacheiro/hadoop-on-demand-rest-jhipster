package es.cesga.hadoop.repository;

import es.cesga.hadoop.domain.Cluster;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Cluster entity.
 */
public interface ClusterRepository extends JpaRepository<Cluster,Integer> {

	@Query("select cluster from Cluster cluster where cluster.username = ?1")
	List<Cluster> findAllForCurrentUser(String username);
	
	@Query("select cluster from Cluster cluster where cluster.cluster_in_system_id = ?1")
	Cluster findByClusterInSystemID(int cluster_in_system_id);
	
	@Override
	@Query("select cluster from Cluster cluster where cluster.username = ?#{principal.username}")
	List<Cluster> findAll();
}
