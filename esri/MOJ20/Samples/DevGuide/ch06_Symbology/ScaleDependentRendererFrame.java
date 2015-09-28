
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ScaleDependentRendererFrame extends JFrame {
  Map map1 = new Map();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer1 = null;
  com.esri.mo2.map.dpy.FeatureLayer flayer2 = null;
  com.esri.mo2.map.dpy.FeatureLayer flayer3 = null;
  JScrollPane jScrollPane1 = new JScrollPane();
  TreeToc treeToc1 = new TreeToc();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();

  public ScaleDependentRendererFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    ScaleDependentRendererFrame frame1 = new ScaleDependentRendererFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("ScaleDependent Renderer");
    this.getContentPane().setLayout(null);
    map1.setBounds(new Rectangle(122, 51, 511, 338));
    jButton1.setBounds(new Rectangle(4, 2, 220, 40));
    jButton1.setText("Apply Renderer");
    jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
    layer1.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/states.shp!");
    layer2.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/ushigh.shp!");
    layer3.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/capitals.shp!");
    jScrollPane1.setBounds(new Rectangle(2, 51, 118, 336));
    treeToc1.setMap(map1);
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(260, 2, 280, 40));
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
       flayer1 = (com.esri.mo2.map.dpy.FeatureLayer)layer1.getLayer();
       flayer2 = (com.esri.mo2.map.dpy.FeatureLayer)layer2.getLayer();
       flayer3 = (com.esri.mo2.map.dpy.FeatureLayer)layer3.getLayer();
     } catch (Exception ex) {
       ex.printStackTrace();
       }
     //
     //Wrap the three default renderers:
     //
     //  - the states layer stops drawing when you zoom in enough
     com.esri.mo2.map.draw.ScaleDependentRenderer scale1 = new com.esri.mo2.map.draw.BaseScaleDependentRenderer();
     scale1.setRenderer(flayer1.getRenderer());
     scale1.setMinimumScale(0.05);
     flayer1.setRenderer(scale1);
     //  - the ushigh layer doesn't draw until you zoom in enough
     com.esri.mo2.map.draw.ScaleDependentRenderer scale2 = new com.esri.mo2.map.draw.BaseScaleDependentRenderer();
     scale2.setRenderer(flayer2.getRenderer());
     scale2.setMaximumScale(0.05);
     flayer2.setRenderer(scale2);
     //  - the capitals layer starts drawing when you zoom in, then stops
     //    when you zoom in more.
     com.esri.mo2.map.draw.ScaleDependentRenderer scale3 = new com.esri.mo2.map.draw.BaseScaleDependentRenderer();
     scale3.setRenderer(flayer3.getRenderer());
     scale3.setRange(0.005, 0.06);
     flayer3.setRenderer(scale3);
     //
     map1.redraw();
     treeToc1.refresh();
  }


class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
  ScaleDependentRendererFrame adaptee;

  Frame1_jButton1_actionAdapter(ScaleDependentRendererFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}
}
