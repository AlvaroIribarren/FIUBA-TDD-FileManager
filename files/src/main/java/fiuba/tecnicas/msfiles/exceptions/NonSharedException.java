package fiuba.tecnicas.msfiles.exceptions;

public class NonSharedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NonSharedException(Long nodeId, Long userId) {
		super(String.format("Node %d is not shared with %d", nodeId, userId));
	}
}
