package fiuba.tecnicas.msfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DamagedNodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DamagedNodeException(Long nodeId) {
		super(String.format("Can't retrieve node %d", nodeId));
	}
}
