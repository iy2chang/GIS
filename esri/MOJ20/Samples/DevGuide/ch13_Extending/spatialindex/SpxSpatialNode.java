
package spatialindex;

public class SpxSpatialNode extends com.esri.mo2.data.spx.SpatialNode {

  protected int mFid;

  //Constructor
  public SpxSpatialNode 
      (double xmin, double ymin, double xmax, double ymax, int fid){
    super (xmin, ymin, xmax, ymax);
    mFid = fid;
  }

  /**
   * For this example data source, the fid property represents
   * the GeoPlace.id property.
   */
  public int getFid() {
    return mFid;
  }


}
