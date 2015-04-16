package es.cesga.hadoop.web.rest;

import es.cesga.hadoop.Application;
import es.cesga.hadoop.domain.Key;
import es.cesga.hadoop.repository.KeyRepository;

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
 * Test class for the KeyResource REST controller.
 *
 * @see KeyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class KeyResourceTest {

    private static final String DEFAULT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_PUBKEY = "SAMPLE_TEXT";
    private static final String UPDATED_PUBKEY = "UPDATED_TEXT";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    @Inject
    private KeyRepository keyRepository;

    private MockMvc restKeyMockMvc;

    private Key key;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        KeyResource keyResource = new KeyResource();
        ReflectionTestUtils.setField(keyResource, "keyRepository", keyRepository);
        this.restKeyMockMvc = MockMvcBuilders.standaloneSetup(keyResource).build();
    }

    @Before
    public void initTest() {
        key = new Key();
        key.setType(DEFAULT_TYPE);
        key.setPubkey(DEFAULT_PUBKEY);
        key.setEnabled(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void createKey() throws Exception {
        // Validate the database is empty
        assertThat(keyRepository.findAll()).hasSize(0);

        // Create the Key
        restKeyMockMvc.perform(post("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isCreated());

        // Validate the Key in the database
        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(1);
        Key testKey = keys.iterator().next();
        assertThat(testKey.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testKey.getPubkey()).isEqualTo(DEFAULT_PUBKEY);
        assertThat(testKey.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void getAllKeys() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);

        // Get all the keys
        restKeyMockMvc.perform(get("/api/keys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(key.getId().intValue()))
                .andExpect(jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.[0].pubkey").value(DEFAULT_PUBKEY.toString()))
                .andExpect(jsonPath("$.[0].enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    public void getKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);

        // Get the key
        restKeyMockMvc.perform(get("/api/keys/{id}", key.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(key.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.pubkey").value(DEFAULT_PUBKEY.toString()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingKey() throws Exception {
        // Get the key
        restKeyMockMvc.perform(get("/api/keys/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);

        // Update the key
        key.setType(UPDATED_TYPE);
        key.setPubkey(UPDATED_PUBKEY);
        key.setEnabled(UPDATED_ENABLED);
        restKeyMockMvc.perform(put("/api/keys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(key)))
                .andExpect(status().isOk());

        // Validate the Key in the database
        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(1);
        Key testKey = keys.iterator().next();
        assertThat(testKey.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testKey.getPubkey()).isEqualTo(UPDATED_PUBKEY);
        assertThat(testKey.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    public void deleteKey() throws Exception {
        // Initialize the database
        keyRepository.saveAndFlush(key);

        // Get the key
        restKeyMockMvc.perform(delete("/api/keys/{id}", key.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Key> keys = keyRepository.findAll();
        assertThat(keys).hasSize(0);
    }
}
