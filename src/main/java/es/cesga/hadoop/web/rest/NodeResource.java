package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.Node;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.repository.NodeRepository;
import es.cesga.hadoop.service.CloudProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Cluster.
 */
@RestController
@RequestMapping("/api")
public class NodeResource {
	

    private final Logger log = LoggerFactory.getLogger(NodeResource.class);
  
    @Inject
    private NodeRepository nodeRepository;
    
    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private CloudProvider cloudProvider;
    
    /**
     * GET  /nodes
     * 
     * 	-> "list" method
     * 	-> get all the nodes from the database
     * 
     * @Return a list of ->

     * 
     */
    @RequestMapping(value = "/nodes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Node> getAll() {
        log.debug("REST request to get all Clusters");
                
        //Get info from database
        List<Node> nodeList = nodeRepository.findAll();
        
        //Get info from cloud 
        //List<Cluster> clusterList = cloudProvider.findAll();
        
        
        return nodeList;    
    }
    
    
    /**
     * GET  /nodes/{id}
     * 	
     * 	-> "get" method
     * 	-> retrieves information about a node from the database
     * 
     * 
     * @Return a list of ->
     * 
     */
    @RequestMapping(value = "/nodes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Node> getNode(@PathVariable int id) {
        log.debug("REST request to get Cluster : {}", id);
               
        //Get info from database
        Node result = nodeRepository.findOne(id);
        
        //Get info from cloud
        //Cluster result = cloudProvider.show(id);
        
        return Optional.ofNullable(result)
            .map(node -> new ResponseEntity<>(
                node,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    
    /**
     * GET  /nodes/cluster/{clusterid}
     * 	
     * 	-> "get" method
     * 	-> retrieves information about a node from the database
     * 
     * 
     * @Return a list of ->
     * 
     */
    @RequestMapping(value = "/nodes/cluster/{clusterid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Node> getClusterNodes(@PathVariable int clusterid) {
        log.debug("REST request to get Node : cluster-{}", clusterid);
       
        //Get info from cloud
        Cluster cluster = clusterRepository.findOne(clusterid);
        List<Node> clusterNodesFromCloud = cloudProvider.getClusterNodes(cluster);
        
        //Get nodes from database
        List<Node> clusterNodesFromDatabase = nodeRepository.findAllNodesForCluster(clusterid);
        
        if(clusterNodesFromCloud.size() == 0){
        	//Probably couldn't get info from cloud
            //Return database info
        	return clusterNodesFromDatabase;
        }
        
        //Persist fresh node info in database
        
        /*
         * This solution should be improved as the inner loop will only match
         * in "size of nodes" cases, but will iterate for "size of nodes" ^ 2 times 
         */
        for(Node nodeCloud : clusterNodesFromCloud){
            for(Node nodeDatabase : clusterNodesFromDatabase){
            	if(nodeCloud.getNodeInSystemId() == nodeDatabase.getNodeInSystemId()){
                	nodeDatabase.setStatus(nodeCloud.getStatus());
                	nodeDatabase.setUcpu(nodeCloud.getUcpu());
                	nodeDatabase.setUmem(nodeCloud.getUmem());
                	nodeDatabase.setUptimeSeconds(nodeCloud.getUptimeSeconds());
                	nodeRepository.saveAndFlush(nodeDatabase);
            	}
            }
        }
        return clusterNodesFromCloud;
    }
}
