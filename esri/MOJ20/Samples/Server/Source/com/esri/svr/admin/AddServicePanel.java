package com.esri.svr.admin;

import com.esri.svr.cmn.ServiceInfo;
import java.awt.*;
import javax.swing.*;

/**
 * This class presents panel for users to add a new map service
 */
public class AddServicePanel extends JPanel {
    private JLabel addServiceTitle = new JLabel();
    private JLabel serviceNameLabel = new JLabel();
    private JLabel serviceTypeLabel = new JLabel();
    private JLabel imageFormatLabel = new JLabel();
    private JLabel outputDirLabel = new JLabel();
    private JLabel outputURLLabel = new JLabel();
    private JLabel axlFileNameLabel = new JLabel();
    private JLabel AXLContentLabel = new JLabel();
    private JButton addServiceBtn = new JButton();
    private JTextArea axlDocArea = new JTextArea();
    private JTextField axlConfigFileTxFd = new JTextField();
    private JButton browseBtn = new JButton();
    private JTextField outputURLTxFd = new JTextField();
    private JTextField outputDirTxFd = new JTextField();
    private JTextField imageFormatTxFd = new JTextField();
    private JTextField serviceTypeTxFd = new JTextField();
    private JTextField serviceNameTxFd = new JTextField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JFileChooser chooser = new JFileChooser();
    private AdvancedEjbAdmin ejbAdmin;

