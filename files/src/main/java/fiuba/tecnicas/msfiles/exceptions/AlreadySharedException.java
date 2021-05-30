package fiuba.tecnicas.msfiles.exceptions;

public class AlreadySharedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AlreadySharedException(Long nodeId, Long userId) {
		super(String.format("Node %d is already shared with %d", nodeId, userId));
	}
}
