package es.cesga.hadoop.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class HadoopPasswordEncoder implements PasswordEncoder {

	public HadoopPasswordEncoder() {		
	}
	
	@Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}
	
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encodedPassword.equals(rawPassword.toString());
	}
	
}
