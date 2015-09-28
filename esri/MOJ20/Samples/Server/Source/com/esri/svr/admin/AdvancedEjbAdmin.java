package com.esri.svr.admin;

import com.esri.svr.ejb.CatalogHome;
import com.esri.svr.ejb.Catalog;
import com.esri.svr.cmn.ServiceInfo;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Properties;
import java.io.InputStream;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.rmi.PortableRemoteObject;

import javax.swing.*;

/**
 * This is the main class that acts as a standalone admin too
 * for the adavnced EJB sample
 */
public class AdvancedEjbAdmin extends JFrame {

    private JButton listServiceBtn = new JButton(listIcon);
    private JButton addServiceBtn = new JButton(addIcon);
    private JButton removeServiceBtn = new JButton(removeIcon);
    private JButton refreshServiceBtn = new JButton(refreshIcon);
    private JButton titleBtn = new JButton(titleIcon);

    private ContentPanel contentPane;
    private ListServicePanel listServicePane;
    private AddServicePanel addServicePane;
    private ServiceInfoPanel serviceInfoPane;
    private JTextArea messageArea = new JTextArea();
    private java.awt.Color backColor = new java.awt.Color(100, 220, 255);
    private JScrollPane messagePane = new JScrollPane();

    private static CatalogHome _catalogHome;
    private static Catalog _catalog;
    private static InitialContext _jndiContext;

    private final static String  AddServicePane = "AddServicePane";
    private final static String  MessagePane = "MessagePane";
    private final static String  ListServicePane = "ServiceListPane";
    private final static String  ServiceInfoPane = "ServiceInfoPane";

    private static ImageIcon addIcon;
    private static ImageIcon listIcon;
    private static ImageIcon titleIcon;
    private static ImageIcon removeIcon;
    private static ImageIcon refreshIcon;

    //For EJB Container, default to JBOSS
    private static String InitialContextFactory = "org.jnp.interfaces.NamingContextFactory";
    private static String ContextProviderURL = "jnp://localhost:1099";
    private static String ContextURLPKGPrefixes = "org.jboss.naming:org.jnp.interfaces";
    private static String MapUrlPrefix = "http://localhost:8080/AdvancedEjbViewer/viewer.htm?ServiceName=";

