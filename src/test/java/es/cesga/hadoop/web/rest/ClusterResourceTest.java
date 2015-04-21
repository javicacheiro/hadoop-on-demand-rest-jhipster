package es.cesga.hadoop.web.rest;

import es.cesga.hadoop.Application;
import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.repository.ClusterRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ClusterResource REST controller.
 *
 * @see ClusterResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ClusterResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_CLUSTERNAME = "SAMPLE_TEXT";
    private static final String UPDATED_CLUSTERNAME = "UPDATED_TEXT";
    private static final String DEFAULT_VERSION = "SAMPLE_TEXT";
    private static final String UPDATED_VERSION = "UPDATED_TEXT";

    private static final Integer DEFAULT_SIZE = 0;
    private static final Integer UPDATED_SIZE = 1;

    private static final Integer DEFAULT_REPLICATION = 0;
    private static final Integer UPDATED_REPLICATION = 1;

    private static final Integer DEFAULT_BLOCKSIZE = 0;
    private static final Integer UPDATED_BLOCKSIZE = 1;

    private static final DateTime DEFAULT_SUBMIT_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_SUBMIT_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_SUBMIT_TIME_STR = dateTimeFormatter.print(DEFAULT_SUBMIT_TIME);

    private static final DateTime DEFAULT_STOP_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_STOP_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_STOP_TIME_STR = dateTimeFormatter.print(DEFAULT_STOP_TIME);

    private static final Integer DEFAULT_EXIT_STATUS = 0;
    private static final Integer UPDATED_EXIT_STATUS = 1;
    private static final String DEFAULT_STATUS = "SAMPLE_TEXT";
    private static final String UPDATED_STATUS = "UPDATED_TEXT";
    private static final String DEFAULT_USERNAME = "SAMPLE_TEXT";
    private static final String UPDATED_USERNAME = "UPDATED_TEXT";

    @Inject
    private ClusterRepository clusterRepository;

    private MockMvc restClusterMockMvc;

    private Cluster cluster;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClusterResource clusterResource = new ClusterResource();
        ReflectionTestUtils.setField(clusterResource, "clusterRepository", clusterRepository);
        this.restClusterMockMvc = MockMvcBuilders.standaloneSetup(clusterResource).build();
    }

    @Before
    public void initTest() {
        cluster = new Cluster();
        cluster.setClustername(DEFAULT_CLUSTERNAME);
        cluster.setVersion(DEFAULT_VERSION);
        cluster.setSize(DEFAULT_SIZE);
        cluster.setReplication(DEFAULT_REPLICATION);
        cluster.setBlocksize(DEFAULT_BLOCKSIZE);
        cluster.setSubmitTime(DEFAULT_SUBMIT_TIME);
        cluster.setStopTime(DEFAULT_STOP_TIME);
        cluster.setExitStatus(DEFAULT_EXIT_STATUS);
        cluster.setStatus(DEFAULT_STATUS);
        cluster.setUsername(DEFAULT_USERNAME);
    }

    @Test
    @Transactional
    public void createCluster() throws Exception {
        int databaseSizeBeforeCreate = clusterRepository.findAll().size();

        // Create the Cluster
        restClusterMockMvc.perform(post("/api/clusters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cluster)))
                .andExpect(status().isCreated());

        // Validate the Cluster in the database
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeCreate + 1);
        Cluster testCluster = clusters.get(clusters.size() - 1);
        assertThat(testCluster.getClustername()).isEqualTo(DEFAULT_CLUSTERNAME);
        assertThat(testCluster.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testCluster.getSize()).isEqualTo(DEFAULT_SIZE);
        assertThat(testCluster.getReplication()).isEqualTo(DEFAULT_REPLICATION);
        assertThat(testCluster.getBlocksize()).isEqualTo(DEFAULT_BLOCKSIZE);
        assertThat(testCluster.getSubmitTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_SUBMIT_TIME);
        assertThat(testCluster.getStopTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_STOP_TIME);
        assertThat(testCluster.getExitStatus()).isEqualTo(DEFAULT_EXIT_STATUS);
        assertThat(testCluster.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCluster.getUsername()).isEqualTo(DEFAULT_USERNAME);
    }

    @Test
    @Transactional
    public void getAllClusters() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusters
        restClusterMockMvc.perform(get("/api/clusters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cluster.getId().intValue())))
                .andExpect(jsonPath("$.[*].clustername").value(hasItem(DEFAULT_CLUSTERNAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.[*].replication").value(hasItem(DEFAULT_REPLICATION)))
                .andExpect(jsonPath("$.[*].blocksize").value(hasItem(DEFAULT_BLOCKSIZE)))
                .andExpect(jsonPath("$.[*].submitTime").value(hasItem(DEFAULT_SUBMIT_TIME_STR)))
                .andExpect(jsonPath("$.[*].stopTime").value(hasItem(DEFAULT_STOP_TIME_STR)))
                .andExpect(jsonPath("$.[*].exitStatus").value(hasItem(DEFAULT_EXIT_STATUS)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())));
    }

    @Test
    @Transactional
    public void getCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", cluster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cluster.getId().intValue()))
            .andExpect(jsonPath("$.clustername").value(DEFAULT_CLUSTERNAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE))
            .andExpect(jsonPath("$.replication").value(DEFAULT_REPLICATION))
            .andExpect(jsonPath("$.blocksize").value(DEFAULT_BLOCKSIZE))
            .andExpect(jsonPath("$.submitTime").value(DEFAULT_SUBMIT_TIME_STR))
            .andExpect(jsonPath("$.stopTime").value(DEFAULT_STOP_TIME_STR))
            .andExpect(jsonPath("$.exitStatus").value(DEFAULT_EXIT_STATUS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCluster() throws Exception {
        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);
		
		int databaseSizeBeforeUpdate = clusterRepository.findAll().size();

        // Update the cluster
        cluster.setClustername(UPDATED_CLUSTERNAME);
        cluster.setVersion(UPDATED_VERSION);
        cluster.setSize(UPDATED_SIZE);
        cluster.setReplication(UPDATED_REPLICATION);
        cluster.setBlocksize(UPDATED_BLOCKSIZE);
        cluster.setSubmitTime(UPDATED_SUBMIT_TIME);
        cluster.setStopTime(UPDATED_STOP_TIME);
        cluster.setExitStatus(UPDATED_EXIT_STATUS);
        cluster.setStatus(UPDATED_STATUS);
        cluster.setUsername(UPDATED_USERNAME);
        restClusterMockMvc.perform(put("/api/clusters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cluster)))
                .andExpect(status().isOk());

        // Validate the Cluster in the database
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeUpdate);
        Cluster testCluster = clusters.get(clusters.size() - 1);
        assertThat(testCluster.getClustername()).isEqualTo(UPDATED_CLUSTERNAME);
        assertThat(testCluster.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testCluster.getSize()).isEqualTo(UPDATED_SIZE);
        assertThat(testCluster.getReplication()).isEqualTo(UPDATED_REPLICATION);
        assertThat(testCluster.getBlocksize()).isEqualTo(UPDATED_BLOCKSIZE);
        assertThat(testCluster.getSubmitTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_SUBMIT_TIME);
        assertThat(testCluster.getStopTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_STOP_TIME);
        assertThat(testCluster.getExitStatus()).isEqualTo(UPDATED_EXIT_STATUS);
        assertThat(testCluster.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCluster.getUsername()).isEqualTo(UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void deleteCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);
		
		int databaseSizeBeforeDelete = clusterRepository.findAll().size();

        // Get the cluster
        restClusterMockMvc.perform(delete("/api/clusters/{id}", cluster.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeDelete - 1);
    }
}
