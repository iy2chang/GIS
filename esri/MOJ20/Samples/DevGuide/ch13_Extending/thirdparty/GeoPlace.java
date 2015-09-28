
package thirdparty;

/**
 * Defines a geographic point in double precision, with an 
 * associated name string
 */
public class GeoPlace extends java.awt.geom.Point2D.Double {

  /**
   * The name attribute of the GeoPlace
   */
  public String name;

  /**
   * The unique ID of the GeoPlace.
   */
  public int id;
  
  /**
   * Constructs and initializes a GeoPlace with coordinates (0,0) and
   * an empty name string.
   */
  public GeoPlace() {
    super();
    setName("");
  }
  
  /**
   * Constructs and initializes a GeoPlace with the specified parameters
   */
  public GeoPlace(double startX, double startY, String startName, int startId) {
    setLocation(startX, startY);
    setName(startName);
    setId(startId);
  }

  /**
   * Returns the name attribute.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the ID attribute.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the name attribute
   * @param newName any String
   */
  public void setName(String newName) {
    name = newName;
  }

  /**
   * Sets the ID attribute
   * @param newId a uniqe identifier for this instance
   */
  public void setId(int newId) {
    id = newId;
  }

  /**
   * Returns a String that represents the value of this GeoPlace
   */
  public String toString() {
    return super.toString()+name;
  }

}
