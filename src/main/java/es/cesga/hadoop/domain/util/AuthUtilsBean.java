package es.cesga.hadoop.domain.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtilsBean {
	public String getUsername() {
		
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    	String username;
    	if (principal instanceof UserDetails) {
    		UserDetails user = (UserDetails)principal;
    		username = user.getUsername();
    	} else {
    		username = principal.toString();
    	}
    	
    	return username;
	}
	
	public String getPassword() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    	String password;
    	if (principal instanceof UserDetails) {
    		UserDetails user = (UserDetails)principal;
    		password = user.getPassword();
    	} else {
    		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
    		password = credentials.toString();
    	}
    	return password;
	}
	
	public String getOneAuth() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    	String username, password;
    	if (principal instanceof UserDetails) {
    		UserDetails user = (UserDetails)principal;
    		username = user.getUsername();
    		password = user.getPassword();
    	} else {
    		username = principal.toString();
    		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
    		password = credentials.toString();
    	}
    	
    	return username + ":" + password;
	}
}
