
import com.esri.mo2.cs.gdb.GeodeticDatabase;
import com.esri.mo2.cs.gdb.GeodeticTable;
import com.esri.mo2.cs.gdb.GeodeticRow;
import com.esri.mo2.cs.gdb.Parameter;
import com.esri.mo2.cs.gdb.TableObjectParameter;
import com.esri.mo2.cs.gdb.InvalidRowDataException;
import com.esri.mo2.cs.gdb.CoordinateSystemSetTable;
import com.esri.mo2.cs.geod.ReferencedCoordinateSystem;
import com.esri.mo2.cs.geod.Ellipsoid;
import com.esri.mo2.cs.geod.GeographicCoordinateSystem;
import com.esri.mo2.cs.geod.LinearUnit;
import com.esri.mo2.cs.geod.ValidationException;
import com.esri.mo2.cs.geod.AngularUnit;
import com.esri.mo2.cs.geod.Unit;
import com.esri.mo2.cs.geod.PrimeMeridian;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Arrays;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Copyright (C) 2003 by Environmental Systems Research Institute Inc.
 * All Rights Reserved.

 * The ViewCoordSys.java utility will extract coordinate system, datum,
 * and projection information from the GeodeticDB.txt file in the esri_mo20res.jar.
 * Data is retrieved in a table structure format from each of the following categories:

 * Referenced Datums
 * Prime Meridians
 * Parameter Schemas
 * Projection Schemas
 * Datum Transformation Schemas
 * Ellipsoids
 * Geographic Coordinate Systems
 * Linear Units
 * Angular Units
 * Coordinate System Sets

 * Each category will have specific attribute columns displayed.
 * The EPSG IDs are also included for the Geographic Coordinate Systems
 * and Referenced Datums.To run the application, first compile it, then run it,
 * making sure all the MapObjects-Java jars installed in the MOJ20/lib
 * directory are referenced in your classpath.
 */
