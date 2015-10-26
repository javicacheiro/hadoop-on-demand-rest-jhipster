package es.cesga.hadoop.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A SshKey.
 */
@Entity
@Table(name = "SSHKEY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SshKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    private String type;

    @NotNull
    @Column(name = "pubkey", length = 2048)
    @Size(max = 2048)
    private String pubkey;

    @Column(name = "enabled")
    private Boolean enabled;

    @JsonIgnore
    @Column(name = "username")
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

        SshKey sshKey = (SshKey) o;

        if ( ! Objects.equals(id, sshKey.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SshKey{" +
                "id=" + id +
                ", type='" + type + "'" +
                ", pubkey='" + pubkey + "'" +
                ", enabled='" + enabled + "'" +
                ", username='" + username + "'" +
                '}';
    }
}
