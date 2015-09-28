
import com.esri.mo2.data.feat.Field;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.toc.TreeToc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GradientFillSymbolFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  Map map1 = new Map();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();

  public GradientFillSymbolFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    GradientFillSymbolFrame frame1 = new GradientFillSymbolFrame();
    frame1.show();
    frame1.setVisible(true);
  }
  private void init() throws Exception {
    this.setTitle("GradientFill Symbol");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    treeToc1.setBounds(new Rectangle(0, 52, 119, 309));
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(200, 5, 244, 43));
    map1.setBounds(new Rectangle(122, 51, 454, 309));
    jButton1.setBounds(new Rectangle(4, 2, 155, 48));
    jButton1.setText("Create Symbol");
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
     com.esri.mo2.map.draw.GradientFillSymbol gradfill =
         new com.esri.mo2.map.draw.GradientFillSymbol();
     com.esri.mo2.map.draw.BaseSimpleRenderer bslr =
         new com.esri.mo2.map.draw.BaseSimpleRenderer();
     gradfill.setStartColor(java.awt.Color.yellow);
     gradfill.setFinishColor(java.awt.Color.red);
     gradfill.setType(com.esri.mo2.map.draw.GradientFillSymbol.GRADIENTFILL_TYPE_VERTICAL);
     bslr.setSymbol(gradfill);

    com.esri.mo2.map.dpy.FeatureLayer flayer = null;

    try {
      flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer1.getLayer();
    }
    catch (Exception ex) {
    }
    flayer.setRenderer(bslr);
    map1.redraw();

  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    GradientFillSymbolFrame adaptee;

    Frame1_jButton1_actionAdapter(GradientFillSymbolFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}

