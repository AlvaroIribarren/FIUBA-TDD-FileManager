package fiuba.tecnicas.auth.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fiuba.tecnicas.auth.domain.Usuario;
import fiuba.tecnicas.auth.exception.ResourceNotFoundException;
import fiuba.tecnicas.auth.repository.UserRepository;
import fiuba.tecnicas.auth.util.JwtTokenUtil;
import fiuba.tecnicas.auth.util.Util;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
    private UserRepository userRepository;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@GetMapping("/users")
	public ResponseEntity<Map<String, Object>> retrieveAll(@RequestHeader String token) {

		token = jwtTokenUtil.getTokenValue(token);

		try {
			jwtTokenUtil.isTokenExpired(token);
		} catch (ExpiredJwtException e) {
			
			return new ResponseEntity<>(Util.makeMap("error", "Token expired"), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(Util.makeMap("users", userRepository.findAll()), HttpStatus.OK);
	}
	
	@PostMapping("/users")
	public ResponseEntity<Map<String, Object>> newUser(@RequestBody Usuario user) {
		
		if (!Objects.isNull(userRepository.findByEmail(user.getEmail()))) {

			return new ResponseEntity<>(Util.makeMap("error", "User already exists"), HttpStatus.FORBIDDEN);
		}

		user.setRegistrado(true);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		//El usuario se guarda en userRepository en este metodo
		return Util.makeOkResponse(user, jwtTokenUtil);
	}
	
	@GetMapping("/users/{id}")
	public Usuario retrieveById(@PathVariable(value = "id") Long id) {
		return userRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}
	
	@PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable(value = "id") Long id, @RequestBody Usuario user,
										@RequestHeader String token) {
		Usuario retrievedUser = userRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		String email;

		token = jwtTokenUtil.getTokenValue(token);

		try {
			email = jwtTokenUtil.getEmailFromToken(token);
		} catch (ExpiredJwtException e) {
			
			return new ResponseEntity<>(Util.makeMap("error", "Token expired"), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
		}

		if (!email.matches(user.getEmail())) {
			return new ResponseEntity<>(Util.makeMap("error", "Emails do not match"), HttpStatus.FORBIDDEN);
		}

		retrievedUser.setName(user.getName());
		retrievedUser.setEmail(user.getEmail());
		retrievedUser.setPassword(user.getPassword());

	    userRepository.save(retrievedUser);

	    return new ResponseEntity<>(Util.makeMap("OK", "User updated"), HttpStatus.OK);
    }
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long id, @RequestHeader String token) {
		Usuario user = userRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		String email;

		token = jwtTokenUtil.getTokenValue(token);

		try {
			email = jwtTokenUtil.getEmailFromToken(token);
		} catch (ExpiredJwtException e) {
			
			return new ResponseEntity<>(Util.makeMap("error", "Token expired"), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
		}

		if (!email.matches(user.getEmail())) {
			return new ResponseEntity<>(Util.makeMap("error", "Emails do not match"), HttpStatus.FORBIDDEN);
		}

	    userRepository.delete(user);

	    return ResponseEntity.ok().build();
	}

}

