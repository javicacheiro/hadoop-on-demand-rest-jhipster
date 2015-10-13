package es.cesga.hadoop.domain.util;

import java.util.List;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.repository.IpRepository;
import es.cesga.hadoop.repository.NodeRepository;

/**
 * This thread takes care of synchronizing the database ips with the active
 * nodes of the given user adding those ips inside database to iptables
 * 
 * @author albertoep
 * 
 */
public class IPAddAllEnabledThread extends OperatorThread {
	
	private IpRepository ipRepository;
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public IPAddAllEnabledThread(String username, Integer cluster_id, NodeRepository nodeRepository, IpRepository ipRepository) {
		super(username, cluster_id, nodeRepository);
		this.ipRepository = ipRepository;
	}

	// ** INTERNAL METHODS ** //
	// ********************** //
	@Override
	void operations(String ip) {
	//void operations(String ip, String nodeId) {
		//Node node = nodeRepository.findOne(Integer.valueOf(nodeId));
		List<Ip> listEnabledIps = ipRepository.findAllEnabledIPsForUser(username);
		for (Ip ipToAdd: listEnabledIps) {
			String cmd = "ssh root@" + ip + " \""
					+ Constants.IP_ADD_PATH + " '" + ipToAdd.getAddress() + "/" + ipToAdd.getMask() + "'\"";
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
