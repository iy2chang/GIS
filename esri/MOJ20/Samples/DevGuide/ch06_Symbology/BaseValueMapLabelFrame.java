
import com.esri.mo2.data.feat.BaseFields;
import com.esri.mo2.data.feat.Feature;
import com.esri.mo2.data.feat.Field;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BaseValueMapLabelFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  Map map1 = new Map();
  JButton jButton1 = new JButton();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer = null;
  JScrollPane jScrollPane1 = new JScrollPane();


  public BaseValueMapLabelFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    BaseValueMapLabelFrame frame1 = new BaseValueMapLabelFrame();
    frame1.setVisible(true);
  }
  private void init() throws Exception {
    this.setTitle("BaseValueMapLabel Renderer");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(200, 5, 244, 43));
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
  }

  Color[] COLORS = new Color[51];
  for(int i=0;i<COLORS.length;i++)
      {
         COLORS[i] = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
      }

      String fname = "SUB_REGION";
      com.esri.mo2.map.draw.BaseValueMapLabelRenderer vmlr =
          new com.esri.mo2.map.draw.BaseValueMapLabelRenderer();

      Fields fields = flayer.getFeatureClass().getFields();
      int fnum = fields.findField(fname);
      Field f = fields.getField(fnum);
      BaseFields flds = new BaseFields();
      flds.addField(f);
      vmlr.setLabelFields(flds);
      vmlr.setField(f);


      //Get Dataset. Retrieve a cursor
      com.esri.mo2.data.feat.BaseQueryFilter qfilter =
          new com.esri.mo2.data.feat.BaseQueryFilter();

      qfilter.setSubFields(fields);
      qfilter.setUseCache(false);
      com.esri.mo2.data.feat.Cursor cursor = null;
      try {
          cursor = flayer.search(qfilter);
      }

      catch(java.lang.IllegalArgumentException ex) {
          System.out.println(ex.toString());
      }

      int t = 0;
      while(cursor.hasMore()) {
        com.esri.mo2.map.draw.SimpleTextSymbol s =
              new com.esri.mo2.map.draw.SimpleTextSymbol();
          if(t>=COLORS.length) t = 0;
          s.setColor(COLORS[t++]);
          s.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
          Feature feat = (Feature)cursor.next();

          com.esri.mo2.map.draw.LegendClass lc =
              new com.esri.mo2.map.draw.BaseLegendClass(s,feat.getValue(fnum));
          vmlr.addLegendClass(lc);
      }
      flayer.setLabelRenderer(vmlr);
      map1.redraw();

  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    BaseValueMapLabelFrame adaptee;

    Frame1_jButton1_actionAdapter(BaseValueMapLabelFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}

