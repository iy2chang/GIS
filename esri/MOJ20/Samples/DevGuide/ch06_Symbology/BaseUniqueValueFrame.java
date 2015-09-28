
import com.esri.mo2.data.feat.QueryFilter;
import com.esri.mo2.data.feat.BaseQueryFilter;
import com.esri.mo2.data.feat.FeatureClass;
import com.esri.mo2.data.feat.Field;
import com.esri.mo2.data.feat.Fields;
import com.esri.mo2.data.feat.Row;
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BaseUniqueValueFrame extends JFrame {
  TreeToc treeToc1 = new TreeToc();
  Map map1 = new Map();
  ZoomPanToolBar zoomPanToolBar1 = new ZoomPanToolBar();
  JButton jButton1 = new JButton();
  Layer layer1 = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  com.esri.mo2.map.dpy.FeatureLayer flayer = null;
  JScrollPane jScrollPane1 = new JScrollPane();


  public BaseUniqueValueFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    BaseUniqueValueFrame frame1 = new BaseUniqueValueFrame();
    frame1.show();
  }
  private void init() throws Exception {
    this.setTitle("BaseUniqueValue Renderer");
    this.getContentPane().setLayout(null);
    treeToc1.setMap(map1);
    treeToc1.setBounds(new Rectangle(0, 52, 119, 309));
    zoomPanToolBar1.setMap(map1);
    zoomPanToolBar1.setBounds(new Rectangle(200, 5, 244, 43));
    map1.setBounds(new Rectangle(123, 51, 510, 338));
    jButton1.setBounds(new Rectangle(4, 2, 180, 48));
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
    this.setSize(new Dimension(635, 415));
    map1.add(layer1, null);
    this.getContentPane().add(jScrollPane1, null);
    this.getContentPane().add(jButton1, null);
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

     com.esri.mo2.map.draw.BaseUniqueValueRenderer buvr =
         new com.esri.mo2.map.draw.BaseUniqueValueRenderer();

     String fname = "STATE_NAME";

     FeatureClass statesfClass = flayer.getFeatureClass();
     Fields fields = statesfClass.getFields();

     //Get the Field index by using the findField() method
     int fnum = fields.findField(fname);
      //Get the Field object to render by using the getField() method
     Field f = fields.getField(fnum);
      //set the field to render by using the setField() method
     buvr.setField(f);
      //get cursor to all the data data
     BaseQueryFilter qfilter = new BaseQueryFilter();
     qfilter.setSubFields(fields);
     qfilter.setUseCache(false);
     com.esri.mo2.data.feat.Cursor cursor = null;
     try{
       cursor = flayer.search(qfilter);
     } catch(java.lang.IllegalArgumentException ex){System.out.println(ex);}


     while(cursor.hasMore()){
        Row r = (Row)cursor.next();

        //Instantiate an instance of the SimplePolygonSymbol class
        com.esri.mo2.map.draw.SimplePolygonSymbol sfs =
            new com.esri.mo2.map.draw.SimplePolygonSymbol();

        Color COLOR =
            new Color((float)java.lang.Math.random(),(float)java.lang.Math.random(),(float)java.lang.Math.random());

        sfs.setTransparency(0.6);
        sfs.setAntialiasing(true);
        sfs.setPaint(com.esri.mo2.map.draw.AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.DIAGONAL_CROSS_FILL, COLOR));
        sfs.setBoundary(true);
        buvr.addValue(sfs, r.getValue(fnum));

     }
     flayer.setRenderer(buvr);
     map1.redraw();
     treeToc1.refresh();
  }


  class Frame1_jButton1_actionAdapter implements java.awt.event.ActionListener {
    BaseUniqueValueFrame adaptee;

    Frame1_jButton1_actionAdapter(BaseUniqueValueFrame adaptee) {
      this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }
}

