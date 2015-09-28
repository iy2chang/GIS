package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * This interface defines functionality of a layout service.
 */
public interface LayoutService extends EJBObject {
    /**
     * get response from server with given set of parameters
     * @param serviceName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisList the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param findString the find string
     * @param mapTitle the title text
     */
    public String getLayoutImage(
            String serviceName,
            int width, int height,
            String extent,
            boolean[] layerVisList,
            int selectedLayerIndex,
            String findString,
            String mapTitle)
            throws RemoteException;

    /**
     * get response from server with given set of parameters
     * @param serviceName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisList the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query filter
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
            throws RemoteException;

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
            String mapTitle) throws RemoteException;
}
