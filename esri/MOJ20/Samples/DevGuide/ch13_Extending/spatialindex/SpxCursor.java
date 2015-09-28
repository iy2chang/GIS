
package spatialindex;

import com.esri.mo2.cs.geom.Point;
import com.esri.mo2.data.feat.BaseDataID;
import com.esri.mo2.data.feat.BaseFeature;
import com.esri.mo2.data.feat.DataID;
import com.esri.mo2.data.feat.Feature;
import com.esri.mo2.data.feat.Fields;

import thirdparty.GeoPlace;
import thirdparty.RemoteTextData;

import java.util.Iterator;
import java.util.Set;

public class SpxCursor extends com.esri.mo2.data.feat.BaseCursor {

  private Iterator _iterator; //contains only SpxSpatialNode
  private RemoteTextData _dataSource;
  private Fields _fields;
  private String _name;

  //Constructor
  public SpxCursor (RemoteTextData source, Iterator iterator, Fields fields) {
    _dataSource = source;
    _iterator = iterator;
    _fields = fields;
    _name = source.getName();
    peek();
  }

  protected void peek() {
    //simply update the value of _next
    if (_iterator.hasNext()) {
      SpxSpatialNode node = (SpxSpatialNode)_iterator.next();
      GeoPlace place = _dataSource.getPlaceFromID(node.getFid());
      _next = getFeature
          (place, new BaseDataID(_name, place.id), _fields);
    } else {
      _next = null;
    }
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
