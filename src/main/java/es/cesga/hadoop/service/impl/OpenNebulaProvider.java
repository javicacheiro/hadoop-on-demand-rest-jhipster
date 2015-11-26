package es.cesga.hadoop.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cesga.hadoop.config.AsyncConfiguration;
import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Node;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.domain.util.AsyncProcessExitHandlerThread;
import es.cesga.hadoop.domain.util.HadoopOperations;
import es.cesga.hadoop.domain.util.Utils;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.IpRepository;
import es.cesga.hadoop.repository.NodeRepository;
import es.cesga.hadoop.repository.SshKeyRepository;
import es.cesga.hadoop.service.CloudProvider;

@Service
@Transactional
public class OpenNebulaProvider implements CloudProvider {

    private final Logger log = LoggerFactory.getLogger(OpenNebulaProvider.class);
    private String ENDPOINT;
    
    
	@Inject
    private ClusterRepository clusterRepository;
    
    @Inject
    private NodeRepository nodeRepository;
    
    @Inject
    private IpRepository ipRepository;
    
    @Inject
    private SshKeyRepository sshKeyRepository;
    
//    @Inject
//    private AsyncProcessExitHandlerThread exitStatusThread;
    
    @Inject
    private AuthUtilsBean auth;
    
    public OpenNebulaProvider(String endpoint) {
    	this.ENDPOINT = endpoint;
    }
    
        
    public OpenNebulaProvider() {
	}
    
	@Override
	public Cluster create(Cluster cluster) {
		Process process;
		String[] cmd;
		
        HadoopOperations hsr = new HadoopOperations();
        hsr.setDfsBlockSize(cluster.getDfsBlocksize());
        hsr.setSize(cluster.getSize());
        hsr.setUser(cluster.getUsername());
        hsr.setDfsReplicas(cluster.getDfsReplicas());
    	
    	
    	try {
    		Utils.createUserOpenNebulaAuthFile(cluster.getUsername(), auth.getPassword());
		} catch (IOException e) {
			//Error encountered, we return null
			return null;
		}
    	
        cmd = hsr.generateStartCmd(String.valueOf(cluster.getCluster_In_System_Id()));
        ProcessBuilder pb = Utils.generateLocalProcessFromCommand(cmd, cluster.getUsername());
        
		try {
			process = pb.start();
		} catch (IOException e) {
			//Error encountered, we return null
			return null;
		}
        
		
//		exitStatusThread.setParams(process, cluster.getId(), cluster.getUsername());
//		exitStatusThread.start();
		
		
		
//		ApplicationContext context = new AnnotationConfigApplicationContext(AsyncConfiguration.class);
//		Executor taskExecutor = (Executor) context.getBean("getAsyncExecutor");
//	    DataObtainerThread thread = (DataObtainerThread) context.getBean("getDataObtainerThread"); 
//	    thread.setParams(process, cluster.getId(), cluster.getUsername());
//	    taskExecutor.execute(thread);
		
		// Async Jobs
		AsyncProcessExitHandlerThread thread = new AsyncProcessExitHandlerThread(clusterRepository, nodeRepository, ipRepository, sshKeyRepository);
		thread.setParams(process, cluster.getId(), cluster.getUsername());
		thread.start();
		//new DataObtainerThread(process, cluster.getId(), cluster.getUsername(), clusterRepository, nodeRepository).start();
		//new DataObtainerThread(process, cluster.getId(), cluster.getUsername()).run();
		
        return cluster;
	}

