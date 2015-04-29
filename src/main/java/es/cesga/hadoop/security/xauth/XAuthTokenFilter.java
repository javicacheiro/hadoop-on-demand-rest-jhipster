package es.cesga.hadoop.security.xauth;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filters incoming requests and installs a Spring Security principal
 * if a header corresponding to a valid user is found.
 */
public class XAuthTokenFilter extends GenericFilterBean {

    private final static String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";

    private UserDetailsService detailsService;

    private TokenProvider tokenProvider;

    public XAuthTokenFilter(UserDetailsService detailsService, TokenProvider tokenProvider) {
        this.detailsService = detailsService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    	String authToken = httpServletRequest.getHeader(XAUTH_TOKEN_HEADER_NAME);

    	if (StringUtils.hasText(authToken) && tokenProvider.validateToken(authToken)) {
    		String username = tokenProvider.getUserNameFromToken(authToken);
    		String password = tokenProvider.getPasswordFromToken(authToken);
    		Collection<GrantedAuthority> grantedAuthorities = tokenProvider.getGrantedAuthoritiesFromToken(authToken);

    		UserDetails details = new org.springframework.security.core.userdetails.User(username, password, grantedAuthorities);

    		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
    		SecurityContextHolder.getContext().setAuthentication(token);
    	}
    	filterChain.doFilter(servletRequest, servletResponse);
    }
}
