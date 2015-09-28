
import com.esri.mo2.src.file.FileSystemConnection;
import com.esri.mo2.src.sys.Connection;

import java.io.IOException;

public class DirectFile {

  private static String dataString =
    "C:\\ESRI\\MOJ20\\Samples\\Data\\Washington\\roads.shp";

  public static void main (String[] args) throws IOException {
    System.out.println("Getting a Layer via direct access using FileSystemConnection");

    Connection con = new FileSystemConnection();
    ContentUtil.showLayer(con, dataString);
  }

}
