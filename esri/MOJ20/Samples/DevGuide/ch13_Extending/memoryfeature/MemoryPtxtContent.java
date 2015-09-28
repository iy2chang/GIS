
package memoryfeature;

import com.esri.mo2.data.feat.MapDataset;
import com.esri.mo2.data.feat.FeatureClass;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.draw.Util;
import com.esri.mo2.src.sys.BaseContent;
import com.esri.mo2.src.sys.Content;
import com.esri.mo2.src.sys.Properties;
import com.esri.mo2.src.sys.ResourceBundle;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

class MemoryPtxtContent extends BaseContent {
	private File _file;
	private InMemoryFeatureClass _fclass = null;
	private static String[] AVAILABLE_CONTENT_TYPES = {
		FeatureClass.class.getName(),
		FeatureLayer.class.getName()
	};


	//constructor
	public MemoryPtxtContent (File file ){
		_file = file;
		
	}

	public String getName() {
		return _file.getName();
	}

	public String getDescription() {
		return "PTXT style point file";
	}

	public Image getIcon() {
		//since this data type only supports points...
		return ResourceBundle.getDatasetIcon(MapDataset.POINT);
	}
	
	public String[] getAvailableContentTypes() {
		return AVAILABLE_CONTENT_TYPES;
	}

	public Object getData(String type) throws IOException {
		Object returnVal = null;
		int idx = findContentType(type);
		switch (idx) {
			case 0:
				returnVal = getFeatureClass(_file);
				break;
			case 1:
				returnVal = new BaseFeatureLayer(getFeatureClass(_file), Util.constructDefaultRenderer(MapDataset.POINT));
				break;
		}
		return returnVal;
	}

	private InMemoryFeatureClass getFeatureClass(File sourceFile) {
		if (_fclass == null) {
    	_fclass = new InMemoryFeatureClass(sourceFile);
		}
		return _fclass;
  }

}

