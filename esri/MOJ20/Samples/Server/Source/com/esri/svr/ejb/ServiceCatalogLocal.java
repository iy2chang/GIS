package com.esri.svr.ejb;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ESRI
 * @version 2.0
 */

import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.util.Environment;
import com.esri.mo2.cs.geod.CoordinateSystem;

/**
 * ServiceCatalog local interface for service catalog bean
 */
public interface ServiceCatalogLocal extends javax.ejb.EJBLocalObject {

    /**
     * Set the service name
     * @param serviceName the service name
     */
    // primary key should allow to be set through interface
    //public void setServiceName(String serviceName);

    /**
     * Get the service name
     * @return service name
     */
    public String getServiceName();

    /**
     * Set the service type
     * @param serviceType the service type
     */
    public void setServiceType(String serviceType);

    /**
     * Get service type
     * @return the service type
     */
    public String getServiceType();

    /**
     * Get the output URL
     * @return output URL
     */
    public String getOutputUrl();

    /**
     * Set output URL of Samba based jCIFS or some other file system such as Network File System
     * @param outputUrl the output URL
     */
    public void setOutputUrl(String outputUrl);

    /**
     * Get output directory
     * @return output directory
     */
    public String getOutputDir();

    /**
     * Set output directory
     * @param outputDir the output directory
     */
    public void setOutputDir(String outputDir);

    /**
     * Get the output image format
     * @return the output image format
     */
    public String getImageFormat();

    /**
     * Set image output format
     * @param format the image format
     */
    //public void setImageFormat(String format);

    /**
     * Get the service configuration
     * @return AXL document
     */
    public String getConfig();

    /**
     * Set the serivce configuration
     * @param config the service configuration
     */
    public void setConfig(String config);

    /**
     * Get layerset
     * @return Layerset
     */
    public Layerset getLayers();

    /**
     * Get map environment
     * @return map Environment
     */
    public Environment getEnvironment();

     /**
     * Get the map's coordinate system
     * @return CoordinateSystem
     */
    public CoordinateSystem getCoordinateSystem();

    /**
     * Get service info
     * @return a String array containing all the service information details
     */
    public String[] getServiceInfo();

    /**
     * Update service info
     * @param type the service type
     * @param config the service configuration
     * @param output_dir the map image output directory
     * @param output_url the map image output URL
     * @param image_format the map image format
     */
    public void updateServiceInfo(
        String type, String config,
        String output_dir, String output_url,
        String image_format); //throws java.rmi.RemoteException;
}