package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.cesga.hadoop.domain.SshKey;
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
    private SshKeyRepository sshKeyRepository;

    /**
     * POST  /sshKeys -> Create a new sshKey.
     */
    @RequestMapping(value = "/sshKeys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody SshKey sshKey) throws URISyntaxException {
        log.debug("REST request to save SshKey : {}", sshKey);
        if (sshKey.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new sshKey cannot already have an ID").build();
        }
        sshKeyRepository.save(sshKey);
        return ResponseEntity.created(new URI("/api/sshKeys/" + sshKey.getId())).build();
    }

    /**
     * PUT  /sshKeys -> Updates an existing sshKey.
     */
    @RequestMapping(value = "/sshKeys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody SshKey sshKey) throws URISyntaxException {
        log.debug("REST request to update SshKey : {}", sshKey);
        if (sshKey.getId() == null) {
            return create(sshKey);
        }
        sshKeyRepository.save(sshKey);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /sshKeys -> get all the sshKeys.
     */
    @RequestMapping(value = "/sshKeys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SshKey> getAll() {
        log.debug("REST request to get all SshKeys");
        return sshKeyRepository.findAll();
    }

    /**
     * GET  /sshKeys/:id -> get the "id" sshKey.
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
     * DELETE  /sshKeys/:id -> delete the "id" sshKey.
     */
    @RequestMapping(value = "/sshKeys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete SshKey : {}", id);
        sshKeyRepository.delete(id);
    }
}
