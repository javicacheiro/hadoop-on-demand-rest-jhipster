package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;

import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.service.CloudProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Cluster.
 */
@RestController
@RequestMapping("/api")
public class ClusterResource {

    private final Logger log = LoggerFactory.getLogger(ClusterResource.class);

    @Inject
    private ClusterRepository clusterRepository;
    
    @Inject
    private CloudProvider cloudProvider;

    /**
     * POST  /clusters -> Create a new cluster.
     */
    @RequestMapping(value = "/clusters",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Cluster cluster) throws URISyntaxException {
        log.debug("REST request to save Cluster : {}", cluster);
        if (cluster.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new cluster cannot already have an ID").build();
        }
        clusterRepository.save(cluster);
        return ResponseEntity.created(new URI("/api/clusters/" + cluster.getId())).build();
    }

    /**
     * PUT  /clusters -> Updates an existing cluster.
     */
    @RequestMapping(value = "/clusters",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Cluster cluster) throws URISyntaxException {
        log.debug("REST request to update Cluster : {}", cluster);
        if (cluster.getId() == null) {
            return create(cluster);
        }
        clusterRepository.save(cluster);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /clusters -> get all the clusters.
     */
    @RequestMapping(value = "/clusters",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Cluster> getAll() {
        log.debug("REST request to get all Clusters");
        //return clusterRepository.findAll();
        return cloudProvider.findAll();
    }

    /**
     * GET  /clusters/:id -> get the "id" cluster.
     */
    @RequestMapping(value = "/clusters/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cluster> get(@PathVariable int id) {
        log.debug("REST request to get Cluster : {}", id);
        //return cloudProvider.show(id);
      return Optional.ofNullable(cloudProvider.show(id))
      .map(cluster -> new ResponseEntity<>(
          cluster,
          HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//        return Optional.ofNullable(clusterRepository.findOne(id))
//            .map(cluster -> new ResponseEntity<>(
//                cluster,
//                HttpStatus.OK))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /clusters/:id -> delete the "id" cluster.
     */
    @RequestMapping(value = "/clusters/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Cluster : {}", id);
        clusterRepository.delete(id);
    }
}
