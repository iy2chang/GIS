package com.esri.svr.map;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import javax.servlet.ServletContext;
import com.esri.svr.map.BaseMapper;
import com.esri.svr.cat.ServiceCatalogRegistry;
import com.esri.svr.cat.ServiceCatalog;

import com.esri.mo2.data.feat.SelectionSet;
import com.esri.mo2.svr.map.DisplayList;

import com.esri.svr.cmn.*;

/**
 * The ServletMapper provides functionality as a map server that runs
 * within the servlet container. It uses the functions provided by BaseMapper
 * class.
 */
public class ServletMapper extends BaseMOJMapper {
    private BaseMapper _mapper;
    private int deltaX=0, deltaY=0;

    /**
     * Constructs a ServletMapper object with given properties, servlet context and BaseMapper object.
     * @param properties properties of JSP page names
     * @param servletContext the calling servlet context
     * @param mapServer the BaseMapper object
     * @param mapName map name
     */
    public ServletMapper (
        java.util.HashMap properties,
        ServletContext servletContext,
        BaseMapper mapServer,
        String mapName)
    {
        _properties = properties;
        _servletContext = servletContext;
        _mapper = mapServer;

        if (_mapper != null && mapName!=null && mapName.length()>1) {
            _mapper.setMapName(mapName);
            while (_mapper.getMap().getLayerCount()>0) _mapper.getMap().getLayerset().removeLayerAt(0);
            ServiceCatalogRegistry.addLayersToMap(mapName, _mapper.getMap());

            _mapper.setOutputDir(ServiceCatalogRegistry.getServiceCatalog(mapName).getOutputDir());

            System.out.println("ServeltMapper service name='" + mapName + "'");
            System.out.println("ServeltMapper " + ServiceCatalogRegistry.getServiceCatalog(mapName));
            System.out.println("ServeltMapper " + ServiceCatalogRegistry.getServiceCatalog(mapName).getOutputDir());
            System.out.println("ServeltMapper " + _mapper.getOutputDir());
        }
    }

//
// protected abstract methods
//

    /**
     * @see BaseMOJMapper#getAllServices
     */
    protected ServiceInfo[] getAllServices() {
        return ServiceCatalogRegistry.getAllServices();
    }

    /**
     * @see BaseMOJMapper#getLayerInfo
     */
    protected LayerInfo getLayerInfo(String mapName, int selectedLayer)
        throws java.rmi.RemoteException
    {
        return _mapper.getLayerInfo(selectedLayer);
    }

    /**
     * @see BaseMOJMapper#select
     */
    protected Feature[] select(String mapName, int selectedLayer, String extent, String queryString)
        throws java.rmi.RemoteException
    {
        System.out.println("ServletMapper.select() " + extent + " " + selectedLayer + " " + queryString);
        return _mapper.select(selectedLayer, extent, queryString);
    }

    /**
     * @see BaseMOJMapper#select
     */
    protected Feature[] select(
        String mapName, int selectedLayer, String extent, String queryString, String findString,
        double bufferDistance, String bufferUnit, int targetLayer)
        throws java.rmi.RemoteException
    {
        return _mapper.select(selectedLayer, extent, queryString, findString,
                        bufferDistance, bufferUnit, targetLayer);
    }

    /**
     * @see BaseMOJMapper#find
     */
    protected Feature[] find(String mapName, int selectedLayer, String findString)
        throws java.rmi.RemoteException
    {
        return _mapper.find(selectedLayer, findString);
    }

    /**
     * @see BaseMOJMapper#getFullExtent
     */
    protected String getFullExtent(String mapName)
        throws java.rmi.RemoteException
    {
        String fullextent = _mapper.getFullMapExtent();
        return fullextent;
    }

