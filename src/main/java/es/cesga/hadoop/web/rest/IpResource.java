package es.cesga.hadoop.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.repository.IpRepository;
import es.cesga.hadoop.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
 * REST controller for managing Ip.
 */
@RestController
@RequestMapping("/api")
public class IpResource {

    private final Logger log = LoggerFactory.getLogger(IpResource.class);

    @Inject
    private IpRepository ipRepository;

    /**
     * POST  /ips -> Create a new ip.
     */
    @RequestMapping(value = "/ips",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Ip ip) throws URISyntaxException {
        log.debug("REST request to save Ip : {}", ip);
        if (ip.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new ip cannot already have an ID").build();
        }
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
        log.debug("REST request to update Ip : {}", ip);
        if (ip.getId() == null) {
            return create(ip);
        }
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
    public ResponseEntity<List<Ip>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Ip> page = ipRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ips", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ips/:id -> get the "id" ip.
     */
    @RequestMapping(value = "/ips/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ip> get(@PathVariable Long id) {
        log.debug("REST request to get Ip : {}", id);
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
        ipRepository.delete(id);
    }
}
