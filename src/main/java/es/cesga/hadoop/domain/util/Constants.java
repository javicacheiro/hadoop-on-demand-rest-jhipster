package es.cesga.hadoop.domain.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Constants {
	// ** PROPERTIES KEYS ** //
	// ********************* //
	public final static String SCRIPTS_PATH_KEY = "SCRIPTS_PATH";
	public final static String USER_MAX_CLUSTERS_KEY = "DEFAULT_USER_MAX_CLUSTERS";
	public final static String USER_MAX_VMS_FOR_CLUSTER_KEY = "DEFAULT_USER_MAX_VMS_FOR_CLUSTER";
	
	// ** DEFAULT PROPERTIES ** //
	// ************************ //
	public final static String DEFAULT_SCRIPTS_PATH = "./scripts/";
	public final static String DEFAULT_USER_MAX_CLUSTERS = "1";
	public final static String DEFAULT_USER_MAX_VMS_FOR_CLUSTER = "20";
	
	// ** SCRIPTS PATH ** //
	// ****************** //
	public final static String SCRIPTS_PATH;
	public final static String HADOOP_START_PATH;
	public final static String HADOOP_STOP_PATH;
	public final static String HADOOP_STATUS_PATH;
	public final static String SSHKEY_ADD_PATH;
	public final static String SSHKEY_DEL_PATH;
	//public final static String SSHKEY_MULTIDEL_PATH;
	public final static String IP_ADD_PATH;
	public final static String IP_DEL_PATH;
	
	// ** USER CONSTRAINTS ** //
	// ********************** //
	public final static int USER_MAX_CLUSTERS;
	public final static int USER_MAX_VMS_FOR_CLUSTER;
	
	static{
		Properties p = new Properties();
		
		try{
			FileInputStream in = new FileInputStream("properties.conf");
			p.load(in);
		}catch(Exception ex){
			ex.printStackTrace();
			p.setProperty(SCRIPTS_PATH_KEY,DEFAULT_SCRIPTS_PATH);
			p.setProperty(USER_MAX_CLUSTERS_KEY,DEFAULT_USER_MAX_CLUSTERS);
			p.setProperty(USER_MAX_VMS_FOR_CLUSTER_KEY,DEFAULT_USER_MAX_VMS_FOR_CLUSTER);
			
			try{
				p.store(new FileOutputStream("properties.conf"), "DEFAULT PROPERTIES");
			}catch(Exception ex2){
				ex2.printStackTrace();
			}
		}
		
		// SCRIPTS //
		SCRIPTS_PATH = p.getProperty(SCRIPTS_PATH_KEY);
		HADOOP_START_PATH=SCRIPTS_PATH+"hadoop-start.sh";
		HADOOP_STOP_PATH=SCRIPTS_PATH+"hadoop-stop.sh";
		HADOOP_STATUS_PATH=SCRIPTS_PATH+"hadoop-status.sh";
		SSHKEY_ADD_PATH=SCRIPTS_PATH+"sshkey-add.sh";
		SSHKEY_DEL_PATH=SCRIPTS_PATH+"sshkey-del.sh";
		//SSHKEY_MULTIDEL_PATH="sshkey-multidel";
		IP_ADD_PATH=SCRIPTS_PATH+"iptables-add.sh";
		IP_DEL_PATH=SCRIPTS_PATH+"iptables-del.sh";
		
		// USER CONSTRAINTS //
		USER_MAX_CLUSTERS = Integer.parseInt(p.getProperty(USER_MAX_CLUSTERS_KEY));
		USER_MAX_VMS_FOR_CLUSTER = Integer.parseInt(p.getProperty(USER_MAX_VMS_FOR_CLUSTER_KEY));
	}
}
