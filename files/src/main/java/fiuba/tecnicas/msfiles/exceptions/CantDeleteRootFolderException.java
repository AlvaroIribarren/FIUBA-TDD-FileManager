package fiuba.tecnicas.msfiles.exceptions;

public class CantDeleteRootFolderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public CantDeleteRootFolderException() {
		super("Can't delete root folder");
	}

}