	@Override
	public void delete(Cluster cluster) {
		
		/*
		 * SIN COMPROBACION PARA MAQUINAS CORRIENDOS
		 */		
//		Process process;
//		try {
//			HadoopStartRequest hsr = new HadoopStartRequest();
//	    	String[] cmd = hsr.generateStopCmd(String.valueOf(cluster.getCluster_In_System_Id()));
//	    	ProcessBuilder pb = Utils.generateLocalProcessFromCommand(cmd, cluster.getUsername());
//	        process = pb.start();
//			process.waitFor();
//		} catch (IOException | InterruptedException e) {
//			throw new RuntimeException("Internal Error");
//		}
//		if(process.exitValue()!=0){
//			throw new RuntimeException("Internal Error");
//		}

		/*
		 * CON COMPROBACION
		 */
		try {
			String cluster_in_system_id = String.valueOf(cluster.getCluster_In_System_Id());
			if(vmNamedWithClusterIDExists(cluster_in_system_id, cluster.getUsername())){
		    	HadoopOperations hpOperations = new HadoopOperations();
		    	
				String[] cmd = hpOperations.generateStopCmd(cluster_in_system_id);
		    	ProcessBuilder pb = Utils.generateLocalProcessFromCommand(cmd, cluster.getUsername());
		    	
		        Process process = pb.start();
				process.waitFor();
	
				//Check that there are no running instances with the cluster id
				if(vmNamedWithClusterIDExists(cluster_in_system_id, cluster.getUsername()))
					//return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
					throw new RuntimeException("Internal Error");
				else
					//return new ResponseEntity<>(HttpStatus.OK);
					return;
			
			}else
				//return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				//throw new RuntimeException("Internal Error");
				return;
	
		} catch (InterruptedException | IOException e) {
			//return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<Cluster> findAll() {
		//Get all the nodes of the cluster
		VirtualMachinePool vmPool = findAllVirtualMachines();
		
		//Get the cluster from the nodes (master node for each cluster)
		List<Cluster> clusters = obtainClustersFromInstances(vmPool);
		
		//For all the cluster, set the size from its nodes
		for (Cluster cluster : clusters) {
			setClusterSizeWithNodes(cluster, vmPool);
		}

//		log.debug("Clusters: " + clusters);
//		
//		for (VirtualMachine vm : vmPool) {
//			log.debug("Name: " + vm.getName());
//		}
		return clusters;
	}
	
	@Override
	public Cluster show(Cluster cluster) {
		VirtualMachinePool vmPool = findAllVirtualMachines();
		setClusterSizeWithNodes(cluster, vmPool);
		return cluster;
	}

	public void setClusterSizeWithNodes(Cluster cluster,
			VirtualMachinePool vmPool) {
		
		List<VirtualMachine> clusterNodesPool = null;

		//Do it until all the cluster nodes (its size), are retrieved, or enough attempts are made
        int tries = 0;
        do{
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				break;
			}
			//Get all the running instances that are part of a cluster, get "the nodes of the cluster"
			clusterNodesPool = filterVirtualMachinesByClusterId(vmPool, cluster.getCluster_In_System_Id());
        	tries++;
		}while(clusterNodesPool.size() < cluster.getSize() && tries < 20);
		
        if(clusterNodesPool != null){
    		//Set the size of the cluster, the number of nodes retrieved
    		cluster.setSize(clusterNodesPool.size());
        }else{
        	cluster.setSize(0);
        }
		log.debug("cluster: " + cluster);
	}

	
	@Override
	public List<Node> getClusterNodes(Cluster cluster){

		//Get all the running instances that are part of a cluster, the "nodes of the cluster"
		VirtualMachinePool vmPool = findAllVirtualMachines();
		List<VirtualMachine> clusterNodesPool = 
				filterVirtualMachinesByClusterId(vmPool, cluster.getCluster_In_System_Id());
		
		List<Node> nodes = new ArrayList<>();
		for (VirtualMachine vm : clusterNodesPool) {
			//create a new node and set its basic information
			Node node = new Node();
			node.setUsername(cluster.getUsername());
			node.setNodeInSystemId(Integer.parseInt(getNodeInClusterId(vm)));
			node.setName(vm.getName());
			
			// OpenNebula uses lcmState to store the sub-state of machines that are already in state ACTIVE
			if (vm.stateStr().equals("ACTIVE")) {
				node.setStatus(vm.lcmStateStr());
			} else {
				node.setStatus(vm.stateStr());
			}
			
			//set the node resource information
			node.setCluster(cluster.getId());
			node.setHost(vm.xpath("HISTORY_RECORDS/HISTORY/HOSTNAME"));
			node.setIp(vm.xpath(""));
			// STIME -> (S)tart TIME: given in seconds (Joda DateTime expects it in milliseconds)
			node.setStartTime(new DateTime(Long.parseLong(vm.xpath("STIME"))*1000L));
			node.setEndTime(new DateTime(Long.parseLong(vm.xpath("ETIME"))*1000L));
			node.setUcpu(Integer.parseInt(vm.xpath("CPU")));
			node.setUmem(Long.parseLong(vm.xpath("MEMORY")));
			node.setUptimeSeconds((System.currentTimeMillis() - node.getStartTime().getMillis())/1000L);
			node.setIp(vm.xpath("TEMPLATE/CONTEXT/ETH0_IP"));
			log.debug("Node: " + node);
			
			//add the node to the list of cluster nodes
			nodes.add(node);
		}
		return nodes;
	}
	
	
	private VirtualMachinePool findAllVirtualMachines() {

		VirtualMachinePool vmPool = null;

		try {
			Client oneClient = new Client(auth.getOneAuth(), ENDPOINT);
			vmPool = new VirtualMachinePool(oneClient);
			OneResponse rc = vmPool.info();
			if(rc.isError()) {
				log.error("Failure obtaining the list virtual machines in OpenNebula");
			}
		} catch (ClientConfigurationException e) {
			log.error("There is a problem in the OpenNebula configuration supplied.");
			e.printStackTrace();
		}
		
		return vmPool;

	}

