
import com.esri.mo2.data.feat.Field;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.data.feat.BaseFields;
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ChartSymbolsFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  Map map1 = new Map();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer = null;

  public ChartSymbolsFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    ChartSymbolsFrame frame1 = new ChartSymbolsFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("ChartSymbols");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    treeToc1.setBounds(new Rectangle(0, 52, 119, 309));
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(200, 5, 244, 43));
    map1.setBounds(new Rectangle(122, 51, 454, 309));
    jButton1.setBounds(new Rectangle(4, 2, 155, 48));
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
     try {
    flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer1.getLayer();
  }
  catch (Exception ex) {
    ex.printStackTrace();
  }

    String fname1 = "MALES";
    String fname2 = "FEMALES";
    //set renderer
    com.esri.mo2.map.draw.ChartSymbol cs =
        new com.esri.mo2.map.draw.ChartSymbol();
    com.esri.mo2.map.draw.BaseSimpleLabelRenderer bslr2 =
        new com.esri.mo2.map.draw.BaseSimpleLabelRenderer();
    BaseFields baseFields = new BaseFields();
    Fields fields = flayer.getFeatureClass().getFields();
    //get data from fields
    int intF1 = fields.findField(fname1);
    int intF2 = fields.findField(fname2);
    Field f1 = fields.getField(intF1);
    Field f2 = fields.getField(intF2);
    //add fields to collection, baseFields
    baseFields.addField(f1);
    baseFields.addField(f2);
    //set fields for rendering
    bslr2.setLabelFields(baseFields);
    //generate the ChartValue objects
    com.esri.mo2.map.draw.ChartValue cv =
        new com.esri.mo2.map.draw.ChartValue();
    com.esri.mo2.map.draw.ChartValue cv2 =
        new com.esri.mo2.map.draw.ChartValue();
    cv.setLookUpField(fname1);
    cv.setColor(Color.pink);
    cv2.setLookUpField(fname2);
    cv2.setColor(Color.cyan);
    //add values to ChartSymbol
    cs.setMaxSize(50);
    cs.setMaxValue(7000000);
    cs.setMinSize(10);
    cs.setMinValue(1000000);
    cs.addChartValue(cv);
    cs.addChartValue(cv2);
    cs.setOutline(Color.black);
    cs.setMode(cs.MODE_PIE);
    bslr2.setSymbol(cs);
    //set the renderer and redraw the map
    flayer.setLabelRenderer(bslr2);
    map1.redraw();
  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    ChartSymbolsFrame adaptee;

    Frame1_jButton1_actionAdapter(ChartSymbolsFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}