    /**
     * @see BaseMOJMapper#getResponse
     */
    protected MapResponse getResponse(
        String mapName,
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayer,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayer)
        throws java.rmi.RemoteException
    {
        return _mapper.getResponse(width, height, extent, layerVisibility, selectedLayer, queryEnvelope, queryString, findString, bufferDistance, bufferUnit, targetLayer);
    }

    /**
     * @see BaseMOJMapper#getResponse
     */
    protected MapResponse getResponse(
        String mapName,
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayer,
        String queryEnvelope,
        String queryString)
        throws java.rmi.RemoteException
    {
        return  _mapper.getResponse(width, height, extent, layerVisibility, selectedLayer, queryEnvelope, queryString);
    }

    /**
     * @see BaseMOJMapper#getResponse
     */
    protected MapResponse getResponse(
        String mapName,
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayer,
        String findString)
        throws java.rmi.RemoteException
    {
        return _mapper.getResponse(width, height, extent, layerVisibility, selectedLayer, findString);
    }

    /**
     * @see BaseMOJMapper#getMapInfo
     */
    protected MapInfo getMapInfo(String mapName)
        throws java.rmi.RemoteException
    {
        return _mapper.getMapInfo();
    }

    /**
     * @see BaseMOJMapper#getServiceInfo
     */
    protected String getServiceInfo(String serviceName)
        throws java.rmi.RemoteException
    {
        ServiceCatalog cinfo = ServiceCatalogRegistry.getServiceCatalog(serviceName);
        StringBuffer sinfo = new StringBuffer();
        sinfo.append(serviceName + ",");
        sinfo.append(cinfo.getServiceType() + ",");
        sinfo.append(cinfo.getConfig()+",");
        sinfo.append(cinfo.getOutputDir()+",");
        sinfo.append(cinfo.getOutputUrl()+",");
        sinfo.append(cinfo.getImageFormat());
        return sinfo.toString();
    }