	private List<Cluster> obtainClustersFromInstances(VirtualMachinePool vmPool) {
		
		List<Cluster> clusters = new ArrayList<>();
		
		// System.out.println("Number of VMs: " + vmPool.getLength());
		
		//Se crea un mapa por hash de los id's
		Set<Integer> clusterIds = new HashSet<>();
		
		//Se filtran todos los id's de las instancias usando el mapa por hash
		for (VirtualMachine vm : vmPool) {
			if(isHadoopInstance(vm)){
				int vmClusterId = Integer.parseInt(vm.getName().split("-")[2]);
				clusterIds.add(vmClusterId);
			}
		}
		
		//Con los id's resultantes se crean clusters 
		for (Integer clusterid : clusterIds) {
			Cluster cluster = new Cluster();
			cluster.setCluster_In_System_Id(clusterid);
			clusters.add(cluster);
		}
		return clusters;
	}


	private List<VirtualMachine> filterVirtualMachinesByClusterId(
			VirtualMachinePool vmPool, int clusterId) {
		
		List<VirtualMachine> vms = new ArrayList<>();
		
		for (VirtualMachine vm : vmPool) {
			if(isMemberOfCluster(vm, clusterId)) {
				vms.add(vm);
			}
		}
		return vms;
	}

	
	private String getNodeInClusterId(VirtualMachine vm) {
		// The VM are named with the format: hadoop-11963-0
		String[] parts = vm.getName().split("-");
		return parts[2];
	}
	
	private boolean isHadoopInstance(VirtualMachine vm) {
		// The VM are named with the format: hadoop-1.1.2-11963-0
		String[] parts = vm.getName().split("-");
		
		if(parts.length >= 3){
			try {
				if(parts[0].equalsIgnoreCase("hadoop"))
					return true;
				else
					return false;
			} catch (NumberFormatException ex){
				//The VM is not part of a Hadoop cluster
				return false;
			}	
		}else{
			//The VM is not part of a Hadoop cluster
			//skip
			return false;
		}
	}
	
	
	private boolean isMemberOfCluster(VirtualMachine vm, int clusterId) {
		int vmClusterId;
		
		// The VM are named with the format: hadoop-11963-0
		String[] parts = vm.getName().split("-");
		
		if(isHadoopInstance(vm)){
			try {
				vmClusterId = Integer.parseInt(parts[1]);
			} catch (NumberFormatException ex){
				//The id is not valid
				return false;
			}	
		}else{
			//The VM is not part of a Hadoop cluster
			//skip
			return false;
		}
		
		//The VM IS part of a hadoop cluster, do the ids match?
		return vmClusterId == clusterId;
	}
    
	/**
	 * This method reads the answer of calling 'onevm list' and check if any
	 * virtual machine name matches the id as a hadoop cluster name will do 
	 * @return True: At least un virtual machine which name matches cluster id exists.
	 * If an exception ocurred this method will return true.
	 */
	private boolean vmNamedWithClusterIDExists(String cluster_in_system_id, String username){
		try{
			String[] cmd = Utils.generateListCmd(cluster_in_system_id);
	    	ProcessBuilder pb = Utils.generateLocalProcessFromCommand(cmd, username);
			
	        Process process = pb.start();
	        process.waitFor();
	        
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while(in.ready()){
				String line = in.readLine();
				if(line.contains(cluster_in_system_id)){
					line = line.replaceAll("\\s+"," ");
					String[] arr = line.split(" ");
					if(arr[4].split("-")[1].equals(cluster_in_system_id)){
						in.close();
						return true;
					}
				}
			}
			
			in.close();
			return false;
		}catch(Exception ex){
			return true;
		}
	}
}
