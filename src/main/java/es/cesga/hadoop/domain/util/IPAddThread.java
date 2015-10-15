package es.cesga.hadoop.domain.util;

import java.util.LinkedList;
import java.util.List;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.NodeRepository;

/**
 * This thread takes care of synchronizing the database ips with the active
 * nodes of the given user adding those ips inside database to iptables
 * 
 * @author albertoep
 * 
 */
public class IPAddThread extends Thread{
	
	// ** ATTRIBUTES ** //
	// **************** //
	private NodeRepository nodeRepository;
	private ClusterRepository clusterRepository;
	private Ip ipToAdd;
	private String username;
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public IPAddThread(String username, ClusterRepository clusterRepository, NodeRepository nodeRepository, Ip ipToAdd) {
		this.nodeRepository = nodeRepository;
		this.clusterRepository = clusterRepository;
		this.ipToAdd = ipToAdd;
		this.username = username;
	}

	@Override
	public void run() {
		List<Cluster> clusterList = clusterRepository.findAllForCurrentUser(username);
		List<Integer> nodeIdsList = new LinkedList<Integer>();
		for(Cluster cluster : clusterList){
			nodeIdsList.addAll(nodeRepository.findMasterNodeIDsForCluster(cluster.getId()));
		}
		for (Integer nodeId : nodeIdsList){
			String ip = nodeRepository.findOne(nodeId).getIp();
			String cmd = Constants.IP_ADD_PATH + " \""+ipToAdd.getAddress()+"/"+ipToAdd.getMask()+"\" " + " \"" + ip + " \"";
			//String cmd = "ssh root@" + ip + " \""
			//		+ Constants.IP_ADD_PATH + " '" + ipToAdd.getAddress() + "/" + ipToAdd.getMask() + "'\"";
			try {
				Process p = Runtime.getRuntime().exec(
						new String[] { "/bin/sh", "-c", cmd });
				
				p.waitFor();
				
				//System.out.println(cmd);
				/*
				 * Hay que esperar a que acabe el proceso antes de ir a por la
				 * siguiente iteracion porque se esta escribiendo en un archivo
				 * y eso se considera seccion critica. No hacerlo podria dar
				 * lugar a inconsistencias.
				 */
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
