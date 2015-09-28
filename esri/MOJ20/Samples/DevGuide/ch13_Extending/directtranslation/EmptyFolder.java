

package directtranslation;

import com.esri.mo2.src.sys.BaseFolder;
import com.esri.mo2.src.sys.Content;


/**
 * Within the artificial content hierarchy, this class represents an
 * empty folder.  It's purpose in this sample is simply to illustrate
 * that the hierarchy can, if neeed, contain different types of
 * Folder implementations.
 */
class EmptyFolder extends BaseFolder {


  //constructor
  EmptyFolder() {
  }

  public Content[] getContents() {
    return new Content[0];
  }

  public String getDescription() {
    return "Empty Folder";
  }

  public String getName() {
    return "EmptyFolder";
  }

}
