package es.cesga.hadoop.repository;



import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.domain.Node;

/**
 * Spring Data JPA repository for the Node entity.
 */
public interface NodeRepository extends JpaRepository<Node,Integer> {

	@Query("select node_id from Node node where node.cluster_id = ?1")
	List<Integer> findAllNodeIDsForCluster(Integer cluster_id);

	@Query("select node_id from Node node where node.cluster_id = ?1 and node.node_in_system_id = 0")
	List<Integer> findMasterNodeIDsForCluster(Integer cluster_id);

	@Query("select node_id from Node node where node.node_in_system_id = 0 and node.username = ?1")
	List<Integer> findMasterNodeIDsForUser(String username);
	
	@Query("select node from Node node where node.cluster_id = ?1")
	List<Node> findAllNodesForCluster(Integer cluster_id);
	
	@Override
	@Query("select node from Node node where node.username = ?#{principal.username}")
	List<Node> findAll();

	
}