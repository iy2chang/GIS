
package thirdparty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * File input support for tab delimited lat/long text files.
 * Exposes the place data for use by applications that
 * want to load all the places into memory at once.
 * <p>
 * For convenience, also exposes the data in an iterator-like form.
 * <p>
 * The files read by this class have three entries per line.
 * The first two entries are latitude and longitude.  The
 * third entry is a string, which is presumed to be a name.
 */
public class LocalTextData {

  private GeoPlace[] geoPlaces;
  private int currentArrayIndex = 0;


  /**
   * Constructs an instance and loads the information from file.
   * @param targetFile the ASCII file from which to load data.
   */
  public LocalTextData(File targetFile) throws IOException {
    geoPlaces = loadFile(targetFile);
  }

  /**
   * Translates the contents of a text file into an array of GeoPlace.
   * @param inFile the file to translate
   * @return an array of GeoPlace, each element of which represents one
   *  line in the text file.
   */
  private GeoPlace[] loadFile (File inFile) throws IOException {
    //Open the file.  Read each line into a geoplace and add to a collection.
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

    //Turn the collection into an array and return it
    GeoPlace[] retPlaces = new GeoPlace[placeList.size()];
    for (int i = 0; i< placeList.size(); i++) {
      retPlaces[i] = (GeoPlace)placeList.get(i);
    }
    return retPlaces;
  }



  /**
   * Returns all the data loaded from file.
   */
  public GeoPlace[] getAll() {
    return geoPlaces;
  }

  /**
   * Returns true if there are any more elements left.
   */
  public boolean hasNext() {
    if (currentArrayIndex < geoPlaces.length) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the next element
   */
  public GeoPlace next() {
    GeoPlace retVal = geoPlaces[currentArrayIndex];
    currentArrayIndex++;
    return retVal;
  }

  /** 
   * Resets the iteration to the first element
   */
  public void reset() {
    currentArrayIndex = 0;
  }

}