    /**
     * Construct a AddServicePanel
     * @param mojadmin AdvancedEjbAdmin ojbect which this class will delegate some functions to
     */
    public AddServicePanel(AdvancedEjbAdmin mojadmin) {
        this.ejbAdmin = mojadmin;
        try {
            init();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    void init() throws Exception {
        addServiceTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        addServiceTitle.setHorizontalAlignment(SwingConstants.LEFT);
        addServiceTitle.setText("Add a new service");
        this.setLayout(gridBagLayout1);
        serviceNameLabel.setText("Service Name:");
        serviceTypeLabel.setText("Service Type:");
        imageFormatLabel.setText("Image Format:");
        outputDirLabel.setText("Output Directory:");
        outputURLLabel.setText("Output URL:");
        axlFileNameLabel.setText("Service Configuration AXL File:");
        AXLContentLabel.setText("Or You can enter service configuration AXL content here: ");
        addServiceBtn.setText("Add");
        browseBtn.setText("Browse...");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(axlDocArea);
        axlDocArea.addKeyListener(new AddDocKeyListener());

        this.add(addServiceTitle,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(13, 20, 0, 72), 138, 13));
        this.add(outputDirLabel,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 88, 5));
        this.add(outputURLLabel,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 111, 6));
        this.add(axlFileNameLabel,  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 11, 6));
        this.add(AXLContentLabel,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 99), 15, 3));
        this.add(addServiceBtn,  new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(9, 20, 10, 94), 26, -5));
        this.add(scrollPane,  new GridBagConstraints(0, 8, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 20, 0, 30), 386, 107));
        this.add(axlConfigFileTxFd,  new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 116, 0));
        this.add(browseBtn,  new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 8, 0, 30), 3, -3));
        this.add(outputURLTxFd,  new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 116, 0));
        this.add(outputDirTxFd,  new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 116, 0));
        this.add(imageFormatTxFd,  new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 116, -1));
        this.add(serviceTypeTxFd,  new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 115, 2));
        this.add(serviceNameLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 20, 0, 0), 98, 5));
        this.add(serviceNameTxFd,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(13, 0, 0, 0), 114, 0));
        this.add(imageFormatLabel,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 97, 8));
        this.add(serviceTypeLabel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 110, 4));

        this.setBackground(java.awt.Color.white);

        AXLFileFilter filter = new AXLFileFilter();
        filter.addExtension("axl");
        filter.setDescription("ArcXML document");
        chooser.setFileFilter(filter);

        browseBtn.addActionListener(new BrowseActionListener());
        addServiceBtn.addActionListener(new AddActionListener());
        addServiceBtn.setEnabled(false);

        KeyListenerForAll keyListener = new KeyListenerForAll();
        serviceNameTxFd.addKeyListener(keyListener);
        serviceTypeTxFd.addKeyListener(keyListener);
        imageFormatTxFd.addKeyListener(keyListener);
        outputDirTxFd.addKeyListener(keyListener);
        outputURLTxFd.addKeyListener(keyListener);
    }


    private void setAddButtonEnabled() {
        boolean hasName = ((serviceNameTxFd.getText()!=null&&serviceNameTxFd.getText().equals(""))?false:true);
        boolean hasType = ((serviceTypeTxFd.getText()!=null&&serviceTypeTxFd.getText().equals(""))?false:true);
        boolean hasFormat = ((imageFormatTxFd.getText()!=null&&imageFormatTxFd.getText().equals(""))?false:true);
        boolean hasDir = ((outputDirTxFd.getText()!=null&&outputDirTxFd.getText().equals(""))?false:true);
        boolean hasURL = ((outputURLTxFd.getText()!=null&&outputURLTxFd.getText().equals(""))?false:true);
        boolean hasAXLFile = ((axlConfigFileTxFd.getText()!=null&&axlConfigFileTxFd.getText().equals(""))?false:true);
        boolean hasAXLDoc = ((axlDocArea.getText()!=null&&axlDocArea.getText().equals(""))?false:true);

        boolean enabled = (hasName & hasType & hasFormat & (hasDir | hasURL) & (hasAXLFile | hasAXLDoc));
        addServiceBtn.setEnabled(enabled);
    }

    private String getAXLDoc() {
        String axl = axlDocArea.getText();
        if (axl==null || axl.trim().equals("")) {
            // read it from an AXL file
            java.io.File file = chooser.getSelectedFile();
            StringBuffer buf = new StringBuffer();
            try {
                java.io.BufferedReader br =
                    new java.io.BufferedReader(new java.io.FileReader(file));
                String str = br.readLine();
                while (str != null) {
                    buf.append(str);
                    buf.append("\n");
                    str = br.readLine();
                }
                axl = buf.toString();
                axlDocArea.setText(axl);
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("AXL=>'" + axl + "'");
        return axl;
    }

    private class BrowseActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            int returnVal = chooser.showOpenDialog(AddServicePanel.this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to open this file: " +  chooser.getSelectedFile().getName());
                axlConfigFileTxFd.setText(chooser.getSelectedFile().getAbsoluteFile().getAbsolutePath());
                setAddButtonEnabled();
            }
        }
    }

    private class KeyListenerForAll extends java.awt.event.KeyAdapter {
        public void keyTyped(java.awt.event.KeyEvent evt){
            setAddButtonEnabled();
        }
    }

    private class AddActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            System.out.println("adding a new service");

            String axlContents = getAXLDoc();

            ServiceInfo serviceinfo = new ServiceInfo();
            serviceinfo.setServiceName(serviceNameTxFd.getText());
            serviceinfo.setServiceType(serviceTypeTxFd.getText());
            serviceinfo.setImageFormat(imageFormatTxFd.getText());
            serviceinfo.setOutputDir(outputDirTxFd.getText());
            serviceinfo.setConfig(axlContents);

            ejbAdmin.addService(serviceinfo);

            // clean up the panel
            serviceNameTxFd.setText("");
            serviceTypeTxFd.setText("");
            imageFormatTxFd.setText("");
            outputDirTxFd.setText("");
            axlConfigFileTxFd.setText("");
            axlDocArea.setText("");
        }
    }

    private class AddDocKeyListener extends java.awt.event.KeyAdapter {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            String axlStr = axlDocArea.getText();
            if (axlStr!=null && axlStr.length()>20) setAddButtonEnabled();
        }
    }
}