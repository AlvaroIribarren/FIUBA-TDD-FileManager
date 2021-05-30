package fiuba.tecnicas.msfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NodeNotPresentException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NodeNotPresentException(Long nodeId) {
		super(String.format("Node %d not present on FileSystem", nodeId));
	}
}
