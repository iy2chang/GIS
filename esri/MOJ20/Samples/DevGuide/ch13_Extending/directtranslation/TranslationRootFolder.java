

package directtranslation;

import com.esri.mo2.src.sys.BaseFolder;
import com.esri.mo2.src.sys.Connection;
import com.esri.mo2.src.sys.Content;
import com.esri.mo2.src.sys.Folder;

import java.io.File;


/**
 * Within the artificial content hierarchy, this class represents
 * the top level folder which contains an EmptyFolder and a
 * TranslationFolder
 */
class TranslationRootFolder extends BaseFolder {

  private File _file;
  private String _details;

  TranslationRootFolder(File file, String details) {
    _file = file;
    _details = details;
  }

  public Content[] getContents() {
    Content[] retVal = new Content[2];
    retVal[0] = new TranslationFolder(_file, _details);
    retVal[1] = new EmptyFolder();
    return retVal;
  }

  public String getName() {
    return "Translation Root";
  }

  protected Connection constructConnection() {
    TranslationConnection connection = new TranslationConnection();
    connection.setConnection(_details);
    return connection;
  }

}

