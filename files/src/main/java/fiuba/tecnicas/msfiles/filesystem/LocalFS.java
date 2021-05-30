package fiuba.tecnicas.msfiles.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:application.properties")
public class LocalFS implements AbstractFileSystem {
	private static String baseLocation;
	
	@Value("${filesystem.baselocation}")
	public void setBaseLocation(String base) {
		baseLocation = base;
	}
	
	@Override
	public boolean saveFile(InputStream in, Long id) {
		try {
			File targetFile = new File(this.getFilePath(id));

		    FileUtils.copyInputStreamToFile(in, targetFile);
		    
		    return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean fileExists(Long id) {
		File f = new File(this.getFilePath(id));
		
		return f.exists() && !f.isDirectory();
	}
	
	@Override
	public Resource getFile(Long id) {
		try {
			Path path = Paths.get(this.getFilePath(id));
			Resource res = new UrlResource(path.toUri());
			
			if (!res.exists() || !res.isFile()) {
				return null;
			}
			
			return res;			
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean deleteFile(Long id) {
		File f = new File(this.getFilePath(id));
		
		if (!f.exists()) {
			return true;
		}
		
		return f.delete();
	}
	
	private String getFilePath(Long id) {
		return String.format("%s/%d", baseLocation, id);
	}

}
