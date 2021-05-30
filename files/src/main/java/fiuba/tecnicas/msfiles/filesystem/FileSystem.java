package fiuba.tecnicas.msfiles.filesystem;

import org.springframework.beans.factory.annotation.Value;

public class FileSystem {
	@Value("${filesystem.classname}")
	private static String configuredFileSystem;
	private static AbstractFileSystem fileSystemInstance;
	
	public static AbstractFileSystem getFileSystem() {
		if (fileSystemInstance == null) {
			/*try {
				fileSystem = (AbstractFileSystem) AbstractFileSystem.class.getClassLoader().loadClass(configuredFileSystem).asSubclass(AbstractFileSystem.class).newInstance();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}*/
			fileSystemInstance = new LocalFS();
		}
		
		return fileSystemInstance;
	}
}
