package es.cesga.hadoop.domain;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Ip.
 */
@Entity
@Table(name = "IP")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ip implements Serializable {

 	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Pattern(regexp = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.\\d{1,3}$")
    @Column(name = "address")
    private String address;

    @NotNull
    @Column(name = "mask")
    private Integer mask;
    
    @NotNull
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

    public String getAddress() {
    	if(this.mask != null){
	    	if(this.mask == 0){
	    		return "0.0.0.0";
	    	}else if(this.mask == 32){
	    		return address;
	    	}else if(this.mask > 0 && this.mask < 32){
	        	 String ipAddr=this.address;
	        	 int cidrMask = this.mask;
	        	 long bits = 0;
	        	 bits = 0xffffffff ^ (1 << 32 - cidrMask) - 1;
	        	 String maskString = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);
	        	 String networkAddr="";
	        	 String[] ipAddrParts=ipAddr.split("\\.");
	        	 String[] maskParts=maskString.split("\\.");
	        	 for(int i=0;i<4;i++){
		        	 int x=Integer.parseInt(ipAddrParts[i]);
		        	 int y=Integer.parseInt(maskParts[i]);
		        	 int z=x&y;
		        	 networkAddr+=z+".";
	        	 }
	        	//Remove last dot
	        	networkAddr = networkAddr.substring(0, networkAddr.length() - 1);
	        	return networkAddr;
	    	}else{
	    		return "0.0.0.0";
	    	}
        }else{
        	return address;
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
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

        Ip ip = (Ip) o;

        if ( ! Objects.equals(id, ip.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ip{" +
                "id=" + id +
                ", address='" + address + "'" +
                ", mask='" + mask + "'" +
                ", enabled='" + enabled + "'" +
                ", username='" + username + "'" +
                '}';
    }
}
