
import com.esri.mo2.data.feat.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.util.Resource;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A simple example of how to use the AttributeTable component.
 */
public class AttributeTableFrame extends javax.swing.JFrame {

    private Map map;
    private Layer layer;
    private JToolBar toolbar;

    public AttributeTableFrame() {
        this.setTitle("Attribute table");
        this.setSize(400,400);
        map = new Map();
        this.getContentPane().setLayout(new java.awt.BorderLayout());
        addLayer();
        addToolBar(layer);
        this.setResizable(false);
        this.show();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Adds the layer to the map
     */
    private void addLayer() {
        //the String for the file connection
        String fileconnection = "com.esri.mo2.src.file.FileSystemConnection!";
        layer = new Layer();
        layer.setDataset(
            fileconnection + "C:/ESRI/MOJ20/Samples/Data/USA/states.shp!");

        //add the layer to the map and redraw it
        map.add(layer);
        this.getContentPane().add(map, BorderLayout.CENTER);
        map.redraw();
    }

    /**
     * Adds an Attribute Table button to the frame
     * and calls the action when pressed
     */
    private void addToolBar(Layer selLyr) {
        toolbar = new JToolBar();
        this.getContentPane().add(toolbar, BorderLayout.NORTH);

        ImageIcon icon = new ImageIcon(Resource.getIcon("tb/attributes.gif"));
        JButton btn1 = new JButton(icon);
        btn1.setToolTipText("Show AttributeTable");
        toolbar.add(btn1);
        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initAttributes();
            }
        }
        );
    }

    /**
     * Gets the feature layer and gets the feature class from it and
     * assigns it to the Attribute Table
     */
    private void initAttributes() {
        //Get the Feature Layer
        com.esri.mo2.map.dpy.FeatureLayer flayer =
        (com.esri.mo2.map.dpy.FeatureLayer)map.getLayer(0);

        //Get the Fields of the Feature layer
        com.esri.mo2.data.feat.Fields fields =
        flayer.getFeatureClass().getFields();

        com.esri.mo2.ui.tbl.AttributeTable atable =
        new com.esri.mo2.ui.tbl.AttributeTable(flayer.getFeatureClass());

        javax.swing.JDialog d =
            new javax.swing.JDialog(this, "TABLE OF: "+ layer.getName());
        d.setSize(400,300);
        d.getContentPane().setLayout(new java.awt.BorderLayout());
        d.getContentPane().add(atable, java.awt.BorderLayout.CENTER);
        d.show();
    }

    public static void main(String[] args) {
        AttributeTableFrame fd = new AttributeTableFrame();
    }

}

