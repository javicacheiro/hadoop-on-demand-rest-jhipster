package es.cesga.hadoop.service;

import java.util.List;

import es.cesga.hadoop.domain.Cluster;

public interface CloudProvider {

	public Cluster create(Cluster cluster);
	
	public void delete(int clusterId);
	
	public List<Cluster> findAll();
	
	public Cluster show(int clusterId);
}
