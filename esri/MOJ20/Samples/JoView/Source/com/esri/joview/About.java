package com.esri.joview;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import com.esri.mo2.util.Resource;
import com.esri.mo2.ui.bean.Version;

/**
 * The interface for About box
 *
 */

public class About extends JDialog {
  JPanel panel1 = new JPanel();
  TitledBorder titledBorder1;
  JLabel lblImage = new JLabel();
  JLabel lblAppTitle = new JLabel();
  JLabel lblVersion = new JLabel();
  JLabel lblInfo = new JLabel();
  JButton btnSysInfo = new JButton();
  JButton btnOK = new JButton();
  JLabel lblMOJVersion = new JLabel();

  public About(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public About() {
    this(null, "", false);
  }

  void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    panel1.setLayout(null);
    //ImageIcon image1 = new ImageIcon(com.esri.joview.JoView.class.getResource("icons/mojimage.jpg"));
    ImageIcon image1 = new ImageIcon(com.esri.mo2.util.Resource.getIcon("mojimage.jpg"));
    lblImage.setIcon(image1);
    lblImage.setBounds(new Rectangle(-1, 108, 426, 85));
    lblAppTitle.setFont(new java.awt.Font("Dialog", 1, 40));
    lblAppTitle.setForeground(new Color(203, 138, 112));
    lblAppTitle.setText("JoView");
    lblAppTitle.setBounds(new Rectangle(8, 0, 220, 48));

    lblVersion.setFont(new java.awt.Font("Dialog", 1, 12));
    lblVersion.setText("Version 2.0");
    lblVersion.setBounds(new Rectangle(324, 8, 83, 19));
    String mojStr ="Developed using MapObjects(R) -- Java(TM) Standard Edition";
    //mojStr = mojStr + com.esri.mo2.ui.bean.Version.RELEASE_VERSION + " Build " + com.esri.mo2.ui.bean.Version.BUILD;
    lblInfo.setFont(new java.awt.Font("Dialog", 0, 12));
    lblInfo.setText("Developed using MapObjects(R) -- Java(TM) Edition");
    lblInfo.setBounds(new Rectangle(6, 66, 387, 20));
    lblMOJVersion.setText("Version " + Version.RELEASE_VERSION + " Build " + Version.BUILD);
    lblMOJVersion.setFont(new java.awt.Font("Dialog", 0, 12));
    lblMOJVersion.setBounds(new Rectangle(6, 90, 373, 17));
    //this.setSize(450,400);
    this.setResizable(false);
    this.setTitle("About JoView");

    btnSysInfo.setBounds(new Rectangle(211, 200, 120, 30));
    btnSysInfo.setPreferredSize(new Dimension(90, 30));
    btnSysInfo.setMargin(new Insets(2, 5, 2, 5));
    btnSysInfo.setText("System Info...");
    btnSysInfo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnSysInfo_actionPerformed(e);
      }
    });
    btnOK.setBounds(new Rectangle(113, 200, 90, 30));
    btnOK.setPreferredSize(new Dimension(90, 30));
    btnOK.setText("OK");
    btnOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnOK_actionPerformed(e);
      }
    });

    this.getContentPane().add(panel1, BorderLayout.CENTER);
    panel1.add(lblAppTitle, null);
    panel1.add(lblImage, null);
    panel1.add(lblVersion, null);
    panel1.add(btnOK, null);
    panel1.add(btnSysInfo, null);
    panel1.add(lblInfo, null);
    panel1.add(lblMOJVersion, null);
  }

  /**
   * Set the title for the application. The default: JoView
   */
  public void setAppTitle(String str){
    lblAppTitle.setText(str);
  }

  /**
   * Set the version number for the application. The default: Version 4.0
   */
   public void setAppVersion(String str){
    lblVersion.setText(str);
  }

  /**
   * Set other information
   * The default: Developed using MapObjects(R) -- Java(TM) Standard Edition
   */
   public void setInfo(String str){
    lblInfo.setText(str);
   }

  void btnSysInfo_actionPerformed(ActionEvent e) {
       javax.swing.JOptionPane.showMessageDialog(this,
          Resource.getBundleText("JavaVersion") + " " + System.getProperty("java.version") +
          Resource.getBundleText("JavaVMVersion") + " " +
          System.getProperty("java.vm.version") + Resource.getBundleText("JavaVMVendor") + " " +
          System.getProperty("java.vm.vendor") + Resource.getBundleText("OSArchitectureName") + " " +
          System.getProperty("os.arch") + " " + System.getProperty("os.name"),
          Resource.getBundleText("SystemInformation"), javax.swing.JOptionPane.INFORMATION_MESSAGE,null);
  }

  void btnOK_actionPerformed(ActionEvent e) {
      dispose();
  }

}