    // load icons and other properties here
    static {
        addIcon = new ImageIcon(
                java.awt.Toolkit.getDefaultToolkit().
                    createImage(com.esri.svr.admin.AdvancedEjbAdmin.class.
                        getResource("icon/addservice.gif")));
        listIcon = new ImageIcon(
                java.awt.Toolkit.getDefaultToolkit().
                    createImage(com.esri.svr.admin.AdvancedEjbAdmin.class.
                        getResource("icon/listservices.gif")));
        titleIcon = new ImageIcon(
                java.awt.Toolkit.getDefaultToolkit().
                    createImage(com.esri.svr.admin.AdvancedEjbAdmin.class.
                        getResource("icon/mojserveradmin.png")));
        removeIcon = new ImageIcon(
                java.awt.Toolkit.getDefaultToolkit().
                    createImage(com.esri.svr.admin.AdvancedEjbAdmin.class.
                        getResource("icon/removeservice.gif")));
        refreshIcon = new ImageIcon(
                java.awt.Toolkit.getDefaultToolkit().
                    createImage(com.esri.svr.admin.AdvancedEjbAdmin.class.
                        getResource("icon/refreshservice.gif")));

        ClassLoader cl =  Thread.currentThread().getContextClassLoader();
        InputStream is =  cl.getResourceAsStream("mojadmin.properties");
        Properties jp = new Properties() ;
        try {
            jp.load(is);
            InitialContextFactory = jp.getProperty("InitialContextFactory");
            ContextProviderURL = jp.getProperty("ContextProviderURL");
            ContextURLPKGPrefixes = jp.getProperty("ContextURLPKGPrefixes");
            MapUrlPrefix = jp.getProperty("MapUrlPrefix");
        } catch (java.io.IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Default constructor
     */
    public AdvancedEjbAdmin() {
        try {
            this.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    System.out.println("Winddow is closing");
                    System.exit(0);
                }
            });

            createUI();
            initJNDI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a selected service info
     * @param serviceName the selected service name
     */
    public void showSelectedService(String serviceName) {
        try {
            ServiceInfo[] serviceInfos =  _catalog.getAllServices();
            for (int i=0; i<serviceInfos.length; i++) {
                if (serviceName.equals(serviceInfos[i].getServiceName())) {
                    serviceInfoPane.setServiceInfo(serviceInfos[i], MapUrlPrefix);
                    contentPane.showPane(ServiceInfoPane);
                    removeServiceBtn.setEnabled(true);
                    refreshServiceBtn.setEnabled(true);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a service info to the admin tool
     * @param serviceinfo the service info to added
     */
    public void addService(ServiceInfo serviceinfo) {
        try {
            _catalog.addService(
                serviceinfo.getServiceName(),
                serviceinfo.getServiceType(),
                serviceinfo.getOutputDir(),
                serviceinfo.getOutputURL(),
                serviceinfo.getImageFormat(),
                serviceinfo.getConfig());
            messageArea.setText("The new service '" + serviceinfo.getServiceName() + "' has been added successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            messageArea.setText("There is a problem in adding the new service '" + serviceinfo.getServiceName() + ". The error message is '" + ex.getMessage() + "'");
        }
        contentPane.showPane(MessagePane);
    }


    private void createUI() {
        listServicePane = new ListServicePanel(this);
        addServicePane = new AddServicePanel(this);
        serviceInfoPane = new ServiceInfoPanel();

        contentPane = new ContentPanel();
        setTitle("MapObjec--Java AdvancedEJB Admin Tool");

        titleBtn.setToolTipText("MOJ Server Admin");
        titleBtn.setBackground(java.awt.Color.white);
        getContentPane().add(titleBtn, BorderLayout.NORTH);

        listServiceBtn.setToolTipText("List deployed services");
        addServiceBtn.setToolTipText("Add a new service");
        removeServiceBtn.setToolTipText("Remove a service");
        refreshServiceBtn.setToolTipText("Refresh a service");

        removeServiceBtn.setEnabled(false);
        refreshServiceBtn.setEnabled(false);

        listServiceBtn.addActionListener(new ListServiceListener());
        addServiceBtn.addActionListener(new AddServiceListener());
        removeServiceBtn.addActionListener(new RemoveServiceListener());
        refreshServiceBtn.addActionListener(new RefreshServiceListener());

        listServiceBtn.setBackground(backColor);
        addServiceBtn.setBackground(backColor);
        removeServiceBtn.setBackground(backColor);
        refreshServiceBtn.setBackground(backColor);

        JPanel commandPane = new JPanel();
        commandPane.setLayout(new BoxLayout(commandPane, BoxLayout.Y_AXIS));
        commandPane.add(listServiceBtn);
        commandPane.add(addServiceBtn);
        commandPane.add(removeServiceBtn);
        commandPane.add(refreshServiceBtn);
        commandPane.setBackground(backColor);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, commandPane, contentPane);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void initJNDI()
        throws Exception
    {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory);
        env.put(Context.PROVIDER_URL, ContextProviderURL);
        if (ContextURLPKGPrefixes != null && !ContextURLPKGPrefixes.equals(""))
            env.put(Context.URL_PKG_PREFIXES, ContextURLPKGPrefixes);

        // Get a naming context
        _jndiContext = new InitialContext(env);

        // catalog
        Object cat  = _jndiContext.lookup("ejb/Catalog");
        _catalogHome = (CatalogHome) PortableRemoteObject.narrow(cat, CatalogHome.class);
        _catalog = _catalogHome.create();
    }

    private class ListServiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String[] serviceNames = {};

            try {
                ServiceInfo[] serviceInfos =  _catalog.getAllServices();
                serviceNames = new String[serviceInfos.length];
                for (int i=0; i<serviceInfos.length; i++) {
                    serviceNames[i] = serviceInfos[i].getServiceName();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            listServicePane.setServiceNames(serviceNames);
            contentPane.showPane(ListServicePane);

            removeServiceBtn.setEnabled(false);
            refreshServiceBtn.setEnabled(false);
        }
    }

    private class AddServiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            removeServiceBtn.setEnabled(false);
            refreshServiceBtn.setEnabled(false);
            contentPane.showPane(AddServicePane);
        }
    }

    private class RemoveServiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String serviceName = serviceInfoPane.getServiceInfo().getServiceName();
            try {
                _catalog.removeService(serviceName);
                messageArea.setText("The service '" + serviceName + "' has been removed successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                messageArea.setText("There is a problem in removing the new service '" + serviceName + ". The error message is '" + ex.getMessage() + "'");
            }
            contentPane.showPane(MessagePane);
            removeServiceBtn.setEnabled(false);
            refreshServiceBtn.setEnabled(false);
        }
    }

    private class RefreshServiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            ServiceInfo serviceInfo = serviceInfoPane.getServiceInfo();
            try {
                _catalog.refreshService(
                        serviceInfo.getServiceName(),
                        serviceInfo.getServiceName(),
                        serviceInfo.getServiceType(),
                        serviceInfo.getOutputDir(),
                        serviceInfo.getOutputURL(),
                        serviceInfo.getImageFormat(),
                        serviceInfo.getConfig()
                        );
                messageArea.setText("The service '" + serviceInfo.getServiceName() + "' has been refreshed successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                messageArea.setText("There is a problem in refreshing the new service '" + serviceInfo.getServiceName() + ". The error message is '" + ex.getMessage() + "'");
            }
            contentPane.showPane(MessagePane);
            removeServiceBtn.setEnabled(false);
            refreshServiceBtn.setEnabled(false);
        }
    }

    class ContentPanel extends JPanel {
        private CardLayout cardLayout = new CardLayout();

        public ContentPanel() {
            setLayout(cardLayout);
            messagePane.getViewport().setView(messageArea);
            messageArea.setFont(new java.awt.Font("Dialog", 1, 14));
            messageArea.setText("What do you want to do today?");

            add(messagePane, MessagePane);
            add(listServicePane, ListServicePane);
            add(addServicePane, AddServicePane);
            add(serviceInfoPane, ServiceInfoPane);

            cardLayout.first(this);
            this.setBackground(java.awt.Color.white);
        }

        public void showPane(String name) {
            cardLayout.show(this, name);
        }
    }

//
//
//

    /**
     * Entry point for running standalone AdvancedEJB admin tool. No argument is needed.
     * @param args
     */
    public static void main(String[] args) {
        AdvancedEjbAdmin mojadmin = new AdvancedEjbAdmin();
        mojadmin.setSize(500, 400);
        mojadmin.pack();
        mojadmin.setVisible(true);
    }
}


