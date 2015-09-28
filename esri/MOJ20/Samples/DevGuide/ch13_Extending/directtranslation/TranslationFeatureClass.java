
package directtranslation;

import com.esri.mo2.cs.geom.*;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.util.EmptyCursor;
import com.esri.mo2.util.IteratorCursor;

import thirdparty.GeoPlace;
import thirdparty.RemoteTextData;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.*;

/**
 * Illustrates the direct translation strategy for loading custom sources
 * of data.  This strategy works best for remote data sources that have
 * built in abilities to search and query the data.  As a result, this
 * class does not try to manage the data in any way.  Instead, it translates
 * MO Java requests into terms understood by the data source, then translates
 * results from the data source back into MO Java terms.  
 */
public class TranslationFeatureClass extends BaseFeatureClass {

  private RemoteTextData _data;
  private String _name;
  private BaseFields _fields;

  //Constructor
  public TranslationFeatureClass(File sourceFile) {
    super();
    _name = sourceFile.getAbsolutePath();
    try {
      loadData(sourceFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Identifiable getData(Comparable oid) {
    Identifiable returnID = super.getData(oid);
    if (returnID != null) {
      return returnID;
    } else {
      //get the place from the source
      GeoPlace tempPlace = _data.getPlaceFromID(((DataID)oid).getID());
      //translate the place to a feature
      return getFeature(tempPlace, (DataID)oid);
    }
  }


  public Cursor searchEnvelope(Envelope env, QueryFilter query) throws Exception {

    Fields queriedFields;  //fields requested by the query
    Fields myFields;       //all the fields known by this featureclass
    Fields returnFields;   //fields that are both known and requested
    
    queriedFields = query.getSubFields();
    myFields = getFields(); //same as using _fields

    if (queriedFields == null || queriedFields.size() == 0) {
      //null or empty fields means use all available
      returnFields = myFields;
    } else {
      //intersect requested and known fields
      // (in other words, ignore fields that not known by this featureclass)
      returnFields = BaseFields.intersect(queriedFields, myFields);
    }

    //delegate the search to the thirdparty api
    GeoPlace[] allPlaces = _data.getPlacesFromExtent(env);
    if (allPlaces.length == 0) {
      //no features fall within the envelope
      return new EmptyCursor();
    }
    //Convert resulting places to features
    ArrayList returnList = new ArrayList();
    BaseFeature tempFeat;
    for (int i = 0; i < allPlaces.length; i++) {
      DataID id = new BaseDataID(_name, allPlaces[i].id);
      returnList.add(getFeature(allPlaces[i], id, returnFields));
    }
    return new IteratorCursor(returnList.iterator());
  }


  /*
   * Sets up the third party api to handle the specified file.
   * This should only be called once (from the constructor)
   */
  private void loadData(File file) throws IOException {
    initFields();
    _data = new RemoteTextData(file);
    //Call setFields, setFeatureType, and setExtent
    setFields(_fields);
    setFeatureType(MapDataset.POINT);
    java.awt.geom.Rectangle2D.Double rect = _data.getExtent();
    setExtent(new Envelope (rect.x, rect.y, rect.width, rect.height));
    setName(file.getName());
  }

  /*
   * Sets up the Fields collection we will be using from time to time.
   * This should only be called once (from the constructor)
   * Note that this data source has a hardcoded list of fields.  In a
   * more general case, this method would check with the third party api
   * to discover what fields are supported by the data.
   */
  private void initFields() {
    _fields = new BaseFields(2);
    _fields.addField(Field.ShapeField);
    _fields.addField(new BaseField("Name", java.sql.Types.VARCHAR, 0, 0));
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

  private Feature getFeature(GeoPlace place, DataID id, Fields fields) {
    BaseFeature feat = new BaseFeature();
    feat.setFields(fields);
    feat.setDataID(id);
    Point point = new Point(place.x, place.y);
    String fieldName;
    for (int i = 0; i < fields.size();i++) {
      if (fields.getField(i).getName().equals("Name")) {
        feat.setValue(1, place.name);
      } else if (fields.getField(i).isShape()) {
        feat.setValue(0, point);
      } else {
        //This will never occur as long as the fields parameter is the
        // result of a call to BaseFields.intersect (perfomed by calling code)
        throw new IllegalArgumentException("Requested Field not in dataset");
      }
    }
    return feat;
  }

}
