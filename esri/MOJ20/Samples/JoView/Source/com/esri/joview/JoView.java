package com.esri.joview;

import javax.swing.UIManager;
import java.awt.*;

/**
 * JoView is an unsupported sample application for MapObjects(R) - Java(TM) Edition
 * It demonstrates much of MOJ functionality
 * within a simple application framework. You may use the Java code in JoView
 * to jump-start your application development.
 *
 *
 * June 2002
 * MOJ team
 * Environmental Systems Research Institute, Inc.
 * www.esri.com
 */

public class JoView {
  boolean packFrame = false;
  protected static JoViewUI frame;
  /**
   * Construct the application
   */
  public JoView() {
    frame = new JoViewUI();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  /**
   * Main method
   */

  public static void main(String[] args) {
    try {

      //CQ00166112 button has focus but Enter does not work
      UIManager.put("Button.focusInputMap", new javax.swing.UIDefaults.LazyInputMap(new Object[] {
            "SPACE", "pressed",
            "released SPACE", "released",
            "ENTER", "pressed",
            "released ENTER", "released"
      }));

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      com.esri.mo2.util.MessageCentre.get().addMessageListener(new MessageLister());

    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new JoView();
  }
}

// class handles messages posted to the MessageCentre by displaying them on System.out and System.err
final class MessageLister implements com.esri.mo2.util.MessageListener {
    public void exception(com.esri.mo2.util.MessageEvent event) {
        System.err.println("MessageCentre: exception="+event.getException().toString());
    }
    public void message(com.esri.mo2.util.MessageEvent event) {
        System.err.println("MessageCentre: message="+event.getMessage());
    }
}
