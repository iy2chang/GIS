
package directtranslation;

import com.esri.mo2.src.sys.BaseSource;
import com.esri.mo2.src.sys.Content;
import com.esri.mo2.src.sys.Source;

import java.io.IOException;
import java.io.File;

public class TranslationSource extends BaseSource {

  private File _ptxtDir;
  private String _connectionDetails;
  private Content[] _rootCon;
  private boolean _connected = false;

  public TranslationSource(File dir, String connectionDetails) {
    _ptxtDir = dir;
    _connectionDetails = connectionDetails;
  }

  /**
   * Returns a single root Folder that represents the main connection.
   */
  public Content[] getRoots() throws IOException {
    return _rootCon;
  }

  /**
   * Populates the Content array that will be returned by getContents
   */
  public void refresh() {
    if (!isConnected()) {
      return;
    } else {
      _rootCon = new Content[1];
      _rootCon[0] = new TranslationRootFolder(_ptxtDir, _connectionDetails);
    }

  }


  //these three connection methods use a boolean variable to simulate the
  // creation and use of a real connection.  Since there is no real 
  // connection, this class could just as easily always return true from
  // the isConnected method and ignore the other two methods.

  public boolean isConnected() {
    return _connected;
  }

  public void connect() {
    _connected = true;
    refresh();
  }

  public void disconnect() {
    _connected = false;
  }
    
}
