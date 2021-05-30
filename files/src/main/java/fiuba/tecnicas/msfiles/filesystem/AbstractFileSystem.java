package fiuba.tecnicas.msfiles.filesystem;

import java.io.InputStream;

import org.springframework.core.io.Resource;

public interface AbstractFileSystem {
	public boolean saveFile(InputStream in, Long id);
	
	public boolean fileExists(Long id);
	
	public Resource getFile(Long id);
	
	public boolean deleteFile(Long id);
}
