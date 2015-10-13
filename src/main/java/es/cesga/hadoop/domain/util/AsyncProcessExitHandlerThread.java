package es.cesga.hadoop.domain.util;

import java.io.IOException;

import javax.inject.Inject;


import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.IpRepository;
import es.cesga.hadoop.repository.NodeRepository;
import es.cesga.hadoop.repository.SshKeyRepository;

public class AsyncProcessExitHandlerThread extends Thread {
	// ** ATTRIBUTES ** //
	// **************** //
	private Process process;
	private Integer cluster_id;
	private String username;
	
	
	//@Inject
    private ClusterRepository clusterRepository;
    
    //@Inject
    private NodeRepository nodeRepository;
    
    //@Inject
    private IpRepository ipRepository;
    
    //@Inject
    private SshKeyRepository sshKeyRepository;
    
    
    public AsyncProcessExitHandlerThread(ClusterRepository clusterRepository, NodeRepository nodeRepository, IpRepository ipRepository, SshKeyRepository sshKeyRepository){
    	this.clusterRepository = clusterRepository;
    	this.nodeRepository = nodeRepository;
    	this.ipRepository = ipRepository;
    	this.sshKeyRepository = sshKeyRepository;
	}
    
	public void setParams(Process process, Integer cluster_id, String username){
		this.process = process;
		this.cluster_id = cluster_id;
		this.username = username;
	}
	
    
	// ** BEHAVIOR ** //
	// ************** //
	@Override
	public void run (){
		try{
			int exitValue = process.waitFor();
			
			Cluster cluster = clusterRepository.findOne(cluster_id);
			cluster.setExitStatus(exitValue);
			clusterRepository.save(cluster);
			
			switch(exitValue){
				case 0:
					onSuccess(exitValue);
					break;
				default:
					System.out.println("DataObtainerThread process exit value was different than 0");
					break;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			this.removeFile();
		}
	}
	
	// ** INTERNAL METHODS ** //
	// ********************** //
	
	// ** onSuccess means exit value was 0 ** //
	private void onSuccess(int exitValue){

		// Synchronize keys
		new SSHKeyAddAllEnabledThread(username, cluster_id, nodeRepository, sshKeyRepository).start();
		
		// Synchronize ips
		new IPAddAllEnabledThread(username, cluster_id, nodeRepository, ipRepository).start();
		
		//Remove the one_auth user file
		this.removeFile();
	}
	
	private void removeFile(){
    	try {
            String[] cmd = Utils.removeOpenNebulaAuthFile(username);
            ProcessBuilder pb = Utils.generateLocalProcessFromCommand(cmd);
    		pb.start();
		} catch (IOException e) {

		}
	}	
}
