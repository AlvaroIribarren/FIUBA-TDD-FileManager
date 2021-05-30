package fiuba.tecnicas.auth.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fiuba.tecnicas.auth.domain.GoogleUserInfo;
import fiuba.tecnicas.auth.domain.LoginForm;
import fiuba.tecnicas.auth.domain.Usuario;
import fiuba.tecnicas.auth.repository.UserRepository;
import fiuba.tecnicas.auth.util.JwtTokenUtil;
import fiuba.tecnicas.auth.util.RestService;
import fiuba.tecnicas.auth.util.Util;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AuthController {

    @Autowired
	private JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> simpleLogin(@RequestBody LoginForm login) {

        String email = login.getEmail();

        String password = login.getPassword();

        Usuario user = userRepository.findByEmail(email);

        if (Objects.isNull(user)) {
            return new ResponseEntity<>(Util.makeMap("error", "Authentication failed"), HttpStatus.FORBIDDEN);
        };

        if (!passwordEncoder.matches(password, user.getPassword()) || !user.getRegistrado()) {
            return new ResponseEntity<>(Util.makeMap("error", "Authentication failed"), HttpStatus.FORBIDDEN);
        };

		return Util.makeOkResponse(user, jwtTokenUtil);
    }

    @PostMapping("/login/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleUserInfo googleUserInfo) {

        RestService restService = new RestService();

        if (!restService.validateGoogleToken(googleUserInfo.getToken())) {

            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        }

        String email = googleUserInfo.getEmail();

        Usuario user = userRepository.findByEmail(email);
        if (user == null) {

            user = new Usuario(googleUserInfo.getName(), email, "");

            user.setRegistrado(false);

            userRepository.save(user);
        }

        return Util.makeOkResponse(user, jwtTokenUtil);
    }

    @GetMapping("refreshToken")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader String refreshToken) {

        refreshToken = jwtTokenUtil.getTokenValue(refreshToken);

        String email;

        try {
            email = jwtTokenUtil.getEmailFromToken(refreshToken);
        } catch (Exception e) {
            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        }

        Usuario user = userRepository.findByEmail(email);

        if (Objects.isNull(user)) {
            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        };

        if (jwtTokenUtil.isTokenExpired(refreshToken)) {
            return new ResponseEntity<>(Util.makeMap("error", "Refresh token expired, log in again"), HttpStatus.FORBIDDEN);
        };

        if (Objects.isNull(user.getRefreshToken()) || !user.getRefreshToken().equals(refreshToken)) {
            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        };

        return Util.makeOkResponse(user, jwtTokenUtil);
    }

    @PostMapping("logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader String refreshToken) {

        refreshToken = jwtTokenUtil.getTokenValue(refreshToken);

        String email;

        try {
            email = jwtTokenUtil.getEmailFromToken(refreshToken);
        } catch (Exception e) {
            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        }

        Usuario user = userRepository.findByEmail(email);

        if (Objects.isNull(user)) {
            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        };

        if (Objects.isNull(user.getRefreshToken()) || !user.getRefreshToken().equals(refreshToken)) {
            return new ResponseEntity<>(Util.makeMap("error", "Invalid token"), HttpStatus.FORBIDDEN);
        };

        user.invalidateRefreshToken();

        userRepository.save(user);

        return new ResponseEntity<>(Util.makeMap("OK", "Logged out"), HttpStatus.OK);
    }
}


