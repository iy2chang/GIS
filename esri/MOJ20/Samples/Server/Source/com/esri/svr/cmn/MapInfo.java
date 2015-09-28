package com.esri.svr.cmn;


/**
 * MapInfo provides map related information
 */
public class MapInfo implements java.io.Serializable {
    private String _mapExtent;
    private int _layerCount;
    private String[] _layerNames;
    private boolean[] _layerVisibility;

    /**
     * The default constructor.
     */
    public MapInfo() {}

    /**
     * Set map extent
     * @param extent map extent
     */
    public void setMapExtent(String extent) {
        _mapExtent = extent;
    }

    /**
     * Get map extent
     * @return map extent
     */
    public String getMapExtent() {
        return _mapExtent;
    }

    /**
     * Set layer count
     * @param count number of layers
     */
    public void setLayerCount(int count) {
        _layerCount = count;
    }

    /**
     * Get number of layers
     * @return number of layers
     */
    public int getLayerCount() {
        return _layerCount;
    }

    /**
     * Set an array of layer names
     * @param names layer names array
     */
    public void setLayerNames(String[] names) {
        _layerNames = names;
    }

    /**
     * Get layer names in an array of string
     * @return an array of string that contains layer names
     */
    public String[] getLayerNames() {
        return _layerNames;
    }

    /**
     * Set a layer visibility boolean array
     * @param visibility an array of boolean that indicates each layer's visibility
     */
    public void setLayerVisibility(boolean[] visibility) {
        _layerVisibility = visibility;
    }

    /**
     * Get layers' visibility array
     * @return a boolean array indicating each layer's visibility
     */
    public boolean[] getLayerVisibility() {
        return _layerVisibility;
    }
}