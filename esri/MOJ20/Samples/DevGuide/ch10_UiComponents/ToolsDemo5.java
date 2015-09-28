import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.util.Resource;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.ui.tbl.*;
import com.esri.mo2.ui.toc.*;
import com.esri.mo2.ui.tb.*;
import com.esri.mo2.map.dpy.*;

public class ToolsDemo5 extends javax.swing.JFrame {
  private Map map = new Map();
  private com.esri.mo2.ui.bean.Layer layer = new com.esri.mo2.ui.bean.Layer();
  private JToolBar toolbar = new JToolBar();
  private TreeToc toc = new TreeToc();
  Pan pan = new Pan();
  ZoomIn zi = new ZoomIn();
  ZoomOut zo = new ZoomOut();
  Identify id = new Identify();
  MyPickAdapter mpa = new MyPickAdapter();
  Object[][] row;
  String[] col;
  MySelect select = new MySelect();
  BaseRubberCircle brcircle = new BaseRubberCircle();
  JLabel label = new JLabel();
  MyToolAdapter adapter = new MyToolAdapter();
  Triangle tri = new Triangle();
  Vector triangles = new Vector();

  /** Creates a new instance of ToolsDemo */
  public ToolsDemo5() {
    this.setTitle("Tools Demo");
    this.setSize(600, 400);
    this.getContentPane().setLayout(new java.awt.BorderLayout());
    this.getContentPane().add(label, BorderLayout.SOUTH);

    addLayer();
    addToolBar();
    addIdentifyTool();
    addSelectionTool();
    addMarkup();

    addCustomTool();  // new

    this.setResizable(false);
    this.show();
  }

  /** add thelayer to the map
   */
  private void addLayer() {
    String fileConnection =
        "com.esri.mo2.src.file.FileSystemConnection";
    //String data = "/home/MOJ20/Samples/Data/USA/states.shp";
    String data = "C:/ESRI/ESRIDATA/USA/states.shp";
    layer.setDataset(fileConnection + "!" + data + "!");

    //add the layer to the map and redraw it
    toc.setMap(map);
    map.add(layer, null);
    map.addMouseMotionListener(adapter);
    map.redraw();

    toc.setBackground(Color.gray);
    toc.setForeground(Color.black);
    this.getContentPane().add(map, BorderLayout.CENTER);
    this.getContentPane().add(toc, BorderLayout.WEST);

    toc.refresh();
  }

