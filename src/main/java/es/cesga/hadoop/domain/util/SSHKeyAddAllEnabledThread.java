package es.cesga.hadoop.domain.util;

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
public class SSHKeyAddAllEnabledThread extends OperatorThread {
	
	private SshKeyRepository sshKeyRepository;
	
	// ** CONSTRUCTOR ** //
	// ***************** //
	public SSHKeyAddAllEnabledThread(String username, Integer cluster_id, NodeRepository nodeRepository, SshKeyRepository sshKeyRepository) {
		super(username, cluster_id, nodeRepository);
		this. sshKeyRepository = sshKeyRepository;
	}

	// ** INTERNAL METHODS ** //
	// ********************** //
	@Override
	//void operations(String ip, String nodeId) {
	void operations(String ip) {
		//Node node = nodeRepository.findOne(Integer.valueOf(nodeId));		
		List<SshKey> listEnabledKeys = sshKeyRepository.findAllEnabledKeysForUSer(username);
		for(SshKey key : listEnabledKeys){
			String cmdHadoop = "ssh hadoop@" + ip + " \""
					+ Constants.SSHKEY_ADD_PATH + " '" + key.getType() + " " + key.getPubkey() + " " 
					+ key.getUsername() + "@cloud.cesga.es" + "'\"";
			String cmdRoot = "ssh root@" + ip + " \""
					+ Constants.SSHKEY_ADD_PATH + " '" + key.getType() + " " + key.getPubkey() + " " 
					+ key.getUsername() + "@cloud.cesga.es" + "'\"";
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
