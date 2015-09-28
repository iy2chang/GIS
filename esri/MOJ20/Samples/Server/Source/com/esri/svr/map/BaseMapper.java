package com.esri.svr.map;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import java.rmi.RemoteException;

import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.data.feat.SelectionSet;
import com.esri.mo2.map.img.ImageSupport;
import com.esri.mo2.svr.map.Map;
import com.esri.mo2.svr.map.DisplayList;

import com.esri.svr.cmn.IOStreamProviderLoader;
import com.esri.svr.cmn.IOStreamProvider;
import com.esri.svr.cmn.MapResponse;
import com.esri.svr.cmn.LayerInfo;
import com.esri.svr.cmn.MapInfo;
import com.esri.svr.cmn.Util;
import com.esri.svr.cmn.Feature;
import com.esri.svr.cmn.Ring;



/**
 * A base implementation of Mapper interface
 */
public class BaseMapper implements Mapper {

    private static boolean _debug = true;
    private String _filenamePrefix = "map_image";
    private String _outputUrl = null;
    private String _outputDir = null;

//
// protected working map object
//
    protected Map _map;
    protected String _mapName;
    protected String _imageFormat = "png";

    private IOStreamProviderLoader _providerLoader = new IOStreamProviderLoader("com.esri.svr.cmn.IOStreamProvider");
    private java.text.NumberFormat _nf = java.text.NumberFormat.getInstance();
    /**
     * Default constructor for BaseMapper.
     */
    public BaseMapper() {
        this(null);
    }

    /**
     * Constructs a BaseMapper with given map name.
     * @param mapName the BaseMapper's name
     */
    public BaseMapper(String mapName) {
        _map = new Map();
        _mapName = mapName;
        _outputDir = System.getProperty("user.dir");
    }

//
// protected methods
//

    /**
     * Clear all AcetatLayers
     */
    public void clearAcetateLayers() {
        getMap().clearAcetateLayers();
    }

    /**
     * Clear all DislayList(s)
     */
    public void clearDisplayLists() {
        getMap().getLayerDisplayLists().clear();
    }

    /**
     * Get the working Map object
     */
    public Map getMap() {
        return _map;
    }

    private LayerInfo createLayerInfo(com.esri.mo2.map.dpy.Layer layer) {
        if (_debug) System.out.println("BaseMapper:getLayerInfo() ");

        LayerInfo layerInfo = new LayerInfo();

        // layer name - 1
        layerInfo.setName(layer.getName());

        // layer type - being IMAGE or FEATURE - 2
        if (layer instanceof com.esri.mo2.map.dpy.FeatureLayer)
            layerInfo.setLayerType("feature_layer");
        else
            layerInfo.setLayerType("image_layer");

        // layer extent - 3
        layerInfo.setExtent(layer.getExtent().toString());

        // layer visibility - 4
        layerInfo.setVisible(layer.isVisible());

        // layer ID -5
        layerInfo.setId(layer.getId());

        if (layer instanceof com.esri.mo2.map.dpy.FeatureLayer) {
            com.esri.mo2.map.dpy.FeatureLayer flayer = (com.esri.mo2.map.dpy.FeatureLayer)layer;

            // field names - 6
            String[] names = flayer.getFeatureClass().getFields().getNames();
            layerInfo.setFieldNames(names);

            // field types - 7
            com.esri.mo2.data.feat.Field fields[] = flayer.getFeatureClass().getFields().getFields();
            String[] fieldTypes = new String[fields.length];
            for (int i=0; i<fields.length; i++) {
                fieldTypes[i] = String.valueOf(fields[i].getType());
            }
            layerInfo.setFieldTypes(fieldTypes);

            // field precision - 8
            String fieldPrecs[] = new String[fields.length];
            for (int i=0; i<fields.length; i++)
                fieldPrecs[i] = _nf.format(fields[i].getPrecision());
            layerInfo.setFieldPrecisions(fieldPrecs);

            // field display size - 9
            String fieldSizes[] = new String[fields.length];
            for (int i=0; i<fields.length; i++)
                fieldSizes[i] = String.valueOf(fields[i].getDisplaySize());
            layerInfo.setFieldDisplaySizes(fieldSizes);

            // layer feature type - being POINT/LINE/POLYGON  -10
            layerInfo.setLayerFeatureType(flayer.getFeatureClass().getFeatureType());
        }

        return layerInfo;
    }
//
// public methods
//

