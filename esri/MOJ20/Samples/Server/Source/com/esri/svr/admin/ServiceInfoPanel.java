package com.esri.svr.admin;

import com.esri.svr.cmn.ServiceInfo;
import java.awt.*;
import javax.swing.*;

/**
 * This panel used to list the detail information for a selected map service
 */

public class ServiceInfoPanel extends JPanel {
    private JLabel serviceInfoTitle = new JLabel();
    private JLabel serviceNameLabel = new JLabel();
    private JLabel serviceTypeLabel = new JLabel();
    private JLabel imageFormatLabel = new JLabel();
    private JLabel outputDirLabel = new JLabel();
    private JLabel outputURLLabel = new JLabel();

    private JLabel AXLContentLabel = new JLabel();
    private JTextArea axlDoc = new JTextArea();

    private JTextField outputURLTxFd = new JTextField();
    private JTextField outputDirTxFd = new JTextField();
    private JTextField imageFormatTxFd = new JTextField();
    private JTextField serviceTypeTxFd = new JTextField();
    private JTextField serviceNameTxFd = new JTextField();
    private JLabel showMapLabel = new JLabel();
    private JLabel mapURLLabel = new JLabel();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private ServiceInfo serviceInfo;

    /**
     * Construct a ServiceInfoPanel
     */
    public ServiceInfoPanel() {
        try {
            init();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() throws Exception {
        serviceInfoTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        serviceInfoTitle.setHorizontalAlignment(SwingConstants.LEFT);
        serviceInfoTitle.setText("Deployed Service Information");
        this.setLayout(gridBagLayout1);

        serviceNameLabel.setText("Service Name:");
        serviceTypeLabel.setText("serviceTypeTxFd:");
        imageFormatLabel.setText("Image Format:");
        outputDirLabel.setText("Output Directory:");
        outputURLLabel.setText("Output URL:");
        AXLContentLabel.setText("Service configuration doc: ");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(axlDoc);

        showMapLabel.setText("");
        serviceNameTxFd.setEditable(false);
        mapURLLabel.setForeground(java.awt.Color.blue);

        this.add(serviceInfoTitle,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(13, 19, 0, 69), 99, 13));
        this.add(outputDirLabel,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 0, 0), 88, 5));
        this.add(outputURLLabel,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 0, 0), 111, 6));
        this.add(AXLContentLabel,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 0, 96), 27, 3));
        this.add(scrollPane,  new GridBagConstraints(0, 8, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 19, 0, 20), 346, 105));
        this.add(outputURLTxFd,  new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 72, 0));
        this.add(outputDirTxFd,  new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 72, 0));
        this.add(imageFormatTxFd,  new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 72, 0));
        this.add(serviceTypeTxFd,  new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 72, 0));
        this.add(serviceNameLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 19, 0, 0), 98, 5));
        this.add(serviceNameTxFd,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(13, 0, 0, 20), 72, 0));
        this.add(imageFormatLabel,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 0, 0), 97, 8));
        this.add(serviceTypeLabel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 0, 0), 110, 4));
        this.add(showMapLabel,  new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 0, 0), 276, 4));
        this.add(mapURLLabel,  new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 10, 0), 350, 4));

        this.setBackground(java.awt.Color.white);

    }

    /**
     * Set service info
     * @param serviceInfo the service info to be displayed
     * @param mapUrlPrefix AdvancedEjbViewer URL prefix
     */
    public void setServiceInfo(ServiceInfo serviceInfo,  String mapUrlPrefix) {

        this.serviceInfo = serviceInfo;

        serviceNameTxFd.setText(serviceInfo.getServiceName());
        serviceTypeTxFd.setText(serviceInfo.getServiceType());
        imageFormatTxFd.setText(serviceInfo.getImageFormat());
        outputDirTxFd.setText(serviceInfo.getOutputDir());
        outputURLTxFd.setText(serviceInfo.getOutputURL());
        axlDoc.setText(serviceInfo.getConfig());
        showMapLabel.setText("Show '" + serviceNameTxFd.getText() + "' map with following URL: ");
        mapURLLabel.setText(mapUrlPrefix + serviceNameTxFd.getText());
    }

    /**
     * Get service info
     * @return a ServiceInfo object
     */
    public ServiceInfo getServiceInfo() {
        ServiceInfo serviceinfo = new ServiceInfo();
        serviceinfo.setServiceName(serviceNameTxFd.getText());
        serviceinfo.setServiceType(serviceTypeTxFd.getText());
        serviceinfo.setImageFormat(imageFormatTxFd.getText());
        serviceinfo.setOutputDir(outputDirTxFd.getText());
        serviceinfo.setOutputURL(outputURLTxFd.getText());
        serviceinfo.setConfig(axlDoc.getText());

        return serviceinfo;
    }

}