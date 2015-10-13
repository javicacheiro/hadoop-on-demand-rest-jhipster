package es.cesga.hadoop.domain.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Node;
import es.cesga.hadoop.repository.NodeRepository;

public abstract class OperatorThread extends Thread{
	// ** ATTRIBUTES ** //
	// **************** //
	String username;
	Integer cluster_id;
	
	@Inject
	protected NodeRepository nodeRepository;
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public OperatorThread(String username, Integer cluster_id, NodeRepository nodeRepository){
		this.username = username;
		this.cluster_id = cluster_id;
		this.nodeRepository = nodeRepository;
	}
	
	// ** BEHAVIOR ** //
	// ************** //
	@Override
	public void run(){
		try{
			
			/*
			 * Get the master node (nodeid=0 en hadoop-{clusterid}-{nodeid}) that belongs to the user
			 * from this cluster. Ideally 1 cluster -> 1 master node.
			 */
			List<Integer> userNodesIDs = nodeRepository.findMasterNodeIDsForCluster(cluster_id);
			
			/*
			 * Para cada nodo de Hadoop que este en ejecución, aplicar la operacion.
			 */
			ArrayList<Thread> threads = new ArrayList<Thread>(0);
			
			for(Integer nodeID : userNodesIDs){
				Node node = nodeRepository.findOne(nodeID);
				final String ip = node.getIp();
				Thread t = new Thread(){
					@Override
					public void run(){
						//operations(ip,String.valueOf(nodeID));
						operations(ip);
					}
				};
				threads.add(t);
				t.start();
			}
		
			// Se puede comentar la seccion de codigo que sigue pero entonces
			// pueden producirse inconsistencias de informacion en el lado del
			// portal tal y como esta realizado a fecha de
			// 			2014 05 26
			for(Thread t : threads){
				try{
					while(t.isAlive()){
						sleep(20);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	// ** INTERNAL METHODS ** //
	// ********************** //
//	private String obtainNodeIp(String nodeId){
//		try{
//			String cmd = Utils.generateExport(username)
//					+" && oneip "+ nodeId;
//			Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",cmd});
//			p.waitFor();
//
//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));			
//			if(in.ready())
//				return in.readLine();
//			
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return null;
//	}
	
	/**
	 * Must be overridden with the actions which desire to perform for each node.
	 * @param ip IP address of the current node
	 */
	//abstract void operations(String ip, String nodeId);
	abstract void operations(String ip);

}
