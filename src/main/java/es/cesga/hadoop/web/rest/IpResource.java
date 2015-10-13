package es.cesga.hadoop.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.domain.util.IPAddAllEnabledThread;
import es.cesga.hadoop.domain.util.IPAddThread;
import es.cesga.hadoop.domain.util.IPDelThread;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.IpRepository;
import es.cesga.hadoop.repository.NodeRepository;

/**
 * REST controller for managing Ip.
 */
@RestController
@RequestMapping("/api")
public class IpResource {

    private final Logger log = LoggerFactory.getLogger(IpResource.class);

    @Inject
    private IpRepository ipRepository;
    
    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private NodeRepository nodeRepository;
    
    @Inject
    private AuthUtilsBean auth;

    
    /**
     * GET  /ips 
     * 
     * -> "getIPs" method
     * -> get all the ips.
     */
    @RequestMapping(value = "/ips",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Ip> getAll() {
    	log.debug("REST {} requests to get all Ips", auth.getUsername());
        //return ipRepository.findAllForCurrentUser();
    	return ipRepository.findAllForUser(auth.getUsername());
    }

    /**
     * GET  /ips/{id}
     * 
     * -> "getIP" method
     * -> get the "id" ip.
     */
    @RequestMapping(value = "/ips/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ip> get(@PathVariable Long id) {
        log.debug("REST {} requests to get Ip : {}", auth.getUsername(), id);
        return Optional.ofNullable(ipRepository.findOne(id))
            .map(ip -> new ResponseEntity<>(
                ip,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    
    /**
     * POST  /ips 
     * 
     * -> "addIP" method
     * -> Create a new ip.
     */
    @RequestMapping(value = "/ips",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Ip ip) throws URISyntaxException {
    	log.info("REST request {} requests to save Ip : {}", auth.getUsername(), ip);

        // Add current username information
        ip.setUsername(auth.getUsername());
        ip = ipRepository.save(ip);
        
		// Add IP to all the clusters
		new IPAddThread(auth.getUsername(), clusterRepository, nodeRepository, ip).start();
        
        return ResponseEntity.created(new URI("/api/ips/" + ip.getId())).build();
    }

    /**
     * PUT  /ips/{id}
     * 
     * -> "editIP" method
     * -> Updates an existing ip.
     */
    @RequestMapping(value = "/ips/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody Ip ip) throws URISyntaxException {
        log.info("REST request {} requests to update Ip : {}", auth.getUsername(), ip);
        Ip existingIP = ipRepository.findOne(id);
        
        if(existingIP == null){
        	//The IP doesn't exist
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if(! existingIP.getUsername().equals(auth.getUsername())){
        	//The IP doesn't belong to this user
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        if(existingIP.getEnabled() == ip.getEnabled()){
        	//No changes to be made
        	return ResponseEntity.ok().build();
        }
        
        //Save the modified IP in the database
        existingIP.setEnabled(ip.getEnabled());
        ipRepository.save(existingIP);
        
        //Synchronize the IP
        if(existingIP.getEnabled()){
    		new IPAddThread(auth.getUsername(), clusterRepository, nodeRepository, existingIP).start();
        }else{
        	new IPDelThread(auth.getUsername(), clusterRepository, nodeRepository, existingIP).start();
        }

        return ResponseEntity.ok().build();
    }

    
    /**
     * DELETE  /ips/{id} 
     * 
     * -> "remIP" method
     * -> delete the "id" ip.
     */
    @RequestMapping(value = "/ips/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Ip : {}", id);
        Ip existingIP = ipRepository.findOne(id);
        
        if(existingIP == null){
        	//The IP doesn't exist
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if(! existingIP.getUsername().equals(auth.getUsername())){
        	//The IP doesn't belong to this user
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        //Delete the Ip from the database
        ipRepository.delete(id);
        
		//Remove the ip from the clusters
		new IPDelThread(auth.getUsername(), clusterRepository, nodeRepository, existingIP).start();
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
