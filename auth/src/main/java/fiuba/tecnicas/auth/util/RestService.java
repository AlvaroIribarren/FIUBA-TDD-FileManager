package fiuba.tecnicas.auth.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    private final RestTemplate restTemplate;


    public RestService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean validateGoogleToken(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken;

        ResponseEntity<Object> response = this.restTemplate.getForEntity(url, Object.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                return true;
            } else {
                return false;
        }
    }
}
