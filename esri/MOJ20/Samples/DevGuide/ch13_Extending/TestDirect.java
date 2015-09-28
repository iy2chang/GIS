
import directtranslation.TranslationFeatureClass;

import com.esri.mo2.data.feat.BaseFeatureClass;
import com.esri.mo2.data.feat.MapDataset;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.Util;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.toc.TreeToc;

import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * Simple app that loads a custom FeatureClass into a Layer and 
 * displays it in a Map.  The custom FeatureClass in this case is
 * a BaseFeatureClass created using the direct translation strategy.
 */
public class TestDirect extends JFrame {

  Map map1;
  TreeToc toc1;
  SelectionToolBar selToolBar;
  ZoomPanToolBar zpToolBar;
  BaseFeatureLayer featureLayer1;
  File testFile = new File ("C:\\ESRI\\MOJ20\\Samples\\DevGuide\\ch13_Extending\\Capitols.ptxt");


  //constructor
  public TestDirect() {
    initUi();
    try {
      initLayer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a FeatureClass, uses it to create a Layer, then adds
   * the Layer to a Map for display.
   */
  private void initLayer() throws Exception {
    BaseFeatureClass testClass = new TranslationFeatureClass(testFile);
    BaseFeatureLayer featureLayer1 = new BaseFeatureLayer(testClass, Util.constructDefaultRenderer(MapDataset.POINT));
    map1.addLayer(featureLayer1);
    selToolBar.setSelectedLayer(featureLayer1);
  }

  /**
   * Sets up the user interface.
   */
  private void initUi() {
    map1 = new Map();
    toc1 = new TreeToc();
    toc1.setBackground(Color.LIGHT_GRAY);
    toc1.setMap(map1);
    selToolBar = new SelectionToolBar();
    selToolBar.setMap(map1);
    zpToolBar = new ZoomPanToolBar();
    zpToolBar.setMap(map1);
    Container mainFrame = this.getContentPane();
    mainFrame.setLayout(new BorderLayout());
    mainFrame.add(map1, BorderLayout.CENTER);
    mainFrame.add(selToolBar, BorderLayout.SOUTH);
    mainFrame.add(zpToolBar, BorderLayout.NORTH);
    mainFrame.add(toc1, BorderLayout.WEST);
    this.setSize(700, 300);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setTitle("Show the Direct Translation Strategy");
  }

  public static void main (String[] args) {
    new TestDirect().setVisible(true);
  }

}