    /**
     * get response from server with given set of parameters
     * @param mapName the service name
     * @param width the image width
     * @param height the image height
     * @param extent the map extent
     * @param layerVisibility the visibility list of each individual layers
     * @param selectedLayerIndex the selected/active layer
     * @param findString the spatial query filter
     * @param mapTitle the map title text
     */
    public String getLayoutImage(
            String mapName,
            int width, int height,
            String extent,
            boolean[] layerVisibility,
            int selectedLayerIndex,
            String findString,
            String mapTitle)
    {
        System.out.println("ServletMapper: getResponse() w=" + width + " h=" + height + " extent=" + extent);

        try {
            // set visibility
            java.util.HashMap displayLists = getDisplayLists(layerVisibility, selectedLayerIndex);
            System.out.println("DisplayLists=" + _mapper.getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            // do find
            if ((findString != null && !findString.equals(""))) {
                SelectionSet ss = _mapper.findFeatures(selectedLayerIndex, findString);
                if (ss != null && ss.size()>0) {
                    System.out.println("ServletMapper: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(_mapper.getMap().getLayer(selectedLayerIndex).getName());
                    dl.setSelectionSet(ss);
                }
            }

            return createLayoutImage(width, height, mapTitle, displayLists, null, extent);

        } catch (Exception ex) {
            return null;
        }
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
            String mapTitle)
    {
        try {

            // set visibility
            java.util.HashMap displayLists = getDisplayLists(layerVisibility, selectedLayerIndex);
            System.out.println("DisplayLists=" + _mapper.getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            // do query
            if ((queryEnvelope != null && !queryEnvelope.equals("")) ||
                (queryString != null && !queryString.equals("")))
            {
                com.esri.mo2.cs.geom.Envelope selectEnvelope = null;
                if (queryEnvelope != null && !queryEnvelope.trim().equals(""))
                    selectEnvelope = com.esri.mo2.cs.geom.Global.parseEnvelope(selectEnvelope, queryEnvelope);

                SelectionSet ss = _mapper.getMap().select(selectedLayerIndex, selectEnvelope, queryString);

                if (ss != null && ss.size()>0) {
                    System.out.println("ServletMapper: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(_mapper.getMap().getLayer(selectedLayerIndex).getName());
                    dl.setSelectionSet(ss);
                }
            }

            return createLayoutImage(width, height, mapTitle, displayLists, null, extent);

        } catch (Exception ex) {
            return null;
        }
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
        try {

            // set visibility
            java.util.HashMap displayLists = getDisplayLists(layerVisibility, selectedLayerIndex);
            System.out.println("DisplayLists=" + _mapper.getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            // do query
            SelectionSet ss = null;
            if ((queryEnvelope != null && !queryEnvelope.equals("")) ||
                (queryString != null && !queryString.equals("")))
            {
                com.esri.mo2.cs.geom.Envelope selectEnvelope = null;
                if (queryEnvelope != null && !queryEnvelope.trim().equals(""))
                    selectEnvelope = com.esri.mo2.cs.geom.Global.parseEnvelope(selectEnvelope, queryEnvelope);

                ss = _mapper.getMap().select(selectedLayerIndex, selectEnvelope, queryString);

                if (ss != null && ss.size()>0) {
                    System.out.println("ServletMapper: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(_mapper.getMap().getLayer(selectedLayerIndex).getName());
                    dl.setSelectionSet(ss);
                }
            }

            // do buffer
            com.esri.mo2.cs.geom.Polygon bufPolygon = null;
            if (bufferDistance > 0d && ss != null && ss.size()>0 )
            {
                int bufUnit = getBufferUnit(bufferUnit);
                bufPolygon = _mapper.getMap().buffer(bufferDistance, bufUnit, selectedLayerIndex, ss);
            }
            System.out.println("Get buffer polygon ? " + (bufPolygon != null));

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
                    ss = _mapper.getMap().select(targetLayerIndex, bufPolygon);
                    if (ss != null && ss.size()>0) {
                        DisplayList dl = (DisplayList)displayLists.get(_mapper.getMap().getLayer(targetLayerIndex).getName());
                        dl.setSelectionSet(ss);
                        com.esri.mo2.map.draw.Renderer selrenderer =
                            com.esri.mo2.map.draw.Util.constructSelectionRenderer(
                                _mapper.getMap().getLayer(targetLayerIndex).getLayerInfo().getType(),
                                new java.awt.Color(150, 50, 50)
                            );
                        dl.setSelectionRenderer(selrenderer);
                    }
                }
            }

            return createLayoutImage(width, height, mapTitle, displayLists, acetateLayer, extent);

        } catch (Exception ex) {
            return null;
        }
    }

    private int getBufferUnit(String bufferUnit) {
        if (bufferUnit==null)
            return _mapper.getMap().getMapUnit();
        else
            return com.esri.mo2.util.Units.stringUnitToInt(bufferUnit.toLowerCase());
    }

    private java.util.HashMap getDisplayLists(
            boolean[] layerVisList,
            int selectedLayerIndex)
    {
        if (selectedLayerIndex < 0) selectedLayerIndex = 0;

        // set layer visibility
        java.util.HashMap displayLists = new java.util.HashMap();
        if (layerVisList!=null && layerVisList.length > 0) {
            for (int i=0; i < _mapper.getMap().getLayerCount(); i++) {
                DisplayList dl = new DisplayList();
                if (i<layerVisList.length)
                    dl.setVisible(layerVisList[i]);
                else
                    dl.setVisible(true);
                displayLists.put(_mapper.getMap().getLayer(i).getName(), dl);

                System.out.println("layer name=" + _mapper.getMap().getLayer(i).getName() + " DisplayList " + dl.isVisible());
            }
        } else {
            for (int i=0; i<_mapper.getMap().getLayerCount(); i++) {
                DisplayList dl = new DisplayList();
                dl.setVisible(true);
                displayLists.put(_mapper.getMap().getLayer(i).getName(), dl);
            }
        }

        return displayLists;
    }

    private synchronized String createLayoutImage(
            int width, int height, String mapTitle, java.util.HashMap displayLists,
            com.esri.mo2.svr.map.AcetateLayer acetateLayer, String extent)
        throws Exception
    {
        _mapper.getMap().setMapExtent(extent);
        for (int i=0; i<displayLists.size(); i++) {
            String name = _mapper.getMap().getLayer(i).getName();
            DisplayList dl = (DisplayList)displayLists.get(name);
            _mapper.getMap().addLayerDisplayList(name, dl);
        }
        if (acetateLayer != null) _mapper.getMap().addAcetateLayer(acetateLayer);

        com.esri.svr.lyt.LayoutService layout = new com.esri.svr.lyt.LayoutService();
        layout.setOutputDir(_mapper.getOutputDir());
        addElementsToPageLayout(layout, width, height, mapTitle);

        String imagefile = layout.exportImage(new java.awt.Dimension(width+deltaX, height+deltaY));
        System.out.println("ServletMapper: createLayoutImage() image file=" + imagefile + " map extent=" + _mapper.getMap().getMapExtent());

        return imagefile;
    }

    private void addElementsToPageLayout(
            com.esri.svr.lyt.LayoutService layout, int width, int height, String mapTitle)
    {
        boolean addBorder=false;

        int dx=10, dy=10;
        int titleHeight = 20, titleWidth = 100;
        int legendWidth = 100, northArrowWidth = 100, northArrowHeight = 100;
        int legendHeight = height - northArrowHeight - dy;
        if (legendHeight > 50) legendHeight = 150;

        int scaleBarWidth = (width + northArrowWidth) / 2;
        int ovmapHeight = 100, scaleBarHeight = 100;
        int ovmapWidth = scaleBarWidth; //(int)( (double)ovmapHeight * ( (double)width/ (double)height) );

        //deltaX = 2*dx + legendWidth;
        //deltaY = 3*dy + titleHeight + ovmapHeight;

        // add a text/title element
        com.esri.mo2.map.elt.TextElement title = new com.esri.mo2.map.elt.TextElement(mapTitle);
        title.setTextSize(titleHeight);

        int textWidth = titleHeight*mapTitle.length();
        if (titleWidth < textWidth) titleWidth = textWidth;

        // north arrow
        com.esri.mo2.map.elt.NorthArrowElement na = new com.esri.mo2.map.elt.NorthArrowElement();
        na.setStyleIndex(0);
        na.setNorthArrowSize(10);
        na.setColor(java.awt.Color.blue);
        na.setAngle(0d);
        // add border to NorthArrowElement
        if (addBorder) {
            na.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(na));
            na.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            na.getBorderLayer().setPaint(java.awt.Color.black);
        }

        // legend
        com.esri.mo2.map.core.Legend legend = new com.esri.mo2.map.elt.BaseLegend(_mapper.getMap());
        com.esri.mo2.map.elt.LegendElement legendEle = new com.esri.mo2.map.elt.LegendElement(legend);
        // add border to legene element
        if (addBorder) {
            legendEle.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(legendEle));
            legendEle.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            legendEle.getBorderLayer().setPaint(java.awt.Color.black);
        }

        // overviewmap
        com.esri.mo2.svr.map.OverviewMap ovmap = new com.esri.mo2.svr.map.OverviewMap();
        ovmap.setMap(_mapper.getMap());
        ovmap.addLayer(_mapper.getMap().getLayer(0));
        com.esri.mo2.map.elt.OverviewMapElement ovmapElt = new com.esri.mo2.map.elt.OverviewMapElement(ovmap);
        // add border to overviewmap element
        //if (addBorder) {
            ovmapElt.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(ovmapElt));
            ovmapElt.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            ovmapElt.getBorderLayer().setPaint(java.awt.Color.black);
        //}

        // scale bar element
        com.esri.mo2.map.elt.ScaleBarElement sbElt = new com.esri.mo2.map.elt.ScaleBarElement();
        sbElt.setMap(_mapper.getMap());
        sbElt.setStyleIndex(1);
        sbElt.setScaleUnit(com.esri.mo2.util.Units.MILES);
        sbElt.setSelectedTotalUnits(2);
        // add border to scale bar element
        if (addBorder) {
            sbElt.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(sbElt));
            sbElt.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            sbElt.getBorderLayer().setPaint(java.awt.Color.black);
        }

        // map element
        com.esri.mo2.map.elt.MapElement mapElement = new com.esri.mo2.map.elt.MapElement(_mapper.getMap());

        // add border to MapElement
        //if (addBorder) {
            mapElement.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(mapElement));
            mapElement.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            mapElement.getBorderLayer().setPaint(java.awt.Color.black);
        //}

