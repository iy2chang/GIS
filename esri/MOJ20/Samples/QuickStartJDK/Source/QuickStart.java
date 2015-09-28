import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.toc.TreeToc;

public class QuickStart extends JFrame {
  Map map = new Map();
  Layer layer = new Layer();
  TreeToc toc = new TreeToc();

  public QuickStart() {
    //Add a title to the window
    super("Quick Start");
    //Set the size
    this.setSize(400, 300);
    //Set up the splitPane;
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    //Add a Toc
    toc.setMap(map);
    splitPane.setLeftComponent(toc);
    getContentPane().add(toc, BorderLayout.WEST);
    //Add the map to the frame
    getContentPane().add(map, BorderLayout.CENTER);
    //Add a shapefile to the map
    addShapefileToMap();
    splitPane.setRightComponent(map);
    getContentPane().add(splitPane, BorderLayout.CENTER);
  }

  private void addShapefileToMap() {
    layer.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/states.shp!");
    map.add(layer);
  }

  public static void main(String[] args) {
    QuickStart qstart = new QuickStart();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
  }
}
