package com.esri.svr.cmn;

/**
 * MapResonse is a serizaliable object that contains information generated
 * by a Mapper.
 */
public class MapResponse implements java.io.Serializable {
    private String _imageFile;
    private String _mapExtent;
    private int _layerCount;
    private String[] _layerNames;
    private boolean[] _layerVisibles;
    private int _selectedLayerIndex;

    /**
     * Default constructor
     */
    public MapResponse(){}

    /**
     * Set generated map image file name
     * @param imageFile the image file name
     */
    public void setImageFile(String imageFile) {
        _imageFile = imageFile;
    }

    /**
     * Get the image file
     * @return the image file name
     */
    public String getImageFile() {
        return _imageFile;
    }

    /**
     * Set map extent in string representation
     * @param mapExtent map extent
     */
    public void setMapExtent(String mapExtent) {
        _mapExtent = mapExtent;
    }

    /**
     * Get map extent
     * @return map extent as string
     */
    public String getMapExtent() {
        return _mapExtent;
    }

    /**
     * Set a string array that contains all layer names
     * @param layerNames layer names
     */
    public void setLayerNames(String[] layerNames) {
        _layerNames = layerNames;
    }

    /**
     * Get layer names
     * @return an array of string containing all layer names
     */
    public String[] getLayerNames() {
        return _layerNames;
    }

    /**
     * Set a layer visibility boolean array
     * @param layerVisibles an array of boolean that indicates each layer's visibility
     */
    public void setLayerVisibilities(boolean[] layerVisibles) {
        _layerVisibles = layerVisibles;
    }

    /**
     * Get layers' visibility array
     * @return a boolean array indicating each layer's visibility
     */
    public boolean[] getLayerVisibilities() {
        return _layerVisibles;
    }

    /**
     * Get selected layer index
     * @return an index of selected layer
     */
    public int getSelectedLayerIndex() {
        return _selectedLayerIndex;
    }

    /**
     * Set selected layer index
     * @param selectedLayerIndex selected layer index
     */
    public void setSelectedLayerIndex(int selectedLayerIndex) {
        _selectedLayerIndex = selectedLayerIndex;
    }

    /**
     * Get number of layers
     * @return layer count
     */
    public int getLayerCount() {
        return _layerCount;
    }

    /**
     * Set the number of layers
     * @param layerCount the number of layers
     */
    public void setLayerCount(int layerCount) {
        _layerCount = layerCount;
    }
}
