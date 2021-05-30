package fiuba.tecnicas.msfiles.exceptions;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException(Long userId) {
		super(String.format("User %d doesn't exists", userId));
	}
}
