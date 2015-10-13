package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Node;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.NodeRepository;
import es.cesga.hadoop.service.CloudProvider;

import org.joda.time.DateTime;
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
import java.util.Random;

/**
 * REST controller for managing Cluster.
 */
@RestController
@RequestMapping("/api")
public class ClusterResource {
	
	private static final int MAX_CLUSTER_ID_NUMBER = 9999;
	
    private final Logger log = LoggerFactory.getLogger(ClusterResource.class);
  
    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private NodeRepository nodeRepository;
    
    @Inject
    private CloudProvider cloudProvider;

    @Inject
    private AuthUtilsBean auth;
    
    /**
     * GET  /clusters
     * 
     * 	-> "list" method
     * 	-> get all the clusters from the database
     * 
     * @Return a list of ->
	 * 		clusterid
	 * 		clustername
	 * 		size
	 * 		dfsReplicas
	 * 		dfsBlocksize
	 * 		exitStatus
	 * 		stop_time
	 * 		submit_time
     * 
     */
    @RequestMapping(value = "/clusters",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Cluster> getAll() {
        log.debug("REST request to get all Clusters");
        
        //Get info from cloud 
        //List<Cluster> clusterList = cloudProvider.findAll();
        
        //Get info from database
        List<Cluster> clusterList = clusterRepository.findAll();
        
        return clusterList;    
    }
    
    
    /**
     * GET  /clusters/{id}
     * 	
     * 	-> "get" method
     * 	-> retrieves information about a cluster from the database
     * 
     * 
     * @Return a list of ->
	 * 		clusterid
	 * 		clustername
	 * 		size
	 * 		dfsReplicas
	 * 		dfsBlocksize
	 * 		exitStatus
	 * 
	 * 		stop_time
	 * 		submit_time
     * 
     */
    @RequestMapping(value = "/clusters/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cluster> get(@PathVariable int id) {
        log.debug("REST request to get Cluster : {}", id);
               
        //Get info from databse
        Cluster result = clusterRepository.findOne(id);
        
        //Get info from cloud
        //Cluster result = cloudProvider.show(id);
        
        return Optional.ofNullable(result)
            .map(cluster -> new ResponseEntity<>(
                cluster,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
  
    /**
     * POST  /clusters 
     * 
     * 	-> "create" method
     * 	-> Create a new cluster.
     * 
     * @Params ->
     * 		size
     * 		dfsBlocksize
     * 		dfsReplicas
     * 		clustername
     * 
     */
    @RequestMapping(value = "/clusters",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
  public ResponseEntity<Void> create(@Valid @RequestBody Cluster cluster) throws URISyntaxException {
    	int cluster_in_system_id;
    	
    	log.debug("REST request to create Cluster : {}", cluster);
        
        // Add current username information
        cluster.setUsername(auth.getUsername());
        
        //Add the time of submission
        cluster.setSubmitTime(new DateTime());
        
        //Save the cluster object to retrieve the assigned id.
        cluster = clusterRepository.save(cluster);
        
        //Get a valid id to launch the cluster using the script
        cluster_in_system_id = getCluster_In_System_Id(cluster.getId());
    	cluster.setCluster_In_System_Id(cluster_in_system_id);
    	
    	//Start the cluster
    	cluster = cloudProvider.create(cluster);
        if(cluster == null){
        	//Something happened
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    	
        //We get the nodes of the created cluster and save them
        List<Node> nodes = cloudProvider.getClusterNodes(cluster);
        nodeRepository.save(nodes);
       
		//Save the cluster and return with the Created Status + Location header
        clusterRepository.save(cluster);
        return ResponseEntity.created(new URI("/api/clusters/" + cluster.getId())).build();
    }
    
        
    
    
    
    
    /**
     * PUT  /clusters/{id}
     * 
     * -> "edit" method
     * -> Updates an existing cluster
     * 
     * @Params ->
     * 		clustername 
     * 
     */
    @RequestMapping(value = "/clusters/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@PathVariable int id, @Valid @RequestBody Cluster cluster) throws URISyntaxException {
        log.debug("REST request to update Cluster : {}", id);

    	Cluster existingCluster  = clusterRepository.findOne(id);
        if(existingCluster != null){
        	//Cluster exists
            existingCluster.setClustername(cluster.getClustername());
            clusterRepository.save(existingCluster);
        }else{
        	//Cluster doesn't exist
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }

    
    /**
     * DELETE  /clusters/{id}
     * 	
     * 	-> "delete" method
     * 	-> removes the specified cluster
     * 
     * @Params ->
     * 		clusterid (in URL)
     */
    @RequestMapping(value = "/clusters/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> removeCluster(@PathVariable int id) throws URISyntaxException {
        log.debug("REST request to delete Cluster : {}", id);
        Cluster existingCluster = clusterRepository.findOne(id);
        
        if(existingCluster == null){
        	//Cluster doesn't exist
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if(! existingCluster.getUsername().equals(auth.getUsername())){
        	//Cluster doesn't belong to this user
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        //Save the stop time in the database
    	existingCluster.setStopTime(new DateTime());
    	clusterRepository.save(existingCluster);
    	
    	//Remove the cluster from the cloud
    	try{
    		cloudProvider.delete(existingCluster);
    		return new ResponseEntity<>(HttpStatus.OK);
    		
    	}catch (RuntimeException e){
    		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
 
    
    private int getCluster_In_System_Id(Integer cluster_db_id){
    	Random rand = new Random();
    	int cluster_in_system_id_int;
    	Cluster alreadySubmittedCluster;
    	
    	do{
    		//Random number from 1 to MAX_CLUSTER_ID_NUMBER (see header of this class for value)
    		cluster_in_system_id_int = rand.nextInt(MAX_CLUSTER_ID_NUMBER) + 1;
        	alreadySubmittedCluster = clusterRepository.findByClusterInSystemID(cluster_in_system_id_int);
        	
    	}while(alreadySubmittedCluster != null);
    	
    	return cluster_in_system_id_int;
    }
    
}