  private void addToolBar() {
    this.getContentPane().add(toolbar, BorderLayout.NORTH);
    //Adding the Pan tool
    javax.swing.ImageIcon icon1 = new javax.swing.ImageIcon(Resource.getIcon(
        "cmn/pan.gif"));
    JButton btnpan = new JButton(icon1);
    btnpan.setToolTipText("Pan Map");
    toolbar.add(btnpan);
    btnpan.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(pan);
        toc.refresh();
      }
    }
    );

    //Adding the Zoom In tool
    ImageIcon icon2 = new ImageIcon(Resource.getIcon("tb/zi.gif"));
    JButton btnzi = new JButton(icon2);
    btnzi.setToolTipText("Zoom IN");
    toolbar.add(btnzi);
    btnzi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(zi);
      }
    }
    );

    //Adding the Zoom out Tool
    ImageIcon icon3 = new ImageIcon(Resource.getIcon("tb/zo.gif"));
    JButton btnzo = new JButton(icon3);
    btnzo.setToolTipText("Zoom Out");
    toolbar.add(btnzo);
    btnzo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(zo);
      }
    }
    );
  }

  public void clearSelection() {
    map.clearSelections();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    ToolsDemo5 td = new ToolsDemo5();
  }

  public void addIdentifyTool() {
    id.setPickWidth(5);
    id.setMap(map);
    id.addPickListener(mpa);
    ImageIcon icon4 = new ImageIcon(Resource.getIcon("cmn/identify.gif"));
    JButton btnid = new JButton(icon4);
    btnid.setToolTipText("Identify Features");
    toolbar.add(btnid);
    btnid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(id);
      }
    }
    );
  }

  public void addSelectionTool() {
    select.setRubberBand(brcircle);
    ImageIcon icon5 = new ImageIcon(Resource.getIcon("tb/select_circle.gif"));
    JButton btn = new JButton(icon5);
    btn.setToolTipText("Select Features");
    toolbar.add(btn);
    FeatureLayer flayer = (FeatureLayer) map.getLayer(0);
    flayer.setSelectionColor(Color.red);
    select.setLayer(flayer);
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(select);
      }
    }
    );
  }

  public void addCustomTool() {
    ImageIcon icon6 = new ImageIcon(Resource.getIcon(
        "lyt/flip_horizontally.gif"));
    JButton btn = new JButton(icon6);
    btn.setToolTipText("Add Triangle Features");
    toolbar.add(btn);
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(tri);
      }
    }
    );

  }

  public void showselection(Object[][] row, Object[] col) {
    JTable tab = new JTable(row, col);
    tab.setPreferredScrollableViewportSize(new Dimension(700, 70));
    javax.swing.JFrame jf = new javax.swing.JFrame();
    JScrollPane scrollPane = new JScrollPane(tab);
    jf.getContentPane().add(scrollPane, BorderLayout.CENTER);
    jf.setSize(700, 150);
    jf.setLocation(420, 0);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    jf.setTitle("Attributes Table:");
    jf.show();

  }

  private int getCount(com.esri.mo2.ui.bean.PickEvent e) {
    com.esri.mo2.data.feat.Cursor cc = e.getCursor();
    Feature ff = (com.esri.mo2.data.feat.Feature) cc.next();
    Fields fl = ff.getFields();
    int count = fl.size() > 10 ? 10 : fl.size();
    return count;

  }

  class MyPickAdapter
      implements
      com.esri.mo2.ui.bean.PickListener {
    public void beginPick(com.esri.mo2.ui.bean.PickEvent pe) {
      System.out.println("beginPick() called!!");
    }

    public void foundData(com.esri.mo2.ui.bean.PickEvent e) {
      System.out.println("FoundData() called!");
      com.esri.mo2.data.feat.Cursor c = e.getCursor();
      int size = getCount(e);
      Feature f = null;
      Fields flds = null;
      FeatureLayer flayer = (FeatureLayer) e.getLayer();
      SelectionSet ss =
          new BaseSelectionSet(flayer.getFeatureClass());
      System.out.println(ss.size() + "eehaaaaaa");
      boolean once = true;
      int count = 0;
      row = new Object[6][size];
      col = new String[size];
      while (c.hasMore()) {

        f = (com.esri.mo2.data.feat.Feature) c.next();
        flds = f.getFields();
        //Prininting outthe fields names
        int nflds = flds.size() > 10 ? 10 : flds.size();
        //Object arrays for the table

        if (once) {
          for (int i = 0; i < nflds; i++) {
            System.out.print(flds.getField(i).getName() + "\t");
            col[i] = flds.getField(i).getName();
          }

          once = false;
        }
        count = count + 1;

        //Print out the identified value
        System.out.println(" ");
        for (int j = 0; j < nflds; j++) {
          //System.out.print(f.getValue(j) + "\t");
          row[count - 1][j] = f.getValue(j);
          ss.add(f.getDataID());
        }

      }

      //Highlighting the selected features
      flayer.setSelectionColor(Color.orange);
      flayer.setSelectionSet(ss);

    }

    public void endPick(com.esri.mo2.ui.bean.PickEvent ee) {
      showselection(row, col);
    }
  }

  class MySelect extends Select {
    public void rubberbandCompleted() {
      super.rubberbandCompleted();

      SelectionSet ss = this.getSelection();
      com.esri.mo2.data.feat.Cursor c = null;
      Feature f = null;
      if (ss != null) {
        c = ss.getSelectedData(ss);
        while (c.hasMore()) {
          f = (Feature) c.next();
          System.out.println(f.getValue(5));
        }
      }

    }
  }

  class MyToolAdapter extends java.awt.event.MouseMotionAdapter {
    public void mouseMoved(java.awt.event.MouseEvent e) {
      com.esri.mo2.cs.geom.Point p = map.transformPixelToWorld(e.getX(), e.getY());
      label.setText("X = " + p.getX() + "  and Y = " + p.getY());
    }

  }

  class Triangle extends com.esri.mo2.ui.bean.Tool {
    com.esri.mo2.map.draw.SimpleMarkerSymbol sms =
        new com.esri.mo2.map.draw.SimpleMarkerSymbol();
    com.esri.mo2.cs.geom.Point p;

    AcetateLayer layer = new AcetateLayer() {
      public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setTransform(map.getWorldToPixelTransform().toAffine());
        g2d.setClip(map.getExtent());
        for (int i = 0; i < triangles.size(); i++) {
          sms.draw( (com.esri.mo2.cs.geom.Point) triangles.elementAt(i), g2d,
                   "");
        }
      }
    };

    public Triangle() {
      sms.setType(com.esri.mo2.map.draw.SimpleMarkerSymbol.TRIANGLE_MARKER);
      sms.setWidth(10);
      sms.setSymbolColor(Color.yellow);
      sms.setTransparency(0.7);
      map.add(layer);

    }

    public void mouseClicked(java.awt.event.MouseEvent e) {
      p = map.transformPixelToWorld(e.getX(), e.getY());
      triangles.add(p);
      layer.repaint();
    }
  }

  public void addMarkup() {
    ImageIcon icon4 = new ImageIcon(Resource.getIcon("mn/modify.gif"));
    JButton btnid = new JButton(icon4);
    btnid.setToolTipText("Identify Features");
    toolbar.add(btnid);
    btnid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //map.setSelectedTool();
      }
    }
    );

  }

}
