package com.esri.svr.cat;


import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.map.elt.MapElement;
import com.esri.mo2.util.Environment;
import com.esri.mo2.xml.prj.LayoutProject;
import com.esri.mo2.svr.map.PageLayout;
import com.esri.mo2.cs.geod.CoordinateSystem;

/**
 * This class provides a base functionality for managing a service information
 * described by an AXL file. Currently it only supports the image service.
 */
public abstract class BaseCatalog implements java.io.Serializable {

    protected String _serviceName;
    protected String _serviceType;
    protected String _outputDir;
    protected String _outputUrl;
    protected String _axlConfig;
    protected String _imageFormat;

    private LayoutProject _layoutProj = null;
    private PageLayout _layout;
    private Environment _environment = null;


    private final static String IMAGE = "image";
    private static String[] serviceTypes = {IMAGE};

    /**
     * The default constructor that will construct the necessary objects needed for the bean to operate
     */
    public BaseCatalog()
    {
        System.out.println("BaseCatalog: BaseCatalog() ... a new service created");
        _environment = new Environment();
        _layout = new PageLayout();
        _layoutProj = new LayoutProject();
        _layoutProj.setRegisteredEnvironment(_environment);
        _layoutProj.setRegisteredLayout(_layout);
    }

    //
    // getter/setter of attributes
    //

    /**
     * Set the service name
     * @param serviceName the service name
     */
    public abstract void setServiceName(String serviceName);

    /**
     * Get the service name
     * @return service name
     */
    public abstract String getServiceName();

    /**
     * Set the service type
     * @param serviceType the service type
     */
    public abstract void setServiceType(String serviceType);

    /**
     * Get service type
     * @return the service type
     */
    public abstract String getServiceType();

    /**
     * Set image output format
     * @param format the image format
     */
    public abstract void setImageFormat(String format);

    /**
     * Get the output image format
     * @return the output image format
     */
    public abstract String getImageFormat();

    /**
     * Set the serivce configuration
     * @param config the service configuration
     */
    public abstract void setConfig(String config) throws java.rmi.RemoteException;

    /**
     * Get the service configuration
     * @return AXL document
     */
    public abstract String getConfig();

    /**
     * Get the output URL
     * @return output URL
     */
    public abstract String getOutputUrl();

    /**
     * Set output URL of Samba based jCIFS
     * @param outputUrl the output URL
     */
    public abstract void setOutputUrl(String outputUrl);

    /**
     * Get output directory
     * @return output directory
     */
    public abstract String getOutputDir();

    /**
     * Set output directory
     * @param outputDir the output directory
     */
    public abstract void setOutputDir(String outputDir);

    /**
     * Get a layerset
     * @return Layerset
     */
    public Layerset getLayers() {
        System.out.println("BaseCatalog: getLayers() ");
        Layerset lset = null;
        if (_layout.getMapElementCount() >0 ) {
            MapElement mapEle = (MapElement)_layout.getMapElements().next();
            lset = mapEle.getMap().getLayerset();
        }
        return lset;
    }

    /**
     * Get the map's coordinate system
     * @return CoordinateSystem
     */
    public CoordinateSystem getCoordinateSystem() {
        System.out.println("BaseCatalog: getCoordinateSystem() ");
        CoordinateSystem cs = null;
        if (_layout.getMapElementCount() >0 ) {
            MapElement mapEle = (MapElement)_layout.getMapElements().next();
            cs = mapEle.getMap().getCoordinateSystem();
        }
        System.out.println("BaseCatalog: getCoordinateSystem() " + cs);
        return cs;
    }

    /**
     * Get map environment
     * @return map Environment
     */
    public Environment getEnvironment() {
      return _environment;
    }

    /**
     * Get service info
     * @return a String array containing all the service information details
     */
    public String[] getServiceInfo() {
        String[] serviceInfo = new String[6];
        serviceInfo[0] = getServiceName();
        serviceInfo[1] = getServiceType();
        serviceInfo[2] = getConfig();
        serviceInfo[3] = getOutputDir();
        serviceInfo[4] = getOutputUrl();
        serviceInfo[5] = getImageFormat();
        return serviceInfo;
    }

