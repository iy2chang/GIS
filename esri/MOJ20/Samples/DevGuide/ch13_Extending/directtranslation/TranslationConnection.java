
package directtranslation;

import com.esri.mo2.src.sys.Connection;
import com.esri.mo2.src.sys.Source;

import java.io.File;
import java.net.URL;

/**
 * Connection to a source that simulates being remote.
 * Even though the data source is local, this Connection implementation
 * treats it as if it were remote.  This illusion of remoteness serves
 * the purpose of showing how to apply the Content API to remote data 
 * sources without forcing you to set up any special server-side software.
 * <p>
 * This Connection creates an artificial content hierarchy for the
 * purpose of showing how a content hierarchy is created.
 * The Source it creates exposes a single root Folder, which has one
 * additional level of folders.  This second level of folders contains
 * two folders, one empty, and one populated with all the .ptxt files 
 * that exist in the directory that was passed to the Connection object.
 * <p>
 * As connection information, this Connection requires the full path to 
 * a directory that has one or more .ptxt files in it.
 */
public class TranslationConnection implements Connection {

  private File _ptxtDir;

  /**
   * Default constructor required for obtaining content via an access string.
   * If you use this constructor, nothing will be defined until after you
   * call <code>setConnection</code>.
   */
  public TranslationConnection() {
    //does nothing
  }

  /**
   * Constucts a TranslationConnection using a URL.  Note that
   * the URL parameter <b>must</b> point to a valid file system directory
   * that contains one or more .ptxt files.
   * If this class supported a real remote
   * data source (instead of this simulation of one) then the URL would
   * need to point to the remote data server.
   * @param ptxtUrl a "file://" url pointing to a directory.
   */
  public TranslationConnection(URL ptxtUrl) {
    _ptxtDir = new File(ptxtUrl.getPath());
    if (! _ptxtDir.exists()) {
      throw new IllegalArgumentException(ptxtUrl.toString() + " does not exist");
    }

  }

  /**
   * Populates the connection.
   * @see #toString
   */
  public void setConnection(String details) {
    _ptxtDir = new File(details);
  }

  /**
   * Generates a connection string that can be used to create an
   * exact copy of this connection.
   * @return a connection string that is in the form of a full file system
   * path to a ptxt directory.
   */
  public String toString() {
    return _ptxtDir.getAbsolutePath();
  }

  public Source constructSource() {
    return new TranslationSource(_ptxtDir, this.toString());
  }

  public boolean equals(Object object) {
    boolean returnVal = false;
    if (object == this) {
      returnVal = true;
    } else if (object instanceof TranslationConnection) {
      if (object.toString().equals(this.toString())) {
        returnVal = true;
      }
    }
    return returnVal;
  }

  public int hashCode() {
    return this.toString().hashCode();
  }

  /**
   * Returns a name that is appropriate for display in a UI.
   * This method gets called from the associated ContentProvider's
   * getConnectionName method
   */
  public String getShortName () {
    return _ptxtDir.getName();
  }

}