    /**
     * Get service name
     * @return service name
     */
    public String getMapName() {
        return _mapName;
    }

    /**
     * Set service name
     * @param serviceName the service name
     */
    public void setMapName(String serviceName) {
        _mapName = serviceName;
    }

    /**
     * get an image with given parameters
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     */
    public String getImage(int width, int height, String extent)
        throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper: getImage() w=" + width + " h=" + height + " extent=" + extent);
        try {
            String imagefile = exportImage(getMap().getImage(new java.awt.Dimension(width, height), extent), _imageFormat);
            if (_debug) System.out.println("BaseMapper: getImage() image file=" + imagefile);
            return imagefile;
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * get response from server with given set of parameters
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param findString the find string
     */
    public MapResponse getResponse(
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayerIndex,
        String findString)
        throws RemoteException
    {
        if (_debug) System.out.println("%%% 1 %%% BaseMapper: getResponse() w=" + width + " h=" + height + " extent=" + extent);

        try {
            // set visibility
            java.util.HashMap displayLists = new java.util.HashMap();

            int totalLayerCount = getMap().getLayerCount();
            for (int i=0; i<totalLayerCount; i++) {
                DisplayList dl = new DisplayList();
                dl.setVisible(layerVisibility[i]);
                String name = getMap().getLayer(i).getName();
                displayLists.put(name, dl);
            }
            if (_debug) System.out.println("DisplayLists=" + getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            // do find
            int index = selectedLayerIndex;
            if ((findString != null && !findString.equals(""))) {
                SelectionSet ss = findFeatures(index, findString);
                if (ss != null && ss.size()>0) {
                    if (_debug) System.out.println("BaseMapper: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(index).getName());
                    dl.setSelectionSet(ss);
                }
            }

            // draw image
            java.awt.image.BufferedImage image = getBufferedImage(displayLists, new java.awt.Dimension(width, height), extent, null);
            String imagefile = exportImage(image, _imageFormat);
            if (_debug) System.out.println("BaseMapper: getResponse() image file=" + imagefile + " map extent=" + getMap().getMapExtent());

            return createResponse(imagefile, selectedLayerIndex, displayLists);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

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
        throws RemoteException
    {
        if (_debug) System.out.println("%%% 2 %%% BaseMapper: getResponse() w=" + width + " h=" + height + " extent=" + extent);

        try {
            // set visibility
            java.util.HashMap displayLists = new java.util.HashMap();
            int totalLayerCount = getMap().getLayerCount();
            for (int i=0; i<totalLayerCount; i++) {
                DisplayList dl = new DisplayList();
                dl.setVisible(layerVisibility[i]);
                String name = getMap().getLayer(i).getName();
                displayLists.put(name, dl);
            }
            if (_debug) System.out.println("DisplayLists=" + getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            // do query
            int index = selectedLayerIndex;
            if ((queryEnvelope != null && !queryEnvelope.equals("")) || (queryString!=null && !queryString.trim().equals(""))) {
                if (_debug) System.out.println(" query envelope=>" + queryEnvelope + " query string" + queryString);
                SelectionSet ss = queryMap(index, queryEnvelope, queryString);
                if (ss != null && ss.size()>0) {
                    if (_debug) System.out.println("BaseMapper: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(index).getName());
                    dl.setSelectionSet(ss);
                }
            }
            if (_debug) System.out.println("BaseMapper: getResponse() queryEnvelope=" + queryEnvelope);

            // draw image
            String imagefile = exportImage(getBufferedImage(displayLists, new java.awt.Dimension(width, height), extent, null), _imageFormat);
            if (_debug) System.out.println("BaseMapper: getResponse() image file=" + imagefile + " map extent=" + getMap().getMapExtent());

            return createResponse(imagefile, selectedLayerIndex, displayLists);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }


    /**
     * get response from server with given set of parameters
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query envelope
     * @param findString the find string
     * @param bufferDistance buffering distance in the give Buffer unit
     * @param bufferUnit the unit for the buffer distance
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
        String bufferUnit)
        throws RemoteException
    {
        return getResponse(width, height, extent, layerVisibility, selectedLayerIndex, queryEnvelope, queryString, findString, bufferDistance, bufferUnit, -1);
    }

    /**
     *
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
        throws RemoteException
    {
        if (_debug) System.out.println("%%% 3 %%% BaseMapper: getResponse() w=" + width + " h=" + height + " extent=" + extent);

        try {
            // set display lists
            java.util.HashMap displayLists = new java.util.HashMap();
            int totalLayerCount = getMap().getLayerCount();
            for (int i=0; i<totalLayerCount; i++) {
                DisplayList dl = new DisplayList();
                dl.setVisible(layerVisibility[i]);
                String name = getMap().getLayer(i).getName();
                getMap().addLayerDisplayList(name, dl);
                displayLists.put(name, dl);
            }
            if (_debug) System.out.println("DisplayLists=" + getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            SelectionSet ss = null;

            // do query
            int index = selectedLayerIndex;
            if ((queryEnvelope != null && !queryEnvelope.trim().equals("")) ||
                (queryString!=null && !queryString.trim().equals("")))
            {
                if (_debug) System.out.println("BaseMapper: getResponse()query envelope=" + queryEnvelope + " query string=>" + queryString );
                ss = queryMap(index, queryEnvelope, queryString);
                if (_debug) System.out.println("BaseMapper: getResponse() select set=" + ss.size());
            } else if (findString!=null && !findString.trim().equals("")) {
                ss = findFeatures(index, findString);
            }
            if (ss != null && ss.size()>0) {
                DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(index).getName());
                dl.setSelectionSet(ss);
            }
            if (_debug) System.out.println("BaseMapper: getResponse() queryEnvelope=" + queryEnvelope);

            // do buffer
            com.esri.mo2.cs.geom.Polygon bufPolygon = null;
            if (bufferDistance > 0d && ss != null && ss.size()>0 )
            {
                int bufUnit = getBufferUnit(bufferUnit);
                bufPolygon = getMap().buffer(bufferDistance, bufUnit, index, ss);
            }
            if (_debug) System.out.println("Get buffer polygon ? " + (bufPolygon != null));

            com.esri.mo2.svr.map.AcetateLayer acetateLayer = null;
            if (bufPolygon != null) {
                //
                // add buffer polygon to the map as acetatelayer
                //
                com.esri.mo2.map.dpy.BaseGraphicsLayer graphicsLayer = new com.esri.mo2.map.dpy.BaseGraphicsLayer();
                graphicsLayer.setVisible(true);
                acetateLayer = new com.esri.mo2.svr.map.AcetateLayer(graphicsLayer);

                // construct a FeatureElement and show it in the AcetateLayer
                com.esri.mo2.map.draw.Renderer renderer = com.esri.mo2.map.draw.Util.constructFillRenderer(
                    java.awt.Color.darkGray,
                    com.esri.mo2.map.draw.AoFillStyle.LIGHT_GRAY_FILL,
                    java.awt.Color.blue);
                com.esri.mo2.map.dpy.Element featureElement =
                    Util.createFeatureElement(
                        bufPolygon, renderer, renderer);

                graphicsLayer.addElement(featureElement);

                // do query on target layer using buffer polygon
                int targetLayerIndex = targetLayer;
                if (targetLayerIndex >= 0) {
                    ss = getMap().select(targetLayerIndex, bufPolygon);
                    if (ss != null && ss.size()>0) {
                        DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(targetLayerIndex).getName());
                        dl.setSelectionSet(ss);
                        com.esri.mo2.map.draw.Renderer selrenderer =
                            com.esri.mo2.map.draw.Util.constructSelectionRenderer(
                                getMap().getLayer(targetLayerIndex).getLayerInfo().getType(),
                                new java.awt.Color(150, 50, 50)
                            );
                        dl.setSelectionRenderer(selrenderer);
                    }
                }
            }
            if (_debug) System.out.println("BaseMapper.getResponse() starting generating image ...");

            // draw image
            java.awt.image.BufferedImage image = getBufferedImage(displayLists, new java.awt.Dimension(width, height), extent, acetateLayer);
            if (_debug) System.out.println("BaseMapper.getResponse() starting exporting image ...");
            String imagefile = exportImage(image, _imageFormat);
            if (_debug) System.out.println("BaseMapper.getResponse() image file=" + imagefile + " map extent=" + getMap().getMapExtent());

            return createResponse(imagefile, selectedLayerIndex, displayLists);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

    private java.awt.image.BufferedImage getBufferedImage(
            java.util.HashMap displayLists,
            java.awt.Dimension imageSize,
            String extent,
            com.esri.mo2.svr.map.AcetateLayer acetateLayer)
            throws java.lang.Exception
    {
        // it's very important to synchronize the addLayerDisplayList and getImage process
        // otherwise during the getImage process, the layer DisplayList could be set
        // by other thread. So usually, you want to set each layer's DisplayList just
        // before you call getImage method
        synchronized (getMap()) {
            this.clearAcetateLayers();
            this.clearDisplayLists();
            if (acetateLayer!=null) getMap().addAcetateLayer(acetateLayer);
            for (int i=0, n=getMap().getLayerCount(); i<n; i++) {
                DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(i).getName());
                if (_debug) System.out.println("@@@ BaseMapper.getBufferedImage() " + dl.isVisible());
                getMap().addLayerDisplayList(getMap().getLayer(i).getName(), dl);
            }
            return getMap().getImage(imageSize, extent);
        }
    }

    private MapResponse createResponse(String imagefile, int selectedLayerIndex, java.util.HashMap displayLists) {
        MapResponse response = new MapResponse();
        response.setImageFile(imagefile);
        response.setMapExtent(getMap().getMapExtent());
        response.setLayerCount(getMap().getLayerCount());
        response.setSelectedLayerIndex(selectedLayerIndex);
        String[] layernames = new String[getMap().getLayerCount()];
        boolean[] layervisibles = new boolean[getMap().getLayerCount()];
        for (int i=0; i<getMap().getLayerCount(); i++) {
            String name = getMap().getLayer(i).getName();
            DisplayList dlist = (DisplayList)displayLists.get(name);
            layernames[i] = name;
            layervisibles[i] = dlist.isVisible();
        }
        response.setLayerNames(layernames);
        response.setLayerVisibilities(layervisibles);

        return response;
    }

    private SelectionSet queryMap(int activeLayerIndex, String queryEnvelope, String whereClause)
        throws Exception
    {
        Envelope extent = null;
        if (queryEnvelope != null) {
            extent = com.esri.mo2.cs.geom.Global.parseEnvelope(extent, queryEnvelope);
        }

        SelectionSet selset = getMap().select(activeLayerIndex, extent, whereClause);
        return selset;
    }

    /**
     *  get full map extent for the given service
     */
    public String getFullMapExtent()
        throws RemoteException
      {
        String fullextent = getMap().getFullMapExtent();
        if (_debug) System.out.println("BaseMapper:getFullMapExtent() " + fullextent);
        return fullextent;
    }

    /**
     * get map extent for the given service
     */
    public String getMapExtent()
        throws RemoteException
    {
        String extent = getMap().getMapExtent();
        if (_debug) System.out.println("BaseMapper:getExtent() " + extent);
        return extent;
    }

    /**
     * get service information for the given service
     */
    public MapInfo getMapInfo()
           throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper:getMapInfo()");
        if (_debug) System.out.println("BaseMapper:getMapInfo() # of layers=" + getMap().getLayerCount() );

        MapInfo mapinfo = new MapInfo();
        mapinfo.setLayerCount(getMap().getLayerCount());
        mapinfo.setMapExtent(getMap().getFullMapExtent());
        String[] names = new String[getMap().getLayerCount()];
        boolean[] visibility = new boolean[getMap().getLayerCount()];
        for (int i=0; i<getMap().getLayerCount(); i++) {
            names[i] = getMap().getLayer(i).getName();
            visibility[i] = getMap().getLayer(i).isVisible();
        }
        mapinfo.setLayerVisibility(visibility);
        mapinfo.setLayerNames(names);
        return mapinfo;
    }

    /**
     * Select feature(s)
     * @param activeLayerIndex the selected/active layer index within the service
     * @param queryEnvelope the selection envelope
     */
    public Feature[] select(int activeLayerIndex, String queryEnvelope)
           throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper:select(x,x) + layer=" + activeLayerIndex + " " + queryEnvelope);

        try {
            Envelope extent = null;
            if (queryEnvelope != null && !queryEnvelope.trim().equals(""))
                extent = com.esri.mo2.cs.geom.Global.parseEnvelope(extent, queryEnvelope);

            Feature[] features = select(extent, "", activeLayerIndex );
            //if (_debug) System.out.println("BaseMapper:select(x,x) before return # of features=" + features.length);
            return features;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Select feature(s)
     * @param activeLayerIndex the selected/active layer index within the service
     * @param queryEnvelope the selection envelope
     * @param queryString the query where clause
     */
    public Feature[] select(
        int activeLayerIndex,
        String queryEnvelope,
        String queryString)
           throws RemoteException
    {

        if (_debug) System.out.println("BaseMapper:select(x,x,x) + layer=" + activeLayerIndex + " " + queryEnvelope + " " + queryString);

        try {
            Envelope extent = null;
            if (queryEnvelope != null && !queryEnvelope.trim().equals(""))
                extent = com.esri.mo2.cs.geom.Global.parseEnvelope(extent, queryEnvelope);

            if (_debug) System.out.println("BaseMapper:select(x,x,x) before return # of extent=" + extent);

            Feature[] features = select(extent, queryString, activeLayerIndex);
            //if (_debug) System.out.println("BaseMapper:select(x,x,x) before return # of features=" + features.length);

            return features;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Select feature(s) in the target layer based on the buffer polygon
     * created by buffering the selected feature(s) of the given layer
     * using the given selection envelope
     * @param selectedLayerIndex the selected/active layer
     * @param queryEnvelope the spatial query filter
     * @param queryString the query where clause
     * @param bufferDistance buffering distance in the give Buffer unit
     * @param bufferUnit the unit for the buffer distance
     * @param targetLayerIndex
     * @return an array of selected features
     */
    public Feature[] select(
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayerIndex)
        throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper: select() find string=" + findString + " query string=" + queryString + " query envelope=" + queryEnvelope);

        try {
            com.esri.mo2.cs.geom.Polygon polygon =
                createBuffer(
                    selectedLayerIndex,
                    queryEnvelope,
                    queryString,
                    findString,
                    bufferDistance,
                    bufferUnit);

            if (_debug) System.out.println("BaseMapper: select() find string=" + findString + " polygon buffer=>" + polygon.getEnvelope());
            return select(polygon, null, targetLayerIndex);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Buffer a set of selected features for the selected/active layer with given buffering distance
     * @param selectedLayerIndex the selected/active layer index
     * @param queryEnvelope the selection envelope
     * @param queryString the query where clause
     * @param findString the find string
     * @param bufferDistance buffering distance in the give Buffer unit
     * @param bufferUnit the unit for the buffer distance
     */
    public Ring[] buffer(
        int selectedLayerIndex,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit)
        throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper: buffer() find string=" + findString + " query string=" + queryString + " query envelope=" + queryEnvelope);
        com.esri.mo2.cs.geom.Polygon polygon =
            createBuffer(
                selectedLayerIndex,
                queryEnvelope, queryString, findString,
                bufferDistance,
                bufferUnit);

        Ring[] ringArray = null;
        com.esri.mo2.cs.geom.Ring[] rings = polygon.getRings();
        if (rings.length > 0) {
            ringArray = new Ring[rings.length];
            for (int i=0; i<rings.length; i++) {
                Ring ring = new Ring();
                ring.setInterior(rings[i].isInterior());
                ring.setExteriorRingIndex(getRingIndex(i, rings[i], rings));
                getRingPoints(rings[i], ring);
                ringArray[i] = ring;
            }
        }
        return ringArray;
    }

    private int getRingIndex(int ringIndex, com.esri.mo2.cs.geom.Ring ring, com.esri.mo2.cs.geom.Ring[] rings) {
        int index = -1;
        for (int i=0; i<rings.length; i++) {
            if (ring == rings[i] && i != ringIndex) index = i;
        }

        return index;
    }

    private void getRingPoints(com.esri.mo2.cs.geom.Ring ring, Ring r) {
        com.esri.mo2.cs.geom.Point pcs[] = ring.getPoints().getPoints();
        double[] x = new double[pcs.length];
        double[] y = new double[pcs.length];
        for (int i=0; i<pcs.length; i++)  {
            x[i] = pcs[i].getX();
            y[i] = pcs[i].getY();
        }
        r.setX(x);
        r.setY(y);
    }

    /**
     * Buffer a set of selected features for the selected/active layer with given buffering distance
     * @param selectedLayerIndex the selected/active layer index
     * @param queryEnvelope the selection envelope
     * @param queryString the query where clause
     * @param findString the find string
     * @param bufferDistance buffering distance in the give Buffer unit
     * @param bufferUnit the unit for the buffer distance
     */
    protected com.esri.mo2.cs.geom.Polygon createBuffer(
            int selectedLayerIndex,
            String queryEnvelope,
            String queryString,
            String findString,
            double bufferDistance,
            String bufferUnit)
        throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper: createBuffer() find string=" + findString + " query string=" + queryString + " query envelope=" + queryEnvelope);

        try {
            com.esri.mo2.data.feat.SelectionSet selset = null;
            boolean validFindString = (findString!=null && !findString.trim().equals(""));
            if (validFindString) {
                selset = findFeatures(selectedLayerIndex, findString);
            } else {
                Envelope extent = null;
                if (queryEnvelope != null && !queryEnvelope.trim().equals(""))
                    extent = com.esri.mo2.cs.geom.Global.parseEnvelope(extent, queryEnvelope);

                selset = getMap().select(selectedLayerIndex, extent, queryString);
            }

            if (_debug) System.out.println("BaseMapper: buffer() find string=" + findString);
            if (selset != null) if (_debug) System.out.println("BaseMapper: buffer() select set=>" + selset.size());

            return getMap().buffer(bufferDistance, getBufferUnit(bufferUnit), selectedLayerIndex, selset);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Gets layer information by its name
     * @param layerName the layer name
     */
    public LayerInfo getLayerInfoByName(String layerName)
        throws RemoteException
    {
        try {
            return createLayerInfo(getMap().getLayer(layerName));
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Find all the feature(s) in the selected layer containing the findString
     * @param selectedLayerIndex the selected layer
     * @param findString the find string
     * @return an array of features
     */
    public Feature[] find(
        int selectedLayerIndex,
        String findString)
        throws RemoteException
    {
        try {
            return getResults(findFeatures(selectedLayerIndex, findString), selectedLayerIndex);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Find features
     * @param selectedLayerIndex the selected layer index
     * @param findString the find string
     * @throws RemoteException
     */
    public com.esri.mo2.data.feat.SelectionSet findFeatures(
            int selectedLayerIndex,
            String findString)
            throws RemoteException
    {
        try {
            com.esri.mo2.data.feat.SelectionSet selset = null;
            boolean validFindValue = (findString!=null && !findString.trim().equals(""));
            if (getMap().getLayer(selectedLayerIndex) instanceof com.esri.mo2.map.dpy.FeatureLayer && validFindValue) {
                selset = findingFeatures(selectedLayerIndex, findString);
            }
            if (_debug) System.out.println("BaseMapper:findFeatures() find string=" + findString + " " + (selset==null));

            return selset;
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Get layer information by its index
     * @param layerIndex where the layer info will be returned
     * @throws RemoteException
     */
    public LayerInfo getLayerInfo(int layerIndex)
        throws RemoteException
    {
        if (_debug) System.out.println("BaseMapper:getLayerInfo() layer index=" + layerIndex);
        try {
            return createLayerInfo(getMap().getLayer(layerIndex));
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

//
// private methods
//

    private int getBufferUnit(String bufferUnit) {
        if (bufferUnit==null)
            return getMap().getMapUnit();
        else
            return com.esri.mo2.util.Units.stringUnitToInt(bufferUnit.toLowerCase());
    }

    private Feature[] select(com.esri.mo2.cs.geom.FeatureGeometry selectEnvelope, String whereClause, int layerindex)
        throws Exception
    {
        com.esri.mo2.data.feat.SelectionSet selset = getMap().select(layerindex, selectEnvelope, whereClause);

        if (_debug)
            if (selset != null)
                System.out.println("BaseMapper.select() # of selection=" + selset.size());
            else
                System.out.println("BaseMapper.select() # of selection set is null!");
        return getResults(selset, layerindex);
    }

    private Feature[] getResults(
        com.esri.mo2.data.feat.SelectionSet selset,
        int layerindex)
    {
        Feature features[] = null;

        if (selset != null && selset.size()>0) {
            features = new Feature[selset.size()];
            String fieldnames[] = null;
            com.esri.mo2.map.dpy.FeatureLayer layer = (com.esri.mo2.map.dpy.FeatureLayer)getMap().getLayer(layerindex);
            int ftype = layer.getFeatureClass().getFeatureType();
            fieldnames = layer.getFeatureClass().getFields().getNames();

            int count=0;
            java.util.Iterator it = selset.iterator();
            while (it.hasNext()) {
                //if (_debug) System.out.println("0 BaseMapper.getResults() count=>" + count);

                com.esri.mo2.data.feat.Feature feature=null;
                java.lang.Object id = it.next();
                feature = (com.esri.mo2.data.feat.Feature)layer.getSelectableDataset().getData((java.lang.Comparable)id);
                if (feature == null) continue;

                Feature row = new Feature();
                com.esri.mo2.data.feat.Fields fields = feature.getFields();
                //StringBuffer fieldValues = new StringBuffer();
                String[] fieldValues = new String[fields.size()];

                for (int i=0; i<fields.size(); i++)
                {
                    if (fields.getField(i).equals(feature.getShapeField())) {
                        String fEle = "[Point]";
                        if (ftype == com.esri.mo2.data.feat.FeatureClass.LINE)
                            fEle = "[Line]";
                        else if  (ftype == com.esri.mo2.data.feat.FeatureClass.POLYGON)
                            fEle = "[Polygon]";

                        row.setType(fEle);
                        row.setGeometry(getGeometry(feature.getValue(i), ftype));
                        fieldValues[i] = "[geometry]";
                    } else {
                        fieldValues[i] = feature.getDisplayValue(i);
                    }
                }
                //if (_debug) System.out.println("BaseMapper.getResults() count=>" + count);
                //if (_debug) System.out.println("BaseMapper.getResults() values=>" + fieldValues);

                //row.setFieldValues(fieldValues.toString());
                row.setFieldValues(fieldValues);
                //if (_debug) System.out.println("6 BaseMapper.getResults() count=>" + count);

                //row.setFieldNames(fieldnamesBuf.toString());
                row.setFieldNames(fieldnames);
                //if (_debug) System.out.println("7 BaseMapper.getResults() count=>" + count);

                row.setLayerName(layer.getName());
                //if (_debug) System.out.println("8 BaseMapper.getResults() count=>" + count);

                features[count++]=row;

                //if (_debug) System.out.println("BaseMapper.getResults() count=>" + count);
            }
            if (_debug) System.out.println("total count=>" + count);
            if (_debug) System.out.println("BaseMapper: getResults() + selection set=" + selset.size() + " active layer index=" +  layerindex);
        }

        return features;
    }

    private String getGeometry(Object feature, int ftype) {
        StringBuffer geometry = new StringBuffer();
        //if (_debug) System.out.println("BaseMapper: getGeometry() feature type=" + ftype);

        switch (ftype) {
            case com.esri.mo2.data.feat.FeatureClass.POINT:
                com.esri.mo2.cs.geom.Point pt = (com.esri.mo2.cs.geom.Point)feature;
                com.esri.mo2.cs.geom.PointCollection pc = pt.getPoints();
                for (int i=0; i<pc.size(); i++)
                    geometry.append(pc.getPoint(i).getX() + "," + pc.getPoint(i).getY() + ",");
                break;

            case com.esri.mo2.data.feat.FeatureClass.LINE:
                com.esri.mo2.cs.geom.Polyline pl = (com.esri.mo2.cs.geom.Polyline)feature;
                com.esri.mo2.cs.geom.Path[] paths = pl.getPaths();
                for (int i=0; i<paths.length; i++) {
                    com.esri.mo2.cs.geom.PointCollection ppc = paths[i].getPoints();
                    geometry.append("[path]," + ppc.size() + ",");
                    for (int j=0; j<ppc.size(); j++)
                        geometry.append(ppc.getPoint(j).getX() + "," + ppc.getPoint(j).getY()+",");
                }
                break;

            case com.esri.mo2.data.feat.FeatureClass.POLYGON:
                com.esri.mo2.cs.geom.Polygon poly = (com.esri.mo2.cs.geom.Polygon)feature;
                com.esri.mo2.cs.geom.Ring[] rings = poly.getRings();
                for (int i=0; i<rings.length; i++) {
                    com.esri.mo2.cs.geom.PointCollection pcc = rings[i].getPoints();
                    geometry.append("[ring]," + pcc.size() + ",");
                    for (int j=0; j<pcc.size(); j++)
                        geometry.append(pcc.getPoint(j).getX() + "," + pcc.getPoint(j).getY() + ",");
                }
        }

        return geometry.toString();
    }

    private com.esri.mo2.data.feat.SelectionSet findingFeatures(int layerindex, String theFindValue) {
        com.esri.mo2.map.dpy.FeatureLayer flayer = (com.esri.mo2.map.dpy.FeatureLayer)getMap().getLayer(layerindex);
        com.esri.mo2.data.feat.Fields fields = flayer.getFeatureClass().getFields();
        com.esri.mo2.data.feat.SelectionSet selset = null;
        for (int i=0; i<fields.getFields().length; i++) {
            if (_debug) System.out.println("i=" + i + " name=" + fields.getField(i).getName() + " string type=" + fields.getField(i).isString());
            if (!fields.getField(i).isString()) continue;

            StringBuffer whereClause = new StringBuffer();
            whereClause.append(fields.getField(i).getName());
            whereClause.append(" like ");
            whereClause.append("'%");
            if (theFindValue.startsWith("'") && theFindValue.endsWith("'")) {
                whereClause.append(theFindValue.substring(1, theFindValue.length()-1));
            } else {
                whereClause.append(theFindValue);
            }
            whereClause.append("%'");
            com.esri.mo2.data.feat.SelectionSet ss  = getMap().select(layerindex, whereClause.toString());

            if (selset == null) selset = ss;
            else selset.add(ss);
        }

        return selset;
    }

    private String exportImage(java.awt.image.BufferedImage image, String imageFormat)
        throws java.io.IOException
    {
        if (_debug) System.out.println("BaseMapper.exportImage( 0 ) format=" + imageFormat);

        com.esri.mo2.map.img.ImageWriter iw = ImageSupport.createWriterByType(imageFormat);

        if (iw == null)
            throw new java.io.IOException("No image writer for the specified image format '" + imageFormat + "'");

        if (_debug) System.out.println("BaseMapper.exportImage( 1 ) format=" + imageFormat);

        String filename = getFileName();

        if (_debug) System.out.println("BaseMapper.exportImage( 2 ) format=" + filename);

        IOStreamProvider provider = null;
        if (_outputUrl != null)
            provider = _providerLoader.getIOStreamProvider("com.esri.svr.plgin.JcifsIOStreamProvider");
        else
            provider = _providerLoader.getIOStreamProvider("com.esri.svr.plgin.FileIOStreamProvider");

        if (provider==null)
            throw new java.io.IOException("No IO stream is found");

        if (_debug) System.out.println("BaseMapper.exportImage( 3 ) starting writing to output stream image w=" + image.getWidth() +  " h=" + image.getHeight());
        if (_debug) System.out.println("BaseMapper.exportImage( 3 ) starting writing to output stream image w=" + image.getWidth() +  " h=" + image.getHeight() + " " + provider.getClass().getName());

        java.io.OutputStream os = provider.getOutputStream(filename);
        iw.exportImage(os, image);
        os.close();

        if (_debug) System.out.println("BaseMapper.exportImage( 4 ) end writing to output stream");

        return filename;
    }

    private String getFileName()
        throws java.io.IOException
    {
        int number = (int)(Math.random() * 100000d);
        StringBuffer filename = new StringBuffer();

        if (_outputUrl != null && !_outputUrl.trim().equals("") && _outputUrl.indexOf("://")>0) {
            filename.append(_outputUrl);
            if (!_outputUrl.endsWith("/")) filename.append("/");
        } else if (_outputDir!=null && !_outputDir.trim().equals("")) {
            filename.append(_outputDir);
            if (!_outputDir.endsWith(java.io.File.separator)) filename.append(java.io.File.separator);
        } else {
            throw new java.io.IOException("No vallid output directory or url. ");
        }

        if (_debug) System.out.println("BaseMapper.getFileName(1) filename=" + filename);

        filename.append(_filenamePrefix);
        filename.append(number + ".");
        filename.append(_imageFormat);

        if (_debug) System.out.println("BaseMapper.getFileName(2) filename=" + filename);

        return filename.toString();
    }

    public void setOutputDir(String outputDir) {
        if (outputDir != null) _outputDir = outputDir;
    }

    public String getOutputDir() {
        return _outputDir;
    }

    public void setOutputUrl(String outputUrl) {
        if (outputUrl != null && !outputUrl.trim().equals("")) _outputUrl = outputUrl;
    }

    public String getOutputUrl() {
        return _outputUrl;
    }

    public void removeLayers() {
        while (getMap().getLayerCount()>0)
            getMap().getLayerset().removeLayerAt(0);
    }
}






