
import com.esri.mo2.client.aims.ArcIms4Connection;
import com.esri.mo2.src.sys.Connection;

import java.io.IOException;

public class DiscoverArcims {

  public static void main (String[] args) throws IOException {
    System.out.println("Searching for a Layer using ArcIms4Connection");
    Connection con = new ArcIms4Connection("http://www.geographynetwork.com");
    ContentUtil.findLayer(con);
  }

}
