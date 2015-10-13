package es.cesga.hadoop.domain.util;

import java.util.LinkedList;
import java.util.List;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.domain.Node;
import es.cesga.hadoop.domain.SshKey;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.NodeRepository;
import es.cesga.hadoop.repository.SshKeyRepository;

/**
 * This thread takes care of synchronizing the database keys with the active
 * nodes of the given user adding those keys inside database to authorized_keys
 * 
 * @author albertoep
 * 
 */
public class SSHKeyAddThread extends Thread {
	
	// ** ATTRIBUTES ** //
	// **************** //
	private NodeRepository nodeRepository;
	private ClusterRepository clusterRepository;
	private SshKey keyToAdd;
	private String username;
	
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public SSHKeyAddThread(String username, ClusterRepository clusterRepository, NodeRepository nodeRepository, SshKey keyToAdd) {
		this.nodeRepository = nodeRepository;
		this.clusterRepository = clusterRepository;
		this.keyToAdd = keyToAdd;
		this.username = username;
	}

	// ** INTERNAL METHODS ** //
	// ********************** //
	@Override
	public void run() {
	//void operations(String ip, String nodeId) {
		List<Cluster> clusterList = clusterRepository.findAllForCurrentUser(username);
		List<Integer> nodeIdsList = new LinkedList<Integer>();
		for(Cluster cluster : clusterList){
			nodeIdsList.addAll(nodeRepository.findMasterNodeIDsForCluster(cluster.getId()));
		}
		for (Integer nodeId : nodeIdsList){
			String ip = nodeRepository.findOne(nodeId).getIp();
			String cmdHadoop = "ssh hadoop@" + ip + " \""
					+ Constants.SSHKEY_ADD_PATH + " '" + keyToAdd.getType() + " " + keyToAdd.getPubkey() + " " 
					+ keyToAdd.getUsername() + "@cloud.cesga.es" + "'\"";
			String cmdRoot = "ssh root@" + ip + " \""
					+ Constants.SSHKEY_ADD_PATH + " '" + keyToAdd.getType() + " " + keyToAdd.getPubkey() + " " 
					+ keyToAdd.getUsername() + "@cloud.cesga.es" + "'\"";
			
			try {
				Process pHadoop = Runtime.getRuntime().exec(
						new String[] { "/bin/sh", "-c", cmdHadoop });
				Process pRoot = Runtime.getRuntime().exec(
						new String[] { "/bin/sh", "-c", cmdRoot });
				pHadoop.waitFor();
				pRoot.waitFor();
						
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
