package com.esri.svr.cat;

/**
 * This class provides a holder for all information for a map service.
 * Please note that two properties, _outputUrl and _serviceURL are
 * not used in the current JSP sample viewers.
 */
public class ServiceCatalog extends BaseCatalog
{
    private String _axlFileName;
    private String _imageUrlPrefix;

    private String _serviceURL;

    /**
     * Default constructor
     */
    public ServiceCatalog()
        throws java.io.IOException
    {
        super();
    }

    /**
     * Construct a service catalog with given service name and axl file name
     * @param serviceName the service name
     * @param axlFileName the AXL configuraiton file name
     */
    public ServiceCatalog(String serviceName, String axlFileName)
        throws java.io.IOException
    {
        this();
        _serviceName = serviceName;
        _axlFileName = axlFileName;
        setConfig(getConfigFile(axlFileName));

    }

    private String getConfigFile(String filename)
        throws java.io.IOException
    {
        StringBuffer sb = new StringBuffer();
        java.io.FileReader fr = new java.io.FileReader(filename);
        java.io.BufferedReader reader = new java.io.BufferedReader(fr);
        String s = reader.readLine();
        while (s != null) {
            sb.append(s);
            s = reader.readLine();
        }

        return sb.toString();
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
     * @param serviceURL the service type
     */
    public void setServiceURL(String serviceURL) {
      _serviceURL = serviceURL;
    }

    /**
     * Get service type
     * @return the service type
     */
    public String getServiceURL() {
      return _serviceURL;
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
    public void setConfig(String config)
        throws java.rmi.RemoteException
    {
      _axlConfig = config;
      loadLayers(config);
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
    public String getOutputUrl() {
      return _outputUrl;
    }

    /**
     * Set output URL of Samba based jCIFS
     * @param outputUrl the output URL
     */
    public void setOutputUrl(String outputUrl) {
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

    public void setAxlFileName(String filename)
        throws java.io.IOException
    {
        _axlFileName = filename;
        setConfig(getConfigFile(_axlFileName));
    }

    public String getAxlFileName() {
        return _axlFileName;
    }

    public void setImageURLPrefix(String imageUrlPrefix) {
        _imageUrlPrefix = imageUrlPrefix;
    }

    public String getImageURLPrefix() {
        return _imageUrlPrefix;
    }

//
// unit testing
//
    public static void main(String[] args) {
        try {
            //ServiceCatalog sc = new ServiceCatalog("usa", "D:/ESRI/MOJ20/Samples/Server/AXLFiles/usa.axl");
            ServiceCatalog sc = new ServiceCatalog("usa", "C:/ArcXML/worldp.axl");
            System.out.println(sc.getCoordinateSystem());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}