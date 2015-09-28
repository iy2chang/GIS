
package spatialindex;

import com.esri.mo2.cs.geom.*;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.data.spx.BaseTree;
import com.esri.mo2.util.EmptyCursor;

import thirdparty.GeoPlace;
import thirdparty.RemoteTextData;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Illustrates the spatial index strategy for loading custom sources of data.
 */
public class SpxFeatureClass extends BaseFeatureClass {

  private RemoteTextData _data;
  private BaseTree index;
  private BaseFields _fields;


  public Identifiable getData(Comparable oid) {
    Identifiable returnID = super.getData(oid);
    if (returnID != null) {
      return returnID;
    } else {
      //get the place from the source and translate it to a feature
      GeoPlace tempPlace = _data.getPlaceFromID(((DataID)oid).getID());
      return getFeature(tempPlace, (DataID)oid);
    }
  }


  public Cursor searchEnvelope
      (Envelope env, QueryFilter query) throws Exception {

    Fields queriedFields;  //fields the query requests
    Fields myFields;       //all the fields known by this featureclass
    Fields returnFields;   //fields that are both known and requested

    queriedFields = query.getSubFields();
    myFields = getFields();

    if (queriedFields == null || queriedFields.size() == 0) {
      //null or empty fields means use all available
      returnFields = myFields;
    } else {
      //intersect requested and known fields
      // (in other words, ignore fields that not known by this featureclass)
      returnFields = BaseFields.intersect(queriedFields, myFields);
    }

    //delegate teh search to the spatial index
    Iterator theList = index.search(env);
    SpxCursor retCursor = new SpxCursor(_data, theList, returnFields);
    return retCursor;
  }


  //Constructor
  public SpxFeatureClass(File sourceFile) {
    super();
    try {
      loadData(sourceFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /*
   * Sets up the third party objects to handle the specified file, as well
   * as the spatial index used to access those objects.
   * This should only be called once (from the constructor)
   */
  private void loadData(File file) throws IOException {
    initFields();
    _data = new RemoteTextData(file);

    //set up the spatial index
    SpxSpatialNode node;
    GeoPlace place;
    index = new BaseTree();
    Iterator allPlaces = _data.listAll();
    while (allPlaces.hasNext()) {
      place = (GeoPlace)allPlaces.next();
      node = new SpxSpatialNode(place.x, place.y, place.x, place.y, place.id);
      index.insert(node);
    }
    
    //initialize the feature class
    setFields(_fields);
    setFeatureType(MapDataset.POINT);
    java.awt.geom.Rectangle2D.Double rect = _data.getExtent();
    setExtent(index.getEnvelope());
    setName("SpxFeatureClass: " + file.getName());

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

}
