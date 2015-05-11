package es.cesga.hadoop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;

import es.cesga.hadoop.service.CloudProvider;
import es.cesga.hadoop.service.impl.OpenNebulaProvider;

@Configuration
public class OpenNebulaConfiguration implements EnvironmentAware {

    private static final String ENV_OPENNEBULA = "cloud.opennebula.";
    private static final String PROP_ENDPOINT = "xml-rpc";

    private final Logger log = LoggerFactory.getLogger(OpenNebulaConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_OPENNEBULA);
    }

    @Bean
    public CloudProvider cloudProvider() {
        log.debug("Configuring OpenNebula");
        String endpoint = propertyResolver.getProperty(PROP_ENDPOINT);

        CloudProvider cloudProvider = null;
        
        cloudProvider = new OpenNebulaProvider(endpoint);
            	
    	return cloudProvider;
    	

    }

}