public class ViewCoordSys extends JFrame {
    JTabbedPane tabbedPane;
    JPanel      ellipsoidPanel;
    JPanel      angleUnitPanel;
    JPanel      linearUnitPanel;
    JPanel      gcsPanel;
    JPanel      setsPanel;
    JPanel      refDatumPanel;
    JPanel      primeMeridianPanel;
    JPanel      parameterPanel;
    JPanel      projectionPanel;
    JPanel      datumTransformationPanel;
    GeodeticDatabase _db;

  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }
    public static void main(String[] args) {
        new ViewCoordSys();
    }
    public ViewCoordSys(){
        _db = GeodeticDatabase.getDatabase();
        setSize(1024, 800);
        init();
        setVisible(true);
    }
    void init(){
        tabbedPane = new JTabbedPane();

        ellipsoidPanel = new EllipsoidPanel();
        gcsPanel = new GCSPanel();
        linearUnitPanel = new LinearUnitPanel();
        angleUnitPanel = new AngularUnitPanel();
        primeMeridianPanel = new PrimeMeridianPanel();
        parameterPanel = new ParameterPanel();
        projectionPanel = new ProjectionPanel();
        datumTransformationPanel = new DatumSchemaPanel();
        refDatumPanel = new ReferencedDatumPanel();
        setsPanel = new CoordSystemSetPanel();

        tabbedPane.addTab("Ellipsoids", ellipsoidPanel);
        tabbedPane.addTab("Geographic CoordinateSystems", gcsPanel);
        tabbedPane.addTab("Linear Units", linearUnitPanel);
        tabbedPane.addTab("Angular Units", angleUnitPanel);
        tabbedPane.addTab("Coordinate System Sets", setsPanel);
        tabbedPane.addTab("Referenced Datums", refDatumPanel);
        tabbedPane.addTab("Prime Meridians", primeMeridianPanel);
        tabbedPane.addTab("Parameter Schemas", parameterPanel);
        tabbedPane.addTab("Projection Schemas", projectionPanel);
        tabbedPane.addTab("Datum Transformation Schemas", datumTransformationPanel);
        tabbedPane.setSelectedIndex(0);
        this.getContentPane().add(tabbedPane);
        tabbedPane.addChangeListener(new TabListener());
    }
    public class TabListener implements ChangeListener{
        public void stateChanged(ChangeEvent e) {
            switch(tabbedPane.getSelectedIndex()){

            }
        }
    }

    class EllipsoidPanel extends JPanel{
        public String[] columnNames = {"Name", "Description", "Long Description", "Semi-major", "Inverse Flattening"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        EllipsoidPanel(){
            fillData();
            _model = new MyTableModel(5, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getEllipsoidTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][5];
            int row = 0;
            int column = 0;
            iter = table.iterator();
            while (iter.hasNext()){
                Ellipsoid e = (Ellipsoid)iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getLongDescription();
                _data[row][column++]=Double.toString(e.semiMajor());
                _data[row][column++]=Double.toString(e.flattening());
                column = 0;
                row++;
            }
        }


    }
    public static class MyTableModel extends AbstractTableModel{
        int _numColumns;
        int _numRows;
        Object[][]_data;
        String []_columnNames;

        public MyTableModel(int numColumns, int rowCount, String []columnNames, Object [][]data){
            _columnNames = columnNames;
            _numColumns = numColumns;
            _numRows = rowCount;
            _data = data;
        }
        public int getRowCount() {
            return _numRows;
        }

        public int getColumnCount() {
            return _numColumns;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return _data[rowIndex][columnIndex];
        }

        public String getColumnName(int column) {
            return _columnNames[column];
        }

    }


    class GCSPanel extends JPanel{
        public String[] columnNames = {"Name", "Description", "Long Description", "Prime Meridian", "EPSG ID", "Ellipsoid Name", "Angular Unit"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        GCSPanel(){
            fillData();
            _model = new MyTableModel(7, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getGeographicCoordinateSystemTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][7];
            int row = 0;
            int column = 0;
            iter = table.iterator();
            while (iter.hasNext()){
                GeographicCoordinateSystem e = (GeographicCoordinateSystem)iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getLongDescription();
                int iat = e.getName().indexOf("@");
                if (iat == -1)
                    _data[row][column++]="GREENWICH";
                else
                    _data[row][column++]=e.getName().substring(iat+1);
                if (e.getCoordSysID() == 0)
                    _data[row][column++]="";
                else
                    _data[row][column++]=Integer.toString(e.getCoordSysID());
                _data[row][column++]=e.getEllipsoid().getName();
                _data[row][column++]=e.getUnit().getName();
                column = 0;
                row++;
            }
        }


    }
    class LinearUnitPanel extends JPanel{

        public String[] columnNames = {"Name", "Description", "Long Description", "Display Suffix", "Factor", "Display Places After Decimal"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        LinearUnitPanel(){
            fillData();
            _model = new MyTableModel(6, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getLinearUnitTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][6];
            int row = 0;
            int column = 0;
            iter = table.iterator();
            while (iter.hasNext()){
                LinearUnit e = (LinearUnit)iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getLongDescription();
                _data[row][column++]=e.getSuffix();
                try {
                    _data[row][column++]=Double.toString(e.convertToMKS(1.0));
                } catch (ValidationException e1) {
                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                _data[row][column++]=Integer.toString(e.getDefaultPrecision());
                column = 0;
                row++;
            }
        }


    }
    class AngularUnitPanel extends JPanel{

        public String[] columnNames = {"Name", "Description", "Long Description", "Java Class Implementation", "Display Suffix", "Factor", "Display Places After Decimal"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        AngularUnitPanel(){
            fillData();
            _model = new MyTableModel(7, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getAngularUnitTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][7];
            int row = 0;
            int column = 0;
            iter = table.iterator();
            while (iter.hasNext()){
                Unit e = (Unit)iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getLongDescription();
                _data[row][column++]=e.getClass().getName();
                _data[row][column++]=e.getSuffix();
                try {
                    if (e instanceof AngularUnit)
                        _data[row][column++]=Double.toString(e.convertToMKS(1.0));
                    else
                        _data[row][column++]="";
                } catch (ValidationException e1) {
                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                _data[row][column++]=Integer.toString(e.getDefaultPrecision());
                column = 0;
                row++;
            }
        }


    }
    class PrimeMeridianPanel extends JPanel{

        public String[] columnNames = {"Name", "Description", "Long Description", "Angular Unit", "Value"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        PrimeMeridianPanel(){
            fillData();
            _model = new MyTableModel(5, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getPrimeMeridianTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][5];
            int row = 0;
            int column = 0;
            iter = table.rowIterator();
            while (iter.hasNext()){
                GeodeticRow e = (GeodeticRow) iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getColumn(2);
                _data[row][column++]=e.getColumn(3);
                _data[row][column++]=e.getColumn(4);
                column = 0;
                row++;
            }
        }


    }

    class ParameterPanel extends JPanel{
//KEY, DESCRIPTION, LONG DESCRIPTION, Java class of parameter, PE NAME, Default value

        public String[] columnNames = {"Name", "Description", "Long Description", "Java Class", "PE Name", "Default Value"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        ParameterPanel(){
            fillData();
            _model = new MyTableModel(6, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getParameterSchemaTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][6];
            int row = 0;
            int column = 0;
            iter = table.rowIterator();
            while (iter.hasNext()){
                GeodeticRow e = (GeodeticRow) iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getColumn(2);
                _data[row][column++]=e.getColumn(3);
                if (e.getLength() <6){
                    _data[row][column++]="";
                    _data[row][column++]=e.getColumn(4);

                }else{
                    if (e.getColumn(4).startsWith("$"))
                        _data[row][column++]="";
                    else
                        _data[row][column++]=e.getColumn(4);
                    _data[row][column++]=e.getColumn(5);
                }
                column = 0;
                row++;
            }
        }


    }

    class ProjectionPanel extends JPanel{
//NAME, DESCRIPTION, LONG DESCRIPTION, Java Class Name, PE Engine Name, PROJECTION INSTANTIATION VALUES

        public String[] columnNames = {"Name", "Description", "Long Description", "Java Class", "PE Name", "Parameters"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        ProjectionPanel(){
            fillData();
            _model = new MyTableModel(6, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getProjectionSchemaTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][6];
            int row = 0;
            int column = 0;
            iter = table.rowIterator();
            while (iter.hasNext()){
                GeodeticRow e = (GeodeticRow) iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getColumn(2);
                _data[row][column++]=e.getColumn(3);
                _data[row][column++]=e.getColumn(4);
                StringBuffer buf = new StringBuffer();
                for(int j=5;j<e.getLength();j++){
                    buf.append(e.getColumn(j));
                    if (j < e.getLength()-1)
                        buf.append(",");
                }
                _data[row][column++]=buf.toString();
                column = 0;
                row++;
            }
        }


    }

    class DatumSchemaPanel extends JPanel{
//NAME ,DESCRIPTION, LONG DESCRIPTION, Java class name, PARAMETER SCHEMAS

        public String[] columnNames = {"Name", "Description", "Long Description", "PE Name", "Java Class","Parameters"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        DatumSchemaPanel(){
            fillData();
            _model = new MyTableModel(6, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getDatumTransformationSchemaTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][6];
            int row = 0;
            int column = 0;
            iter = table.rowIterator();
            while (iter.hasNext()){
                GeodeticRow e = (GeodeticRow) iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getColumn(2);
                _data[row][column++]=e.getColumn(3);
                _data[row][column++]=e.getColumn(4);
                StringBuffer buf = new StringBuffer();
                for(int j=5;j<e.getLength();j++){
                    buf.append(e.getColumn(j));
                    if (j < e.getLength()-1)
                        buf.append(",");
                }
                _data[row][column++]=buf.toString();
                column = 0;
                row++;
            }
        }


    }
//NAME, DESCRIPTION, LONG DESCRIPTION, EPSG DATUM TRANS #, DATUM TRANS TYPE NAME, ELLIPSOID, DATUM TRANS INSTANCE DATA, PRIME MERIDAN (ASSUMED GREENWICH)
//    SPHERE,"GDB829","GDB830", 0, NONE
//    ABIDJAN,"GDB831","GDB832", 8414, MOLODENSKY, -124.76, 53, 466.79

    class ReferencedDatumPanel extends JPanel{
//NAME ,DESCRIPTION, LONG DESCRIPTION, Java class name, PARAMETER SCHEMAS

        public String[] columnNames = {"Name", "Description", "Long Description", "EPSG ID", "Datum Transformation","Values"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private MutableJList _caches;


        ReferencedDatumPanel(){
            fillData();
            _model = new MyTableModel(6, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(){
            //_n = members.size();
            GeodeticTable table = _db.getReferencedDatumTable();
            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][6];
            int row = 0;
            int column = 0;
            iter = table.rowIterator();
            while (iter.hasNext()){
                GeodeticRow e = (GeodeticRow) iter.next();
                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getColumn(2);
                if (e.getColumn(3).equals("0"))
                    _data[row][column++]="";
                else
                    _data[row][column++]=e.getColumn(3);
                _data[row][column++]=e.getColumn(4);
                StringBuffer buf = new StringBuffer();
                for(int j=5;j<e.getLength();j++){
                    buf.append(e.getColumn(j));
                    if (j < e.getLength()-1)
                        buf.append(",");
                }
                _data[row][column++]=buf.toString();
                column = 0;
                row++;
            }
        }


    }



    class CoordSystemSetPanel extends JPanel{
//NAME ,DESCRIPTION, LONG DESCRIPTION, Java class name, PARAMETER SCHEMAS

        public String[] columnNames = {"Name", "Description", "Long Description", "EPSG ID", "Referenced Datum", "Projection", "Angular Unit", "Linear Unit", "Values"};
        private Object[][]_data;
        private int _n;
        private JTable _table=null;
        private TableModel _model=null;
        private Map _map = null;
        private JComboBox _sets;


        CoordSystemSetPanel(){
            Vector vec = new Vector();
            Enumeration enum = _db.coordSetEnumeration();
            while(enum.hasMoreElements()){
                CoordinateSystemSetTable table = (CoordinateSystemSetTable) enum.nextElement();
                vec.add(table.getDescription());
            }
            String []strings = (String[])vec.toArray(new String[0]);
            Arrays.sort(strings);
            _sets = new JComboBox(strings);
            _sets.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox cb = (JComboBox)e.getSource();
                    String desc = (String)cb.getSelectedItem();
                    fillData(desc);
                    _model = new MyTableModel(9, _n, columnNames, _data);
                    _table.setModel(_model);
                }
            });

            add(_sets);
            fillData(strings[0]);
            _model = new MyTableModel(9, _n, columnNames, _data);
            _table = new JTable(_model);
            _table.setModel(_model);
            _table.setPreferredScrollableViewportSize(
              new Dimension(900, 600));
            add(new JScrollPane(_table));
            setVisible(true);

        }
        public void fillData(String set){
            Enumeration enum = _db.coordSetEnumeration();
            CoordinateSystemSetTable table=null;
            while(enum.hasMoreElements()){
                 table = (CoordinateSystemSetTable) enum.nextElement();
                if (table.getDescription().equalsIgnoreCase(set))
                    break;
            }
            //_n = members.size();

            Iterator iter = table.iterator();
            _n = 0;
            while(iter.hasNext()){
                iter.next();
                _n++;
            }

            _data = new Object[_n][9];
            int row = 0;
            int column = 0;
            iter = table.rowIterator();
            while (iter.hasNext()){
                GeodeticRow r = (GeodeticRow)iter.next();
                ReferencedCoordinateSystem e = null;
                try {
                    e = (ReferencedCoordinateSystem)table.createObject(r);
                } catch (InvalidRowDataException e1) {
                    e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
//{"Name", "Description", "Long Description", "EPSG ID", "Referenced Datum", "Projection", "Angular Unit", "Linear Unit", "Values"};

                _data[row][column++]=e.getName();
                _data[row][column++]=e.getDescription();
                _data[row][column++]=e.getLongDescription();
                _data[row][column++]=Integer.toString(e.getCoordSysID());
                _data[row][column++]=e.getReferencedDatum().getName();
                _data[row][column++]=e.getProjection().getName();
                _data[row][column++]=e.getProjection().getAngularUnit().getName();
                _data[row][column++]=e.getProjection().getUnit().getName();
                StringBuffer buf = new StringBuffer();
                for(int j=8;j<r.getLength();j++){
                    buf.append(r.getColumn(j));
                    if (j < r.getLength()-1)
                        buf.append(",");
                }
                _data[row][column++]=buf.toString();
                column = 0;
                row++;
            }
        }


    }
    public static class MutableJList extends JList {
        public MutableJList() {
            super(new DefaultListModel());
        }
        MutableJList(String strings[]){
            super(new DefaultListModel());
            setData(strings);
        }
        DefaultListModel getContents() {
            return (DefaultListModel)getModel();
        }
        public void setData(String []objs){
            getContents().removeAllElements();
            if (objs != null){
                for (int i=0;i<objs.length;i++)
                    getContents().addElement(objs[i]);
                if (objs.length > 0){
                    this.setSelectedIndex(0);
                }
            }
        }
    }


}