    /**
     * Update service info
     * @param type the service type
     * @param config the service configuration
     * @param output_dir the map image output directory
     * @param output_url the map image output URL
     * @param image_format the map image format
     */
    public void updateServiceInfo(
        String type,
        String config,
        String output_dir,
        String output_url,
        String image_format)
        //throws java.rmi.RemoteException
    {

        System.out.println("BaseCatalog: updateServiceInfo() 1 output_url=" + output_url);

        //check service type
        if (!isServiceTypeSupported(type))
            throw new java.lang.IllegalArgumentException("Service type is not supported!");
        if (!isServiceConfigurationValid(config))
            throw new java.lang.IllegalArgumentException("Service configuration is not valid!");

        boolean validDir = ((output_dir != null) && !output_dir.trim().equals(""));
        boolean validUrl = ((output_url != null) && !output_url.trim().equals("") && !(output_url.indexOf("://") == -1));
        if (!validDir && !validUrl)
            throw new java.lang.IllegalArgumentException("Either output directory or output URL must be specified!");

        if (!validDir) output_dir = "";
        if (!validUrl) output_url = "";

        setOutputUrl(output_url);
        setServiceType(type.trim());

        try {
            setConfig(config.trim());
        } catch (java.rmi.RemoteException ex) {
            ex.printStackTrace();
        }

        if (image_format != null) setImageFormat(image_format.trim());
        if (output_dir != null) setOutputDir(output_dir.trim());
    }

    /**
     * Load layers from data source based on the service configuration
     * @param axl the service configuration
     */
    public void loadLayers(String axl)
        throws java.rmi.RemoteException
    {
        loadLayersWithLayoutProject(axl, _layoutProj);
    }

    private void loadLayersWithLayoutProject(String axl, LayoutProject axlProj)
        throws java.rmi.RemoteException
    {
        if (axl == null) // || !axl.startsWith("<?xml"))
            throw new java.rmi.RemoteException("ArcXML doc is null!");

        // get only  the part ended with ARCXML tag in case some system conversion introduced
        // some hidden characters that could cause the ArcXML parsing error
        int p = axl.indexOf("</ARCXML>");
        axl = axl.substring(0, p+9);

        System.out.println("BaseCatalog: loadLayers() p=" + p + " # of chars=" + axl.length());
        System.out.println("BaseCatalog: loadLayers() # of chars=" + axl.length() + " " + axl.substring(axl.length()-10, axl.length()) );
        //System.out.println(axl);

        axlProj.setFileAccess(new StringFileAccess(axl));
        try {
            axlProj.load();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new java.rmi.RemoteException(ex.getMessage());
        }
        System.out.println("BaseCatalog: loadLayers() GOOD");
    }

    /**
     * Check to see if the given service type is supported or not
     * @param type the service type
     * @return true or false
     */
    public boolean isServiceTypeSupported(String type) {
        if (type != null) {
            for (int i=0; i<serviceTypes.length; i++)
                if (type.trim().equals(serviceTypes[i])) return true;
        }
        return false;
    }

    /**
     * Check to see if the given image format is supported or not
     * @param format the image format
     * @return true or false
     */
    public boolean isImageFormatSupported(String format) {
        if (format != null) {
            String[] formats = com.esri.mo2.map.img.ImageSupport.getListOfWriterByTypeName();
            for (int i=0; i<formats.length; i++)
                if (format.trim().compareToIgnoreCase(formats[i])==0) return true;
        }
        return false;
    }

    /**
     * Check to see if the given service configuration valid or not
     * @param config the service configuration document
     * @return true or false
     */
    public boolean isServiceConfigurationValid(String config) {
        try {
            LayoutProject layoutProj = new LayoutProject();
            Environment environment = new Environment();
            PageLayout layout = new PageLayout();

            layoutProj.setRegisteredEnvironment(environment);
            layoutProj.setRegisteredLayout(layout);
            loadLayersWithLayoutProject(config, layoutProj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

}