package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.rmi.RemoteException;

import com.esri.svr.map.BaseMapper;
import com.esri.svr.cmn.Feature;
import com.esri.svr.cmn.LayerInfo;
import com.esri.svr.cmn.MapInfo;
import com.esri.svr.cmn.Ring;
import com.esri.svr.cmn.MapResponse;


/**
 *
 * This class provides implementation of MapServiceII and MapServiceIIHome interfaces
 * in addition to SessionBean interface. This is stateless session bean so every method call will remove
 *  previous layers and add the layers based on the given service name
 */
public class MapServiceIIBean implements SessionBean {

    protected BaseMapper baseMapper;
    protected SessionContext ctx;

    private ServiceCatalogLocalHome _serviceCatalogLocalHome;

//
// default constructor
//
    /**
     * The Default consutrctor that will create a server Map object
     */
    public MapServiceIIBean() {
        baseMapper = new BaseMapper();
    }

//
// public methods
//

    /**
     * add layers based on the given service name from service catalog local bean
     * @param servicename the service's layers to be added
     */
    public void addLayers(String servicename)
       throws RemoteException
    {
        /*System.out.println("1 MapServiceIIBean:addLayers() before adding # of layers=>" + getMap().getLayerCount());
        System.out.println("2 ########## service name=" + servicename + " old name=" + getServiceName());
        long t1 = System.currentTimeMillis();
        */
        System.err.println("3 ########## service name=" + servicename);

        baseMapper.clearAcetateLayers();
        baseMapper.clearDisplayLists();

        // skip the add layers process if the service's layers have been added and the
        // the requested service is identical to the one already loaded
        boolean isSameService = (baseMapper.getMapName() != null &&
            baseMapper.getMapName().equalsIgnoreCase(servicename.trim()) &&
            baseMapper.getMap().getLayerCount()>0);

        System.err.println("4 ########## service name=" + servicename + " is the same service=>" + isSameService);
        if (isSameService)  return;

        baseMapper.removeLayers();

        System.out.println("5 ########## service name=" + servicename);

        try {
            ServiceCatalogLocal serviceCatalogLocal = getServiceCatalogLocalHome().findByServiceName(servicename);

            //getMap().setExtent(serviceCatalogLocal.getRegisteredProperties().getEnvelope());
            com.esri.mo2.map.dpy.Layerset lset = serviceCatalogLocal.getLayers();
            System.out.println("@@@ MapServiceIIBean:addLayers() # of layers=" + lset.size());

            //for (int i=lset.getSize()-1; i>=0; i--) {
            for (int i=0; i<lset.size(); i++) {
              baseMapper.getMap().getLayerset().addLayer(lset.layerAt(i));
              System.out.println("i=" + i + " Layer Name=" + lset.layerAt(i).getName() + " visible=" + lset.layerAt(i).isVisible());
            }

            // set output url or directory
            System.out.println("@@@ MapServiceIIBean:addLayers() output url=" + serviceCatalogLocal.getOutputUrl());
            System.out.println("@@@ MapServiceIIBean:addLayers() output dir=" + serviceCatalogLocal.getOutputDir());

            String url = serviceCatalogLocal.getOutputUrl();
            String dir = serviceCatalogLocal.getOutputDir();
            if (url != null && !url.trim().equals(""))
                baseMapper.setOutputUrl(url);
            else if (dir != null && !dir.equals(""))
                baseMapper.setOutputDir(dir);

            // set map units here
            baseMapper.getMap().setMapUnit(serviceCatalogLocal.getLayers().getDisplayManager().getMapUnit());

            // set map coordinate system
            baseMapper.getMap().setCoordinateSystem(serviceCatalogLocal.getCoordinateSystem());

        } catch (javax.ejb.FinderException fex) {
            fex.printStackTrace();
        }

        System.out.println("6 ########## service name=" + servicename);

        baseMapper.setMapName(servicename.trim());

        //long t2 = System.currentTimeMillis();
        //System.out.println("2 MapServiceIIBean:addLayers() # of layers=>" +
        //    getMap().getLayerCount() + " total time (ms)=" + (t2-t1) + " map unit=" + getMap().getMapUnit());

        // testing
        //long tt1 = System.currentTimeMillis();
        //com.esri.mo2.svr.core.Map map = new com.esri.mo2.svr.core.Map();
        //long tt2 = System.currentTimeMillis();
        //System.out.println("MapServiceIIBean:addLayers() time to create a Map object=" + (tt2-tt1));
    }

