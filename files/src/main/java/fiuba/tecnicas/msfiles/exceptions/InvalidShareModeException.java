package fiuba.tecnicas.msfiles.exceptions;

public class InvalidShareModeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public InvalidShareModeException(Character mode) {
		super(String.format("%c isn't a valid share mode", mode));
	}
}
