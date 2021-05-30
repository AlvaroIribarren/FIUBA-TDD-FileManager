package fiuba.tecnicas.msfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NodeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public NodeNotFoundException(Long id) {
        super(String.format("The node %d is missing", id != null ? id : "root"));
    }
}
