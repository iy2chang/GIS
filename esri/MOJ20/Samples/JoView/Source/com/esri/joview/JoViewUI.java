package com.esri.joview;

import javax.swing.*;
import java.awt.*;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.*;
import com.esri.mo2.ui.toc.*;
import com.esri.mo2.ui.ren.*;
import java.awt.event.*;
import javax.swing.tree.*;

/**
 * This is the main interface for this sample application.
 *
 */

public class JoViewUI extends JFrame {
  JMenuBar jMenuBar1 = new JMenuBar();
  JPanel toolBarsPanel = new JPanel();
  JSplitPane jSplitPane1 = new JSplitPane();
  LayerToolBar _layerToolBar = new LayerToolBar();
  ZoomPanToolBar _zoomPanToolBar = new ZoomPanToolBar();
  SelectionToolBar _selectionToolBar= new SelectionToolBar();
  javax.swing.ButtonGroup bGroup = new  javax.swing.ButtonGroup();
  com.esri.mo2.ui.toc.TreeToc _toc = new com.esri.mo2.ui.toc.TreeToc();
  JScrollPane _tocScrollPane = new JScrollPane(_toc);

  Map _mainMap = new Map();
  boolean legendSelected = false;
  JMenu jMenuFile = new JMenu();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuItemAddLayer = new JMenuItem();
  JMenuItem jMenuItemRemoveLayer = new JMenuItem();
  JMenuItem jMenuItemLayerProperties = new JMenuItem();
  JMenuItem jMenuItemPrint = new JMenuItem();
  JMenuItem jMenuItemExit = new JMenuItem();
  JMenuItem jMenuItemAbout = new JMenuItem();
  //com.esri.mo2.map.dpy.Layer layer = null;
  com.esri.mo2.ui.ren.LayerProperties lp;
  TocPopupMenu _popup = new TocPopupMenu();

  TreeTocListener _treeTocListener = new TreeTocListener();
  LayerListener _layerListener = new LayerListener();
  com.esri.mo2.map.dpy.Layer _selectedLy = null;

  private static java.awt.Image mojIcon16Image=null;

  public JoViewUI() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setSize(800, 600);
    this.setTitle("JoView");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
        });

    mojIcon16Image = com.esri.mo2.util.Resource.getIcon("bean/16/mojava16.gif");
    this.setIconImage(mojIcon16Image);

    this.setJMenuBar(jMenuBar1);
    _mainMap.setMapUnit(com.esri.mo2.util.Units.DECIMAL_DEGREES);  //default at decimal degree
    _mainMap.addLayerListener(_layerListener);

    jMenuFile.setText("File");
    jMenuHelp.setText("Help");

    ImageIcon image3 = new ImageIcon(com.esri.joview.JoView.class.getResource("icons/properties.gif"));
    ImageIcon image4 = new ImageIcon(com.esri.joview.JoView.class.getResource("icons/print.gif"));

    _zoomPanToolBar.setButtonGroup(bGroup);
    _zoomPanToolBar.setRollover(true);
    _zoomPanToolBar.setMap(_mainMap);

    _selectionToolBar.setButtonGroup(bGroup);
    _selectionToolBar.setRollover(true);
    _selectionToolBar.setMap(_mainMap);
    _selectionToolBar.removeAction(SelectionToolBarActions.SEARCH);
    _selectionToolBar.refresh();

    _layerToolBar.setButtonGroup(bGroup);
    _layerToolBar.setRollover(true);
    _layerToolBar.setMap(_mainMap);

    toolBarsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    _tocScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    _tocScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    _toc.setMap(_mainMap);
    _toc.addTocListener(_treeTocListener);
    _toc.setLegendOutOfScaleBehaviour(com.esri.mo2.ui.toc.TreeToc.OUT_OF_SCALE_LEGEND_DISABLE);

    _popup.setMap(_mainMap);
    _popup.setToc(_toc);
    _popup.setLayerToolBar(_layerToolBar);
    _popup.addDefaultActions();
    _toc.setTocPopupMenu(_popup);

    jMenuItemAddLayer.setText("Add Layer");
    javax.swing.Action _addLayerAction =
       _layerToolBar.getActions().getAction(LayerToolBar.ADD_LAYER);
    jMenuItemAddLayer.setAction(_addLayerAction);

    jMenuItemRemoveLayer.setText("Remove Layer");
    javax.swing.Action _removeLayerAction =
       _layerToolBar.getActions().getAction(LayerToolBar.REMOVE_LAYER);
    jMenuItemRemoveLayer.setAction(_removeLayerAction);


    jMenuItemLayerProperties.setText("Layer Properties");
    jMenuItemLayerProperties.setIcon(image3);
    jMenuItemLayerProperties.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItemLayerProperties_actionPerformed(e);
      }
    });

    jMenuItemPrint.setText("Print");
    jMenuItemPrint.setIcon(image4);
    jMenuItemPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItemPrint_actionPerformed(e);
      }
    });
    jMenuItemExit.setText("Exit");
    jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItemExit_actionPerformed(e);
      }
    });
    jMenuItemAbout.setText("About");
    jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItemAbout_actionPerformed(e);
      }
    });
    this.getContentPane().add(toolBarsPanel, BorderLayout.NORTH);
    toolBarsPanel.add(_layerToolBar, FlowLayout.LEFT);
    toolBarsPanel.add(_zoomPanToolBar,FlowLayout.CENTER);
    toolBarsPanel.add(_selectionToolBar, FlowLayout.RIGHT);
    this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(_tocScrollPane, JSplitPane.LEFT);
    jSplitPane1.add(_mainMap, JSplitPane.RIGHT);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    jMenuFile.add(jMenuItemAddLayer);
    jMenuFile.add(jMenuItemRemoveLayer);
    //jMenuFile.add(jMenuItemLayerProperties);
    jMenuFile.add(jMenuItemPrint);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuItemExit);
    jMenuHelp.add(jMenuItemAbout);
    jSplitPane1.setDividerLocation(150);
    //jMenuItemRemoveLayer.setEnabled(false);
    //jMenuItemLayerProperties.setEnabled(false);
  }



    private void displayLayerProperties(com.esri.mo2.map.dpy.DatasetLayer ly){
      LayerProperties lp = new LayerProperties();
      //lp.setLegend(_legend);
      //lp.addWindowListener( new WindowClosingListener());
      lp.setLayer(ly);
      lp.setMap(_mainMap);
      lp.setSelectedTabIndex(0);
      lp.setVisible(true);
  }

  private void displayLayerProperties(com.esri.mo2.ui.toc.LegendNode lNode){
      LayerProperties lp = new LayerProperties();
      lp.setLayer((com.esri.mo2.map.dpy.DatasetLayer)lNode.getLayer());
      lp.setMap(_mainMap);
      lp.setSelectedTabIndex(0);
      lp.setModal(true);
      lp.setVisible(true);
      if (!lp.isCancelled){
        //lNode.refresh();
        ((DefaultTreeModel)_toc.getModel()).reload(lNode);
        lNode.refresh();
      }
   }

