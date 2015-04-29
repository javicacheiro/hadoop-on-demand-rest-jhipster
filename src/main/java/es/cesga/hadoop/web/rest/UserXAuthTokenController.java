package es.cesga.hadoop.web.rest;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;

import es.cesga.hadoop.security.xauth.Token;
import es.cesga.hadoop.security.xauth.TokenProvider;

@RestController
@RequestMapping("/api")
public class UserXAuthTokenController {

	private final Logger log = LoggerFactory.getLogger(UserXAuthTokenController.class);
	
    @Inject
    private TokenProvider tokenProvider;
    
    @Inject
    private LdapTemplate ldapTemplate;
    
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.POST)
    @Timed
    public Token authorize(@RequestParam String username, @RequestParam String password) {

        validateUserCredentialsWithLdap(username, password);
        
        //All user are assigned to the ROLE_USER
        //FIXME: Assign ROLE_ADMIN based on username or LDAP groups
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        		
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password,
                grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails details = new org.springframework.security.core.userdetails.User(username, password,
                grantedAuthorities);
        
        return tokenProvider.createToken(details);
    }

	public void validateUserCredentialsWithLdap(String username, String password) {
		try {
        	ldapTemplate.authenticate(query().where("uid").is(username), password);
        	log.info("LDAP succesful authentication of user " + username);
        } catch(Exception ex) {
        	log.warn("LDAP authentication failure for user " + username);
        	log.warn(ex.getMessage());
        	throw new BadCredentialsException("Invalid username/password");
        }
	}


}
