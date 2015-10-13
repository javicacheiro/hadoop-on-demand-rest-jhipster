package es.cesga.hadoop.web.rest;

import es.cesga.hadoop.Application;
import es.cesga.hadoop.domain.SshKey;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.repository.SshKeyRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;

import org.mockito.Mock;
import org.mockito.Mockito;
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
 * Test class for the SshKeyResource REST controller.
 *
 * @see SshKeyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SshKeyResourceTest {

    private static final String DEFAULT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_PUBKEY = "SAMPLE_TEXT";
    private static final String UPDATED_PUBKEY = "UPDATED_TEXT";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;
    private static final String DEFAULT_USERNAME = "SAMPLE_TEXT";
    private static final String UPDATED_USERNAME = "UPDATED_TEXT";

    @Inject
    private SshKeyRepository sshKeyRepository;

    private MockMvc restSshKeyMockMvc;

    private SshKey sshKey;
    
    @Mock
    private AuthUtilsBean auth;
    
    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SshKeyResource sshKeyResource = new SshKeyResource();
        ReflectionTestUtils.setField(sshKeyResource, "sshKeyRepository", sshKeyRepository);
        ReflectionTestUtils.setField(sshKeyResource, "auth", auth);
        this.restSshKeyMockMvc = MockMvcBuilders.standaloneSetup(sshKeyResource).build();
        Mockito.when(auth.getUsername()).thenReturn(DEFAULT_USERNAME);
    }

    @Before
    public void initTest() {
        sshKey = new SshKey();
        sshKey.setType(DEFAULT_TYPE);
        sshKey.setPubkey(DEFAULT_PUBKEY);
        sshKey.setEnabled(DEFAULT_ENABLED);
        sshKey.setUsername(DEFAULT_USERNAME);
    }

    @Test
    @Transactional
    public void createSshKey() throws Exception {
        int databaseSizeBeforeCreate = sshKeyRepository.findAll().size();

        // Create the SshKey
        restSshKeyMockMvc.perform(post("/api/sshKeys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sshKey)))
                .andExpect(status().isCreated());

        // Validate the SshKey in the database
        List<SshKey> sshKeys = sshKeyRepository.findAll();
        assertThat(sshKeys).hasSize(databaseSizeBeforeCreate + 1);
        SshKey testSshKey = sshKeys.get(sshKeys.size() - 1);
        assertThat(testSshKey.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSshKey.getPubkey()).isEqualTo(DEFAULT_PUBKEY);
        assertThat(testSshKey.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void getAllSshKeys() throws Exception {
        // Initialize the database
        sshKeyRepository.saveAndFlush(sshKey);

        // Get all the sshKeys
        restSshKeyMockMvc.perform(get("/api/sshKeys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sshKey.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].pubkey").value(hasItem(DEFAULT_PUBKEY.toString())))
                .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
                .andExpect(jsonPath("$.[*].username").doesNotExist());
    }

    @Test
    @Transactional
    public void getSshKey() throws Exception {
        // Initialize the database
        sshKeyRepository.saveAndFlush(sshKey);

        // Get the sshKey
        restSshKeyMockMvc.perform(get("/api/sshKeys/{id}", sshKey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sshKey.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.pubkey").value(DEFAULT_PUBKEY.toString()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    @Transactional
    public void getNonExistingSshKey() throws Exception {
        // Get the sshKey
        restSshKeyMockMvc.perform(get("/api/sshKeys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSshKey() throws Exception {
        // Initialize the database
        sshKeyRepository.saveAndFlush(sshKey);
		
		int databaseSizeBeforeUpdate = sshKeyRepository.findAll().size();

        // Update the sshKey
        sshKey.setType(UPDATED_TYPE);
        sshKey.setPubkey(UPDATED_PUBKEY);
        sshKey.setEnabled(UPDATED_ENABLED);
        restSshKeyMockMvc.perform(put("/api/sshKeys/{id}", sshKey.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sshKey)))
                .andExpect(status().isOk());

        // Validate the SshKey in the database
        List<SshKey> sshKeys = sshKeyRepository.findAll();
        assertThat(sshKeys).hasSize(databaseSizeBeforeUpdate);
        SshKey testSshKey = sshKeys.get(sshKeys.size() - 1);
        assertThat(testSshKey.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSshKey.getPubkey()).isEqualTo(UPDATED_PUBKEY);
        assertThat(testSshKey.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    public void deleteSshKey() throws Exception {
        // Initialize the database
    	sshKey.setUsername(auth.getUsername());
        sshKeyRepository.saveAndFlush(sshKey);
		
		int databaseSizeBeforeDelete = sshKeyRepository.findAll().size();

        // Get the sshKey
        restSshKeyMockMvc.perform(delete("/api/sshKeys/{id}", sshKey.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SshKey> sshKeys = sshKeyRepository.findAll();
        assertThat(sshKeys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
