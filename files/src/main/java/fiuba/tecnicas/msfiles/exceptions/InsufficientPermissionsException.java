package fiuba.tecnicas.msfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fiuba.tecnicas.msfiles.models.sharing.ShareModes;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InsufficientPermissionsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InsufficientPermissionsException(Long nodeId, ShareModes mode) {
		super(String.format(mode.equals(ShareModes.READ) ? "You don't have access to the node %d" : "You must be the owner of node %d to modify it", nodeId));		
	}
}
