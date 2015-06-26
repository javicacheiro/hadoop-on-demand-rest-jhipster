package es.cesga.hadoop.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Node;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.service.CloudProvider;

@Service
@Transactional
public class OpenNebulaProvider implements CloudProvider {

    private final Logger log = LoggerFactory.getLogger(OpenNebulaProvider.class);
    
    @Inject
    private AuthUtilsBean auth;
    
    private String ENDPOINT;
    
    public OpenNebulaProvider(String endpoint) {
    	this.ENDPOINT = endpoint;
    }
    
    public OpenNebulaProvider() {
	}
    
	@Override
	public Cluster create(Cluster cluster) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int clusterId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Cluster> findAll() {
		
		VirtualMachinePool vmPool = findAllVirtualMachines();
		
		List<Cluster> clusters = obtainClustersIdsFrom(vmPool);
		
		for (Cluster cluster : clusters) {
			fillClusterInformation(cluster, vmPool);
		}

		log.debug("Clusters: " + clusters);
		
		for (VirtualMachine vm : vmPool) {
			log.debug("Name: " + vm.getName());
		}
		return clusters;
	}
	
	@Override
	public Cluster show(int clusterId) {
		VirtualMachinePool vmPool = findAllVirtualMachines();
		Cluster cluster = new Cluster();
		cluster.setClusterid(clusterId);
		fillClusterInformation(cluster, vmPool);
		return cluster;
	}

	public void fillClusterInformation(Cluster cluster,
			VirtualMachinePool vmPool) {
		
		List<VirtualMachine> clusterVMs = filterVirtualMachinesByClusterId(vmPool, cluster.getClusterid());
		cluster.setSize(clusterVMs.size());
		
		List<Node> nodes = new ArrayList<>();
		for (VirtualMachine vm : clusterVMs) {
			Node node = new Node();
			node.setNodeid(Integer.parseInt(vm.getId()));
			node.setName(vm.getName());
			// OpenNebula uses lcmState to store the sub-state of machines that are already in state ACTIVE
			if (vm.stateStr().equals("ACTIVE")) {
				node.setStatus(vm.lcmStateStr());
			} else {
				node.setStatus(vm.stateStr());
			}
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
			nodes.add(node);
		}
		cluster.setNodes(nodes);
		
		cluster.setUsername(auth.getUsername());
		// We will obtain the name of the cluster from the name of the first node removing the "-0" at the end
		// Eg. hadoop-1.2.1-2580-0 -> hadoop-1.2.1-2580
		String name = cluster.getNodes().get(0).getName();
		cluster.setClustername(name.substring(0, name.length() - 2));
		
		log.debug("cluster: " + cluster);
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

	private List<Cluster> obtainClustersIdsFrom(VirtualMachinePool vmPool) {
		
		List<Cluster> clusters = new ArrayList<>();
		
		// System.out.println("Number of VMs: " + vmPool.getLength());
		
		Set<Integer> clusterIds = new HashSet<>();
		
		for (VirtualMachine vm : vmPool) {
			String[] parts = vm.getName().split("-");
			int vmClusterId = Integer.parseInt(parts[2]);
			clusterIds.add(vmClusterId);
		}
		
		for (Integer clusterid : clusterIds) {
			Cluster cluster = new Cluster();
			cluster.setClusterid(clusterid);
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

	private boolean isMemberOfCluster(VirtualMachine vm, int clusterId) {
		String[] parts = vm.getName().split("-");
		// The VM are named with the format: hadoop-1.1.2-11963-0
		int vmClusterId = Integer.parseInt(parts[2]);
		return vmClusterId == clusterId;
	}
    
    

}
