package es.cesga.hadoop.service;

import java.util.List;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Node;

public interface CloudProvider {

	public Cluster create(Cluster cluster);
	
	public List<Cluster> findAll();
	
	void delete(Cluster cluster);

	Cluster show(Cluster cluster);

	public List<Node> getClusterNodes(Cluster cluster);
}
