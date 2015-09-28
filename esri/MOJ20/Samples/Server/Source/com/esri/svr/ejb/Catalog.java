package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import com.esri.svr.cmn.ServiceInfo;

import javax.ejb.EJBObject;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.ejb.FinderException;
import javax.ejb.EJBException;
import java.rmi.RemoteException;


/**
 * This interface defines the functionality of a Catalog EJB.
 */
public interface Catalog extends EJBObject  {

    /**
     * Get a list of all services
     * @return a linked list of service names
     * @throws FinderException
     * @throws EJBException
     */
    public ServiceInfo[] getAllServices() throws FinderException,RemoteException;

    /**
     * Get all the details for a given service
     * @param name the service name
     * @return a array of String containing details of a service
     * @throws FinderException
     * @throws EJBException
     */
    public String[] getServiceInfo(String name) throws FinderException,RemoteException;

    /**
     * Remove a service from service catalog based on the given service name
     * @param serviceName the service to be removed
     * @throws RemoveException
     * @throws CreateException
     * @throws FinderException
     * @throws EJBException
     */
    public void removeService(String serviceName) throws RemoveException, CreateException, FinderException,RemoteException;

    /**
     * Add a new service to the service catalog with the given service detail information, where
     * service name, type and configuration are required, and others can take default values.
     * @param serviceName the service name
     * @param serviceType the service type
     * @param config the service configuration document
     * @param output_dir the map image output directory
     * @param output_url the map image output URL
     * @param image_format the map image format
     * @throws CreateException
     * @throws EJBException
     */
    public void addService(
        String serviceName,
        String serviceType,
        String config,
        String output_dir,
        String output_url,
        String image_format) throws CreateException,RemoteException;

    /**
     * Refresh a service with the given service detail information
     * @param serviceName the new service name
     * @param oldServiceName the original service name
     * @param serviceType the service type
     * @param config the service configuraiton document
     * @param output_dir the map image output directory
     * @param output_url the map image output URL for (jCIFS)
     * @param image_format the map image format
     * @throws CreateException
     * @throws RemoveException
     * @throws EJBException
     * @throws FinderException
     */
    public void refreshService(
        String serviceName,
        String oldServiceName,
        String serviceType,
        String config,
        String output_dir,
        String output_url,
        String image_format) throws CreateException, RemoveException, FinderException,RemoteException;
}