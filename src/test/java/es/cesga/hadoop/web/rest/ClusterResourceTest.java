package es.cesga.hadoop.web.rest;

import es.cesga.hadoop.Application;
import es.cesga.hadoop.domain.Cluster;
import es.cesga.hadoop.domain.util.AuthUtilsBean;
import es.cesga.hadoop.repository.ClusterRepository;
import es.cesga.hadoop.service.CloudProvider;
import es.cesga.hadoop.service.impl.OpenNebulaProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.fail;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
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

        
    private static final String DEFAULT_CLUSTERNAME = "cluster-prueba-hadoop-1";
    private static final String UPDATED_CLUSTERNAME = "UPDATED_TEXT";
    private static final String INVALID_CLUSTERNAME = "nombre con espacio";
    
    private static final Integer DEFAULT_SIZE = 1;
    private static final Integer INVALID_SIZE_1 = -1;
    private static final Integer INVALID_SIZE_2 = 110;
    

    private static final Integer DEFAULT_REPLICATION = 1;
    private static final Integer INVALID_REPLICATION_1 = -1;
    private static final Integer INVALID_REPLICATION_2 = 10;

    private static final Integer DEFAULT_BLOCKSIZE = 16;

    private static final DateTime EXPECTED_SUBMIT_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final String EXPECTED_SUBMIT_TIME_STR = dateTimeFormatter.print(EXPECTED_SUBMIT_TIME);

    private static final DateTime EXPECTED_STOP_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final String EXPECTED_STOP_TIME_STR = dateTimeFormatter.print(EXPECTED_STOP_TIME);

    private static final Integer EXPECTED_EXIT_STATUS = 0;
    
    private static final int DEFAULT_CLUSTER_IN_SYSTEM_ID = 9000;
    
    private static final String DEFAULT_USERNAME = "jenes";
    private static final String INVALID_USERNAME = "mal nombre";
    private static final String DEFAULT_PASSWORD = "1RedLizard";
    
    @Resource
    private FilterChainProxy springSecurityFilterChain;
        
    @Inject
    private ClusterRepository clusterRepository;

    private MockMvc restClusterMockMvc;

    private Cluster databaseCluster;
    
    private Cluster applicationCluster;
    
    @Mock
    private AuthUtilsBean auth;

    
    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        

        CloudProvider provider = new OpenNebulaProvider();
        ReflectionTestUtils.setField(provider, "clusterRepository", clusterRepository);
        ReflectionTestUtils.setField(provider, "auth", auth);
        
        ClusterResource clusterResource = new ClusterResource();
        ReflectionTestUtils.setField(clusterResource, "clusterRepository", clusterRepository);
        ReflectionTestUtils.setField(clusterResource, "auth", auth);
        ReflectionTestUtils.setField(clusterResource, "cloudProvider", provider);

        this.restClusterMockMvc = MockMvcBuilders.standaloneSetup(clusterResource).build();
        Mockito.when(auth.getUsername()).thenReturn(DEFAULT_USERNAME);
        Mockito.when(auth.getPassword()).thenReturn(DEFAULT_PASSWORD);
    }
    
    public Cluster getValidCluster(){
    	Cluster validCluster = new Cluster();
    	validCluster.setClustername(DEFAULT_CLUSTERNAME);
    	validCluster.setSize(DEFAULT_SIZE);
    	validCluster.setDfsReplicas(DEFAULT_REPLICATION);
    	validCluster.setDfsBlocksize(DEFAULT_BLOCKSIZE);
    	validCluster.setCluster_In_System_Id(DEFAULT_CLUSTER_IN_SYSTEM_ID);
    	return validCluster;
    }
    
    
    @Before
    public void initTest() {
    	//This is the cluster used to launch a new cluster in the createCluster test
    	applicationCluster = new Cluster();
    	applicationCluster.setClustername(DEFAULT_CLUSTERNAME);
    	applicationCluster.setSize(DEFAULT_SIZE);
    	applicationCluster.setDfsReplicas(DEFAULT_REPLICATION);
    	applicationCluster.setDfsBlocksize(DEFAULT_BLOCKSIZE);
    	applicationCluster.setCluster_In_System_Id(DEFAULT_CLUSTER_IN_SYSTEM_ID);
    	
    	//This is the cluster used to test the database CRUD
    	databaseCluster = new Cluster();
    	databaseCluster.setClustername(DEFAULT_CLUSTERNAME);
    	databaseCluster.setSize(DEFAULT_SIZE);
    	databaseCluster.setDfsReplicas(DEFAULT_REPLICATION);
    	databaseCluster.setDfsBlocksize(DEFAULT_BLOCKSIZE);
    	databaseCluster.setExitStatus(EXPECTED_EXIT_STATUS);
    	databaseCluster.setStopTime(EXPECTED_STOP_TIME);
    	databaseCluster.setSubmitTime(EXPECTED_SUBMIT_TIME);
    	databaseCluster.setUsername(DEFAULT_USERNAME);
    	databaseCluster.setCluster_In_System_Id(DEFAULT_CLUSTER_IN_SYSTEM_ID);
    }
    
    @Test
    @Transactional
    public void startAndStopCluster() throws Exception {
        
    	boolean errorThrown = false;
    	int databaseSizeBeforeAnything = clusterRepository.findAll().size();
    	String clusterId = null;
    	try{
	        // Create the Cluster
	        clusterId = restClusterMockMvc.perform(post("/api/clusters")
	        			.accept(MediaType.APPLICATION_JSON)
	        			.contentType(TestUtil.APPLICATION_JSON_UTF8)
	        			.content(TestUtil.convertObjectToJsonBytes(applicationCluster))
	                )
	                .andExpect(status().isCreated())
	                .andReturn().getResponse().getHeader("Location");
	        
	        //URI is like /api/clusters/3 where the last number is the id of the new cluster
	        clusterId = clusterId.split("/")[3];
	        
	        // Validate the Cluster in the database
	        List<Cluster> clusters = clusterRepository.findAll();
	        assertThat(clusters).hasSize(databaseSizeBeforeAnything + 1);
	        Cluster testCluster = clusters.get(clusters.size() - 1);
	        assertThat(testCluster.getClustername()).isEqualTo(DEFAULT_CLUSTERNAME);
	        assertThat(testCluster.getSize()).isEqualTo(DEFAULT_SIZE);
	        assertThat(testCluster.getDfsReplicas()).isEqualTo(DEFAULT_REPLICATION);
	        assertThat(testCluster.getDfsBlocksize()).isEqualTo(DEFAULT_BLOCKSIZE);
	        
	        //Give OpenNebula some time to start the cluster
	        Thread.sleep(10000);
	        
//	        //applicationCluster = clusterRepository.findOne(Integer.valueOf(clusterId));
//	        while(applicationCluster.getExitStatus() == null){
//	        	Thread.sleep(10000);
//	        	applicationCluster = clusterRepository.findOne(Integer.valueOf(clusterId));
//	        }
//	        //Check that the cluster deployment by the script finished successfully
//	        assertThat(applicationCluster.getExitStatus()).isEqualTo(0);
	        
	        
    	}catch(Exception e){
    		errorThrown = true;
    		
    	}finally{
    		if(errorThrown){
    			
    		}else{
    			if(clusterId != ""){
	        		//Stop the cluster
	                restClusterMockMvc.perform(delete("/api/clusters/{id}", clusterId)
	                        .accept(TestUtil.APPLICATION_JSON_UTF8))
	                        .andExpect(status().isOk());
	
	                // Validate the database is empty
	                //List<Cluster> clusters = clusterRepository.findAll();
	                //assertThat(clusters).hasSize(databaseSizeBeforeAnything);
	                //The cluster is not removed from the database, it is stopped and its stop timestamp is set
	                
	                Cluster deletedCluster = clusterRepository.findOne(Integer.valueOf(clusterId));
	                assertThat(deletedCluster.getStopTime().isBeforeNow());
    			}    			
    		}
    	}
    }
    
    @Test
    @Transactional
    public void getAllClusters() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(databaseCluster);

        // Get all the clusters
        restClusterMockMvc.perform(get("/api/clusters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].cluster_In_System_Id").value(DEFAULT_CLUSTER_IN_SYSTEM_ID))
                .andExpect(jsonPath("$.[*].clustername").value(DEFAULT_CLUSTERNAME.toString()))
                .andExpect(jsonPath("$.[*].username").value(DEFAULT_USERNAME.toString()))
                .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.[*].dfsReplicas").value(hasItem(DEFAULT_REPLICATION)))
                .andExpect(jsonPath("$.[*].dfsBlocksize").value(hasItem(DEFAULT_BLOCKSIZE)))
                .andExpect(jsonPath("$.[*].submitTime").value(hasItem(EXPECTED_SUBMIT_TIME_STR)))
                .andExpect(jsonPath("$.[*].stopTime").value(hasItem(EXPECTED_STOP_TIME_STR)))
                .andExpect(jsonPath("$.[*].exitStatus").value(hasItem(EXPECTED_EXIT_STATUS)));
    }

    @Test
    @Transactional
    public void getCluster() throws Exception {
        // Initialize the database
    	clusterRepository.saveAndFlush(databaseCluster);

        // Get the cluster
        ResultActions results = restClusterMockMvc.perform(get("/api/clusters/{id}", databaseCluster.getId()));
        
        String resultString = results.andReturn().getResponse().getContentAsString();
        results.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.cluster_In_System_Id").value(DEFAULT_CLUSTER_IN_SYSTEM_ID))
            .andExpect(jsonPath("$.clustername").value(DEFAULT_CLUSTERNAME.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE))
            .andExpect(jsonPath("$.dfsReplicas").value(DEFAULT_REPLICATION))
            .andExpect(jsonPath("$.dfsBlocksize").value(DEFAULT_BLOCKSIZE))
            .andExpect(jsonPath("$.submitTime").value(EXPECTED_SUBMIT_TIME_STR))
            .andExpect(jsonPath("$.stopTime").value(EXPECTED_STOP_TIME_STR))
            .andExpect(jsonPath("$.exitStatus").value(EXPECTED_EXIT_STATUS));
    }

    @Test
    @Transactional
    public void getNonExistingCluster() throws Exception {
        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", Integer.MAX_VALUE))
        		.andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(databaseCluster);
		
		int databaseSizeBeforeUpdate = clusterRepository.findAll().size();

        // Update the cluster
		databaseCluster.setClustername(UPDATED_CLUSTERNAME);
        restClusterMockMvc.perform(put("/api/clusters/{id}", databaseCluster.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(databaseCluster)))
                .andExpect(status().isOk());

        // Validate the Cluster in the database
        List<Cluster> clusters = clusterRepository.findAll();
        assertThat(clusters).hasSize(databaseSizeBeforeUpdate);
        Cluster testCluster = clusters.get(clusters.size() - 1);
        assertThat(testCluster.getClustername()).isEqualTo(UPDATED_CLUSTERNAME);
    }
    
    @Test
    @Transactional
    public void testInvalidCluster() throws IOException, Exception{
    	Cluster invalidCluster;
    	
    	
    	//Bad clustername
    	invalidCluster = getValidCluster();
    	invalidCluster.setClustername(INVALID_CLUSTERNAME);
        restClusterMockMvc.perform(post("/api/clusters")
    			.accept(MediaType.APPLICATION_JSON)
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
    			.content(TestUtil.convertObjectToJsonBytes(invalidCluster))
            )
            .andExpect(status().isBadRequest());
        

        //Bad cluster size
    	invalidCluster = getValidCluster();
    	invalidCluster.setSize(INVALID_SIZE_1);
        ResultActions result = restClusterMockMvc.perform(post("/api/clusters")
    			.accept(MediaType.APPLICATION_JSON)
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
    			.content(TestUtil.convertObjectToJsonBytes(invalidCluster))
            )
            .andExpect(status().isBadRequest());
    	invalidCluster = getValidCluster();
    	invalidCluster.setSize(INVALID_SIZE_2);
        restClusterMockMvc.perform(post("/api/clusters")
    			.accept(MediaType.APPLICATION_JSON)
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
    			.content(TestUtil.convertObjectToJsonBytes(invalidCluster))
            )
            .andExpect(status().isBadRequest());

        
        //Bad cluster replication
    	invalidCluster = getValidCluster();
    	invalidCluster.setDfsReplicas(INVALID_REPLICATION_1);
        restClusterMockMvc.perform(post("/api/clusters")
    			.accept(MediaType.APPLICATION_JSON)
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
    			.content(TestUtil.convertObjectToJsonBytes(invalidCluster))
            )
            .andExpect(status().isBadRequest());
    	invalidCluster = getValidCluster();
    	invalidCluster.setDfsReplicas(INVALID_REPLICATION_2);
        restClusterMockMvc.perform(post("/api/clusters")
    			.accept(MediaType.APPLICATION_JSON)
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
    			.content(TestUtil.convertObjectToJsonBytes(invalidCluster))
            )
            .andExpect(status().isBadRequest());

        
        
        
    }
}
