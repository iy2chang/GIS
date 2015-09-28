
package memoryfeature;

import com.esri.mo2.src.file.FileHandler;
import com.esri.mo2.src.sys.Content;
import com.esri.mo2.src.sys.Folder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class MemoryFileHandler implements FileHandler {

	public String[] getAvailableContentTypes() {
		return new String[0];
	}

	public String getContentDescription(String type) {
		return null;
	}

	public Content createContent(Folder folder, String name, String type) throws java.io.IOException {
		return null;
	}

	public void filter(File[] list, LinkedList contents) {
		String suffix = ".ptxt";
		
		for (int i = 0; i < list.length; i++) {
			File candidate = list[i];
			if (candidate != null) {
				String path = candidate.getAbsolutePath();
				if (path.length() > 5) {
					String extension = path.substring(path.length()-5);
					if (extension.equalsIgnoreCase(suffix)) {
						//Create the content and put it in the list, then consume the file
						Content content = new MemoryPtxtContent(candidate);
						contents.add(content);
						list[i] = null;
					}
				}
			}
		}
	}

}

