
import com.esri.mo2.src.file.FileSystemConnection;
import com.esri.mo2.src.sys.Connection;

import java.io.IOException;

public class DiscoverFile {

  private static String dataString = "C:\\ESRI\\MOJ20\\Samples\\Data";

  public static void main (String[] args) throws IOException {
    System.out.println("Searching for a Layer using FileSystemConnection");
    Connection con = new FileSystemConnection();
    ContentUtil.findLayerIn(con, dataString);
  }

}
