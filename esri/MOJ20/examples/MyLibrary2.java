/* Name: Ian Chang
 * Class: GIS CS537, LAB3
 * This is the last lab of the class, referenced the book from
 * page 207-212
 */
import com.esri.mo2.cs.geod.CoordinateSystem;
import com.esri.mo2.ui.geod.CoordSysTreePanel;
import java.io.*; //FileReader, BufferedReader
import javax.swing.*; 
import javax.swing.event.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.StringTokenizer;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.tb.*;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.ui.bean.Tool;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.cs.geom.BasePointsArray; 
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.file.shp.*;

class MyLibrary2 extends JFrame {
	static MyLibrary2 lib;
	static Map map = new Map();
	Legend legend;
	Layer layer = new Layer();
	Layer roadLayer = new Layer(); // new road layers
	Layer collegeLayer = new Layer(); // college layers
	static com.esri.mo2.map.dpy.Layer layer4;
	Toc toc = new Toc();
	String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\SDcounty\\Supervisor_Districts.shp";
	String road = "C:\\ESRI\\MOJ20\\Samples\\Data\\SDroad\\Major_Roads.shp";
	String college = "C:\\ESRI\\MOJ20\\Samples\\Data\\SDcollege\\colleges.shp";
	ZoomPanToolBar zptb = new ZoomPanToolBar();
	SelectionToolBar stb = new SelectionToolBar();
	JToolBar jtb = new JToolBar();
	JPanel panel = new JPanel();
	JPanel panel2 = new JPanel();
	JLabel statusLabel = new JLabel();
	JButton hotjb = new JButton(new ImageIcon("hotlink.gif"));    // hotlink mouse
	JButton normaljb = new JButton(new ImageIcon("pointer.gif")); // normal mouse
	Arrow arrow = new Arrow();
	JButton XYjb = new JButton("Add Points Layer");	//points layer
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image bolt = tk.getImage("hotlink.gif");
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000");
	java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new Point(6,30),"bolt");
	ActionListener lis;
	TocAdapter mytocadapter;
	int pickID;

	MyPickAdapter picklis = new MyPickAdapter();
	Identify hotlink = new Identify();
	class MyPickAdapter implements PickListener {
		public void beginPick(PickEvent pe) {}
		public void endPick(PickEvent pe) {}
		public void foundData(PickEvent pe) {
				try{
					java.awt.event.MouseEvent me = pe.getMouseEvent();
					com.esri.mo2.data.feat.BaseFeature foundData = (BaseFeature) pe.getCursor().next();
					pickID = foundData.getDataID().getID();
					HotPick hotpick = new HotPick(pickID);
					hotpick.setVisible(true);
				}
				catch(IOException e){}
		}
	};

	public MyLibrary2() {
		super("My Library");
		this.setSize(1366,728);
		zptb.setMap(map);
		stb.setMap(map);
		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == hotjb) {
					hotlink.setCursor(boltCursor);
					map.setSelectedTool(hotlink);
				}
				else if(source == normaljb){
					map.setSelectedTool(arrow);
				}
				else if(source == XYjb){
					try {
						AddXYtheme addXYtheme = new AddXYtheme();
						addXYtheme.setMap(map);
						addXYtheme.setVisible(true);
						map.redraw();
					} catch (IOException e){}
				}
			}
		};
		toc.setMap(map);
		mytocadapter = new TocAdapter() {
			public void click(TocEvent e) {
				legend = e.getLegend();
				layer4 = legend.getLayer();
				stb.setSelectedLayer(layer4);
				zptb.setSelectedLayer(layer4);
				com.esri.mo2.map.dpy.Layer[] layers = {layer4};
				hotlink.setSelectedLayers(layers);
				//remlyritem.setEnabled(true);
				//propsitem.setEnabled(true);
			}
		};

		map.addMouseMotionListener(new MouseMotionAdapter() {	//gives coordinates
			public void mouseMoved(MouseEvent me) {
				com.esri.mo2.cs.geom.Point worldPoint = null;
				if (map.getLayerCount() > 0) {
					worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
					String s = "X:"+df.format(worldPoint.getX())+" "+
							"Y:"+df.format(worldPoint.getY());
					statusLabel.setText(s);
				}
				else
					statusLabel.setText("X:0.0000 Y:0.0000");
			}
		});

		toc.addTocListener(mytocadapter);
		hotlink.addPickListener(picklis);
		hotlink.setPickWidth(5);
		
		hotjb.addActionListener(lis);
		hotjb.setToolTipText("Hotlink Tool");
		normaljb.addActionListener(lis);
		normaljb.setToolTipText("pointer");
		XYjb.addActionListener(lis);
		XYjb.setToolTipText("Add a layer of points from a CSV file");
		jtb.add(hotjb);
		jtb.add(normaljb);
		jtb.add(XYjb);	//points layer
		panel.add(jtb);
		panel.add(zptb);
		panel.add(stb);
		panel2.add(statusLabel);
		addShapefileToMap(layer,s1);
		addShapefileToMap(roadLayer,road);	 // add road to SD county map
		addShapefileToMap(collegeLayer, college); // add colleges to SD county
		getContentPane().add(map,BorderLayout.CENTER);
		getContentPane().add(panel,BorderLayout.NORTH);
		getContentPane().add(toc,BorderLayout.WEST);
		getContentPane().add(panel2,BorderLayout.SOUTH);

		CoordinateSystem sys = getCoordinateSystem(this);
		System.out.println("coord : " + sys);
		map.setCoordinateSystem(sys);
	}
	private void addShapefileToMap(Layer layer, String s) {
		String datapath = s;
		layer.setDataset("0;"+datapath);
		map.add(layer);
		toc.add(layer);
	}