        com.esri.mo2.cs.geom.Envelope titleLoc = new com.esri.mo2.cs.geom.Envelope(dx+(width+legendWidth-titleWidth)/2, dy, titleWidth, titleHeight);
        com.esri.mo2.cs.geom.Envelope mapLoc = new com.esri.mo2.cs.geom.Envelope (dx, 2*dy+titleHeight, width, height);
        com.esri.mo2.cs.geom.Envelope legendLoc = new com.esri.mo2.cs.geom.Envelope (dx*2+width, 2*dy+titleHeight, legendWidth, legendHeight);
        com.esri.mo2.cs.geom.Envelope northArrowLoc = new com.esri.mo2.cs.geom.Envelope (dx*2+width, 2*dy+titleHeight+height-northArrowHeight, northArrowWidth, northArrowHeight);
        com.esri.mo2.cs.geom.Envelope ovmapLoc = new com.esri.mo2.cs.geom.Envelope (dx, 3*dy+titleHeight+height, ovmapWidth, ovmapHeight);

        int delta = ovmapWidth;
        if (ovmapWidth < scaleBarWidth) delta =scaleBarWidth;
        com.esri.mo2.cs.geom.Envelope scaleBarLoc = new com.esri.mo2.cs.geom.Envelope (dx*2+delta, 3*dy+titleHeight+height, scaleBarWidth, scaleBarHeight);

