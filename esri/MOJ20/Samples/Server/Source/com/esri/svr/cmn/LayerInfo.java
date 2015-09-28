package com.esri.svr.cmn;


/**
 * LayerInfo contains layer related information
 */
public class LayerInfo implements java.io.Serializable {
    private String _name;
    private String _layerType;
    private String _extent;
    private String _id;
    private boolean _visible;
    private String[] _fieldNames; // fdname1, fdname2, ...
    private String[] _fieldTypes; // fdtype1, fdtype2, ...
    private String[] _fieldPrecisions; // fdtype1, fdtype2, ...
    private String[] _fieldDisplaySizes; // fdtype1, fdtype2, ...
    private int _layerFeatureType;

    /**
     * The default constructor.
     */
    public LayerInfo() {
    }

    /**
     * Set layer name
     * @param name layer name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Get layer name
     * @return layer name
     */
    public String getName() {
        return _name;
    }

    /**
     * Set layer type
     * @param layerType layer type
     */
    public void setLayerType(String layerType) {
        _layerType= layerType;
    }

    /**
     * Get layer type
     * @return layer type
     */
    public String getLayerType() {
        return _layerType;
    }

    /**
     * Set layer extent
     * @param extent layer extent
     */
    public void setExtent(String extent) {
        _extent = extent;
    }

    /**
     * Get layer extent
     * @return layer extent
     */
    public String getExtent() {
        return _extent;
    }

    /**
     * Set display size for each field
     * @param fieldDisplaySizes an array of field display size
     */
    public void setFieldDisplaySizes(String[] fieldDisplaySizes) {
        _fieldDisplaySizes = fieldDisplaySizes;
    }

    /**
     * Get field display sizes
     * @return an array of field display sizes
     */
    public String[] getFieldDisplaySizes() {
        return _fieldDisplaySizes;
    }

    /**
     * Set field names
     * @param fieldNames an array of string containing all field names
     */
    public void setFieldNames(String[] fieldNames) {
        _fieldNames = fieldNames;
    }

    /**
     * Get all field names
     * @return an array of field names
     */
    public String[] getFieldNames() {
        return _fieldNames;
    }

    /**
     * Set field precisions
     * @param fieldPrecisions an array of field precisions
     */
    public void setFieldPrecisions(String[] fieldPrecisions) {
        _fieldPrecisions = fieldPrecisions;
    }

    /**
     * Get field precisions
     * @return an array of field precisions
     */
    public String[] getFieldPrecisions() {
        return _fieldPrecisions;
    }

    /**
     * Set field types
     * @param fieldTypes an array of field types
     */
    public void setFieldTypes(String[] fieldTypes) {
        _fieldTypes = fieldTypes;
    }

    /**
     * Get field types
     * @return an array of field types
     */
    public String[] getFieldTypes() {
        return _fieldTypes;
    }

    /**
     * Set layer ID
     * @param id layer ID
     */
    public void setId(String id) {
        _id = id;
    }

    /**
     * Get layer ID
     * @return layer ID
     */
    public String getId() {
        return _id;
    }

    /**
     * Set layer feature type
     * @param layerFeatureType layer feature type
     */
    public void setLayerFeatureType(int layerFeatureType) {
        _layerFeatureType = layerFeatureType;
    }

    /**
     * Get layer feature type
     * @return
     */
    public int getLayerFeatureType() {
        return _layerFeatureType;
    }

    /**
     * Set layer visibility
     * @param visible boolean indicating layer visibility
     */
    public void setVisible(boolean visible) {
        _visible = visible;
    }

    /**
     * Get layer visibility
     * @return boolean indicating layer visibility
     */
    public boolean isVisible() {
        return _visible;
    }
}