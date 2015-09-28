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

public class Lab3 extends JFrame {
	static Lab3 lab3;
	static Map map = new Map();
	Legend legend;
	Layer layer = new Layer();
	static com.esri.mo2.map.dpy.Layer layer4;
	Toc toc = new Toc();
	String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\World\\country.shp";
	ZoomPanToolBar zptb = new ZoomPanToolBar();
	SelectionToolBar stb = new SelectionToolBar();
	JToolBar jtb = new JToolBar();
	JPanel panel = new JPanel();
	JPanel panel2 = new JPanel();
	JLabel statusLabel = new JLabel();
	JButton hotjb = new JButton(new ImageIcon("hotlink.gif"));
	JButton XYjb = new JButton("Add Points Layer");	//points layer
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image bolt = tk.getImage("hotlink.gif");
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000");
	java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new Point(6,30),"bolt");
	ActionListener lis;
	TocAdapter mytocadapter;

	MyPickAdapter picklis = new MyPickAdapter();
	Identify hotlink = new Identify();
	class MyPickAdapter implements PickListener {
		public void beginPick(PickEvent pe) {}
		public void endPick(PickEvent pe) {}
		public void foundData(PickEvent pe) {
				java.awt.event.MouseEvent me = pe.getMouseEvent();
			//	pickPoint = me.getPoint();
				HotPick hotpick = new HotPick();
				hotpick.mouseClicked(me);
		}
	};

	public Lab3() {
		super("Lab 3");
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
				else {
					try {
						AddXYtheme addXYtheme = new AddXYtheme();
						addXYtheme.setMap(map);
						addXYtheme.setVisible(false);
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
		XYjb.addActionListener(lis);
		XYjb.setToolTipText("Add a layer of points from a CSV file");
		jtb.add(hotjb);
		jtb.add(XYjb);	//points layer
		panel.add(jtb);
		panel.add(zptb);
		panel.add(stb);
		panel2.add(statusLabel);
		addShapefileToMap(layer,s1);
		getContentPane().add(map,BorderLayout.CENTER);
		getContentPane().add(panel,BorderLayout.NORTH);
		getContentPane().add(toc,BorderLayout.WEST);
		getContentPane().add(panel2,BorderLayout.SOUTH);
	}
	private void addShapefileToMap(Layer layer, String s) {
		String datapath = s;
		layer.setDataset("0;"+datapath);
		map.add(layer);toc.add(layer);
	}

	public static void main(String args[]) {
		lab3 = new Lab3();
		lab3.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		lab3.setVisible(true);	
	}
}

class AddXYtheme extends JDialog {	//points layer
	Map map;
	Vector s2 = new Vector();
	JFileChooser jfc = new JFileChooser();
	BasePointsArray bpa = new BasePointsArray();
	FeatureLayer XYlayer;
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
				StringTokenizer st = new StringTokenizer(s,",");
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				bpa.insertPoint(n,new com.esri.mo2.cs.geom.Point(x,y));
				s2.addElement(st.nextToken());
			}
		} catch (IOException e){}
		XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,s2);
		XYlayer = xyfl;
		xyfl.setVisible(true);
		map = Lab3.map;
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

class HotPick extends Tool {
	String l = "C:\\ESRI\\MOJ20\\examples\\milwaukee.html";		//-87.9500,43.0500
	String l1 = "C:\\ESRI\\MOJ20\\examples\\montreal.html";		//-73.5667,45.5000
	String l2 = "C:\\ESRI\\MOJ20\\examples\\tehran.html";		//51.4231,35.6961
	String l3 = "C:\\ESRI\\MOJ20\\examples\\arboga.html";		//15.8333,59.4000
	String l4 = "C:\\ESRI\\MOJ20\\examples\\staryoskol.html";	//37.8333,51.3000
	String l5 = "C:\\ESRI\\MOJ20\\examples\\pohang.html";		//129.3650,36.0322
	String l6 = "C:\\ESRI\\MOJ20\\examples\\manaus.html";		//-60.0167,-3.1000
	String l7 = "C:\\ESRI\\MOJ20\\examples\\saigon.html";		//106.6667,10.7500
	String l8 = "C:\\ESRI\\MOJ20\\examples\\kawasaki.html";		//139.7000,35.5167
	String l9 = "C:\\ESRI\\MOJ20\\examples\\dublin.html";		//-6.2597,53.3478	
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000");
	public void mouseClicked(MouseEvent me) {
		Map map = Lab3.map;
		com.esri.mo2.cs.geom.Point point = map.transformPixelToWorld(me.getX(),me.getY());
		//String getX = df.format(point.getX());
		//String getY = df.format(point.getY());
		double x = Double.parseDouble(df.format(point.getX()));
		double y = Double.parseDouble(df.format(point.getY()));
		try {
			if (x < 1 && y < 50) {
				java.awt.Desktop.getDesktop().open(new File(l));
			}
		} catch(Exception e){}
	}
}
/*class HotImage extends JDialog {
	HotImage() throws IOException {
		setTitle("Pick");
		setBounds(50,50,350,350);
		getContentPane().add(label, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}
	JLabel label = new JLabel(new ImageIcon("duke13.gif"));
}*/