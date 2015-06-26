package es.cesga.hadoop.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import es.cesga.hadoop.domain.util.CustomDateTimeDeserializer;
import es.cesga.hadoop.domain.util.CustomDateTimeSerializer;

@Entity
@Table(name = "NODE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
      
    @Column(name = "nodeid")
    private Integer nodeid;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "ip")
    private String ip;
    
    @Column(name = "status")
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "clusterid")
    private Cluster cluster;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "start_time")
    private DateTime startTime;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "end_time")
    private DateTime endTime;
    
    // Currently used CPU
    @Column(name = "ucpu")
    private Integer ucpu;

    // Currently used Memory
    @Column(name = "umem")
    private Long umem;

    @Column(name = "host")
    private String host;

    @Column(name = "uptime_seconds")
    private Long uptimeSeconds;
    


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public Integer getUcpu() {
		return ucpu;
	}

	public void setUcpu(Integer ucpu) {
		this.ucpu = ucpu;
	}

	public Long getUmem() {
		return umem;
	}

	public void setUmem(Long umem) {
		this.umem = umem;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Long getUptimeSeconds() {
		return uptimeSeconds;
	}

	public void setUptimeSeconds(Long uptimeSeconds) {
		this.uptimeSeconds = uptimeSeconds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((ucpu == null) ? 0 : ucpu.hashCode());
		result = prime * result + ((umem == null) ? 0 : umem.hashCode());
		result = prime * result
				+ ((uptimeSeconds == null) ? 0 : uptimeSeconds.hashCode());
		result = prime * result + ((nodeid == null) ? 0 : nodeid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (ucpu == null) {
			if (other.ucpu != null)
				return false;
		} else if (!ucpu.equals(other.ucpu))
			return false;
		if (umem == null) {
			if (other.umem != null)
				return false;
		} else if (!umem.equals(other.umem))
			return false;
		if (uptimeSeconds == null) {
			if (other.uptimeSeconds != null)
				return false;
		} else if (!uptimeSeconds.equals(other.uptimeSeconds))
			return false;
		if (nodeid == null) {
			if (other.nodeid != null)
				return false;
		} else if (!nodeid.equals(other.nodeid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", nodeid=" + nodeid + ", status=" + status
				+ ", ucpu=" + ucpu + ", umem=" + umem + ", host=" + host
				+ ", uptimeSeconds=" + uptimeSeconds + ", name=" + name
				+ ", ip=" + ip + "]";
	}

}
