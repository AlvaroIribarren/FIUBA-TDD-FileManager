package fiuba.tecnicas.msfiles.exceptions;

public class InvitationNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvitationNotFoundException(Long userId, Long nodeId) {
		super(String.format("Invitation for user %d to collaborate in node %d wasn't found", userId, nodeId));
	}
}
