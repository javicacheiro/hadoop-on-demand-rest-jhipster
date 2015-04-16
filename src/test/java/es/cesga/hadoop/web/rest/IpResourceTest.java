package es.cesga.hadoop.web.rest;

import es.cesga.hadoop.Application;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.repository.IpRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the IpResource REST controller.
 *
 * @see IpResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class IpResourceTest {

    private static final String DEFAULT_ADDRESS = "SAMPLE_TEXT";
    private static final String UPDATED_ADDRESS = "UPDATED_TEXT";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    @Inject
    private IpRepository ipRepository;

    private MockMvc restIpMockMvc;

    private Ip ip;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        IpResource ipResource = new IpResource();
        ReflectionTestUtils.setField(ipResource, "ipRepository", ipRepository);
        this.restIpMockMvc = MockMvcBuilders.standaloneSetup(ipResource).build();
    }

    @Before
    public void initTest() {
        ip = new Ip();
        ip.setAddress(DEFAULT_ADDRESS);
        ip.setEnabled(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void createIp() throws Exception {
        // Validate the database is empty
        assertThat(ipRepository.findAll()).hasSize(0);

        // Create the Ip
        restIpMockMvc.perform(post("/api/ips")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ip)))
                .andExpect(status().isCreated());

        // Validate the Ip in the database
        List<Ip> ips = ipRepository.findAll();
        assertThat(ips).hasSize(1);
        Ip testIp = ips.iterator().next();
        assertThat(testIp.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testIp.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void getAllIps() throws Exception {
        // Initialize the database
        ipRepository.saveAndFlush(ip);

        // Get all the ips
        restIpMockMvc.perform(get("/api/ips"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(ip.getId().intValue()))
                .andExpect(jsonPath("$.[0].address").value(DEFAULT_ADDRESS.toString()))
                .andExpect(jsonPath("$.[0].enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    public void getIp() throws Exception {
        // Initialize the database
        ipRepository.saveAndFlush(ip);

        // Get the ip
        restIpMockMvc.perform(get("/api/ips/{id}", ip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ip.getId().intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingIp() throws Exception {
        // Get the ip
        restIpMockMvc.perform(get("/api/ips/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIp() throws Exception {
        // Initialize the database
        ipRepository.saveAndFlush(ip);

        // Update the ip
        ip.setAddress(UPDATED_ADDRESS);
        ip.setEnabled(UPDATED_ENABLED);
        restIpMockMvc.perform(put("/api/ips")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ip)))
                .andExpect(status().isOk());

        // Validate the Ip in the database
        List<Ip> ips = ipRepository.findAll();
        assertThat(ips).hasSize(1);
        Ip testIp = ips.iterator().next();
        assertThat(testIp.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testIp.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    public void deleteIp() throws Exception {
        // Initialize the database
        ipRepository.saveAndFlush(ip);

        // Get the ip
        restIpMockMvc.perform(delete("/api/ips/{id}", ip.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ip> ips = ipRepository.findAll();
        assertThat(ips).hasSize(0);
    }
}
