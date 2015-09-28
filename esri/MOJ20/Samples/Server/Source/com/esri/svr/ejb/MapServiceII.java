package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI </p>
 * @version 2.0
 */

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

import com.esri.svr.cmn.Ring;
import com.esri.svr.cmn.Feature;
import com.esri.svr.cmn.LayerInfo;
import com.esri.svr.cmn.MapInfo;
import com.esri.svr.cmn.MapResponse;

/**
 * This interface defines functionality of a map service.
 */
public interface MapServiceII extends EJBObject {
    /**
     * Get a string representation of service's full map extent
     * @param serviceName the service name
     * @return a full map extent in string
     */
    public String getFullMapExtent(String serviceName)
        throws RemoteException;

    /**
     * Get map extent in string representation for a given service
     * @param serviceName the service name
     * @return a map extent in string
     */
    public String getMapExtent(String serviceName)
        throws RemoteException;

    /**
     * Get service info for a given service
     * @param serviceName the service name
     * @return a string representation of service information
     */
    public String getServiceInfo(String serviceName)
        throws RemoteException;

    /**
     * Select feature(s) in an active layer with given selection envelope
     * @param serviceName the service name
     * @param activeLayerIndex the active layer index
     * @param queryEnvelope the selection envelope
     * @param queryString the query where clause
     * @return a Vector of an identified feature's attributes
     */
    public Feature[] select(
            String serviceName,
            int activeLayerIndex,
            String queryEnvelope,
            String queryString)
        throws RemoteException;

    /**
     * Select feature(s) in the target layer based on the buffer polygon
     * created by buffering the selected feature(s) of the given layer
     * using the given selection envelope
     * @param serviceName the service name
     * @param selectedLayerIndex  the active/selected layer index
     * @param queryEnvelope the query envelope
     * @param queryString  the query string (where clause)
     * @param findString the find string
     * @param bufferDistance  the buffer distance
     * @param bufferUnit the buffer unit
     * @param targetLayerIndex the target layer index
     * @return a group of features
     * @throws RemoteException
     */
    public Feature[] select(
        String serviceName,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayerIndex)
        throws RemoteException;


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
        throws RemoteException;

    /**
     * Get a map image
     * @param serviceName the service name
     * @param width image's width
     * @param height image's height
     * @param extent the map extent
     * @return an image file name
     */
    public String getImage(String serviceName, int width, int height, String extent)
        throws RemoteException;

    /**
     * Get a response from bean with given parameters
     * @param serviceName the service name
     * @param width image width
     * @param height image height
     * @param extent map extent
     * @param layerVisList visible layer list
     * @param selectedLayerIndex active layer index
     * @param findString the find string
     * @return a MapResponse object
     */
    public MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayerIndex,
        String findString)
        throws RemoteException;


    /**
     * Get a response from bean with given parameters
     * @param serviceName the service name
     * @param width image width
     * @param height image height
     * @param extent map extent
     * @param layerVisList visible layer list
     * @param selectedLayerIndex active layer index
     * @param queryEnvelope query envelope
     * @param queryString the query string (where clause)
     * @return a MapResponse object
     */
    public MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisList,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString)
        throws RemoteException;

    /**
     *
     * @param serviceName the service name
     * @param selectedLayerIndex  the active/selected layer index
     * @param queryEnvelope the query envelope
     * @param queryString  the query string (where clause)
     * @param findString the find string
     * @param bufferDistance  the buffer distance
     * @param bufferUnit the buffer unit
     * @param targetLayerIndex the target layer index
     * @return a group of features
     * @throws RemoteException
     * @return a MapResponse object
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
        throws RemoteException;

    /**
     * Add layers to the MapServiceII
     * @param serviceName the new service name where layers come from
     */
    public void addLayers(String serviceName)
        throws RemoteException;

        /**
     *
     * @param serviceName
     * @param layerName
     * @return
     * @throws RemoteException
     */
    public LayerInfo getLayerInfo(String serviceName, String layerName)
        throws RemoteException;

    /**
     * Get service info for a given service
     * @return a string representation of service information
     */
    public MapInfo getMapInfo(String serviceName)
        throws RemoteException;

    /**
     *
     * @param serviceName
     * @param layerIndex
     * @return
     * @throws RemoteException
     */
    public LayerInfo getLayerInfo(String serviceName, int layerIndex)
        throws RemoteException;

        /**
     * Find all the feature(s) in the selected layer containing the findString
     * @param serviceName the service name
     * @param selectedLayerIndex the selected layer
     * @param findString
     * @return a Vector of found feature(s)
     */
    public Feature[] find(String serviceName, int selectedLayerIndex, String findString)
        throws RemoteException;
}