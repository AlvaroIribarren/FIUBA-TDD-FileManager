package fiuba.tecnicas.msfiles.exceptions;

public class FileSystemException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public FileSystemException(String message) {
		super(message);
	}
}
