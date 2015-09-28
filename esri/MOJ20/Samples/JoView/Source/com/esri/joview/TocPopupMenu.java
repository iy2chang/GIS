package com.esri.joview;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * This popup menu is extended from com.esri.mo2.ui.toc.TocPopup.
 *
 */

public class TocPopupMenu extends com.esri.mo2.ui.toc.TreeTocPopup {
  com.esri.mo2.ui.cmn.Action _action = null;

  /**
   * The default constructor
   */
  public TocPopupMenu() {

  }

 /**
  * By overwriting this method, clicking the "Add Data" menu will equal to
  * clicking the "Add Data" button in the toolbar
  */
  public void openAddDataPanel(){
     System.out.println("open add data panel from toc");
     _action.actionPerformed(null);
  }

 /**
  * Setting a LayerToolBar with this popup menu for retriving
  * the "Add Layer" action
  * @param lyToolBar
  */
  public void setLayerToolBar(com.esri.mo2.ui.tb.LayerToolBar lyToolBar){
     _action = lyToolBar.getActions().getAction(com.esri.mo2.ui.tb.LayerToolBar.ADD_LAYER);
  }
}
