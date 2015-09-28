
package directtranslation;

import com.esri.mo2.src.sys.BaseFolder;
import com.esri.mo2.src.sys.Content;
import com.esri.mo2.src.sys.Folder;

import java.io.File;
import java.io.IOException;


/**
 * Within the artificial content hierarchy, this class represents
 * the folder that has the content in it.
 */
class TranslationFolder extends BaseFolder {

  private File _dir;
  private String _details;

  TranslationFolder(File dir, String details) {
    _dir = dir;
    _details = details;
  }

  public Content[] getContents() {
    //return one content for each .ptxt file in the folder
    File[] files = _dir.listFiles(new PtxtFileFilter());
    Content[] returnArray = new Content[files.length];
    for (int i= 0; i< files.length; i++) {
      returnArray[i] = new TranslationContent(files[i], _details);
    }
    return returnArray;
  }

  public String getName() {
    return "PTXTFolder";
  }


  class PtxtFileFilter implements java.io.FileFilter {
    private static final String _suffix = ".ptxt";

    public boolean accept (File path) {
      String name = path.getName();
      if (name.length() <= 5) {
        return false;
      } else if (name.substring(name.length()-5).equalsIgnoreCase(_suffix)) {
        return true;
      } else {
        return false;
      }
    }

  }

}
