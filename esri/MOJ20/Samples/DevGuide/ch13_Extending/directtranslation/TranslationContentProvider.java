package directtranslation;

import com.esri.mo2.src.cat.*;
import com.esri.mo2.src.sys.*;
import java.awt.event.*;
import java.io.IOException;
import directtranslation.TranslationConnection;


/**
 * Provides dynamic Content API information for TranslationFeatureClass,
 * which accesses PTXT data.  This, and associated classes illustrate
 * how to provide dynamic access to custom remote data sources.
 * See the associated chapter in the Developer's Guide book for
 * details of how to implement each method.
 */ 
public class TranslationContentProvider extends ConnectionContentProvider {

  protected String getAddConnectionName() {
    return "Add PTXT Connection";
  }

  protected String getConnectionName(Connection con) {
    return con.toString();
  }

  public String getConnectionType() {
    // This must be a fully-qualified class name for the custom connection
    // object.
    return TranslationConnection.class.getName();
  }

  public String getRootPath(){
    //  Use this line if you want your "Add PTXT Connection" icon next
    //  to "Add Internet Servier" in the existing "Internet Servers" folder
    //return "Internet Servers";

    //  Use this line if you want it in a folder by itself
    return "Remote PTXT";
  }

  protected void openAddConnectionDialog(Object source) {
    System.out.println("in openAddConnection");
    MouseEvent evt = (MouseEvent)source;
    TranslationConnectionDialog dialog = new TranslationConnectionDialog();
    dialog.setLocationRelativeTo((java.awt.Component)evt.getSource());
    dialog.setModal(true);
    dialog.setVisible(true);

    if(dialog.okPressed){
      //dialog.dispose(); //MARKZ is this needed?  I don't think so

      TranslationConnection myConnection = new TranslationConnection();
      String path = dialog.jTextField1.getText();
      myConnection.setConnection(path);

      //If you need to test the connection or get the root contents
      // from the connection, this is where you would do it.
      // (make sure to use the SourceCache)

      try {
        CatalogModel.get().addConnection(myConnection);
      } catch (IOException ex) {
        ex.printStackTrace();
      }

    }
  }

}

