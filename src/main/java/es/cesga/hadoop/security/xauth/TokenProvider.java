package es.cesga.hadoop.security.xauth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

public class TokenProvider {

	private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
	
	private static final String SEPARATOR = ":";
	// Regexp to use for spliting. If using "." as separator then splitter would be "\\."
	private static final String SPLITTER = ":";
	
    private final String secretKey;
    private final int tokenValidity;
    private Crypter textEncryptor;

    public TokenProvider(String secretKey, int tokenValidity) {
        this.secretKey = secretKey;
        this.tokenValidity = tokenValidity;
        //this.textEncryptor = new BasicTextEncryptor();
        //this.textEncryptor.setPassword(secretKey);
        try {
			this.textEncryptor = new Crypter(secretKey);
		} catch (KeyczarException e) {
			// TODO Auto-generated catch block
			log.error("Error creating the textEncryptor for the token provider");
			e.printStackTrace();
		}
    }

    public Token createToken(UserDetails userDetails) {
        long expires = System.currentTimeMillis() + 1000L * tokenValidity;
        
        String username = userDetails.getUsername();
        String password = userDetails.getPassword();
        //TODO: Currently we only use one role (the first returned by the collection)
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        
        final StringBuilder details = new StringBuilder(170);
		details.append(toBase64(username))
		       .append(SEPARATOR)
		       .append(encrypt(password))
		       .append(SEPARATOR)
		       .append(toBase64(role));
		String subject = details.toString();     
		
		String signature = computeSignature(subject, expires);
		
		final StringBuilder sbToken = new StringBuilder(170);
		sbToken.append(subject)
		     .append(SEPARATOR)
		     .append(expires)
		     .append(SEPARATOR)
		     .append(signature);
		String token = sbToken.toString();
		
		return new Token(token, expires);
		

    }

    public String computeSignature(String details, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(details).append(SEPARATOR);
        signatureBuilder.append(expires).append(SEPARATOR);
        signatureBuilder.append(secretKey);

        //TODO: Use javax.crypto.Mac instead of MD5
        // https://github.com/Robbert1/boot-stateless-auth/blob/master/src/main/java/com/jdriven/stateless/security/TokenHandler.java
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(SPLITTER);
        return fromBase64(parts[0]);
    }
    
    public String getPasswordFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(SPLITTER);
        return decrypt(parts[1]);
    }
    
	public Collection<GrantedAuthority> getGrantedAuthoritiesFromToken(String authToken) {
    	if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(SEPARATOR);
        String role = fromBase64(parts[2]);
    	List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
    	grantedAuthorities.add(new SimpleGrantedAuthority(role));
        
        return grantedAuthorities;
    }

    public boolean validateToken(String authToken) {
        String[] parts = authToken.split(SPLITTER);
        if (parts.length != 5) {
        	return false;
        }
        
        final StringBuilder sbSubject = new StringBuilder(170);
		sbSubject.append(parts[0])
		         .append(SEPARATOR)
 		         .append(parts[1])
		         .append(SEPARATOR)
		         .append(parts[2]);
		String subject = sbSubject.toString();  

        long expires = Long.parseLong(parts[3]);
        
		String signature = parts[4];
        String signatureToMatch = computeSignature(subject, expires);
        return expires >= System.currentTimeMillis() && signature.equals(signatureToMatch);
    }
    
	private String toBase64(String content) {
		return Base64.getUrlEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
	}
	
	private String fromBase64(String content) {
		return new String(Base64.getUrlDecoder().decode(content), StandardCharsets.UTF_8);
	}
	
	private String encrypt(String content) {
		String encrypted = null;
		try {
			encrypted = textEncryptor.encrypt(content);
		} catch (KeyczarException e) {
			log.error("Error encryting text");
			e.printStackTrace();
		}
		return encrypted;
	}
	
	private String decrypt(String content) {
		String decrypted = null;
		try {
			decrypted = textEncryptor.decrypt(content);
		} catch (KeyczarException e) {
			log.error("Error decryting text");
			e.printStackTrace();
		}
		return decrypted;
	}
}
