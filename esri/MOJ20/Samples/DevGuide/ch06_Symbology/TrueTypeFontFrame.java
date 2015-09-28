
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class TrueTypeFontFrame extends JFrame {
  Map map1 = new Map();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer = null;
  JScrollPane jScrollPane1 = new JScrollPane();
  TreeToc treeToc1 = new TreeToc();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();

  public TrueTypeFontFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    TrueTypeFontFrame frame1 = new TrueTypeFontFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("TrueTypeFont Renderer");
    this.getContentPane().setLayout(null);
    map1.setBounds(new Rectangle(122, 51, 511, 338));
    jButton1.setBounds(new Rectangle(4, 2, 155, 48));
    jButton1.setText("Apply Renderer");
    jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
    layer1.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/states.shp!");
    layer1.setBounds(new Rectangle(125, 91, 32, 32));
    layer2.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/ushigh.shp!");
    layer2.setBounds(new Rectangle(261, 153, 32, 32));
    layer3.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/capitals.shp!");
    layer3.setBounds(new Rectangle(274, 93, 32, 32));
    jScrollPane1.setBounds(new Rectangle(2, 51, 118, 336));
    treeToc1.setMap(map1);
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(174, 5, 244, 43));
    this.getContentPane().add(map1, null);
    this.setSize(new Dimension(635, 420));
    map1.add(layer1, null);
    this.getContentPane().add(jButton1, null);
    this.getContentPane().add(jScrollPane1, null);
    this.getContentPane().add(zoomPanToolBar1, null);
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.getViewport().add(treeToc1, null);
    map1.add(layer2, null);
    map1.add(layer3, null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

   void jButton1_actionPerformed(ActionEvent e) {
     try {
     flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer3.getLayer();
  }
     catch (Exception ex) {
       ex.printStackTrace();
     }

     //Create a TrueTypeMarkerSymbol, specify the attributes,
     //and display it in a BaseSimpleRenderer
     com.esri.mo2.map.draw.TrueTypeMarkerSymbol ttm =
         new com.esri.mo2.map.draw.TrueTypeMarkerSymbol();
     com.esri.mo2.map.draw.BaseSimpleRenderer bslr =
         new com.esri.mo2.map.draw.BaseSimpleRenderer();
     ttm.setFont(new Font("ESRI TELECOM", Font.BOLD, 12));
     ttm.setCharacter("53");
     ttm.setColor(java.awt.Color.red);
     bslr.setSymbol(ttm);
     flayer.setRenderer(bslr);
     map1.redraw();
 }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    TrueTypeFontFrame adaptee;

    Frame1_jButton1_actionAdapter(TrueTypeFontFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}
