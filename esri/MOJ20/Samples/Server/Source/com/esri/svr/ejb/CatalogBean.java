package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import java.util.Collection;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.RemoteException;

import com.esri.svr.cmn.ServiceInfo;

/**
 * This class implements the functionality defined in Catalog and CatalogHome interfaces.
 */
public class CatalogBean implements SessionBean  {


    private ServiceCatalogLocalHome _serviceCatalogLocalHome;

    /**
     * Get all services
     * @return a linked list containing all service information
     * @throws javax.ejb.FinderException
     * @throws RemoteException
     */
    public ServiceInfo[] getAllServices()
        throws javax.ejb.FinderException,RemoteException
    {
        System.out.println("CatalogBean:getAllServices()");
        java.util.Vector list = new java.util.Vector();

        Collection set = getServiceCatalogLocalHome().findAllServices();
        System.out.println("CatalogBean:getAllServices() # of services=" + set.size());

        java.util.Iterator it = set.iterator();
        ServiceCatalogLocal serviceCatalogLocal = null;
        while (it.hasNext()) {
            serviceCatalogLocal = (ServiceCatalogLocal)it.next();

            System.out.println("CatalogBean:getAllServices() ServiceCatalogLocal object=" + serviceCatalogLocal);

            ServiceInfo service = new ServiceInfo();
            service.setServiceName(serviceCatalogLocal.getServiceName());
            service.setServiceType(serviceCatalogLocal.getServiceType());
            service.setOutputDir(serviceCatalogLocal.getOutputDir());
            service.setOutputURL(serviceCatalogLocal.getOutputUrl());
            service.setImageFormat(serviceCatalogLocal.getImageFormat());
            service.setConfig(serviceCatalogLocal.getConfig());

            System.out.println("CatalogBean:getAllServices() name=" + service.getServiceName());
            System.out.println("CatalogBean:getAllServices() type=" + service.getServiceType());
            System.out.println("CatalogBean:getAllServices() format=" + service.getImageFormat());
            System.out.println("CatalogBean:getAllServices() output=" + service.getOutputDir());

            list.add(service);
        }

        ServiceInfo sinfo[] = new ServiceInfo[list.size()];
        for (int i=0; i<list.size(); i++) {
            sinfo[i] = (ServiceInfo)list.elementAt(i);
            System.out.println("CatalogBean:getAllServices() " + ((ServiceInfo)(list.get(i))).getServiceName());
        }
        return sinfo;
    }

    /**
     * Get a service detail information
     * @param name the service name
     * @return a String array containing all service information
     * @throws javax.ejb.FinderException
     * @throws RemoteException
     */
    public String[] getServiceInfo(String name)
        throws javax.ejb.FinderException,RemoteException
    {
        ServiceCatalogLocal serviceCatalogLocal = getServiceCatalogLocalHome().findByServiceName(name);
        String[] details = new String[6];
        details[0] = serviceCatalogLocal.getServiceName();
        details[1] = serviceCatalogLocal.getServiceType();
        details[2] = serviceCatalogLocal.getConfig();
        details[3] = serviceCatalogLocal.getOutputDir();
        details[4] = serviceCatalogLocal.getOutputUrl();
        details[5] = serviceCatalogLocal.getImageFormat();

        return details;
    }

    /**
     * Remove a service
     * @param serviceName the service name
     * @throws javax.ejb.RemoveException
     * @throws javax.ejb.CreateException
     * @throws javax.ejb.FinderException
     * @throws RemoteException
     */
    public void removeService(String serviceName)
        throws javax.ejb.CreateException, javax.ejb.RemoveException,
            javax.ejb.FinderException,RemoteException
    {
        System.out.println("CatalogBean: removeService() " + serviceName);

        ServiceCatalogLocal serviceCatalogLocal = getServiceCatalogLocalHome().findByServiceName(serviceName);
        serviceCatalogLocal.remove();
    }

