package com.esri.svr.map;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import com.esri.svr.ejb.MapServiceII;
import com.esri.svr.ejb.LayoutService;
import javax.servlet.ServletContext;

import com.esri.svr.cmn.Feature;
import com.esri.svr.cmn.LayerInfo;
import com.esri.svr.cmn.ServiceInfo;
import com.esri.svr.cmn.MapResponse;
import com.esri.svr.cmn.MapInfo;

/**
 * This class extends from BaseMOJMapper to provide implementations
 * for those protected functions defined in BaseMOJMapper class.
 * It's used by AdvancedEJB sample controller, EJBControllerII, to access
 * AdvancedEJB (MapServiceII) running in an EJB container.
 */
public class EJBMapperII extends BaseMOJMapper {
    private MapServiceII _mapService;
    private LayoutService _layoutService;

    /**
     * Constructs an EJBMapperII object with the given properties, servlet context,
     * MapServiceII (AdvancedEJB) object and LayoutService object.
     * @param properties properties holding JSP page names
     * @param servletContext the servlet context object
     * @param mapService MapServiceII object
     * @param layoutService LayoutService object
     */
    public EJBMapperII(
        java.util.HashMap properties,
        ServletContext servletContext,
        MapServiceII mapService,
        LayoutService layoutService)
    {
        _properties = properties;
        _servletContext = servletContext;
        _mapService = mapService;
        _layoutService = layoutService;
    }

//
// protected abstract methods
//
    /**
     * @see BaseMOJMapper#getMapInfo
     */
    protected MapInfo getMapInfo(String serviceName)
        throws java.rmi.RemoteException
    {
        return _mapService.getMapInfo(serviceName);
    }

    /**
     * @see BaseMOJMapper#getLayerInfo
     */
    protected LayerInfo getLayerInfo(String serviceName, int selectedLayer)
        throws java.rmi.RemoteException
    {
        return _mapService.getLayerInfo(serviceName, selectedLayer);
    }

    /**
     * @see BaseMOJMapper#getAllServices
     */
    protected ServiceInfo[] getAllServices()
        throws java.rmi.RemoteException
    {
        throw new java.rmi.RemoteException("This method shouldn't be called!");
    }

    /**
     * @see BaseMOJMapper#select
     */
    protected Feature[] select(String serviceName, int selectedLayer, String extent, String queryString)
        throws java.rmi.RemoteException
    {
        return _mapService.select(serviceName, selectedLayer, extent, queryString);
    }

    /**
     * @see BaseMOJMapper#select
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
     * @see BaseMOJMapper#find
     */
    protected Feature[] find(String serviceName, int selectedLayer, String findString)
        throws java.rmi.RemoteException
    {
        return _mapService.find(serviceName, selectedLayer, findString);
    }

    /**
     * @see BaseMOJMapper#getFullExtent
     */
    protected String getFullExtent(String serviceName)
        throws java.rmi.RemoteException
    {
        return _mapService.getFullMapExtent(serviceName);
    }

    /**
     * @see BaseMOJMapper#getResponse
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
     * @see BaseMOJMapper#getResponse
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
     * @see BaseMOJMapper#getResponse
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
        try {
            String imageFile = _layoutService.getLayoutImage(serviceName, width, height, extent, layerVisList, selectedLayerIndex, findString, mapTitle);
            return imageFile;
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
        String imageFile = null;
        try {
            imageFile = _layoutService.getLayoutImage(serviceName, width, height, extent, layerVisList, selectedLayerIndex, queryEnvelope, queryString, mapTitle);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return imageFile;
    }

    /**
     * get response from server with given set of parameters
     * @param serviceName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query filter
     * @param queryString the query where clause
     * @param bufferDistance buffer distance
     * @param bufferUnit the buffer unit
     * @param targetLayer the target layer for the buffer
     * @param mapTitle layout title
     * @return
     */
    public String getLayoutImage(
            String serviceName,
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
        String imageFile = null;
        try {
            imageFile = _layoutService.getLayoutImage(serviceName, width, height, extent, layerVisibility, selectedLayerIndex, queryEnvelope, queryString, bufferDistance, bufferUnit, targetLayer, mapTitle);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return imageFile;
    }
}