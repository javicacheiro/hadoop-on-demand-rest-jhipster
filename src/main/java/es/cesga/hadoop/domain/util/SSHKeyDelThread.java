package es.cesga.hadoop.domain.util;

import java.util.LinkedList;
import java.util.List;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.SshKey;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.NodeRepository;

/**
 * This thread takes care of synchronizing the database keys with the active nodes
 * of the given user deleting the given sshkeys
 * @author albertoep
 *
 */
public class SSHKeyDelThread extends Thread{
	
	// ** ATTRIBUTES ** //
	// **************** //
	private NodeRepository nodeRepository;
	private ClusterRepository clusterRepository;
	private SshKey keyToDel;
	private String username;
	
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public SSHKeyDelThread(String username, ClusterRepository clusterRepository, NodeRepository nodeRepository, SshKey keyToDel) {
		this.nodeRepository = nodeRepository;
		this.clusterRepository = clusterRepository;
		this.keyToDel = keyToDel;
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
			
			String cmdHadoop = Constants.SSHKEY_DEL_PATH + " " 
					+ "hadoop" + " "
					+ "\"" + keyToDel.getPubkey() + "\""  + " " 
					+ ip;
			
			String cmdRoot = Constants.SSHKEY_DEL_PATH + " " 
					+ "root" + " "
					+ "\"" + keyToDel.getPubkey()	+"\"" + " " 
					+ ip;
			//String cmdHadoop = "ssh hadoop@" + ip + " \""
			//		+ Constants.SSHKEY_DEL_PATH + " '" + keyToDel + "'\"";
			//String cmdRoot = "ssh root@" + ip + " \""
			//		+ Constants.SSHKEY_DEL_PATH + " '" + keyToDel + "'\"";
			
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
