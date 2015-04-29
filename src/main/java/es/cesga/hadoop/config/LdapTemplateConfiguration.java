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

@Configuration
public class LdapTemplateConfiguration implements EnvironmentAware {

    private static final String ENV_SPRING_LDAP = "authentication.ldap.";
    private static final String PROP_URL = "url";
    private static final String PROP_BASE = "base";

    private final Logger log = LoggerFactory.getLogger(LdapTemplateConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_SPRING_LDAP);
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        log.debug("Configuring LDAP");
        String url = propertyResolver.getProperty(PROP_URL);
        String base = propertyResolver.getProperty(PROP_BASE);

        LdapTemplate ldapTemplate = null;
        
        if( isConfigurationValid(url, base) ){
        	LdapContextSource lcs = new LdapContextSource();
        	lcs.setUrl(url);
        	lcs.setBase(base);
        	lcs.setDirObjectFactory(DefaultDirObjectFactory.class);
        	lcs.afterPropertiesSet();
        	ldapTemplate = new LdapTemplate(lcs);
        }
    	
    	return ldapTemplate;
    	

    }

	public boolean isConfigurationValid(String url, String base) {
		if (url == null || url.isEmpty() || base == null || base.isEmpty()) {
            log.error("Warning! Your LDAP server is not configured.");
            log.info("Did you configure your LDAP settings in your application.yml?");
            return false;
        } else {
        	return true;
        }
	}
}
