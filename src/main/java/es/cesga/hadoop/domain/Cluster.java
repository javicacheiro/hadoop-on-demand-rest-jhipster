package es.cesga.hadoop.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.cesga.hadoop.domain.util.CustomDateTimeDeserializer;
import es.cesga.hadoop.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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

    @Column(name = "clustername")
    private String clustername;

    @Column(name = "version")
    private String version;

    @Column(name = "size")
    private Integer size;

    @Column(name = "replication")
    private Integer replication;

    @Column(name = "blocksize")
    private Integer blocksize;

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

    @Column(name = "exit_status")
    private Integer exitStatus;

    @Column(name = "status")
    private String status;

    @Column(name = "username")
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClustername() {
        return clustername;
    }

    public void setClustername(String clustername) {
        this.clustername = clustername;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public Integer getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(Integer exitStatus) {
        this.exitStatus = exitStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cluster cluster = (Cluster) o;

        if ( ! Objects.equals(id, cluster.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "id=" + id +
                ", clustername='" + clustername + "'" +
                ", version='" + version + "'" +
                ", size='" + size + "'" +
                ", replication='" + replication + "'" +
                ", blocksize='" + blocksize + "'" +
                ", submitTime='" + submitTime + "'" +
                ", stopTime='" + stopTime + "'" +
                ", exitStatus='" + exitStatus + "'" +
                ", status='" + status + "'" +
                ", username='" + username + "'" +
                '}';
    }
}
