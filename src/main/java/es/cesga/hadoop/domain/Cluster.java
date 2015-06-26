package es.cesga.hadoop.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import es.cesga.hadoop.domain.util.CustomDateTimeDeserializer;
import es.cesga.hadoop.domain.util.CustomDateTimeSerializer;

/**
 * A Cluster.
 */
@Entity
@Table(name = "CLUSTER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Cluster implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    private String username;
    
    @Column(name = "clusterid")
    private Integer clusterid;
    
    @Column(name = "clustername")
    private String clustername;

    @OneToMany(mappedBy = "nodeid")
    @OrderBy
    List<Node> nodes;
    
    /**
     * Requested properties at launch time
     */
    @Column(name = "size")
    private Integer size;

    @Column(name = "replication")
    private Integer replication;

    @Column(name = "blocksize")
    private Integer blocksize;

    @Column(name = "exit_status")
    private Integer exitStatus;
    
    /**
     * Audit fields
     */
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "submit_time")
    private DateTime submitTime;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "stop_time")
    private DateTime stopTime;

    /**
     * Properties obtained from jobtracker.jsp
     */
    @Column(name = "jobtracker_status")
    private String jobtrackerStatus;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "safemode")
    private String safeMode;
    
    @Column(name = "tasktracker_nodes")
    private Integer tasktrackerNodes;
    
    @Column(name = "blacklisted_nodes")
    private Integer blacklistedNodes;
    
    @Column(name = "graylisted_nodes")
    private Integer graylistedNodes;

    /**
     * Properties obtained from dfshealth.jsp
     */
    @Column(name = "hdfs_status")
    private String hdfsStatus;
    
    @Column(name = "capacity")
    private Float hdfsCapacity;
    
    @Column(name = "hdfs_used_percentage")
    private Float hdfsUsedPercentage;
    
    @Column(name = "hdfs_nodes")
    private Integer hdfsNodes;
    
    @Column(name = "hdfs_dead_nodes")
    private Integer hdfsDeadNodes;
    
    @Column(name = "hdfs_decommmissioned_nodes")
    private Integer hdfsDecommissionedNodes;
    
    @Column(name = "hdfs_under_replicated_blocks")
    private Integer hdfsUnderreplicatedBlocks;
    

 
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getClusterid() {
		return clusterid;
	}

	public void setClusterid(Integer clusterid) {
		this.clusterid = clusterid;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public String getClustername() {
		return clustername;
	}

	public void setClustername(String clustername) {
		this.clustername = clustername;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getReplication() {
		return replication;
	}

	public void setReplication(Integer replication) {
		this.replication = replication;
	}

	public Integer getBlocksize() {
		return blocksize;
	}

	public void setBlocksize(Integer blocksize) {
		this.blocksize = blocksize;
	}

	public Integer getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(Integer exitStatus) {
		this.exitStatus = exitStatus;
	}

	public DateTime getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(DateTime submitTime) {
		this.submitTime = submitTime;
	}

	public DateTime getStopTime() {
		return stopTime;
	}

	public void setStopTime(DateTime stopTime) {
		this.stopTime = stopTime;
	}

	public String getJobtrackerStatus() {
		return jobtrackerStatus;
	}

	public void setJobtrackerStatus(String jobtrackerStatus) {
		this.jobtrackerStatus = jobtrackerStatus;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSafeMode() {
		return safeMode;
	}

	public void setSafeMode(String safeMode) {
		this.safeMode = safeMode;
	}

	public Integer getTasktrackerNodes() {
		return tasktrackerNodes;
	}

	public void setTasktrackerNodes(Integer tasktrackerNodes) {
		this.tasktrackerNodes = tasktrackerNodes;
	}

	public Integer getBlacklistedNodes() {
		return blacklistedNodes;
	}

	public void setBlacklistedNodes(Integer blacklistedNodes) {
		this.blacklistedNodes = blacklistedNodes;
	}

	public Integer getGraylistedNodes() {
		return graylistedNodes;
	}

	public void setGraylistedNodes(Integer graylistedNodes) {
		this.graylistedNodes = graylistedNodes;
	}

	public String getHdfsStatus() {
		return hdfsStatus;
	}

	public void setHdfsStatus(String hdfsStatus) {
		this.hdfsStatus = hdfsStatus;
	}

	public Float getHdfsCapacity() {
		return hdfsCapacity;
	}

	public void setHdfsCapacity(Float hdfsCapacity) {
		this.hdfsCapacity = hdfsCapacity;
	}

	public Float getHdfsUsedPercentage() {
		return hdfsUsedPercentage;
	}

	public void setHdfsUsedPercentage(Float hdfsUsedPercentage) {
		this.hdfsUsedPercentage = hdfsUsedPercentage;
	}

	public Integer getHdfsNodes() {
		return hdfsNodes;
	}

	public void setHdfsNodes(Integer hdfsNodes) {
		this.hdfsNodes = hdfsNodes;
	}

	public Integer getHdfsDeadNodes() {
		return hdfsDeadNodes;
	}

	public void setHdfsDeadNodes(Integer hdfsDeadNodes) {
		this.hdfsDeadNodes = hdfsDeadNodes;
	}

	public Integer getHdfsDecommissionedNodes() {
		return hdfsDecommissionedNodes;
	}

	public void setHdfsDecommissionedNodes(Integer hdfsDecommissionedNodes) {
		this.hdfsDecommissionedNodes = hdfsDecommissionedNodes;
	}

	public Integer getHdfsUnderreplicatedBlocks() {
		return hdfsUnderreplicatedBlocks;
	}

	public void setHdfsUnderreplicatedBlocks(Integer hdfsUnderreplicatedBlocks) {
		this.hdfsUnderreplicatedBlocks = hdfsUnderreplicatedBlocks;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (blacklistedNodes == null) {
			if (other.blacklistedNodes != null)
				return false;
		} else if (!blacklistedNodes.equals(other.blacklistedNodes))
			return false;
		if (blocksize == null) {
			if (other.blocksize != null)
				return false;
		} else if (!blocksize.equals(other.blocksize))
			return false;
		if (clustername == null) {
			if (other.clustername != null)
				return false;
		} else if (!clustername.equals(other.clustername))
			return false;
		if (exitStatus == null) {
			if (other.exitStatus != null)
				return false;
		} else if (!exitStatus.equals(other.exitStatus))
			return false;
		if (graylistedNodes == null) {
			if (other.graylistedNodes != null)
				return false;
		} else if (!graylistedNodes.equals(other.graylistedNodes))
			return false;
		if (hdfsCapacity == null) {
			if (other.hdfsCapacity != null)
				return false;
		} else if (!hdfsCapacity.equals(other.hdfsCapacity))
			return false;
		if (hdfsDeadNodes == null) {
			if (other.hdfsDeadNodes != null)
				return false;
		} else if (!hdfsDeadNodes.equals(other.hdfsDeadNodes))
			return false;
		if (hdfsDecommissionedNodes == null) {
			if (other.hdfsDecommissionedNodes != null)
				return false;
		} else if (!hdfsDecommissionedNodes
				.equals(other.hdfsDecommissionedNodes))
			return false;
		if (hdfsNodes == null) {
			if (other.hdfsNodes != null)
				return false;
		} else if (!hdfsNodes.equals(other.hdfsNodes))
			return false;
		if (hdfsStatus == null) {
			if (other.hdfsStatus != null)
				return false;
		} else if (!hdfsStatus.equals(other.hdfsStatus))
			return false;
		if (hdfsUnderreplicatedBlocks == null) {
			if (other.hdfsUnderreplicatedBlocks != null)
				return false;
		} else if (!hdfsUnderreplicatedBlocks
				.equals(other.hdfsUnderreplicatedBlocks))
			return false;
		if (hdfsUsedPercentage == null) {
			if (other.hdfsUsedPercentage != null)
				return false;
		} else if (!hdfsUsedPercentage.equals(other.hdfsUsedPercentage))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (jobtrackerStatus == null) {
			if (other.jobtrackerStatus != null)
				return false;
		} else if (!jobtrackerStatus.equals(other.jobtrackerStatus))
			return false;
		if (replication == null) {
			if (other.replication != null)
				return false;
		} else if (!replication.equals(other.replication))
			return false;
		if (safeMode == null) {
			if (other.safeMode != null)
				return false;
		} else if (!safeMode.equals(other.safeMode))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (stopTime == null) {
			if (other.stopTime != null)
				return false;
		} else if (!stopTime.equals(other.stopTime))
			return false;
		if (submitTime == null) {
			if (other.submitTime != null)
				return false;
		} else if (!submitTime.equals(other.submitTime))
			return false;
		if (tasktrackerNodes == null) {
			if (other.tasktrackerNodes != null)
				return false;
		} else if (!tasktrackerNodes.equals(other.tasktrackerNodes))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((blacklistedNodes == null) ? 0 : blacklistedNodes.hashCode());
		result = prime * result
				+ ((blocksize == null) ? 0 : blocksize.hashCode());
		result = prime * result
				+ ((clustername == null) ? 0 : clustername.hashCode());
		result = prime * result
				+ ((exitStatus == null) ? 0 : exitStatus.hashCode());
		result = prime * result
				+ ((graylistedNodes == null) ? 0 : graylistedNodes.hashCode());
		result = prime * result
				+ ((hdfsCapacity == null) ? 0 : hdfsCapacity.hashCode());
		result = prime * result
				+ ((hdfsDeadNodes == null) ? 0 : hdfsDeadNodes.hashCode());
		result = prime
				* result
				+ ((hdfsDecommissionedNodes == null) ? 0
						: hdfsDecommissionedNodes.hashCode());
		result = prime * result
				+ ((hdfsNodes == null) ? 0 : hdfsNodes.hashCode());
		result = prime * result
				+ ((hdfsStatus == null) ? 0 : hdfsStatus.hashCode());
		result = prime
				* result
				+ ((hdfsUnderreplicatedBlocks == null) ? 0
						: hdfsUnderreplicatedBlocks.hashCode());
		result = prime
				* result
				+ ((hdfsUsedPercentage == null) ? 0 : hdfsUsedPercentage
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((jobtrackerStatus == null) ? 0 : jobtrackerStatus.hashCode());
		result = prime * result
				+ ((replication == null) ? 0 : replication.hashCode());
		result = prime * result
				+ ((safeMode == null) ? 0 : safeMode.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result
				+ ((stopTime == null) ? 0 : stopTime.hashCode());
		result = prime * result
				+ ((submitTime == null) ? 0 : submitTime.hashCode());
		result = prime
				* result
				+ ((tasktrackerNodes == null) ? 0 : tasktrackerNodes.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

    
    @Override
	public String toString() {
		return "Cluster [id=" + id + ", username=" + username + ", clusterid="
				+ clusterid + ", clustername=" + clustername + ", nodes="
				+ nodes + ", size=" + size + ", replication=" + replication
				+ ", blocksize=" + blocksize + ", exitStatus=" + exitStatus
				+ ", submitTime=" + submitTime + ", stopTime=" + stopTime
				+ ", jobtrackerStatus=" + jobtrackerStatus + ", version="
				+ version + ", safeMode=" + safeMode + ", tasktrackerNodes="
				+ tasktrackerNodes + ", blacklistedNodes=" + blacklistedNodes
				+ ", graylistedNodes=" + graylistedNodes + ", hdfsStatus="
				+ hdfsStatus + ", hdfsCapacity=" + hdfsCapacity
				+ ", hdfsUsedPercentage=" + hdfsUsedPercentage + ", hdfsNodes="
				+ hdfsNodes + ", hdfsDeadNodes=" + hdfsDeadNodes
				+ ", hdfsDecommissionedNodes=" + hdfsDecommissionedNodes
				+ ", hdfsUnderreplicatedBlocks=" + hdfsUnderreplicatedBlocks
				+ "]";
	}
}
