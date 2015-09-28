
package directtranslation;

import com.esri.mo2.data.feat.FeatureClass;
import com.esri.mo2.data.feat.MapDataset;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.draw.Util;
import com.esri.mo2.src.sys.BaseContent;
import com.esri.mo2.src.sys.Connection;
import com.esri.mo2.src.sys.Folder;
import com.esri.mo2.src.sys.Properties;
import com.esri.mo2.src.sys.ResourceBundle;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

//NOTE: A developer would never need to directly instantiate this class.
//  The class gets instantiated by a custom class written by you (either a
//  custom Folder or a FileHandler).  Thus, this class does not need to be 
//  public.  It could be an inner class, but is kept separate in this
//  example to make the code easier to read.

class TranslationContent extends BaseContent {

  private static String[] AVAILABLE_CONTENT_TYPES = {
    FeatureClass.class.getName(),
    FeatureLayer.class.getName()
  };

  private File _ptxtFile;
  private String _connectionDetails;
  private TranslationFeatureClass _fclass = null;

  /**
   * Constructs a TranslationContent.
   * @param ptxtFile the data file that will be used to create the FeatureClass
   * @param connectionDetatails the string that can be used to create an exact
   * copy of the TranslationConnection object that resulted in this Content
   */
  protected TranslationContent(File ptxtFile, String connectionDetails) {
    _ptxtFile = ptxtFile;
    _connectionDetails = connectionDetails;
  }

  public Image getIcon() {
    //this data type only supports points
    return ResourceBundle.getDatasetIcon(MapDataset.POINT);
  }

  public String[] getAvailableContentTypes() {
    return AVAILABLE_CONTENT_TYPES;
  }

  public Object getData(String type) throws IOException {
    Object returnVal = null;
    int idx = findContentType(type);
    switch (idx) {
      case 0:
        returnVal = getFClass();
        break;
      case 1:
        returnVal = new BaseFeatureLayer(getFClass(), Util.constructDefaultRenderer(MapDataset.POINT));
        break;
    }
    return returnVal;
  }

  private FeatureClass getFClass() {
    //don't create duplicate featureclass instances
    if (_fclass == null) {
      _fclass = new TranslationFeatureClass(_ptxtFile);
    }
    return _fclass;
  }

  public String getName() {
    return _ptxtFile.getName();
  }

  public Properties getProperties(){
    //This method illustrates the correct way to override the getProperties
    // method.  Note that in a real case, (as opposed to this simplified
    // example), you would only add properties that might be interesting
    // to an end user.
    Properties props = super.getProperties();
    props.put("NumberOfAttributes", "One");
    return props;
  }

  public String getDescription() {
    return "PTXT Point FeatureClass";
  }

}

