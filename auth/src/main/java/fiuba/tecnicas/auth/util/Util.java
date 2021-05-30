package fiuba.tecnicas.auth.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import fiuba.tecnicas.auth.domain.Usuario;
import fiuba.tecnicas.auth.repository.UserRepository;

public class Util {
    
    public static boolean isGuest(Authentication authentication) {
        return Objects.isNull(authentication) || authentication instanceof AnonymousAuthenticationToken;
    }

    public static Usuario getUserFromAuthentication(Authentication authentication,
                                                     UserRepository userRepository) {
        return (userRepository.findByEmail(authentication.getName()));
    }

    public static Map<String, Object> makeMap(String string, Object object) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(string, object);
        return map;
    }

    public static ResponseEntity<Map<String, Object>> makeOkResponse(Usuario user, JwtTokenUtil jwtTokenUtil) {

        Map<String, Object> tokens = jwtTokenUtil.generateTokens(user);

        Map<String, Object> responseBody = Util.makeMap("id", user.getId());

        HttpHeaders responseHeader = new HttpHeaders();

        responseHeader.add("Access-Control-Expose-Headers", "token, refreshToken");
        responseHeader.add("token", (String) tokens.get("token"));
        responseHeader.add("refreshToken", (String) tokens.get("refreshToken"));

        return ResponseEntity.ok().headers(responseHeader).body(responseBody);
    }
}