    /**
     * Add a new service
     * @param serviceName the service name
     * @param serviceType the service type
     * @param output_dir the output directory
     * @param output_url the output URL
     * @param image_format the output image format
     * @param config the service configuration
     * @throws javax.ejb.CreateException
     * @throws RemoteException
     */
    public void addService(
        String serviceName,
        String serviceType,
        String output_dir,
        String output_url,
        String image_format,
        String config)
        throws javax.ejb.CreateException,RemoteException
    {
        System.out.println("CatalogBean: addService(String, ...) called. serviceName=" + serviceName);
        System.out.println("CatalogBean: addService(String, ...) called. serviceType=" + serviceType);
        System.out.println("CatalogBean: addService(String, ...) called. output_url=" + output_url);
        System.out.println("CatalogBean: addService(String, ...) called. output_dir=" + output_dir);
        System.out.println("CatalogBean: addService(String, ...) called. image_format=" + image_format);
        System.out.println("CatalogBean: addService(String, ...) called. config=" + config.substring(0, 20));

        if ( (serviceName == null || serviceName.trim().equals("")) ||
             (serviceType == null || serviceType.trim().equals("")) ||
             (config == null || config.trim().equals("")))
            throw new javax.ejb.CreateException("Service name, type and configuration must have valid values");

        try {
            // check if the service name already exists
            System.out.println("  #### CatalogBean: start calling findByServiceName() " + serviceName);
            getServiceCatalogLocalHome().findByServiceName(serviceName);
            System.out.println("  #### CatalogBean: end calling findByServiceName() " + serviceName);
        } catch (javax.ejb.FinderException fex) {
            System.out.println("  #### CatalogBean: start calling create() " + serviceName);
            getServiceCatalogLocalHome().create(serviceName, serviceType, output_dir, output_url, image_format, config);
            System.out.println("  #### CatalogBean: end calling create() " + serviceName);
            return;
        } catch (Exception ex) { }

        throw new javax.ejb.CreateException("Service '" + serviceName + "' already exists!");
    }

    /**
     * Refresh a service. If the new name equals the original name,
     * it will just update other fields.Otherwise, it will remove the old one and
     * re-create the new one.
     * @param serviceName the new service name
     * @param oldServiceName the oroginal service name
     * @param serviceType the service type
     * @param output_dir output directory
     * @param output_url output URL
     * @param image_format output image format
     * @param config the service configuration
     * @throws javax.ejb.CreateException
     * @throws javax.ejb.RemoveException
     * @throws javax.ejb.FinderException
     * @throws RemoteException
     */
    public void refreshService(
        String serviceName,
        String oldServiceName,
        String serviceType,
        String output_dir,
        String output_url,
        String image_format,
        String config)
        throws javax.ejb.CreateException, javax.ejb.RemoveException,
            javax.ejb.FinderException,RemoteException
    {
        System.out.println("CatalogBean: refreshService() " + serviceName);
        System.out.println("CatalogBean: refreshService() " + oldServiceName);
        //System.out.println("CatalogBean: refreshService(String, ...) called. serviceName=" + serviceName);
        //System.out.println("CatalogBean: refreshService(String, ...) called. serviceType=" + serviceType);
        System.out.println("CatalogBean: refreshService(String, ...) called. output_url=" + output_url);
        System.out.println("CatalogBean: refreshService(String, ...) called. output_dir=" + output_dir);
        //System.out.println("CatalogBean: refreshService(String, ...) called. image_format=" + image_format);
        //System.out.println("CatalogBean: refreshService(String, ...) called. config=" + config);

        if ( (serviceName == null || serviceName.trim().equals("")) ||
             (serviceType == null || serviceType.trim().equals("")) ||
             (config == null || config.trim().equals("")))
            throw new javax.ejb.CreateException("Service name, type and configuration must have valid values");

        // update the service
        if (oldServiceName.trim().equals(serviceName.trim())) {
            //try {
                ServiceCatalogLocal serviceCatalogLocal = getServiceCatalogLocalHome().findByServiceName(serviceName.trim());
                System.out.println("CatalogBean calling ServiceCatalogLocal.updateServiceInfo() output_url=" + output_url);
                serviceCatalogLocal.updateServiceInfo(serviceType, config, output_dir, output_url, image_format);
            //} catch (java.io.IOException ex) {
            //    throw new javax.ejb.CreateException(ex.getMessage() + " " + ex.getClass().getName());
            //}
        }

        // remove and old one and re-create a new service
        else {
            addService(serviceName, serviceType, output_dir, output_url, image_format, config);
            removeService(oldServiceName.trim());
        }
    }

    // cache the home object
    private ServiceCatalogLocalHome getServiceCatalogLocalHome()
        throws javax.ejb.EJBException
    {
        if (_serviceCatalogLocalHome == null) {
            try {
                Context ctx = new InitialContext();
                Object result = ctx.lookup("java:comp/env/ejb/ServiceCatalogLocalHome1");
                _serviceCatalogLocalHome = (ServiceCatalogLocalHome)javax.rmi.PortableRemoteObject.narrow(result, ServiceCatalogLocalHome.class);
            } catch (javax.naming.NamingException ex) {
                //ex.printStackTrace();
                throw new javax.ejb.EJBException(ex.getMessage());
            }
        }

        return _serviceCatalogLocalHome;
    }

//
// session bean required methods
//

    public void setSessionContext(SessionContext ctx) throws javax.ejb.EJBException, java.rmi.RemoteException {
        System.out.println("CatalogBean:setSessionContext()");
    }
    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
        System.out.println("CatalogBean:ejbRemove()");
    }
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
        System.out.println("CatalogBean:ejbActivate()");
    }
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
        System.out.println("CatalogBean:ejbPassivate()");
    }

    public void ejbCreate() throws javax.ejb.CreateException {
        System.out.println("CatalogBean:ejbCreate()");
    }
}