/**
 * Pop up the About box
 */
  void jMenuItemAbout_actionPerformed(ActionEvent e) {
      About aboutBox= new About();
      aboutBox.setSize(420,265);
      aboutBox.show();
  }

  /**
   * Pop up the layerProperties UI using a menu
   */
  void jMenuItemLayerProperties_actionPerformed(ActionEvent e) {
      displayLayerProperties((com.esri.mo2.map.dpy.DatasetLayer)_selectedLy);
  }

  /**
   * Print the map using a menu
   */
  void jMenuItemPrint_actionPerformed(ActionEvent e) {
      com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
      mapPrint.setMap(_mainMap);
      mapPrint.doPrint();
  }

  /**
   * Exit
   */
  void jMenuItemExit_actionPerformed(ActionEvent e) {
      this.dispose();
      System.exit(0);
  }

  void this_windowClosing(WindowEvent e) {
    jMenuItemExit_actionPerformed(null);
  }

  /**
  * This class is for listening the layer remove event in the map.
  * When the selected layers are removed from the map, some toolbars
  * should be notified accordingly.
  *
  */

 class LayerListener extends com.esri.mo2.map.dpy.LayerAdapter {
    public void remove (com.esri.mo2.map.dpy.LayerEvent e){
      _selectionToolBar.setSelectedLayer(null);
      //_layerToolBar.setSelectedLayer(null);
      _zoomPanToolBar.setSelectedLayer(null);
    }
 }

 /**
  * This class is for listening three events from the toc
  *
  */
 class TreeTocListener extends com.esri.mo2.ui.toc.TreeTocAdapter {
    public void click(TreeTocEvent e) {
      java.awt.event.MouseEvent mEvent = e.getEvent();
      _layerToolBar.setSelectedLayers(_toc.getSelectedLayers());
      _zoomPanToolBar.setSelectedLayers(_toc.getSelectedLayers());
      //com.esri.mo2.map.dpy.Layer ly = null;
      com.esri.mo2.ui.toc.LegendNode lgNode = null;
      int selectedLayers = _toc.getSelectedLegendNodes().size();
      if ( selectedLayers == 1){
        lgNode = (com.esri.mo2.ui.toc.LegendNode) _toc.getSelectedLegendNodes().get(0);
        _selectedLy = lgNode.getLayer();
      }else{
        _selectedLy = null;
      }
      _zoomPanToolBar.setSelectedLayer(_selectedLy);
      _selectionToolBar.setSelectedLayer(_selectedLy);
      TreePath treePath =_toc.getClosestPathForLocation(mEvent.getX(),mEvent.getY());
      if (treePath == null) return ;
      DefaultMutableTreeNode n = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      /*//if (javax.swing.SwingUtilities.isRightMouseButton(mEvent)) {
        if (n instanceof LegendNode) {    // two more methods should be set up
          LegendNode lnode = (LegendNode)n;
          //_popup.setLayer(lnode.getLayer());
          //_popup.setLegendNode(lnode);
           _popup.setClickedNode(lnode);
         }
        _popup.checkAllDefaultActions(n);
        //_popup.show(_toc,mEvent.getX(),mEvent.getY());
      //}else*/

      if(mEvent.getClickCount()==2){
        if (n instanceof LegendNode) {    // popup the layer properties UI
          LegendNode lnode = (LegendNode)n;
          displayLayerProperties(lnode);
          _toc.refresh(lnode);
        }
      }
    }

    public void check(TreeTocEvent e) { }

    public void swatch(TreeTocEvent e) { }
    }
}
