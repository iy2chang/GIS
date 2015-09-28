package com.esri.svr.cmn;


/**
 * A Feature contains both geometry and attributes.
 */
public class Feature implements java.io.Serializable {

    private String _type;
    private String _geometry;
    private String[] _fieldValues; // fdvalue1, fdvalue2, ...
    private int _numberOfFields;
    private String[] _fieldNames;  // fdname1, fdname2, ...
    private String _layerName;

    /**
     * The default constructor.
     */
    public Feature() {
    }

    /**
     * Set feature type
     * @param type feature type
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * Get feature type
     * @return
     */
    public String getType() {
        return _type;
    }

    /**
     * Set feature geometry
     * @param geometry feature geometry
     */
    public void setGeometry(String geometry) {
        _geometry = geometry;
    }

    /**
     * Get feature geometry
     * @return
     */
    public String getGeometry() {
        return _geometry;
    }

    /**
     * Set field values
     * @param fieldValues an array of field values
     */
    public void setFieldValues(String[] fieldValues) {
        _fieldValues = fieldValues;
    }

    /**
     * Get field values
     * @return an array of field values
     */
    public String[] getFieldValues() {
        return _fieldValues;
    }

    /**
     * Set field names
     * @param fieldNames an array of field names
     */
    public void setFieldNames(String[] fieldNames) {
        _fieldNames = fieldNames;
    }

    /**
     * Get field names
     * @return an array of field names
     */
    public String[] getFieldNames() {
        return _fieldNames;
    }

    /**
     * Set layer name this feature belongs to
     * @param layerName layer name
     */
    public void setLayerName(String layerName) {
        _layerName = layerName;
    }

    /**
     * Get layer name
     * @return layer name
     */
    public String getLayerName() {
        return _layerName;
    }

    /**
     * Set number of fields
     * @param numberOfFields number of fields
     */
    public void setNumberOfFields(int numberOfFields) {
        _numberOfFields = numberOfFields;
    }

    /**
     * Get number of fields
     * @return number of fields
     */
    public int getNumberOfFields() {
        return _numberOfFields;
    }
}