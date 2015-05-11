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
import es.cesga.hadoop.repository.IpRepository;

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
    private AuthUtilsBean auth;

    /**
     * POST  /ips -> Create a new ip.
     */
    @RequestMapping(value = "/ips",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Ip ip) throws URISyntaxException {
    	log.info("REST request {} requests to save Ip : {}", auth.getUsername(), ip);
        if (ip.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new ip cannot already have an ID").build();
        }
        // Add current username information
        ip.setUsername(auth.getUsername());
        ipRepository.save(ip);
        return ResponseEntity.created(new URI("/api/ips/" + ip.getId())).build();
    }

    /**
     * PUT  /ips -> Updates an existing ip.
     */
    @RequestMapping(value = "/ips",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Ip ip) throws URISyntaxException {
    	
        log.info("REST request {} requests to update Ip : {}", auth.getUsername(), ip);
        if (ip.getId() == null) {
            return create(ip);
        }
        // Add current username information
        ip.setUsername(auth.getUsername());
        //FIXME: Before saving verify that the ip referenced by the ID belongs to this user 
        ipRepository.save(ip);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /ips -> get all the ips.
     */
    @RequestMapping(value = "/ips",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Ip> getAll() {
    	log.debug("REST {} requests to get all Ips", auth.getUsername());
        return ipRepository.findAllForCurrentUser();
    }

    /**
     * GET  /ips/:id -> get the "id" ip.
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
     * DELETE  /ips/:id -> delete the "id" ip.
     */
    @RequestMapping(value = "/ips/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Ip : {}", id);
        //FIXME: Before saving verify that the ip referenced by the ID belongs to this user
        ipRepository.delete(id);
    }
}