private CoordinateSystem getCoordinateSystem(JFrame parent){
JDialog jd = new JDialog(parent, true);
CoordSysTreePanel tpanel = new CoordSysTreePanel();
jd.getContentPane().add(tpanel,BorderLayout.CENTER);
jd.pack();
jd.setVisible(true);
return tpanel.getSystem();
}
	public static void main(String args[]) {
		lib = new MyLibrary2();
		lib.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		lib.setVisible(true);	
	}
}

class AddXYtheme extends JDialog {	//points layer
	Map map;
	Vector s2 = new Vector();
	JFileChooser jfc = new JFileChooser();
	BasePointsArray bpa = new BasePointsArray();
	AddXYtheme() throws IOException {
		setBounds(50,50,520,430);
		jfc.showOpenDialog(this);
		try {
			File file  = jfc.getSelectedFile();
			FileReader fred = new FileReader(file);
			BufferedReader in = new BufferedReader(fred);
			String s; // = in.readLine();
			double x,y;
			int n = 0;
			while ((s = in.readLine()) != null) {
				System.out.println("hi i am here " + s);
				StringTokenizer st = new StringTokenizer(s,",");
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				bpa.insertPoint(n,new com.esri.mo2.cs.geom.Point(x,y));
				s2.addElement(st.nextToken());
			}
		} 
		catch (IOException e){}
	
		FeatureLayer XYlayer;
		XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,s2);
		XYlayer = xyfl;
		xyfl.setVisible(true);
		map = MyLibrary2.map;
		map.getLayerset().addLayer(xyfl);
		map.redraw();
	}
	public void setMap(com.esri.mo2.ui.bean.Map map1){
		map = map1;
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
			feature.setValue(1,new Integer(i));  // point data
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
class Arrow extends Tool{
	public void mouseClicked(MouseEvent me){

	}
}
class HotPick extends JDialog {
	HotPick(int pickID) throws IOException{
		JLabel label = new JLabel(new ImageIcon("duke13.gif"));
		setTitle("This was your pick");
		setBounds(50,50,350,350);
		String[][] details = new String[10][4];
		details[0][0] = "Mission Valley";
		details[0][1] = "missionvalley.jpg";
		details[0][2] = "Address: 2123 Fenton Pkway, San Diego, CA 92108";
		details[0][3] = "Phone: 858-573-5007";
		details[1][0] = "SDSU";
		details[1][1] = "SDSU.jpg";
		details[1][2] = "Address: 5500 Capanile Dr, San Diego, CA 92115";
		details[1][3] = "Phone: 619-594-6782";
		details[2][0] = "Scripps Library";
		details[2][1] = "Scripps.jpg";
		details[2][2] = "Address: 10301 Scripps Lake Dr, San Diego, CA 92131";
		details[2][3] = "Phone: 858-538-8158";
		details[3][0] = "UCSD Library";
		details[3][1] = "UCSD.jpg";
		details[3][2] = "Address: 9500 Gilman Dr, San Diego, CA 92093";
		details[3][3] = "Phone: 858-534-3336";
		details[4][0] = "Mira Mesa Library";
		details[4][1] = "MiraMesa.jpg";
		details[4][2] = "Address: 8405 New Salem St, San Diego, CA 92126";
		details[4][3] = "Phone: 858-538-8165";
		details[5][0] = "Poway Library";
		details[5][1] = "Poway.jpg";
		details[5][2] = "Address: 13137 County Highway S4, Poway CA 92064";
		details[5][3] = "Phone: 858-513-2900";
		details[6][0] = "El Cajon Library";
		details[6][1] = "ELCajon.jpg";
		details[6][2] = "Address: 201 E Douglas Ave, El Cajon CA 92020";
		details[6][3] = "Phone: 619-588-3718";
		details[7][0] = "La Jolla Library";
		details[7][1] = "LaJolla.jpg";
		details[7][2] = "Address: 7555 Draper Ave, La Jolla CA 92037";
		details[7][3] = "Phone: 858-552-1657";
		JLabel image1 = new JLabel(new ImageIcon("C:\\esri\\MOJ20\\examples\\"+details[pickID][1]));
		String htmltext = "<html><h1>"
				+ details[pickID][0]
				+ "</h1>"
				+ details[pickID][2]
				+ "<p>"
				+ details[pickID][3]
				+ "</html>";

		JLabel htmlLabel = new JLabel(htmltext);
		getContentPane().add(image1,BorderLayout.CENTER);
		getContentPane().add(htmlLabel,BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}
		});
	}
}

