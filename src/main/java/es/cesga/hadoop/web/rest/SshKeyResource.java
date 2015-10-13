package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;

import es.cesga.hadoop.domain.SshKey;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.domain.util.IPAddThread;
import es.cesga.hadoop.domain.util.IPDelThread;
import es.cesga.hadoop.domain.util.SSHKeyAddAllEnabledThread;
import es.cesga.hadoop.domain.util.SSHKeyAddThread;
import es.cesga.hadoop.domain.util.SSHKeyDelThread;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.IpRepository;
import es.cesga.hadoop.repository.NodeRepository;
import es.cesga.hadoop.repository.SshKeyRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SshKey.
 */
@RestController
@RequestMapping("/api")
public class SshKeyResource {

    private final Logger log = LoggerFactory.getLogger(SshKeyResource.class);
    
    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private NodeRepository nodeRepository;
    
    @Inject
    private SshKeyRepository sshKeyRepository;
    
    @Inject
    private AuthUtilsBean auth;
    
    /**
     * GET  /sshKeys 
     * 
     * 	-> "getKeys" method
     * 	-> get all the sshKeys
     */
    @RequestMapping(value = "/sshKeys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SshKey> getAll() {
        log.debug("REST request to get all SshKeys");
        return sshKeyRepository.findAllForUser(auth.getUsername());
    }

    
    /**
     * GET  /sshKeys/{id} 
     * 
     * 	-> "getKey" method
     * 	-> get the "id" sshKey.
     */
    @RequestMapping(value = "/sshKeys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SshKey> get(@PathVariable Long id) {
        log.debug("REST request to get SshKey : {}", id);
        return Optional.ofNullable(sshKeyRepository.findOne(id))
            .map(sshKey -> new ResponseEntity<>(
                sshKey,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    
    /**
     * POST  /sshKeys 
     * 
     * -> "addKey" method
     * -> Create a new sshKey.
     */
    @RequestMapping(value = "/sshKeys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody SshKey sshKey) throws URISyntaxException {
        log.debug("REST request: {} requests to save SshKey : {}", auth.getUsername(), sshKey);
        
        if (sshKey.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new sshKey cannot already have an ID").build();
        }
        
        //Save the new key in the database
        sshKey.setUsername(auth.getUsername());
        sshKey = sshKeyRepository.save(sshKey);
        
		// Synchronize keys
		new SSHKeyAddThread(auth.getUsername(), clusterRepository, nodeRepository, sshKey).start();
		
        return ResponseEntity.created(new URI("/api/sshKeys/" + sshKey.getId())).build();
    }

    /**
     * PUT  /sshKeys/{id}
     * 
     * 	-> "editKey" method
     * 	-> Updates an existing sshKey.
     */
    @RequestMapping(value = "/sshKeys/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody SshKey sshKey) throws URISyntaxException {
        log.debug("REST request: {} requests to update SshKey : {}", auth.getUsername(), sshKey);
        SshKey existingKey = sshKeyRepository.findOne(id);
        
        if(existingKey == null){
        	//Key doesn't exist
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if(! existingKey.getUsername().equals(auth.getUsername())){
        	//The SSHKey doesn't belong to this user
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        if(existingKey.getEnabled() == sshKey.getEnabled()){
        	//No changes to be made
        	return ResponseEntity.ok().build();
        }
        
        //Save the modified Key in the database
        existingKey.setEnabled(sshKey.getEnabled());
        sshKeyRepository.save(existingKey);
        
        //Synchronize the SSHKey in the clusters
        if(existingKey.getEnabled()){
    		new SSHKeyAddThread(auth.getUsername(), clusterRepository, nodeRepository, existingKey).start();
        }else{
        	new SSHKeyDelThread(auth.getUsername(), clusterRepository, nodeRepository, existingKey).start();
        }
        
        return ResponseEntity.ok().build();
    }

    

    /**
     * DELETE  /sshKeys/{id} 
     * 
     * 	-> "remKey" method
     * 	-> delete the "id" sshKey.
     */
    @RequestMapping(value = "/sshKeys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete SshKey : {}", id);
        SshKey existingKey = sshKeyRepository.findOne(id);
        
        if(existingKey == null){
        	//Key doesn't exist
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if(! existingKey.getUsername().equals(auth.getUsername())){
        	//Key doesn't belong to this user
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        //Delete the key from the database
        sshKeyRepository.delete(id);
        
        //Delete the Key from the instances
        new SSHKeyDelThread(auth.getUsername(), clusterRepository, nodeRepository, existingKey).start();

        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
