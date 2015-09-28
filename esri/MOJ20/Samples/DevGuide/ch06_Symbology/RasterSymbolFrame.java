
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.util.Resource;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RasterSymbolFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  Map map1 = new Map();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();

  public RasterSymbolFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    RasterSymbolFrame frame1 = new RasterSymbolFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("Raster Symbols");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(260, 2, 280, 40));
    treeToc1.setBounds(new Rectangle(0, 52, 119, 309));
    map1.setBounds(new Rectangle(122, 51, 454, 309));
    jButton1.setBounds(new Rectangle(4, 2, 220, 40));
    jButton1.setText("Create Symbols");
    jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
    layer1.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/states.shp!");
    layer1.setBounds(new Rectangle(125, 91, 32, 32));
    layer2.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/ushigh.shp!");
    layer2.setBounds(new Rectangle(261, 153, 32, 32));
    layer3.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/capitals.shp!");
    layer3.setBounds(new Rectangle(274, 93, 32, 32));
    this.getContentPane().add(treeToc1, null);
    this.getContentPane().add(map1, null);
    this.getContentPane().add(zoomPanToolBar1, null);
    this.setSize(new Dimension(580, 363));
    map1.add(layer1, null);
    this.getContentPane().add(jButton1, null);
    map1.add(layer2, null);
    map1.add(layer3, null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    //Create a new RasterMarkerSymbol and display a specified image for each capital
    com.esri.mo2.map.draw.RasterMarkerSymbol ssym =
        new com.esri.mo2.map.draw.RasterMarkerSymbol();
    com.esri.mo2.map.draw.BaseSimpleRenderer bslr =
        new com.esri.mo2.map.draw.BaseSimpleRenderer();
    ssym.setImage(Resource.getIcon("cmn/find.gif"));
    bslr.setSymbol(ssym);
    com.esri.mo2.map.dpy.FeatureLayer flayer = null;
    try {
      flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer3.getLayer();
    }
    catch (Exception ex) {
    ex.printStackTrace();
    }
    flayer.setRenderer(bslr);
    map1.redraw();

  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    RasterSymbolFrame adaptee;

    Frame1_jButton1_actionAdapter(RasterSymbolFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}


