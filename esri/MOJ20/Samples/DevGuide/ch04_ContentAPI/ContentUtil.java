
import com.esri.mo2.map.dpy.Layer;
import com.esri.mo2.src.sys.*;

import java.io.IOException;

/**
 * Three static utility methods that illustrate ways to navigate the 
 * Content API, looking for a Content that can return a Layer.
 * These examples all start with a Connection, get a Source from it, and
 * connect the Source. 
 * One method starts at the root of the hierarchy.
 * One method uses a content path string to open a specified Content directly.
 * One method uses a content path string to open a folder in the hierarchy
 * and then searches the sub-hierarchy within that Folder.
 */
public class ContentUtil {

  private static String layerType = Layer.class.getName();

  /**
   * Use direct access to print information about a Layer from a Content.
   * @param connection any valid Connection
   * @param contentPath a Content Path string that describes a Content within
   *  the connection's content hierarchy.
   */
  public static void showLayer(Connection connection, String contentPath) throws IOException{

    System.out.println("Starting with Connection: "+connection.toString());
    System.out.println("");

    Source source = SourceCache.getSource(connection);
    if (! source.isConnected()) {
      source.connect();
    }

    Content content = source.getContent(contentPath);
    if (content == null) {
      System.out.println("Something is wrong.  The Content is null.");
    } else {
      Layer layer = (Layer)content.getData(layerType);
      System.out.println("Found Layer: " + layer.getName());
    }

  }

  /**
   * Given a valid Connection object, work through the content hierarchy
   * using discovery to find and print information about the first
   * Layer that can be found.
   */
  public static void findLayer (Connection connection) throws IOException {

    System.out.println("Starting with Connection: "+connection.toString());
    System.out.println("");

    Source source = SourceCache.getSource(connection);
    if (! source.isConnected()) {
      source.connect();
    }

    Content[] contents = source.getRoots();
    Content layerContent = parseContents(contents);
    System.out.print("Found ");
    printContent(layerContent);
    Layer layer = (Layer)layerContent.getData(layerType);
    System.out.println("Found Layer: " + layer.getName());
  }

  /**
   * Given a valid Connection object, work through the content hierarchy
   * (starting at a specified location in the hierarchy) using discovery 
   * to find and print information about the first Layer that can be found.
   * @param connection the Connection
   * @param startPath a valid content path which refers to a Folder within
   *   the content hierarchy.  This content will not get checked for a Layer.
   */
  public static void findLayerIn (Connection connection, String startPath) throws IOException {

    System.out.println("Starting with Connection: "+connection.toString());
    System.out.println("Starting search at "+startPath);
    System.out.println("");

    Source source = SourceCache.getSource(connection);
    if (! source.isConnected()) {
      source.connect();
    }

    Folder startFolder = (Folder)source.getContent(startPath);
    Content[] contents = startFolder.getContents();
    Content layerContent = parseContents(contents);
    System.out.print("Found ");
    printContent(layerContent);
    Layer layer = (Layer)layerContent.getData(layerType);
    System.out.println("Found Layer: " + layer.getName());
  }

  /**
   * Simple recursion search through a Folder/Content tree for a
   * Content that can return a Layer.
   * @return a Content that can generate a Layer object.
   */
  private static Content parseContents(Content[] cc) throws IOException {

    for (int i = 0; i < cc.length; i++) {
      //Uncomment the next two lines to watch the search progress...
      //System.out.println("Checking...");
      //printContent(cc[i]);

      //if it has a Layer, return it.
      if (cc[i].isContentTypeAvailable(layerType)) {
        return cc[i];
      }

      //if it is a Folder, recurse
      if (cc[i] instanceof Folder) {
        Folder folder = (Folder)cc[i];
        Content found = parseContents(folder.getContents());
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static void printContent (Content c) {
    if (c == null) {
      System.out.println("Content is null");
      return;
    }
    System.out.println("Content Path = "+c.getPath());
    if (c instanceof Folder) {
      System.out.println("Content is a Folder");
      Folder f = (Folder)c;
    }
    System.out.println("");
  }

}
