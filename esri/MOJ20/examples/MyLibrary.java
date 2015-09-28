/*
 * Name: Ian Chang
 * Account: Masc0673
 * Class Info: cs537 GIS, lab3
 * Date: 12/15/13
 * This is the last lab of the GIS class, I referenced the book from page 207-212
 */

import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.ui.bean.Tool;

class MyLibrary extends JFrame{
    static MyLibrary qstart;
    static Map map = new Map();
    Legend legend;
    Layer layer = new Layer();
    Layer layer2 = new Layer();
    static com.esri.mo2.map.dpy.Layer mapLayer;
    // mouse xy
    JLabel statusLabel = new JLabel();
    java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000");

    JMenuBar mbar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenuItem propsitem = new JMenuItem("Legend Editor", 
            new ImageIcon("properties.gif"));
    Toc toc = new Toc();
    String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
    String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
    ZoomPanToolBar zptb = new ZoomPanToolBar();
    SelectionToolBar stb = new SelectionToolBar();
    JToolBar jtb = new JToolBar();
    JPanel myjp = new JPanel();
    // JButton
    JButton hotjb = new JButton(new ImageIcon("hotlink.gif"));
    JButton XYjb = new JButton("XY");

    Toolkit tk = Toolkit.getDefaultToolkit();
    Image bolt = tk.getImage("hotlink.gif"); // 16x16 gif file
    Cursor boltCursor = tk.createCustomCursor(bolt, new Point(6,30), "bolt");
    ActionListener lis;
    ActionListener layerLis;
    ActionListener actList;
    TocAdapter myTocAdapter;
    Identify hotlink = new Identify();
    MyPickAdapter pickLis = new MyPickAdapter();
    // class MyPickAdapter 
    class MyPickAdapter implements PickListener{
        public void beginPick(PickEvent pe){
            System.out.println("begin pick");
        }
        public void endPick(PickEvent pe){};
        public void foundData(PickEvent pe){
            System.out.println("HOLA PICK");
            try {
                HotPick hotpick = new HotPick();
                hotpick.setVisible(true);
            } 
	    catch(Exception e){
		System.out.println(e);
	    }
        }
    };
    public MyLibrary(){
        super("Library in San Diego");
        this.setSize(750,550);
        zptb.setMap(map);
        stb.setMap(map);
        setJMenuBar(mbar);
        lis = new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                Object source = ae.getSource();
                if(source == hotjb) {
                    System.out.println("helllooooooo");
                    map.setSelectedTool(hotlink);
                }
            }
        };
        toc.setMap(map);
        myTocAdapter = new TocAdapter(){
            public void click(TocEvent e){
                legend = e.getLegend();
                mapLayer = legend.getLayer();
                stb.setSelectedLayer(mapLayer);
                zptb.setSelectedLayer(mapLayer);
                com.esri.mo2.map.dpy.Layer[] layers = { mapLayer };
                hotlink.setSelectedLayers(layers);
                propsitem.setEnabled(true);
            }
        };	
        // get coordinates
        map.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent me){
                com.esri.mo2.cs.geom.Point worldPoint = null;
                if(map.getLayerCount() > 0){
                    worldPoint = map.transformPixelToWorld(me.getX(),
                        me.getY());
                    String s = "X:"+df.format(worldPoint.getX()) + " " +
                        "Y:" + df.format(worldPoint.getY());
                    statusLabel.setText(s);
                }
                else
                    statusLabel.setText("X:0.0000 Y:0.0000");
            }
        });
        toc.addTocListener(myTocAdapter);
        propsitem.setEnabled(false);
        propsitem.addActionListener(layerLis);
        file.add(propsitem);
        mbar.add(file);
        hotlink.addPickListener(pickLis);
        hotlink.setPickWidth(5);	// sets the tolerance for hotlinkl clicks
        hotjb.addActionListener(lis);
        hotjb.setToolTipText("HotLink tool--click an icon on the map");
        XYjb.addActionListener(lis);
        XYjb.setToolTipText("Add points layer");
        jtb.add(hotjb);
        jtb.add(XYjb);
        myjp.add(jtb);
        myjp.add(zptb);
        myjp.add(statusLabel);
        getContentPane().add(map, BorderLayout.CENTER);
        getContentPane().add(myjp, BorderLayout.NORTH);
        addShapefileToMap(layer, s1);
        getContentPane().add(toc, BorderLayout.WEST);
    }

    private void addShapefileToMap(Layer layer, String s){
        String datapath = s;
        layer.setDataset("0;"+datapath);
        map.add(layer);
        toc.add(layer);
    }
    public static void main(String[] args){
        qstart = new MyLibrary();
        qstart.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.out.println("Thanks, My Program is exiting");
                System.exit(0);
            }
        });
        qstart.setVisible(true);
    }
    class HotPick extends JDialog {
        HotPick() throws IOException{
            setTitle("This was your pick");
            setBounds(50,50,350,350);
            getContentPane().add(label,BorderLayout.CENTER);
            addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    setVisible(false);
                }
            });	
        }
        JLabel label = new JLabel(new ImageIcon("duke12.gif"));
    }
    
    class AddXYtheme extends JDialog { // point layer
	Map map;
	Vector vec = new Vector();
	JFileChooser fileChooser = new JFileChooser();
	BasePointsArray bpa = new BasePointsArray();
	FeatureLayer XYLayer;
	AddXYtheme() throws IOException{
	    setBounds(50,50,520,430);
	    fileChooser.showOpenDialog(this);
	    try{
		File file = fileChooser.getSelectedFile();
		FileReader fr = new FileReader(file);
		BufferedReader bf = new BufferedReader(fr);
		String s;
		double x,y;
		int n = 0;
		while((s = br.readLine())!= null){
		    StringTokenizer st = StringTokenizer(s,",");
		    x = Double.parseDouble(st.nextToken());
		    y = Double.parseDouble(st.nextToken());
		    bpa.insertPoint(n,new com.esri.mo2.cs.geom.Point(x,y));
		    vec.addElement(st.nextToken());
		}
	    }
	    catch(IOException e){
		System.out.println(e);
	    }
	    XYfeatureLayer f1 = new XYFeatureLayer(bpa,map,vec);
	    XYlayer = f1;
	    xy.setVisible(true);
	    map = MyLibrary.map;
	    map.getLayerset().addLayer(f1);
	    map.redraw();
	}
	public void setMap(com.esri.mo2.ui.bean.Map myMap){
	    map = myMap;
	}
    }

