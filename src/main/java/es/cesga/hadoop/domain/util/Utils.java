package es.cesga.hadoop.domain.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;


/**
 * Contains util static methods
 * 
 * @author albertoep
 */
public class Utils {
	/**
	 * Generates the exporting sentence for ONE_AUTH corresponding to the given user.
	 * @param user
	 * @return
	 */

	public static String getOpenNebulaAuthDir(String username) {
		return "/tmp/" + username + "/.one/";
	}
	
	public static String getOpenNebulaAuthFile(String username) {
		return  getOpenNebulaAuthDir(username) + "one_auth";
	}

	public static String getEnvironmentVariable(String username) {
		return  getOpenNebulaAuthDir(username) + "one_auth";
	}
	
	public static String[] removeOpenNebulaAuthFile(String username) {
		String[] cmd = {
				"rm -Rf",
				"/tmp/"+ username};
		return cmd;
	}

	public static void createUserOpenNebulaAuthFile(String username, String password) throws IOException{
		new File(Utils.getOpenNebulaAuthDir(username)).mkdirs();
		PrintWriter writer = new PrintWriter(Utils.getOpenNebulaAuthFile(username), "UTF-8");
		writer.println(username + ":" + password);
		writer.close();
	}
	
	public static String[] generateListCmd(String cluster_in_system_id) {
		String[] cmd = {
					"onevm",
					"list"};	
		return cmd;
	}

	
	
	public static ProcessBuilder generateLocalProcessFromCommand(String[] cmd){
    	ProcessBuilder pb = new ProcessBuilder(cmd);
        Map<String, String> env = pb.environment();
    	return pb;
	}
	
	public static ProcessBuilder generateLocalProcessFromCommand(String[] cmd, String username){
    	ProcessBuilder pb = new ProcessBuilder(cmd);
        Map<String, String> env = pb.environment();
    	env.put("ONE_AUTH", Utils.getEnvironmentVariable(username));
    	env.put("ONE_XMLRPC", "http://cloud.cesga.es:2633/RPC2");
    	return pb;
	} 
	
//	
//	/**
//	 * Forces the first CPU of each array of virtual machines for each cluster to have 2 as value
//	 * and all the next to have 1
//	 * @param cl
//	 */
//	public static void giveStaticCPUValueToNodes(ClusterList cl){
//		boolean isFirst = true;
//		
//		for(HadoopCluster c : cl.getClusters()){
//			isFirst = true;
//			for(VirtualMachine vm : c.getVms()){
//				if(isFirst){
//					vm.setUcpu((short)2);
//					isFirst = false;
//				}else
//					vm.setUcpu((short)1);
//			}
//		}
//	}
		
//	public static int countUserClusters(String username){
//		String onevmList[] = doCountUserClustersOnevmList(username);
//		if(onevmList == null)
//			return 0;
//
//		ArrayList<String> clusterNames = new ArrayList<String>(0);
//		
//		for(String str : onevmList){
//			String splitted[] = str.split(" ");
//			String clusterName = splitted[4].substring(0,splitted[4].lastIndexOf("-"));
//			
//			if(clusterNames.indexOf(clusterName)==-1) // Si no esta en el ArrayList
//				clusterNames.add(clusterName);
//		}
//		
//		return clusterNames.size();
//	}
//	
//	public static String[] doCountUserClustersOnevmList(String username){
//		try{
//			Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",
//					generateExport(username)+" && onevm list | tail -n +2 |tr -s ' '"});
//			p.waitFor();
//			
//			ArrayList<String> lines = new ArrayList<String>();
//			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			
//			while(br.ready())
//				lines.add(br.readLine());
//			
//			if(lines.size()==1 && lines.get(0).length()<=1)
//				return null;
//			return lines.toArray(new String[0]);
//		}catch(Exception ex){
//			ex.printStackTrace();
//			return null;
//		}
//	}

}
