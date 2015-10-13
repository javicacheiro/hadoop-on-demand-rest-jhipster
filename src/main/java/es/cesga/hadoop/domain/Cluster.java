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
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="cluster_id")
    private Integer cluster_id;
    
    
    @Column(name = "username")
    private String username;
    
    
    @Min(1)
    @Max(9999)
    @Column(name = "cluster_in_system_id")
    private Integer cluster_in_system_id;
    
    
    //String composed by chars, numbers and the "-_" punct chars. No whitespaces allowed.
    @Pattern(regexp = "^[a-zA-Z_\\-0-9]*$",
    		message="The clustername can only contain characters, numbers and \"_-\". No whitespaces allowed.")
    @NotNull
    @Column(name = "clustername")
    private String clustername;

    
//    @OneToMany(mappedBy = "cluster_id")
//    @OrderBy
//    List<Node> nodes;
    
    
    
    /**
     * Requested properties at launch time
     */
    @Min(1)
    @Max(99)
    @NotNull
    @Column(name = "size")
    private Integer size;
    
    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "replication")
    private Integer replication;

    @NotNull
    @Column(name = "blocksize")
    private Integer blocksize;

    
    /**
     * Audit fields
     */
    @Column(name = "exit_status")
    private Integer exitStatus;
    
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

        
    public Integer getId() {
		return cluster_id;
	}

	public void setId(Integer cluster_id) {
		this.cluster_id = cluster_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCluster_In_System_Id() {
		return cluster_in_system_id;
	}

	public void setCluster_In_System_Id(int cluster_in_system_id) {
		this.cluster_in_system_id = cluster_in_system_id;
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

	public Integer getDfsReplicas() {
		return replication;
	}

	public void setDfsReplicas(Integer replication) {
		this.replication = replication;
	}

	public Integer getDfsBlocksize() {
		return blocksize;
	}

	public void setDfsBlocksize(Integer blocksize) {
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		
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
		
		if (cluster_id == null) {
			if (other.cluster_id != null)
				return false;
		} else if (!cluster_id.equals(other.cluster_id))
			return false;
		
		if (replication == null) {
			if (other.replication != null)
				return false;
		} else if (!replication.equals(other.replication))
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
		
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ ((blocksize == null) ? 0 : blocksize.hashCode());
		result = prime * result
				+ ((clustername == null) ? 0 : clustername.hashCode());
		result = prime * result
				+ ((exitStatus == null) ? 0 : exitStatus.hashCode());
		result = prime * result + ((cluster_id == null) ? 0 : cluster_id.hashCode());
		result = prime * result
				+ ((replication == null) ? 0 : replication.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result
				+ ((stopTime == null) ? 0 : stopTime.hashCode());
		result = prime * result
				+ ((submitTime == null) ? 0 : submitTime.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

    
    @Override
	public String toString() {
		return "Cluster [cluster_id=" + cluster_id + ", username=" + username + ", cluster_in_system_id="
				+ cluster_in_system_id + ", clustername=" + clustername 
				//+ ", nodes="+ nodes + ", size=" 
				+ size + ", replication=" + replication
				+ ", blocksize=" + blocksize + ", exitStatus=" + exitStatus
				+ ", submitTime=" + submitTime + ", stopTime=" + stopTime + "]";
	}
}
