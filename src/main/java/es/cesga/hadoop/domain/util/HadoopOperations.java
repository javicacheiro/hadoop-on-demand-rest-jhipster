package es.cesga.hadoop.domain.util;


/**
 * HadoopStartRequest 
 * @author albertoep
 */
public class HadoopOperations {
	// ** CONSTANTS ** //
	// *************** //
	public final static Integer DEFAULT_SIZE = 10;
	public final static Integer DEFAULT_DFS_REPLICAS = 3;
	public final static Integer DEFAULT_DFS_BLOCK_SIZE = 16; // MB
	public final static Integer DEFAULT_REDUCE_TASKS_NUMBER = 1;
	
	// ** ATTRIBUTES ** //
	// **************** //
	//hadoop-start [-s SIZE] [-r dfs.replication] [-b  <dfs.block.size>] [-t <mapred.reduce.tasks>]
	private Integer size;
	private Integer dfsReplicas;
	private Integer dfsBlockSize;
	private Integer reduceTasksNumber;
	private String user;
	
	// ** CONSTRUCTORS ** //
	// ****************** //
	public HadoopOperations(){
		size = DEFAULT_SIZE;
		dfsReplicas = DEFAULT_DFS_REPLICAS;
		dfsBlockSize = DEFAULT_DFS_BLOCK_SIZE;
		reduceTasksNumber = DEFAULT_REDUCE_TASKS_NUMBER;
	}
	
	public HadoopOperations(Integer size, Integer dfsReplicas,
			Integer dfsBlockSize, Integer reduceTasksNumber, String user){
		this.size = size;
		this.dfsReplicas = dfsReplicas;
		this.dfsBlockSize = dfsBlockSize;
		this.reduceTasksNumber = reduceTasksNumber;
		this.user = user;
	}

	// ** GETTERS n SETTERS ** //
	// *********************** //
	
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getDfsReplicas() {
		return dfsReplicas;
	}

	public void setDfsReplicas(Integer dfsReplicas) {
		this.dfsReplicas = dfsReplicas;
	}

	public Integer getDfsBlockSize() {
		return dfsBlockSize;
	}

	public void setDfsBlockSize(Integer dfsBlockSize) {
		this.dfsBlockSize = dfsBlockSize;
	}

	public Integer getReduceTasksNumber() {
		return reduceTasksNumber;
	}

	public void setReduceTasksNumber(Integer reduceTasksNumber) {
		this.reduceTasksNumber = reduceTasksNumber;
	}
	
	public String getUser(){
		return user;
	}
	
	public void setUser(String user){
		this.user = user;
	}
	
	// ** METHODS ** //
	// ************* //
	public String[] generateStartCmd(String clusterId){
		String x = clusterId.trim();
		String[] cmd ={
				"/bin/bash",
				Constants.HADOOP_START_PATH,
				"-R",
				"-c" + x,
				"-s " + size,
				"-r " + dfsReplicas, 
				"-b " + dfsBlockSize, 
				"-t " + reduceTasksNumber};
		return cmd;
	}
	public String[] generateStopCmd(String clusterId){
		String x = clusterId.trim();
		String[] cmd ={
				"/bin/bash",
				Constants.HADOOP_STOP_PATH,
				"-R",
				"-c" + x
			};
		return cmd;
	}
	
	// ** toString ** //
	// ************** //
	@Override
	public String toString() {
		return "{"+
				"size : '"+size+"',"+
				"dfsReplicas : '"+dfsReplicas+"',"+
				"dfsBlockSize : '"+dfsBlockSize+"',"+
				"reduceTasksNumber : '"+reduceTasksNumber+"'"+
				"user : '"+user+"'"+
				"}";
	}
}
