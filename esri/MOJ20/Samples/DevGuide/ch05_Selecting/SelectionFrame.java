
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.toc.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Simple example showing how to populate a Cursor and use a SelectionSet
 */
public class SelectionFrame extends JFrame implements ActionListener{
  Map map1 = new Map();
  TreeToc treeToc1 = new TreeToc();
  Layer layer1 = new Layer();
  Layer layer3 = new Layer();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  TreeListener listener = new TreeListener();
  boolean exit = true;
  boolean notcancel = true;
  JOptionPane option;
  String criteria;
  com.esri.mo2.data.feat.Cursor c = null;
  SelectionSet ss = null;
  JButton jButton3 = new JButton();


  public SelectionFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SelectionFrame frame1 = new SelectionFrame();
    frame1.show();
  }

  private void init() throws Exception {
    this.setLocale(java.util.Locale.getDefault());
    this.setResizable(false);
    this.getContentPane().setLayout(null);
    this.setSize(new Dimension(605, 402));
    this.setTitle("Selecting and Retrieving Information");
    map1.setBounds(new Rectangle(104, 54, 499, 346));
    treeToc1.setMap(map1);
    treeToc1.addTocListener(listener);
    treeToc1.setBounds(new Rectangle(1, 54, 98, 346));
    layer1.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/states.shp!");
    layer1.setBounds(new Rectangle(195, 80, 32, 32));
    layer3.setDataset("com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/MOJ20/Samples/Data/USA/capitals.shp!");
    layer3.setBounds(new Rectangle(316, 97, 32, 32));
    jButton1.setBounds(new Rectangle(7, 11, 118, 35));
    jButton1.setToolTipText("");
    jButton1.addActionListener(this);
    jButton1.setText("Search...");
    jButton2.setText("Selection Set");
    jButton2.addActionListener(this);
    jButton1.setEnabled(false);
    jButton2.setEnabled(false);
    jButton2.setBounds(new Rectangle(136, 10, 98, 35));
    jButton3.setBounds(new Rectangle(243, 9, 82, 35));
    jButton3.setText("Clear");
    jButton3.addActionListener(this);
    jButton3.setEnabled(false);
    this.getContentPane().add(map1, null);
    map1.add(layer1, null);
    map1.add(layer3, null);
    this.getContentPane().add(treeToc1, null);
    this.getContentPane().add(jButton1, null);
    this.getContentPane().add(jButton2, null);
    this.getContentPane().add(jButton3, null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  public void actionPerformed(ActionEvent e) {
    FeatureLayer flayer = (FeatureLayer) treeToc1.getSelectedLayers()[0];
    Fields flds = flayer.getFeatureClass().getFields();
    notcancel = true;
    exit = true;
    c = null;
    if (e.getSource() == jButton1) {
      System.out.println("Button 1 was pressed");
      BaseSpatialFilter bsf = new BaseSpatialFilter();
      bsf.setUseCache(false);
      bsf.setSubFields(flds);
      bsf.setGeometry(map1.getExtent());
      bsf.setSpatialRelation(SpatialFilter.AREA_INTERSECTION);
      //Obtaining the search String from the User
      while (exit) {
        try {
          criteria = "POP1990 > 50000";
          criteria = option.showInputDialog(
              "Enter Query (by default-POP1990 > 50000").trim();
          if (criteria.length() < 5) {
            criteria = "POP1990 > 50000";
          }
          bsf.setWhereClause(criteria);
          c = flayer.search(bsf);
          exit = false;
          notcancel = true;
        }
        catch (Exception exp) {
          JOptionPane.showMessageDialog(null, "Please Enter the query again",
                                        "Error", JOptionPane.ERROR_MESSAGE);
          exit = false;
          notcancel = false;
        }
      }
      if (notcancel) {
        //invoking the Spatial Filtering results printing
        JTextArea text = new JTextArea();
        while (c.hasMore()) {
          Feature d = (Feature)c.next();
          if(flayer.getLayerInfo().getName().equals("states")) {
            text.append(d.getValue(5).toString());
          } else {
             text.append(d.getValue(1).toString());
          }
          text.append("\n");
        }
        JDialog jd = new JDialog();
        jd.setTitle("Search Results:");
        JScrollPane spanel = new JScrollPane(text);
        spanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jd.setSize(300,300);
        jd.setLocation(600,300);
        jd.getContentPane().add(spanel);
        jd.setVisible(true);
      }
    }
    if (e.getSource() == jButton2) {
      System.out.println("Button 2 was pressed");
      BaseQueryFilter bqf = new BaseQueryFilter();
      notcancel = true;
      exit = true;
      ss =null;
      //Adding the code for the JOptionPanes
      while (exit) {
        try {
          criteria = "POP1990 > 10000000";
          criteria = option.showInputDialog(
              "Enter Query (by default-POP1990 > 10000000").trim();
          if (criteria.length() < 5) {
            criteria = "POP1990 > 10000000";
          }
          exit = false;
          notcancel = true;
        }
        catch (Exception exp) {
          JOptionPane.showMessageDialog(null, "Please Enter the query again",
                                        "Error", JOptionPane.ERROR_MESSAGE);
          exit = false;
          notcancel = false;
        }
      }

      if (notcancel) {
        bqf.setWhereClause(criteria);
        bqf.setUseCache(false);
        bqf.setSubFields(flds);
        flayer.setSelectionColor(Color.red);
        ss = flayer.select(bqf);
        flayer.setSelectionSet(ss);
        jButton3.setEnabled(true);
      }
    }
    if(e.getSource()==jButton3) {
      ss = null;
      flayer.setSelectionSet(ss);
      jButton3.setEnabled(false);
    }
  }


  class TreeListener extends TreeTocAdapter {

    public void click(com.esri.mo2.ui.toc.TreeTocEvent e) {
       java.awt.event.MouseEvent me = e.getEvent();

       com.esri.mo2.map.dpy.Layer lyrs[] = treeToc1.getSelectedLayers();
       if(lyrs.length >= 1) {
         jButton1.setEnabled(true);
         jButton2.setEnabled(true);

       }
         else {
          jButton1.setEnabled(false);
          jButton2.setEnabled(false);

        }

    }
  }
}
