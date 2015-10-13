package es.cesga.hadoop.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import es.cesga.hadoop.Application;
import es.cesga.hadoop.domain.Ip;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.repository.IpRepository;

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

    private static final String DEFAULT_ADDRESS = "192.168.0.10";
    private static final String UPDATED_ADDRESS = "10.0.0.0";

    private static final Integer DEFAULT_MASK = 32;
    private static final Integer UPDATED_MASK = 8;

    private static final Boolean DEFAULT_ENABLED = true;
    private static final Boolean UPDATED_ENABLED = false;
    private static final String DEFAULT_USERNAME = "user";

    @Inject
    private IpRepository ipRepository;

    private MockMvc restIpMockMvc;

    private Ip ip;
    
    @Mock
    private AuthUtilsBean auth;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        IpResource ipResource = new IpResource();
        ReflectionTestUtils.setField(ipResource, "ipRepository", ipRepository);
        ReflectionTestUtils.setField(ipResource, "auth", auth);
        this.restIpMockMvc = MockMvcBuilders.standaloneSetup(ipResource).build();
        Mockito.when(auth.getUsername()).thenReturn(DEFAULT_USERNAME);
    }

    @Before
    public void initTest() {
        ip = new Ip();
        ip.setAddress(DEFAULT_ADDRESS);
        ip.setMask(DEFAULT_MASK);
        ip.setEnabled(DEFAULT_ENABLED);
        ip.setUsername(DEFAULT_USERNAME);
    }

    @Test
    @Transactional
    public void createIp() throws Exception {
        int databaseSizeBeforeCreate = ipRepository.findAll().size();

        // Create the Ip
        restIpMockMvc.perform(post("/api/ips")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ip)))
                .andExpect(status().isCreated());

        // Validate the Ip in the database
        List<Ip> ips = ipRepository.findAll();
        assertThat(ips).hasSize(databaseSizeBeforeCreate + 1);
        Ip testIp = ips.get(ips.size() - 1);
        assertThat(testIp.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testIp.getMask()).isEqualTo(DEFAULT_MASK);
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
                .andExpect(jsonPath("$.[*].id").value(hasItem(ip.getId().intValue())))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].mask").value(hasItem(DEFAULT_MASK)))
                .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
                .andExpect(jsonPath("$.[*].username").doesNotExist());
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
            .andExpect(jsonPath("$.mask").value(DEFAULT_MASK))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    @Transactional
    public void getNonExistingIp() throws Exception {
        // Get the ip
        restIpMockMvc.perform(get("/api/ips/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIp() throws Exception {
        // Initialize the database
        ipRepository.saveAndFlush(ip);
		
		int databaseSizeBeforeUpdate = ipRepository.findAll().size();

        // Update the ip
        ip.setAddress(UPDATED_ADDRESS);
        ip.setMask(UPDATED_MASK);
        ip.setEnabled(UPDATED_ENABLED);
        restIpMockMvc.perform(put("/api/ips/{id}", ip.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ip)))
                .andExpect(status().isOk());

        // Validate the Ip in the database
        List<Ip> ips = ipRepository.findAll();
        assertThat(ips).hasSize(databaseSizeBeforeUpdate);
        Ip testIp = ips.get(ips.size() - 1);
        assertThat(testIp.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testIp.getMask()).isEqualTo(UPDATED_MASK);
        assertThat(testIp.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    public void deleteIp() throws Exception {
        // Initialize the database
        ipRepository.saveAndFlush(ip);
		
		int databaseSizeBeforeDelete = ipRepository.findAll().size();

        // Get the ip
        restIpMockMvc.perform(delete("/api/ips/{id}", ip.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ip> ips = ipRepository.findAll();
        assertThat(ips).hasSize(databaseSizeBeforeDelete - 1);
    }
}
