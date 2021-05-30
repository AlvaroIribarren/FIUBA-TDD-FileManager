package fiuba.tecnicas.auth.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fiuba.tecnicas.auth.domain.Usuario;
import fiuba.tecnicas.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	@Autowired
    UserRepository userRepository;

	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY = 15 * 60;

	public static final long JWT_REFRESH_TOKEN_VALIDITY = 365 * 24 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;

	//retrieve email from jwt token
	public String getEmailFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
    //for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	//check if the token has expired
	public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	//generate token for user
	public String generateToken(Usuario user) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, user.getEmail(),
			new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000));
	}

	public String generateRefreshToken(Usuario user) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, user.getEmail(),
			new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY * 1000));
	}

	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string 
	private String doGenerateToken(Map<String, Object> claims, String subject, Date expiration) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	//validate token
	public Boolean validateToken(String token, Usuario user) {
		final String username = getEmailFromToken(token);
		return (username.equals(user.getEmail()) && !isTokenExpired(token));
	}

	public Map<String, Object> generateTokens(Usuario user) {
		String token = this.generateToken(user);
		String refreshToken = this.generateRefreshToken(user);

		user.setRefreshToken(refreshToken);
		userRepository.save(user);

		Map<String, Object> tokens = new HashMap<>();
		tokens.put("token", "Bearer " + token);
		tokens.put("refreshToken", "Bearer " + refreshToken);

		return tokens;
	}

	public String getTokenValue(String header) {
		
		return header.split(" ")[1];
	}
}
