package fiuba.tecnicas.msfiles.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fiuba.tecnicas.msfiles.models.User;
import fiuba.tecnicas.msfiles.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    private final UserRepository userRepository;

	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY = 2; //2 SEGUNDOS SOLO PARA TEST

	public static final long JWT_REFRESH_TOKEN_VALIDITY = 365 * 24 * 60 * 60;

	private static String secret;
	@Value("${jwt.secret}")
	public void setSecret(String sec) {
		secret = sec;
	}
	
	public JwtTokenUtil(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
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
		return expiration.before(new Date(System.currentTimeMillis()));
	}

	//generate token for user
	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, user.getEmail(),
			new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000));
	}

	public String generateRefreshToken(User user) {
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
	public Boolean validateToken(String token, User user) {
		final String username = getEmailFromToken(token);
		return (username.equals(user.getEmail()) && !isTokenExpired(token));
	}


	public Map<String, Object> generateTokens(User user) {
		String token = this.generateToken(user);
		String refreshToken = this.generateRefreshToken(user);

		user.setRefreshToken(refreshToken);
		userRepository.save(user);

		Map<String, Object> tokens = new HashMap<>();
		tokens.put("token", token);
		tokens.put("refreshToken", refreshToken);

		return tokens;
	}
}