    /**
     * get the service catalog home object
     */
    public ServiceCatalogLocalHome getServiceCatalogLocalHome()
        throws RemoteException
    {
        //if (_serviceCatalogLocalHome == null) {
            try {
                Context ctx = new InitialContext();
                Object result = ctx.lookup("java:comp/env/ejb/ServiceCatalogLocalHome2");
                _serviceCatalogLocalHome = (ServiceCatalogLocalHome)javax.rmi.PortableRemoteObject.narrow(result, ServiceCatalogLocalHome.class);
            } catch (javax.naming.NamingException ex) {
                ex.printStackTrace();
                throw new RemoteException(ex.getMessage());
            }
        //}
        return _serviceCatalogLocalHome;
    }

    /**
     * Get service info for a given service
     * @param serviceName the service name
     * @return a string representation of service information
     */
    public String getServiceInfo(String serviceName)
        throws RemoteException
    {
        try {
            String[] serviceinfo =  getServiceCatalogLocalHome().findByServiceName(serviceName).getServiceInfo();
            StringBuffer strbuf = new StringBuffer();
            for (int i=0; i<serviceinfo.length; i++) strbuf.append(serviceinfo[i]+",");
            return strbuf.toString();
        } catch (javax.ejb.FinderException ex) {
            throw new RemoteException(ex.getLocalizedMessage());
        }
    }