class XYfeatureLayer extends BaseFeatureLayer {	//points layer
	BaseFields fields;
	private java.util.Vector featureVector;
	public XYfeatureLayer(BasePointsArray bpa,Map map,Vector s2) {
		createFeaturesAndFields(bpa,map,s2);
		BaseFeatureClass bfc = getFeatureClass("Locations",bpa);
		setFeatureClass(bfc);
		BaseSimpleRenderer srd = new BaseSimpleRenderer();
		SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
		sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
		sms.setSymbolColor(new Color(255,0,0));
		sms.setWidth(5);
		srd.setSymbol(sms);
		setRenderer(srd);
		XYLayerCapabilities lc = new XYLayerCapabilities();
		setCapabilities(lc);
	}
	private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2) {
		featureVector = new java.util.Vector();
		fields = new BaseFields();
		createDbfFields();
		for(int i=0;i<bpa.size();i++) {
			BaseFeature feature = new BaseFeature();  //feature is a row
			feature.setFields(fields);
			com.esri.mo2.cs.geom.Point p = new
					com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
			feature.setValue(0,p);
			feature.setValue(1,new Integer(0));  // point data
			feature.setValue(2,(String)s2.elementAt(i));
			feature.setDataID(new BaseDataID("Locations",i));
			featureVector.addElement(feature);
		}
	}
	private void createDbfFields() {
		fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
		fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
		fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,16,0));
	}
	public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
		com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
		try {
			featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,
					fields);
		} catch (IllegalArgumentException iae) {}
		featClass.setName(name);
		for (int i=0;i<bpa.size();i++) {
			featClass.addFeature((Feature) featureVector.elementAt(i));
		}
		return featClass;
	}
	private final class XYLayerCapabilities extends com.esri.mo2.map.dpy.LayerCapabilities {
		XYLayerCapabilities() {
			for (int i=0;i<this.size(); i++) {
				setAvailable(this.getCapabilityName(i),true);
				setEnablingAllowed(this.getCapabilityName(i),true);
				getCapability(i).setEnabled(true);
			}
		}
	}
    }

  
}
