package com.esri.svr.ejb;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ESRI
 * @version 2.0
 */

import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.EJBException;

import com.esri.svr.cat.BaseCatalog;


/**
 * This class provides partial implementations of ServiceCatalogLocal and ServiceCatalogLocalHome interfaces
 * in addition to the EntityBean interface. This abstract class could be used as CMP bean.
 */
public abstract class ServiceCatalogBean extends BaseCatalog implements EntityBean {

    protected EntityContext ctx;

    /**
     * The default constructor that will construct the necessary objects needed for the bean to operate
     */
    public ServiceCatalogBean()
      //throws java.io.IOException
    {
        super();
      System.out.println("ServiceCatalogBean: ServiceCatalogBean() ... a new service bean created by EJB container");
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
    public abstract void setConfig(String config);

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
     * @param outputJCIFSUrl the output URL
     */
    public abstract void setOutputUrl(String outputJCIFSUrl);

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

    //
    // EJB-related methods
    //

    /**
     * Check to see if the given service type is supported or not
     * @param type the service type
     * @return true or false
     */
    public boolean ejbHomeIsServiceTypeSupported(String type) {
        return isServiceTypeSupported(type);
        /*
        if (type != null) {
            for (int i=0; i<serviceTypes.length; i++)
                if (type.trim().equals(serviceTypes[i])) return true;
        }
        return false;
        */
    }

    /**
     * Check to see if the given image format is supported or not
     * @param format the image format
     * @return true or false
     */
    public boolean ejbHomeIsImageFormatSupported(String format) {
        return isImageFormatSupported(format);
    }

    /**
     * Check to see if the given service configuration valid or not
     * @param config the service configuration document
     * @return true or false
     */
    public boolean ejbHomeIsServiceConfigurationValid(String config) {
        return isServiceConfigurationValid(config);
    }

    /**
     * Create a service catalog bean instance with given parameters
     * @param serviceName the service name
     * @param serviceType the service type
     * @param output_dir the output directory
     * @param output_url the output URL
     * @param image_format the output image format
     * @param config the service configuration
     * @return service name as primary key
     */
    public String ejbCreate(
        String serviceName,
        String serviceType,
        String output_dir,
        String output_url,
        String image_format,
        String config) throws javax.ejb.CreateException
    {
        System.out.println("ServiceCatalogBean:ejbCreate(String, ...) called. serviceName='" + serviceName + "'");
        //System.out.println("ServiceCatalogBean:ejbCreate(String, ...) called. serviceType='" + serviceType + "'");
        //System.out.println("ServiceCatalogBean:ejbCreate(String, ...) called. output_url=" + output_url);
        //System.out.println("ServiceCatalogBean:ejbCreate(String, ...) called. output_dir=" + output_dir);
        //System.out.println("ServiceCatalogBean:ejbCreate(String, ...) called. image_format=" + image_format);
        //System.out.println("ServiceCatalogBean:ejbCreate(String, ...) called. config='" + config + "'");

        if ( (serviceName == null || serviceName.trim().equals("")) ||
             (serviceType == null || serviceType.trim().equals("")) ||
             (config == null || config.trim().equals("")))
            throw new EJBException("Service name, type and configuration must have valid values");

        config = config.trim();
        serviceName = serviceName.trim();
        serviceType = serviceType.trim();
        image_format = image_format.trim();
        if (output_dir != null) output_dir = output_dir.trim();
        if (output_url != null) output_url = output_url.trim();

        if (!ejbHomeIsServiceTypeSupported(serviceType))
            throw new EJBException("Service type is not valid!");

        if (output_dir == null && output_url == null)
            throw new EJBException("Either output directory or output URL must be specified!");

        if (output_url != null && !output_url.trim().equals(""))
            if (!output_url.startsWith("smb://"))
                throw new EJBException("Output URL is not valid! " + output_url);

        if (image_format != null)
            if (!ejbHomeIsImageFormatSupported(image_format))
                throw new EJBException("Map output image format is not valid!");

        try {
            loadLayers(config);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        setServiceName(serviceName);
        setServiceType(serviceType);
        setOutputDir(output_dir);
        setOutputUrl(output_url);
        setImageFormat(image_format);
        setConfig(config);

        return serviceName;
    }

    /**
     * Remove a service catalog bean (empty implementation)
     */
    public void ejbRemove() {
      System.out.println("ServiceCatalogBean:ejbRemove() called.");
    }

    /**
     * Activates a service catalog bean (empty implementation)
     */
    public void ejbActivate() {
        System.out.println("ServiceCatalogBean: ejbActivate() called. ");
    }

    /**
     * Passivates a service catalog bean (empty implementation)
     */
    public void ejbPassivate() {
        System.out.println("ServiceCatalogBean: ejbPassivate() called. ");
    }

    /**
     * Load data set for the service catalog bean
     * @throws EJBException
     */
    public void ejbLoad()
        throws javax.ejb.EJBException
    {
        //System.out.println("ServiceCatalogBean: ejbLoad() called. " + getConfig());
        try {
            loadLayers(getConfig());
        } catch (Exception ex) {
            System.out.println("ServiceCatalogBean: ejbLoad() throws an exception! " + ex.getMessage());
            ex.printStackTrace();
            throw new javax.ejb.EJBException(ex.getMessage());
        }
    }

    /**
     * Store the service catalog bean (empty implementation)
     */
    public void ejbStore() {
      System.out.println("ServiceCatalogBean: ejbStore() called. " + getServiceName());
      //System.out.println("ServiceCatalogBean: ejbStore() called. " + getServiceType());
      //System.out.println("ServiceCatalogBean: ejbStore() called. " + getOutputDir());
      //System.out.println("ServiceCatalogBean: ejbStore() called. " + getOutputURL());
      //System.out.println("ServiceCatalogBean: ejbStore() called. " + getImageFormat());
      //System.out.println("ServiceCatalogBean: ejbStore() called. " + getConfig());
    }

    /**
     * Set entity bean context
     * @param ctx the entity bean context
     */
    public void setEntityContext(EntityContext ctx) {
      System.out.println("ServiceCatalogBean: setEntityContext() called. "/* + ctx.getPrimaryKey()*/);
      this.ctx = ctx;
    }

    /**
     * Unset the entity bean context
     */
    public void unsetEntityContext() {
      System.out.println("ServiceCatalogBean: unsetEntityContext() called. ");
      this.ctx = null;
    }

    /**
     * Post create method (empty implementation)
     * @param serviceName the service name
     * @param serviceType the service type
     * @param output_dir the output directory
     * @param output_url the output URL
     * @param image_format the output image format
     * @param config the service configuration
     */
    public void ejbPostCreate(
        String serviceName,
        String serviceType,
        String output_url,
        String output_dir,
        String image_format,
        String config)
    {
        System.out.println("ServiceCatalogBean:ejbPostCreate(String, ...) called.");
    }
}