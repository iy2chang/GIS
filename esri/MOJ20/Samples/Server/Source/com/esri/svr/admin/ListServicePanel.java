package com.esri.svr.admin;

import java.awt.*;
import javax.swing.*;

/**
 * The panel presents a list of map services
 */

public class ListServicePanel extends JPanel {
    private JLabel listTitle = new JLabel();
    private JLabel listSubTitle = new JLabel();
    private JList serviceList = new JList();
    private JButton showDetailsBtn = new JButton();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    private AdvancedEjbAdmin mojadmin;

    /**
     * Construct a ListServicePanel
     * @param mojadmin AdvancedEjbAdmin ojbect which this class will delegate some functions to
     */
    public ListServicePanel(AdvancedEjbAdmin mojadmin) {
        this.mojadmin = mojadmin;

        try {
            init();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() throws Exception {
        listTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        listTitle.setHorizontalAlignment(SwingConstants.LEFT);
        listTitle.setText("Service List");

        this.setLayout(gridBagLayout1);
        listSubTitle.setText("Here are the deployed services (select one to see details)");
        this.setFont(new java.awt.Font("Dialog", 1, 24));
        serviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showDetailsBtn.setText("Show Service Details");
        this.add(listTitle,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 38, 0, 49), 191, 6));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(serviceList);

        this.add(listSubTitle,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 31, 0, 39), 13, 13));
        this.add(scrollPane,  new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 31, 0, 54), 311, 124));
        this.add(showDetailsBtn,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 114, 28, 119), 16, -5));

        this.setBackground(java.awt.Color.white);

        showDetailsBtn.setEnabled(false);
        showDetailsBtn.addActionListener(new ShowDetailActionListener());
        serviceList.addListSelectionListener(new ServiceListSelectionListener());

    }

    /**
     * Set a list of service names
     * @param serviceNames service name array
     */
    public void setServiceNames(String[] serviceNames) {
        if (serviceNames != null)
            serviceList.setListData(serviceNames);
    }

    private class ShowDetailActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            String name = (String)serviceList.getSelectedValue();
            System.out.println("selected serice name=" + name);
            if (name != null)
                mojadmin.showSelectedService(name);
        }
    }

    private class ServiceListSelectionListener implements javax.swing.event.ListSelectionListener {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            showDetailsBtn.setEnabled(true);
        }
    }
}