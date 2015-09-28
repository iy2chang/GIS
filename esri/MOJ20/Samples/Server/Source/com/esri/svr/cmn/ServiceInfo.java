package com.esri.svr.cmn;


/**
 * ServiceInfo holds the information about a map service. It could be used to
 * create a service catalog
 */
public class ServiceInfo implements java.io.Serializable {

    private String _serviceName;
    private String _serviceType;
    private String _outputDir;
    private String _outputUrl;
    private String _axlConfig;
    private String _imageFormat;

    private String _axlFileName;
    private String _imageUrlPrefix;
    private String _serviceURL;

    /**
     * Default constructor.
     */
    public ServiceInfo() {
    }

    /**
     * Set the service name
     * @param serviceName the service name
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    /**
     * Get the service name
     * @return service name
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * Set the service type
     * @param serviceType the service type
     */
    public void setServiceType(String serviceType) {
      _serviceType = serviceType;
    }

    /**
     * Get service type
     * @return the service type
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * Set image output format
     * @param format the image format
     */
    public void setImageFormat(String format) {
        _imageFormat = format;
    }

    /**
     * Get the output image format
     * @return the output image format
     */
    public String getImageFormat() {
        return _imageFormat;
    }

    /**
     * Set the serivce configuration
     * @param config the service configuration
     */
    public void setConfig(String config) {
        _axlConfig = config;
    }

    /**
     * Get the service configuration
     * @return AXL document
     */
    public String getConfig() {
        return _axlConfig;
    }

    /**
     * Get the output URL
     * @return output URL
     */
    public String getOutputURL() {
        return _outputUrl;
    }

    /**
     * Set output URL of Samba based jCIFS
     * @param outputUrl the output URL
     */
    public void setOutputURL(String outputUrl) {
        _outputUrl = outputUrl;
    }

    /**
     * Get output directory
     * @return output directory
     */
    public String getOutputDir() {
        return _outputDir;
    }

    /**
     * Set output directory
     * @param outputDir the output directory
     */
    public void setOutputDir(String outputDir) {
        _outputDir = outputDir;
    }

    /**
     * Set ArcXML configuration file name
     * @param filename the AXL file name
     */
    public void setAxlFileName(String filename) {
        _axlFileName = filename;
    }

    /**
     * Get ArcXML configuration file name
     * @return AXL file name
     */
    public String getAxlFileName() {
        return _axlFileName;
    }

    /**
     * Set image URL prefix so that a generated map images could be
     * accessed by a common url.
     * @param imageUrlPrefix the image URL prefix
     */
    public void setImageURLPrefix(String imageUrlPrefix) {
        _imageUrlPrefix = imageUrlPrefix;
    }

    /**
     * Get a prefix for generated image URL
     * @return a prefix
     */
    public String getImageURLPrefix() {
        return _imageUrlPrefix;
    }

    /**
     * Set service URL
     * @param url the URL to be set
     */
    public void setServiceURL(String url) {
        _serviceURL = url;
    }

    /**
     * Get service URL
     * @return URL in string
     */
    public String getServiceURL() {
        return _serviceURL;
    }
}