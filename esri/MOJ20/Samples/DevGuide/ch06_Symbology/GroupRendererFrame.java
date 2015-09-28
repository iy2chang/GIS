
import com.esri.mo2.data.feat.Field;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.map.draw.AoLineStyle;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.toc.TreeToc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GroupRendererFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  Map map1 = new Map();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer = null;
  JScrollPane jScrollPane1 = new JScrollPane();


  public GroupRendererFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    GroupRendererFrame frame1 = new GroupRendererFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("Group Renderer");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    map1.setBounds(new Rectangle(122, 51, 511, 338));
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(200, 5, 244, 43));
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
    flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer2.getLayer();
  }
  catch (Exception ex) {
    ex.printStackTrace();
  }

     com.esri.mo2.map.draw.BaseGroupRenderer bgr =
         new com.esri.mo2.map.draw.BaseGroupRenderer();
     com.esri.mo2.map.draw.BaseSimpleRenderer r1 =
         new com.esri.mo2.map.draw.BaseSimpleRenderer();
     com.esri.mo2.map.draw.BaseSimpleRenderer r2 =
         new com.esri.mo2.map.draw.BaseSimpleRenderer();

     //Line symbols
     com.esri.mo2.map.draw.SimpleLineSymbol lSym1 =
         new com.esri.mo2.map.draw.SimpleLineSymbol();
     com.esri.mo2.map.draw.SimpleLineSymbol lSym2 =
         new com.esri.mo2.map.draw.SimpleLineSymbol();

     //format line symbols
     lSym1.setLineColor(Color.blue);
     lSym2.setLineColor(Color.red);
     lSym1.setStroke(AoLineStyle.getStroke(AoLineStyle.DASH_LINE, 5));

     r1.setSymbol(lSym1);
     r2.setSymbol(lSym2);

     // add the simple renderers to the group renderer by using the addRenderer() method
     bgr.addRenderer(r1);
     bgr.addRenderer(r2);
     //apply the renderer to the highway FeatureLayer using the setRenderer() method
     flayer.setRenderer(bgr);
     //refresh Map and Toc
     map1.redraw();
     treeToc1.refresh();

  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    GroupRendererFrame adaptee;

    Frame1_jButton1_actionAdapter(GroupRendererFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}

