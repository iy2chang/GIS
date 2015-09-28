package com.esri.svr.map;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */
import java.rmi.RemoteException;
import com.esri.svr.cmn.Ring;
import com.esri.svr.cmn.Feature;
import com.esri.svr.cmn.LayerInfo;
import com.esri.svr.cmn.MapResponse;
import com.esri.svr.cmn.MapInfo;

/**
 * Mapper defines a set of base mapping functionality
 */
public interface Mapper extends java.rmi.Remote {
    /**
     * Buffer a set of selected features for the selected/active layer with given buffering distance
     * @param selectedLayerIndex the selected/active layer index
     * @param queryEnvelope the selection envelope
     * @param distance the buffering distance
     */
    public Ring[] buffer(
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double distance,
        String bufferUnit)
        throws RemoteException;

    /**
     * Get a string representation of service's full map extent
     * @return a full map extent in string
     */
    public String getFullMapExtent()
        throws RemoteException;

    /**
     * Get map extent in string representation for a given service
     * @return a map extent in string
     */
    public String getMapExtent()
        throws RemoteException;

    /**
     * Get service info for a given service
     * @return a string representation of service information
     */
    public MapInfo getMapInfo()
        throws RemoteException;

    /**
     * Select feature(s) in an active layer with given selection envelope
     * @param activeLayerIndex the active layer index
     * @param queryEnvelope the selection envelope
     * @param queryString the query where clause
     * @return a Vector of an identified feature's attributes
     */
    public Feature[] select(
        int activeLayerIndex,
        //String activeLayerIndex,
        String queryEnvelope,
        String queryString)
        throws RemoteException;


    /**
     * Get a map image
     * @param width image's width
     * @param height image's height
     * @param extent the map extent
     * @return an image file name
     */
    public String getImage(
        int width, int height,
        //String width,
        //String height,
        String extent)
        throws RemoteException;

    /**
     * get response from server with given set of parameters
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param findString the find string
     * @return a Response object representation of response from bean
     */
    public MapResponse getResponse(
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayerIndex,
        String findString)
        throws RemoteException;


    /**
     * get response from server with given set of parameters
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query filter
     * @param queryString the query where clause
     */
    public MapResponse getResponse(
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString)
        throws RemoteException;

   /**
     * get response from server with given set of parameters
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query envelope
     * @param queryString the query where clause
     * @param findString the find string
     * @param bufferDistance buffering distance in the give Buffer unit
     * @param bufferUnit the unit for the buffer distance
     * @param targetLayer the layer the buffer result will apply to do a selection
     */
    public MapResponse getResponse(
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayer)
        throws RemoteException;

    /**
     * Gets layer information by its name
     * @param layerName the layer name
     */
    public LayerInfo getLayerInfoByName(String layerName)
        throws RemoteException;

    /**
     * Get layer information by its index
     * @param layerIndex where the layer info will be returned
     * @throws RemoteException
     */
    public LayerInfo getLayerInfo(int layerIndex)
        throws RemoteException;

    /**
     * Find all the feature(s) in the selected layer containing the findString
     * @param selectedLayerIndex the selected layer
     * @param findString the find string
     * @return an array of features
     */
    public Feature[] find(
        int selectedLayerIndex,
        //String selectedLayerIndex,
        String findString)
        throws RemoteException;
}