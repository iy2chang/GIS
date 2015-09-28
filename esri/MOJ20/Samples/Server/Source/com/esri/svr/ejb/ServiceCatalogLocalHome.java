package com.esri.svr.ejb;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ESRI
 * @version 2.0
 */

import java.util.Collection;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import javax.ejb.CreateException;


/**
 * This interface defines methods for creating and finding SeriviceCatalogLocal object
 * and other supporting methods for returing all services and checking service type,
 * validity and supported image formats.
 */
public interface ServiceCatalogLocalHome extends EJBLocalHome {
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
    public ServiceCatalogLocal create(
        String serviceName,
        String serviceType,
        String output_url,
        String output_dir,
        String image_format,
        String config)
        throws CreateException;

    /**
     * Find a service based on a service primary key (i.e. service name)
     * @param key the service primary key
     * @return a service name as primary key
     * @throws FinderException
     */
    public ServiceCatalogLocal findByPrimaryKey(String key)
        throws FinderException;

    /**
     * Find a service based the given service name
     * @param serviceName the service name
     * @return a service name as primary key
     * @throws FinderException
     */
    public ServiceCatalogLocal findByServiceName(String serviceName)
        throws FinderException;

    /**
     * Find all the service names
     * @return a sorted set that contains all the service names
     */
    public Collection findAllServices()
        throws FinderException;

    /**
     * Check to see if the given service type is supported or not
     * @param type the service type
     * @return true or false
     */
    public boolean isServiceTypeSupported(String type);

    /**
     * Check to see if the given image format is supported or not
     * @param format the image format
     * @return true or false
     */
    public boolean isImageFormatSupported(String format);

    /**
     * Check to see if the given service configuration valid or not
     * @param config the service configuration document
     * @return true or false
     */
    public boolean isServiceConfigurationValid(String config);
}