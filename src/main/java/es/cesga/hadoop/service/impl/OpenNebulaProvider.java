package es.cesga.hadoop.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.service.CloudProvider;

import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Cluster show(int clusterId) {
		VirtualMachinePool vmPool = findAllVirtualMachines();
		List<VirtualMachine> clusterVMs = filterVirtualMachinesByClusterId(vmPool, clusterId);
		Cluster cluster = new Cluster();
		cluster.setSize(clusterVMs.size());
		return cluster;
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

	private List<Cluster> obtainClustersFrom(VirtualMachinePool vmPool) {
		
		List<Cluster> clusters = new ArrayList<>();
		
		// System.out.println("Number of VMs: " + vmPool.getLength());
		
		for (VirtualMachine vm : vmPool) {
			Cluster cluster = new Cluster();
			System.out.println("\tID: " + vm.getId() +
					", Name: " + vm.getName() );
		}
		
		return clusters;
		
		
	}


	private List<VirtualMachine> filterVirtualMachinesByClusterId(
			VirtualMachinePool vmPool, int clusterId) {
		
		List<VirtualMachine> vms = new ArrayList<>();
		
		for (VirtualMachine vm : vmPool) {
			if(isMemberOfCluster(vm,clusterId)) {
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
