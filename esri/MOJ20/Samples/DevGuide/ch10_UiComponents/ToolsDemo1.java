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

public class ToolsDemo1 extends JFrame {
  private Map map = new Map();
  private Layer layer = new Layer();
  private JToolBar toolbar = new JToolBar();
  private TreeToc toc = new TreeToc();

  private Pan pan = new Pan();
  private ZoomIn zi = new ZoomIn();
  private ZoomOut zo = new ZoomOut();

  public ToolsDemo1() {
    init();

    addLayer();
    addToolBar();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void init() {
    setTitle("Tools Demo");
    setSize(600, 400);

    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(toc, BorderLayout.WEST);
    getContentPane().add(toolbar, BorderLayout.NORTH);
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
    ToolsDemo1 td = new ToolsDemo1();
  }
}
