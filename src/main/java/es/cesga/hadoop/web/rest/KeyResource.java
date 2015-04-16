package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.cesga.hadoop.domain.Key;
import es.cesga.hadoop.repository.KeyRepository;
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
 * REST controller for managing Key.
 */
@RestController
@RequestMapping("/api")
public class KeyResource {

    private final Logger log = LoggerFactory.getLogger(KeyResource.class);

    @Inject
    private KeyRepository keyRepository;

    /**
     * POST  /keys -> Create a new key.
     */
    @RequestMapping(value = "/keys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Key key) throws URISyntaxException {
        log.debug("REST request to save Key : {}", key);
        if (key.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new key cannot already have an ID").build();
        }
        keyRepository.save(key);
        return ResponseEntity.created(new URI("/api/keys/" + key.getId())).build();
    }

    /**
     * PUT  /keys -> Updates an existing key.
     */
    @RequestMapping(value = "/keys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Key key) throws URISyntaxException {
        log.debug("REST request to update Key : {}", key);
        if (key.getId() == null) {
            return create(key);
        }
        keyRepository.save(key);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /keys -> get all the keys.
     */
    @RequestMapping(value = "/keys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Key> getAll() {
        log.debug("REST request to get all Keys");
        return keyRepository.findAll();
    }

    /**
     * GET  /keys/:id -> get the "id" key.
     */
    @RequestMapping(value = "/keys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Key> get(@PathVariable Long id) {
        log.debug("REST request to get Key : {}", id);
        return Optional.ofNullable(keyRepository.findOne(id))
            .map(key -> new ResponseEntity<>(
                key,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /keys/:id -> delete the "id" key.
     */
    @RequestMapping(value = "/keys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Key : {}", id);
        keyRepository.delete(id);
    }
}
