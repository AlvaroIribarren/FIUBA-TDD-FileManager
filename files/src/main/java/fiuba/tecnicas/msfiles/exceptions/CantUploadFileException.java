package fiuba.tecnicas.msfiles.exceptions;

public class CantUploadFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CantUploadFileException(String reason) {
		super(reason);
	}
}
