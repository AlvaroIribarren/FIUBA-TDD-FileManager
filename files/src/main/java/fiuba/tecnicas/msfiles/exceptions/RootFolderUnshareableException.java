package fiuba.tecnicas.msfiles.exceptions;

public class RootFolderUnshareableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RootFolderUnshareableException() {
		super("Root folder is unshareable");
	}
}
