package fiuba.tecnicas.msfiles.exceptions;

public class MissingParameterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MissingParameterException(String parameter) {
		super(String.format("%s is required"));
	}
}
