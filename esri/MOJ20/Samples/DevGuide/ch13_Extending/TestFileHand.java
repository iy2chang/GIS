

import memoryfeature.MemoryFileHandler;

import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.draw.Util;
import com.esri.mo2.src.file.FileSystemConnection;
import com.esri.mo2.src.file.FileSystemSource;
import com.esri.mo2.src.sys.Connection;
import com.esri.mo2.src.sys.Content;
import com.esri.mo2.src.sys.Source;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.toc.TreeToc;

import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * Simple app that loads a Layer from a custom FileHandler into a Map.
 */
public class TestFileHand extends JFrame {

  Map map1;
  TreeToc toc1;
  SelectionToolBar selToolBar;
  ZoomPanToolBar pzToolBar;
  FeatureLayer featureLayer1;
  File testFile = new File ("C:\\ESRI\\MOJ20\\Samples\\DevGuide\\ch13_Extending\\Capitols.ptxt");


  //constructor
  public TestFileHand() {
    initUi();
    try {
      initLayer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets a Layer from the Content API and loads it into the Map.
   */
  private void initLayer() throws Exception {
    FileSystemSource.addFileHandler(new MemoryFileHandler());
    Connection myConnection = new FileSystemConnection();
    Source mySource = myConnection.constructSource();
    Content myContent = mySource.getContent(testFile.getAbsolutePath());
    if (myContent == null) {
      System.out.println("sorry, that content path string is not valid");
    }
      
    String contentType = FeatureLayer.class.getName();
    featureLayer1 = (FeatureLayer)myContent.getData(contentType);

    map1.addLayer(featureLayer1);
    Envelope ext = map1.getExtent();
    ext.grow(1.1);
    map1.setExtent(ext);
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
    pzToolBar = new ZoomPanToolBar();
    pzToolBar.setMap(map1);
    
    Container mainFrame = this.getContentPane();
    mainFrame.setLayout(new BorderLayout());
    mainFrame.add(map1, BorderLayout.CENTER);
    mainFrame.add(selToolBar, BorderLayout.SOUTH);
    mainFrame.add(pzToolBar, BorderLayout.NORTH);
    mainFrame.add(toc1, BorderLayout.WEST);
    this.setSize(700, 300);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setTitle("Access a Content Using a Custom FileHandler");
  }

  public static void main (String[] args) {
      new TestFileHand().setVisible(true);
  }

}
