
import com.esri.mo2.data.feat.Field;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BaseClassBreaksFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  Map map1 = new Map();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer = null;
  JScrollPane jScrollPane1 = new JScrollPane();


  public BaseClassBreaksFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    BaseClassBreaksFrame frame1 = new BaseClassBreaksFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("BaseClassBreaks Renderer");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    map1.setBounds(new Rectangle(151, 51, 482, 338));
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
    jScrollPane1.setBounds(new Rectangle(2, 51, 148, 336));
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
    flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer1.getLayer();
  }
  catch (Exception ex) {
    ex.printStackTrace();
  }

  com.esri.mo2.map.draw.BaseClassBreaksRenderer cbr =
      new com.esri.mo2.map.draw.BaseClassBreaksRenderer();

  //get field to use in building query
  String fname = "POP1990";
  Fields fields = flayer.getFeatureClass().getFields();

  int fnum = fields.findField(fname);

  Field f = fields.getField(fnum);

  //set the field to the renderer
  cbr.setField(f);
  //set class breaks
  //values for next three variables are dependent on what field is being used.
  //to change class breaks, change classes, minValue and maxValue and fname (above)
  int classes = 5;
  int minValue = 0;
  int maxValue = 30000000;
  //generate class breaks
  int increm = (maxValue/(classes+1));
  int t = 0;
  Comparable minV, maxV = null;
  int r = 255, g = 255, b = 0;

  while(t<=classes){
    minV = (Comparable)f.parse(String.valueOf(minValue));
    maxV = (Comparable)f.parse(String.valueOf(minValue+increm));
    com.esri.mo2.map.draw.BaseRange br =
        new  com.esri.mo2.map.draw.BaseRange(minV, maxV);
    minValue += increm;
    com.esri.mo2.map.draw.SimplePolygonSymbol s =
        new com.esri.mo2.map.draw.SimplePolygonSymbol();
    //yellow to red color schema
    g = 255-((255/classes)*t);
    s.setPaint(new Color(r,g,b));
    cbr.addBreak(s, br);
    t++;
  }

  //set the renderer and redraw the map
     flayer.setRenderer(cbr);
     map1.redraw();
     treeToc1.refresh();
  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    BaseClassBreaksFrame adaptee;

    Frame1_jButton1_actionAdapter(BaseClassBreaksFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}

