import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.dpy.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.toc.TreeToc;
import com.esri.mo2.util.Resource;
import java.util.Vector;

public class ToolsDemo3 extends JFrame {
  private Map map = new Map();
  private Layer layer = new Layer();
  private JToolBar toolbar = new JToolBar();
  private TreeToc toc = new TreeToc();

  private Pan pan = new Pan();
  private ZoomIn zi = new ZoomIn();
  private ZoomOut zo = new ZoomOut();

  private Identify id = new Identify();
  private MyPickAdapter mpa = new MyPickAdapter();

  private MySelect select = new MySelect();
  private BaseRubberCircle brcircle = new BaseRubberCircle();

  public ToolsDemo3() {
    init();

    addLayer();
    addToolBar();
    addIdentifyTool();

    addSelectionTool(); //new

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void init() {
    setTitle("Tools Demo");
    setSize(600, 400);

    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(toc, BorderLayout.WEST);
    getContentPane().add(toolbar, BorderLayout.NORTH);
    //getContentPane().add(label, BorderLayout.SOUTH);
  }

  private void addLayer() {
    String fileConnection =
        "com.esri.mo2.src.file.FileSystemConnection";
    //String data = "/home/MOJ20/Samples/Data/USA/states.shp";
    String data = "C:/ESRI/ESRIDATA/USA/states.shp";
    layer.setDataset(fileConnection + "!" + data + "!");

    //add the layer to the map and redraw it
    toc.setMap(map);
    map.add(layer);
    //map.addMouseMotionListener(adapter);
    map.redraw();

    toc.setBackground(Color.gray);
    toc.setForeground(Color.black);
    toc.refresh();
  }

  private void addToolBar() {
    //Adding the Pan tool
    ImageIcon icon1 = new
        ImageIcon(Resource.getIcon("cmn/pan.gif"));
    JButton btnpan = new JButton(icon1);
    btnpan.setToolTipText("Pan Map");
    toolbar.add(btnpan);
    btnpan.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(pan);
        toc.refresh();
      }
    });

    //Adding the Zoom In tool
    ImageIcon icon2 = new
        ImageIcon(Resource.getIcon("tb/zi.gif"));
    JButton btnzi = new JButton(icon2);
    btnzi.setToolTipText("Zoom In");
    toolbar.add(btnzi);
    btnzi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(zi);
      }
    });

    //Adding the Zoom out Tool
    ImageIcon icon3 = new
        ImageIcon(Resource.getIcon("tb/zo.gif"));
    JButton btnzo = new JButton(icon3);
    btnzo.setToolTipText("Zoom Out");
    toolbar.add(btnzo);
    btnzo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(zo);
      }
    });
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    ToolsDemo3 td = new ToolsDemo3();
  }

  public void addIdentifyTool() {
    id.setPickWidth(5);
    id.setMap(map);
    id.addPickListener(mpa);
    ImageIcon icon4 = new
        ImageIcon(Resource.getIcon("cmn/identify.gif"));
    JButton btnid = new JButton(icon4);
    btnid.setToolTipText("Identify Features");
    toolbar.add(btnid);
    btnid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.setSelectedTool(id);
      }
    });
  }

  public void addSelectionTool() {
    select.setRubberBand(brcircle);
    ImageIcon icon5 = new
        ImageIcon(Resource.getIcon("tb/select_circle.gif"));
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
    });
  }

  public void showSelection(Object[][] row, Object[] col) {
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

  class MyPickAdapter implements PickListener {
    private Object[][] row;
    private String[] col;
    int maxrow = 10;

    public void beginPick(com.esri.mo2.ui.bean.PickEvent pe) {
      System.out.println("beginPick() called..");
    }

    public void foundData(com.esri.mo2.ui.bean.PickEvent e) {
      System.out.println("FoundData() called..");
      com.esri.mo2.data.feat.Cursor c =
          e.getCursor();

      FeatureLayer flayer = (FeatureLayer) e.getLayer();
      SelectionSet ss =
          new BaseSelectionSet(flayer.getFeatureClass());

      row = new Object[maxrow][maxrow];
      col = new String[maxrow];
      int nflds = 0;
      int count = 0;
      Feature f = null;
      Fields flds = null;
      while (c.hasMore()) {
        f = (Feature) c.next();

        //creates the column names for the table.
        if (flds == null) {
          flds = f.getFields();
          //only displays first 10 fields
          nflds = flds.size() > maxrow ? maxrow : flds.size();

          //Object arrays for the table
          for (int i = 0; i < nflds; i++) {
            col[i] = flds.getField(i).getName();
          }
        }
        count = count + 1;

        //makes the coulmn/row values for the table.
        for (int j = 0; j < nflds; j++) {
          row[count - 1][j] = f.getValue(j);
          ss.add(f.getDataID());
        }
      } // end of while

      //Highlighting the selected features
      flayer.setSelectionColor(Color.orange);
      flayer.setSelectionSet(ss);
    }

    public void endPick(com.esri.mo2.ui.bean.PickEvent ee) {
      showSelection(row, col);
    }
  }

  class MySelect extends Select {
    public void rubberbandCompleted() {
      SelectionSet ss = this.getSelection();
    }
  }

}