    /**
     * get an image with given parameters
     * @param serviceName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     */
    public String getImage(String serviceName, int width, int height, String extent)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            System.out.println("MapServiceIIBean.getImage()");
            return baseMapper.getImage(width, height, extent);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     *
     * @param serviceName
     * @param width
     * @param height
     * @param extent
     * @param layerVisList
     * @param selectedLayerIndex
     * @param findString
     * @return
     * @throws RemoteException
     */
    public MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayerIndex,
        String findString)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            System.out.println("MapServiceIIBean.getResponse( 1 )");
            return baseMapper.getResponse(width, height, extent, layerVisList, selectedLayerIndex, findString);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     *
     * @param serviceName
     * @param width
     * @param height
     * @param extent
     * @param layerVisList
     * @param selectedLayerIndex
     * @param queryEnvelope
     * @param queryString
     * @return
     * @throws RemoteException
     */
    public MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString)
    throws RemoteException
    {
        try {
            addLayers(serviceName);
            System.out.println("MapServiceIIBean.getResponse( 2 )");
            return baseMapper.getResponse(width, height, extent, layerVisList, selectedLayerIndex, queryEnvelope, queryString);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

     /**
     *
     * @param serviceName
     * @param width
     * @param height
     * @param extent
     * @param layerVisList
     * @param selectedLayerIndex
     * @param queryEnvelope
     * @param bufferDistance
     * @return
     * @throws RemoteException
     */
    public MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit)
        throws java.rmi.RemoteException
    {
        try {
            addLayers(serviceName);
            System.out.println("MapServiceIIBean.getResponse( 3 )");
            return baseMapper.getResponse(width, height, extent, layerVisList, selectedLayerIndex, queryEnvelope, queryString, findString, bufferDistance, bufferUnit);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new java.rmi.RemoteException(ex.getMessage());
        }
    }

    /**
     *
     * @param serviceName
     * @param width
     * @param height
     * @param extent
     * @param layerVisList
     * @param selectedLayerIndex
     * @param queryEnvelope
     * @param queryString
     * @param findString
     * @param bufferDistance
     * @param bufferUnit
     * @param targetLayerIndex
     * @return
     * @throws RemoteException
     */
     public MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayerIndex)
    throws RemoteException
    {
        try {
            addLayers(serviceName);
            System.out.println("MapServiceIIBean.getResponse( 4 )");
            return baseMapper.getResponse(
                width, height,
                extent, layerVisList, selectedLayerIndex, queryEnvelope, queryString, findString,
                bufferDistance, bufferUnit, targetLayerIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     *  get full map extent for the given service
     *  @param serviceName the service name
     */
    public String getFullMapExtent(String serviceName)
        throws RemoteException
    {
        addLayers(serviceName);
        return baseMapper.getFullMapExtent();
    }

    /**
     * get map extent for the given service
     * @param serviceName the service name
     */
    public String getMapExtent(String serviceName)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.getMapExtent();
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * get service information for the given service
     * @param serviceName the service name
     */
    public MapInfo getMapInfo(String serviceName)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.getMapInfo();
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Select feature(s)
     * @param serviceName the service name
     * @param activeLayerIndex the selected/active layer index within the service
     * @param queryEnvelope
     * @param queryString
     * @return
     * @throws RemoteException
     */
    public Feature[] select(
            String serviceName,
            int activeLayerIndex,
            String queryEnvelope,
            String queryString)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.select(activeLayerIndex, queryEnvelope, queryString);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Select feature(s) in the target layer based on the buffer polygon
     * created by buffering the selected feature(s) of the given layer
     * using the given selection envelope
     * @param serviceName
     * @param activeLayerIndex
     * @param queryEnvelope
     * @param bufferDistance
     * @param targetLayerIndex
     * @return a vector of selected features
     * @throws RemoteException
     */
    public Feature[] select(
        String serviceName,
        int activeLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayerIndex)
           throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.select(activeLayerIndex, queryEnvelope, queryString, findString, bufferDistance, bufferUnit, targetLayerIndex);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
      * Buffer a set of selected features for the selected/active layer with given buffering distance
      *
      * @param serviceName
      * @param selectedLayerIndex
      * @param queryEnvelope
      * @param queryString
      * @param findString
      * @param distance
      * @param unit
      * @return
      * @throws RemoteException
      */
    public Ring[] buffer(
            String serviceName,
            int selectedLayerIndex,
            String queryEnvelope,
            String queryString,
            String findString,
            double distance,
            String unit)
        throws RemoteException
    {
        addLayers(serviceName);
        return baseMapper.buffer(selectedLayerIndex, queryEnvelope, queryString, findString, distance, unit);
    }

    /**
     * Find all the feature(s) in the selected layer containing the findString
     * @param serviceName the service name
     * @param selectedLayerIndex the selected layer
     * @param findString
     * @return a Vector of found feature(s)
     */
    public Feature[] find(String serviceName, int selectedLayerIndex, String findString)
        throws RemoteException
    {
        addLayers(serviceName);
        return baseMapper.find(selectedLayerIndex, findString);
    }

    /**
     *
     * @param serviceName
     * @param layerName
     * @return
     * @throws RemoteException
     */
    public LayerInfo getLayerInfo(String serviceName, String layerName)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.getLayerInfoByName(layerName);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     *
     * @param serviceName
     * @param layerIndex
     * @return
     * @throws RemoteException
     */
    public LayerInfo getLayerInfo(String serviceName, int layerIndex)
        throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.getLayerInfo(layerIndex);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }
//
// session bean required methods
//

    /**
     * Set session context
     * @param ctx the session bean context
     */
    public void setSessionContext(SessionContext ctx) throws javax.ejb.EJBException {
        System.out.println("MapServiceIIBean:setSessionContext()");
        this.ctx = ctx;
    }

    /**
     * Remove the session bean (empty implementation)
     */
    public void ejbRemove() throws javax.ejb.EJBException {
        System.out.println("MapServiceIIBean:ejbRemove()");
    }

    /**
     * Activate the session bean (empty implementation)
     */
    public void ejbActivate() throws javax.ejb.EJBException {
        System.out.println("MapServiceIIBean:ejbActivate()");
        // re-set _serviceCatalogLocalHome to null since it could be changed after it serialized
        // so to force the bean to look for it again
        _serviceCatalogLocalHome = null;
    }

    /**
     * Passivate the session bean (empty implementation)
     */
    public void ejbPassivate() throws javax.ejb.EJBException {
        System.out.println("MapServiceIIBean:ejbPassivate()");
    }

    /**
     * Create the session bean (empty implementation)
     */
    public void ejbCreate() throws javax.ejb.CreateException {
        System.out.println("MapServiceIIBean:ejbCreate()");
    }
}