        System.out.println("Title loc=>" + titleLoc);
        System.out.println("map loc=>" + mapLoc);
        System.out.println("legned loc=>" + legendLoc);
        System.out.println("north arrow loc=>" + northArrowLoc);
        System.out.println("overviewmap loc=>" + ovmapLoc);
        System.out.println("scaleBar loc=>" + scaleBarLoc);

        java.awt.Dimension dim = new java.awt.Dimension(width+dx*3+legendWidth, 4*dy+height+titleHeight+ovmapHeight);
        com.esri.mo2.cs.geom.Envelope extent = new com.esri.mo2.cs.geom.Envelope(0, 0, dim.width, dim.height);

        title.setEnvelope(titleLoc);  // otherwise it will use default envelope
        layout.getPageLayout().setExtent(extent);
        layout.getPageLayout().addElement(title,      0, "Title1",       titleLoc);
        layout.getPageLayout().addElement(legendEle,  1, "Legend",       legendLoc);
        layout.getPageLayout().addElement(na,         2, "North Arrow",  northArrowLoc);
        layout.getPageLayout().addElement(ovmapElt,   3, "OVMap",        ovmapLoc);
        layout.getPageLayout().addElement(sbElt,      4, "ScaleBar",     scaleBarLoc);
        layout.getPageLayout().addElement(mapElement, 5, "Map1",         mapLoc);

        System.out.println("ServletMapper.addElementsToPageLayout() title envelope=" + title.getEnvelope());
        System.out.println("ServletMapper.addElementsToPageLayout() # of elements=" + layout.getPageLayout().getElementCount());
    }
}