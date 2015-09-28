
package thirdparty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * File input support for tab delimited lat/long text files.
 * Exposes the place data for use by applications that don't
 * want to load all the places into memory at once.  Instead,
 * they ask this class to manage data selection and lifetime.
 * <p>
 * While this class does load all the features into memory, it
 * does this only for the sake of simplicity. 
 * From the perspective of the API user, this class exposes
 * results in a way that simulates a remote data source.
 * <p>
 * The files read by this class have three entries per line.
 * The first two entries are latitude and longitude.  The
 * third entry is a string, which is presumed to be a name.
 */
public class RemoteTextData {

  private GeoPlace[] geoPlaces;
  private int currentArrayIndex = 0;
  private String _name;

  /**
   * Constructs an instance of RemoteTextData
   * @param targeFile the file to load
   */
  public RemoteTextData(File targetFile) throws IOException {
    //read the file and process it into local memory
    geoPlaces = loadFile(targetFile);
    _name = targetFile.getName();
    /*
     * For a truely remote source, the constructor would set up and open the
     * connection to the database.  It would probably also load up any
     * indexing information it needed.   It would not load all the
     * features into memory like this one does.
     */
  }

  public String getName() {
    return _name;
  }

  /**
   * Find all the places that fall within a rectangle extent.
   */
  public GeoPlace[] getPlacesFromExtent(java.awt.geom.Rectangle2D.Double rect){
    return getPlacesFromExtent(rect.x, rect.y, rect.x+rect.width, rect.y+rect.height);
  }

  /**
   * Find all the places that fall within an extent defined by two coords.
   */
  public GeoPlace[] getPlacesFromExtent(double xmin, double ymin, double xmax, double ymax) {
    //NOTE: this method uses a brute force approach by looping through
    // all the places, testing each one to see if it is inside the extent.
    // Many sources of data will provide much faster ways to do this.
    // The point is that the third-party API handles it for
    // you so you don't have to do it yourself.

    ArrayList workingList = new ArrayList();
    double x,y;
    GeoPlace place;
    for (int i = 0; i < geoPlaces.length; i++) {
      place = getPlaceFromID(i);
      x = place.x;
      y = place.y;
      if (x >= xmin && x <= xmax && y >= ymin && y <= ymax) {
        workingList.add(place);
      }
    }
    //now translate the list to an array
    GeoPlace[] retArray = new GeoPlace[workingList.size()];
    for (int i = 0; i < workingList.size(); i++) {
      retArray[i] = (GeoPlace)workingList.get(i);
    }
    return retArray;
  }

  /**
   * Find the place represented by an ID
   * returns null if id doesn't represent a known place.
   */
  public synchronized GeoPlace getPlaceFromID(int id) {
    if (id > geoPlaces.length) {
      return null;
    } else {
      return geoPlaces[id];
    }
  }

  /**
   * Finds the bounding rectangle for all places in this source
   * All the places in the array will either be inside the resulting
   * rectangle, or on its border.
   */
  public java.awt.geom.Rectangle2D.Double getExtent() {
    if (geoPlaces.length == 0) {
      return new java.awt.geom.Rectangle2D.Double();
    }
    double minx = geoPlaces[0].x;
    double miny = geoPlaces[0].y;
    double maxx = geoPlaces[0].x;
    double maxy = geoPlaces[0].y;
    for (int i = 0; i < geoPlaces.length; i++) {
      if (geoPlaces[i].x < minx)
        minx = geoPlaces[i].x;
      if (geoPlaces[i].y < miny)
        miny = geoPlaces[i].y;
      if (geoPlaces[i].x > maxx)
        maxx = geoPlaces[i].x;
      if (geoPlaces[i].y > maxy)
        maxy = geoPlaces[i].y;
    }
    return new java.awt.geom.Rectangle2D.Double(minx, miny, maxx-minx, maxy-miny
);
  }


  /**
   * Returns (as an array) all known GeoPlace instances with the name
   * matching nameClause.
   */
  public GeoPlace[] getPlacesFromQuery(String nameClause) {
    ArrayList workingList = new ArrayList();
    String name;
    GeoPlace place;
    for (int i = 0; i < geoPlaces.length; i++) {
      place = getPlaceFromID(i);
      name = place.getName();
      if (name.equals(nameClause)) {
        workingList.add(place);
      }
    }
    //now translate the list to an array
    GeoPlace[] retArray = new GeoPlace[workingList.size()];
    for (int i = 0; i < workingList.size(); i++) {
      retArray[i] = (GeoPlace)workingList.get(i);
    }
    return retArray;
  }

  /**
   * Closes all connections and dispose of resources
   */
  public void dispose() {
    //In this simulation, there is no real connection to close
  }

  /**
   * Get access to all the places in the set.
   * Note that the Iterator returned here COULD be a custom iterator
   * that creates GeoPlace instances on a just-in-time basis.
   * (in this case, it does not, but for a large data set, it would be wise)
   * @return an iterator containing only GeoPlace instances
   */
  public Iterator listAll() {
    return Arrays.asList(geoPlaces).iterator();
  }

  /**
   * Get access to the places in the set that fall within a bounding box.
   * Note that the Iterator returned here COULD be a custom iterator
   * that creates GeoPlace instances on a just-in-time basis.
   * (in this case, it does not, but for a large data set, it would be wise)
   * @return an iterator containing only GeoPlace instances
   */
  public Iterator listFromExtent(java.awt.geom.Rectangle2D.Double bbox) {
    return Arrays.asList(getPlacesFromExtent(bbox)).iterator();
  }

  /**
   * Returns (as an Iterator) all known GeoPlace instances with the name
   * matching nameClause.  The nameClause takes the form of 
   * name='foo'.  In this case, the method would return all
   * places named foo.
   */
  public Iterator listPlacesFromQuery(String nameClause) {
    return Arrays.asList(getPlacesFromQuery(nameClause)).iterator();
  }

  /**
   * Translates the contents of a text file into an array of GeoPlace.
   * @param inFile the file to translate
   * @return an array of GeoPlace, each element of which represents one
   *  line in the text file.
   */
  private GeoPlace[] loadFile (File inFile) throws IOException {

    ArrayList placeList = new ArrayList();
    GeoPlace tempPlace;

    BufferedReader in = new BufferedReader(new FileReader(inFile));

    String line ;
    String[] tokens;
    double x, y;
    int arrayIndex = 0;
    while ((line = in.readLine()) != null) {
      tokens = line.split("\t", 3);
      y = Double.valueOf(tokens[0]).doubleValue();
      x = Double.valueOf(tokens[1]).doubleValue();
      tempPlace = new GeoPlace(x,y, tokens[2], arrayIndex++);
      placeList.add(tempPlace);
    }
    in.close();

    //turn the collection into an array and return it
    GeoPlace[] retPlaces = new GeoPlace[placeList.size()];
    for (int i = 0; i< placeList.size(); i++) {
      retPlaces[i] = (GeoPlace)placeList.get(i);
    }
    return retPlaces;
  }

}
