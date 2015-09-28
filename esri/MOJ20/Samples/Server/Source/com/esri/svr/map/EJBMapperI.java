package com.esri.svr.map;

import com.esri.svr.ejb.MapServiceI;
import com.esri.svr.cmn.*;

import javax.servlet.ServletContext;

/**
 * This class extends from BaseMOJMapper to provide implementations
 * for those protected functions defined in BaseMOJMapper class.
 * It's used by SimpleEJB sample controller, EJBControllerI, to access
 * SimplEJB (MapServiceI) running in an EJB container.
 */
public class EJBMapperI extends BaseMOJMapper {
    private MapServiceI _mapService;

    /**
     * Constructs a EJBMapperI object with given properties, servlet context and MapServiceI object.
     * @param properties properties holding JSP page names
     * @param servletContext the servlet context object
     * @param mapService MapServiceI object
     */
    public EJBMapperI(
        java.util.HashMap properties,
        ServletContext servletContext,
        MapServiceI mapService)
    {
        _properties = properties;
        _servletContext = servletContext;
        _mapService = mapService;
    }

//
// protected abstract methods
//
    /**
     * @see BaseMapper#getMapInfo
     */
    protected MapInfo getMapInfo(String serviceName)
        throws java.rmi.RemoteException
    {
        return _mapService.getMapInfo(serviceName);
    }

    /**
     * @see BaseMapper#getLayerInfo
     */
    protected LayerInfo getLayerInfo(String serviceName, int selectedLayer)
        throws java.rmi.RemoteException
    {
        return _mapService.getLayerInfo(serviceName, selectedLayer);
    }

    /**
     * Get all of the service information with each ServiceInfo object describing
     * one map service. The service info is loaded by EJB container.
     * @return an arry of ServiceInfo objects
     * @throws java.rmi.RemoteException
     */
    protected ServiceInfo[] getAllServices()
        throws java.rmi.RemoteException
    {
        try {
            return _mapService.getAllServices();
        } catch (Exception ex) {
            throw new java.rmi.RemoteException(ex.getMessage());
        }
    }

    /**
     * @see BaseMapper#select
     */
    protected Feature[] select(String serviceName, int selectedLayer, String extent, String queryString)
        throws java.rmi.RemoteException
    {
        return _mapService.select(serviceName, selectedLayer, extent, queryString);
    }

    /**
     * @see BaseMapper#select
     */
    protected Feature[] select(
        String serviceName, int selectedLayer, String extent, String queryString, String findString,
        double bufferDistance, String bufferUnit, int targetLayer)
        throws java.rmi.RemoteException
    {
        System.out.println("EJBMapperII: select() extent=" + extent + " query string=" + queryString + " findString=" + findString +
            " buffer distance=" + bufferDistance + " buffer unit=" + bufferUnit + " targetLayer=" + targetLayer);
        return _mapService.select(serviceName, selectedLayer, extent, queryString, findString,
            bufferDistance, bufferUnit, targetLayer);
    }

    /**
     * @see BaseMapper#find
     */
    protected Feature[] find(String serviceName, int selectedLayer, String findString)
        throws java.rmi.RemoteException
    {
        return _mapService.find(serviceName, selectedLayer, findString);
    }

    /**
     * Get full map exent for a given service
     * @param serviceName the service name
     * @return a map extent
     * @throws java.rmi.RemoteException
     */
    protected String getFullExtent(String serviceName)
        throws java.rmi.RemoteException
    {
        return _mapService.getFullMapExtent(serviceName);
    }

    /**
     * @see BaseMapper#getResponse
     */
    protected MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayer,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayer)
        throws java.rmi.RemoteException
    {
        return _mapService.getResponse(serviceName, width, height, extent, layerVisList, selectedLayer, queryEnvelope, queryString, findString, bufferDistance, bufferUnit, targetLayer);
    }

    /**
     * @see BaseMapper#getResponse
     */
    protected MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayer,
        String queryEnvelope,
        String queryString)
        throws java.rmi.RemoteException
    {
        return _mapService.getResponse(serviceName, width, height, extent, layerVisList, selectedLayer, queryEnvelope, queryString);
    }

    /**
     * @see BaseMapper#getResponse
     */
    protected MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayer,
        String findString)
        throws java.rmi.RemoteException
    {
        return _mapService.getResponse(serviceName, width, height, extent, layerVisList, selectedLayer, findString);
    }

    /**
     * @see BaseMOJMapper#getServiceInfo
     */
    protected String getServiceInfo(String serviceName)
        throws java.rmi.RemoteException
    {
        return _mapService.getServiceInfo(serviceName);
    }


    /**
     * get response from server with given set of parameters
     * @param serviceName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisList the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param findString the find string
     * @param mapTitle the map title text
     */
    public String getLayoutImage(
            String serviceName,
            int width, int height,
            String extent,
            boolean[] layerVisList,
            int selectedLayerIndex,
            String findString,
            String mapTitle)
    {
        return null; // this function isn't implemented for SimpleEJB sample
    }

    /**
     * get response from server with given set of parameters
     * @param serviceName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisList the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query filter
     * @param queryString the query string
     * @param mapTitle the map title text
     */
    public String getLayoutImage(
            String serviceName,
            int width, int height,
            String extent,
            boolean[] layerVisList,
            int selectedLayerIndex,
            String queryEnvelope,
            String queryString,
            String mapTitle)
    {
        return null; // this function isn't implemented for SimpleEJB sample
    }

    /**
     * get response from server with given set of parameters
     * @param mapName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query filter
     * @param queryString the quyer string/where clause
     * @param mapTitle the map title text
     */
    public String getLayoutImage(
            String mapName,
            int width, int height,
            String extent,
            boolean[] layerVisibility,
            int selectedLayerIndex,
            String queryEnvelope,
            String queryString,
            double bufferDistance,
            String bufferUnit,
            int targetLayer,
            String mapTitle)
    {
           return null;
    }



}
