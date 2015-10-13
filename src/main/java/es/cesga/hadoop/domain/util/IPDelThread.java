package es.cesga.hadoop.domain.util;

import java.util.LinkedList;
import java.util.List;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.NodeRepository;

/**
 * This thread takes care of synchronizing the database keys with the active nodes
 * of the given user deleting the given sshkeys
 * @author albertoep
 *
 */
public class IPDelThread extends Thread{

	// ** ATTRIBUTES ** //
	// **************** //
	private NodeRepository nodeRepository;
	private ClusterRepository clusterRepository;
	private Ip ipToDel;
	private String username;
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public IPDelThread(String username, ClusterRepository clusterRepository, NodeRepository nodeRepository, Ip ipToDel) {
		this.nodeRepository = nodeRepository;
		this.clusterRepository = clusterRepository;
		this.ipToDel = ipToDel;
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
			String cmd = "ssh root@" + ip + " \""
					+ Constants.IP_DEL_PATH + " '" + ipToDel.getAddress() + "/" + ipToDel.getMask() + "'\"";
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
