package fiuba.tecnicas.plugins.extras.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import fiuba.tecnicas.plugins.extras.domain.UserProfile;
import fiuba.tecnicas.plugins.extras.exception.ResourceNotFoundException;
import fiuba.tecnicas.plugins.extras.repositories.UserProfileRepository;
import fiuba.tecnicas.plugins.extras.repositories.UserRepository;
import fiuba.tecnicas.plugins.extras.utils.JwtTokenUtil;
import fiuba.tecnicas.plugins.extras.utils.User;
import fiuba.tecnicas.plugins.extras.utils.Util;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@RestController
@CrossOrigin
@RequestMapping
public class UserProfileController {
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User getUserFromToken(String bearer) throws RuntimeException {
		if (bearer == null) {
			throw new RuntimeException("Missing token");
		}
		
		if (!bearer.startsWith("Bearer ") ) {			
			throw new RuntimeException("Malformed token");
		}
		
		String token = bearer.replace("Bearer ", "");
		
		JwtTokenUtil jtu = new JwtTokenUtil(this.userRepository);
		try {
			String email = jtu.getEmailFromToken(token);
			
			return this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid token"));
		}
		//ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
		catch (ExpiredJwtException e) {
			throw new RuntimeException("Token expired");
		}
		catch (SignatureException e) {
			throw new RuntimeException("Malformed or invalid token");
		}
		catch (MalformedJwtException e) {
			throw new RuntimeException("Invalid token");			
		}
		
	}
	
	@GetMapping("/usersprofile/{id}")
	public UserProfile retrieveById(@PathVariable(value = "id") Long userId) {
		return userProfileRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "id", userId));
	}
	
	@PostMapping("/usersprofile")
	public ResponseEntity<Map<String, Object>> newUserProfile(@RequestBody UserProfile userProfile, @RequestHeader String token) {
		User requestUser = this.getUserFromToken(token);
		User retrievedUser = userRepository.findById(userProfile.getUserId())
	            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userProfile.getUserId()));
		
		userProfileRepository.save(userProfile);

		return new ResponseEntity<>(Util.makeMap("OK", "User Profile created"), HttpStatus.OK);
	}
	
	@PutMapping("/usersprofile/{id}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@PathVariable(value = "id") Long id, @RequestBody UserProfile userProfile,
										@RequestHeader String token) {
		User requestUser = this.getUserFromToken(token);
		
		User retrievedUser = userRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		if (!requestUser.getEmail().matches(retrievedUser.getEmail())) {
			return new ResponseEntity<>(Util.makeMap("error", "Emails do not match"), HttpStatus.FORBIDDEN);
		}
		
		UserProfile retrievedUserProfile = userProfileRepository.findById(retrievedUser.getId())
	            .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", retrievedUser.getId()));

    	retrievedUserProfile.setName(userProfile.getName());
    	retrievedUserProfile.setSurename(userProfile.getSurename());
    	retrievedUserProfile.setBirthdate(userProfile.getBirthdate());
    	retrievedUserProfile.setCountry(userProfile.getCountry());
    	retrievedUserProfile.setAddress(userProfile.getAddress());
    	retrievedUserProfile.setPhone(userProfile.getPhone());

	    userProfileRepository.save(retrievedUserProfile);

	    return new ResponseEntity<>(Util.makeMap("OK", "User Profile updated"), HttpStatus.OK);
    }
	
	@DeleteMapping("/usersprofile/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long id, @RequestHeader String token) {
		User requestUser = this.getUserFromToken(token);
		
		User user = userRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		UserProfile userProfile = userProfileRepository.findById(user.getId())
	            .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", user.getId()));

	    userProfileRepository.delete(userProfile);

	    return ResponseEntity.ok().build();
	}

}
