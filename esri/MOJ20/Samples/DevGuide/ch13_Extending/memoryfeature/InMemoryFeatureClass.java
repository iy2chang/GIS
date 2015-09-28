
package memoryfeature;

import thirdparty.GeoPlace;
import thirdparty.LocalTextData;

import com.esri.mo2.cs.geom.*;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.mem.MemoryFeatureClass;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.*;


/**
 * Loads ptxt data into a feature class.
 * Note that you can either extend MemoryFeatureClass, like this class does,
 * or create a factory class that instantiates MemoryFeatureClass directly
 * and populates it from the data source.  
 * In an effort to make this sample easier to read, this class takes the
 * first approach by extending MemoryFeatureClass.
 * It would also work to have the MemoryPtxtContent class serve as a 
 * MemoryFeatureClass factory.
 */
public class InMemoryFeatureClass extends MemoryFeatureClass {

  private static BaseFields _fields;
  static {
    _fields = new BaseFields(2);
    _fields.addField(Field.ShapeField);
    _fields.addField(new BaseField("Name", java.sql.Types.VARCHAR, 0, 0));
  }

  //Constructor
  public InMemoryFeatureClass(File sourceFile) {
    super(MapDataset.POINT, _fields);
    try {
      loadData(sourceFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  

  /*
   * Loads the data from a file into memory for later use.
   */
  private void loadData(File file) throws IOException {
    //Load the information from the file and translate it into Features
    LocalTextData loader = new LocalTextData(file);
    GeoPlace tempPlace;
    Feature tempFeature;
    int idcounter = -1; //so first one goes in slot zero of the array
    while (loader.hasNext()) {
      tempPlace = loader.next();
      //MARKZ use the File object to get the full path to the data for the name
      String name = file.getAbsolutePath();
      tempFeature = getFeature(tempPlace, new BaseDataID(name, idcounter++));
      //Note that in this example, the id for this basedataid is not obtained
      // directly from the data source.   Rather, we generate it arbitrarily.
      // If your data source has some sort of built-in id, use that instead.
      addFeature(tempFeature);
    }

    //Call required init methods
    setName(file.getName());
  }

  /**
   * Converts a single GeoPlace to a Feature, with a specified DataID
   */
  private Feature getFeature(GeoPlace place, DataID id) {
    BaseFeature feat = new BaseFeature();
    feat.setFields(_fields);
    Point point = new Point(place.x, place.y);
    feat.setValue(0, point);
    feat.setValue(1, place.name);
    feat.setDataID(id);
    return feat;
  }


}
