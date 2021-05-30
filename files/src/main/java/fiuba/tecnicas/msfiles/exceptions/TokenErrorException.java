package fiuba.tecnicas.msfiles.exceptions;

public class TokenErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TokenErrorException(String reason) {
		super(reason);
	}
}
