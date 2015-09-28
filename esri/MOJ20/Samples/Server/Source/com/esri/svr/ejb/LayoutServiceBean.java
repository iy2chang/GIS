package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import javax.ejb.SessionBean;
import javax.ejb.EJBException;
import java.rmi.RemoteException;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.svr.map.Map;
import com.esri.mo2.svr.map.DisplayList;
import com.esri.mo2.data.feat.SelectionSet;
import com.esri.svr.lyt.LayoutService;
import com.esri.svr.cmn.Util;

import com.esri.mo2.map.elt.NorthArrowElement;
import com.esri.mo2.map.elt.OverviewMapElement;
import com.esri.mo2.map.elt.ScaleBarElement;
import com.esri.mo2.map.elt.LegendElement;
import com.esri.mo2.map.elt.TextElement;

/**
 * This class provides implementation of LayoutService and LayoutServiceHome interfaces
 * in addition to SessionBean interface.
 */
public class LayoutServiceBean implements SessionBean {

    protected SessionContext ctx;

    //
    // private working map object
    //
    private ServiceCatalogLocalHome _serviceCatalogLocalHome;
    private Map _map = new com.esri.mo2.svr.map.Map();
    private String _serviceName;
    private LayoutService _layout = new LayoutService();
    private boolean DEBUG=true;

//
// public methods
//
    /**
     * Create the session bean (empty implementation)
     */
    public void ejbCreate() throws javax.ejb.CreateException {
        if (DEBUG) System.out.println("LayoutServiceBean:ejbCreate()");
    }

    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void setSessionContext(SessionContext parm1) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    /**
     * add layers based on the given service name from service catalog local bean
     * @param servicename the service's layers to be added
     */
    public void addLayers(String servicename)
    {
        System.err.println("3 ########## service name=" + servicename);

        clearAcetateLayers();
        clearDisplayLists();

        System.err.println("4 ########## service name=" + servicename);

        // skip the add layers process if the service's layers have been added and the
        // the requested service is identical to the one already loaded
        if (getServiceName() != null &&
            getServiceName().equals(servicename.trim()) &&
            getMap().getLayerCount()>0)
            return;

        removeLayers();

        if (DEBUG) System.out.println("5 ########## service name=" + servicename);

        try {
            ServiceCatalogLocal serviceCatalogLocal = getServiceCatalogLocalHome().findByServiceName(servicename);

            //getMap().setExtent(serviceCatalogLocal.getRegisteredProperties().getEnvelope());
            com.esri.mo2.map.dpy.Layerset lset = serviceCatalogLocal.getLayers();
            if (DEBUG) System.out.println("@@@ MapServiceIIBean:addLayers() # of layers=" + lset.size());

            //for (int i=lset.getSize()-1; i>=0; i--) {
            for (int i=0; i<lset.size(); i++) {
              getMap().getLayerset().addLayer(lset.layerAt(i));
              if (DEBUG) System.out.println("i=" + i + " Layer Name=" + lset.layerAt(i).getName() + " visible=" + lset.layerAt(i).isVisible());
            }

            // set output url or directory
            if (DEBUG) System.out.println("@@@ MapServiceIIBean:addLayers() output url=" + serviceCatalogLocal.getOutputUrl());
            if (DEBUG) System.out.println("@@@ MapServiceIIBean:addLayers() output dir=" + serviceCatalogLocal.getOutputUrl());

            //String url = serviceCatalogLocal.getOutputUrl();
            String dir = serviceCatalogLocal.getOutputDir();
            /*if (url != null && url.startsWith("smb://")) {
                _layout.setOutputUrl(url);
            } else */
            if (dir != null && !dir.equals("")) {
                _layout.setOutputDir(dir);
            }

            // set map units here
            getMap().setMapUnit(serviceCatalogLocal.getLayers().getDisplayManager().getMapUnit());
            // set map coordinate system
            getMap().setCoordinateSystem(serviceCatalogLocal.getCoordinateSystem());

        } catch (javax.ejb.FinderException fex) {
            fex.printStackTrace();
        }

        if (DEBUG) System.out.println("6 ########## service name=" + servicename);
        setServiceName(servicename.trim());
    }

    /**
     * Remove all the layers from the current Map object
     */
    protected void removeLayers() {
        long t1 = System.currentTimeMillis();

        // remove all the layers from layerset
        while(getMap().getLayerCount()>0)
            getMap().getLayerset().removeLayerAt(0);

        long t2 = System.currentTimeMillis();
        if (DEBUG) System.out.println("LayoutServiceBean: removeLayers() total time(ms)=" + (t2-t1));
    }

    protected void clearAcetateLayers() {
        // clear AcetateLayer(s)
        getMap().clearAcetateLayers();
    }

    protected void clearDisplayLists() {
        // clear layer DisplayList(s)
        getMap().getLayerDisplayLists().clear();
    }

    /**
     * get the working Map object
     */
    public Map getMap() {
        return _map;
    }

    /**
	  *
	  *
     */
	 public LayoutService getLayoutService() {
	 	  return _layout;
	 }

    /**
     * Get service name
     * @return service name
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * Set service name
     * @param serviceName the service name
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    /**
     * get the service catalog home object
     */
    public ServiceCatalogLocalHome getServiceCatalogLocalHome()
        throws EJBException
    {
        if (_serviceCatalogLocalHome == null) {
            try {
                Context ctx = new InitialContext();
                Object result = ctx.lookup("java:comp/env/ejb/ServiceCatalogLocalHome3");
                _serviceCatalogLocalHome = (ServiceCatalogLocalHome)javax.rmi.PortableRemoteObject.narrow(result, ServiceCatalogLocalHome.class);
            } catch (javax.naming.NamingException ex) {
                ex.printStackTrace();
                throw new EJBException(ex.getMessage());
            }
        }
        return _serviceCatalogLocalHome;
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
        throws RemoteException
    {
        if (DEBUG) System.out.println("LayoutServiceBean: getResponse() w=" + width + " h=" + height + " extent=" + extent);
        if (DEBUG) System.out.println("LayoutServiceBean: getResponse() layer visibiliyt=>" + layerVisList);

        try {
            addLayers(serviceName);
            getMap().setMapExtent(extent);

            // set visibility
            java.util.HashMap displayLists = getDisplayLists(layerVisList, selectedLayerIndex);
            if (DEBUG) System.out.println("DisplayLists=" + getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);

            // do find
            if ((findString != null && !findString.equals(""))) {
                SelectionSet ss = findFeatures(selectedLayerIndex, findString);
                if (ss != null && ss.size()>0) {
                    if (DEBUG) System.out.println("LayoutServiceBean: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(selectedLayerIndex).getName());
                    dl.setSelectionSet(ss);
                }
            }

            return createLayoutImage(width, height, mapTitle, displayLists, null);

        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
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
     * @param queryString the query where clause string
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
        throws RemoteException
    {
        if (DEBUG) System.out.println("LayoutServiceBean: getResponse() w=" + width + " h=" + height + " extent=" + extent);
        if (DEBUG) System.out.println("LayoutServiceBean: getResponse() layer visibiliyt=>" + layerVisList);

        try {
            addLayers(serviceName);
            getMap().setMapExtent(extent);

            java.util.HashMap displayLists = getDisplayLists(layerVisList, selectedLayerIndex);
            if (DEBUG) System.out.println("DisplayLists=" + getMap().getLayerDisplayLists().size() + " active layer index=" + selectedLayerIndex);
            if (extent != null) {
                getMap().setMapExtent(extent);
            }

            // do query
            if ((queryEnvelope != null && !queryEnvelope.equals("")) || (queryString!=null && !queryString.trim().equals(""))) {
                SelectionSet ss = queryMap(selectedLayerIndex, queryEnvelope, queryString);
                if (ss != null && ss.size()>0) {
                    if (DEBUG) System.out.println("LayoutServiceBean: getResponse() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(selectedLayerIndex).getName());
                    dl.setSelectionSet(ss);
                }
            }
            if (DEBUG) System.out.println("LayoutServiceBean: getResponse() queryEnvelope=" + queryEnvelope);

            return createLayoutImage(width, height, mapTitle, displayLists, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
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
     * @param queryString the quyer string/where clause
     * @param mapTitle the map title text
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
        try {
            addLayers(serviceName);
            getMap().setMapExtent(extent);

            // set visibility
            java.util.HashMap displayLists = getDisplayLists(layerVisibility, selectedLayerIndex);
            if (DEBUG) System.out.println("LayoutServiceBean: getLayoutImage() DisplayLists=" + displayLists.size() + " active layer index=" + selectedLayerIndex);

            // do query
            SelectionSet ss = null;
            if ((queryEnvelope != null && !queryEnvelope.equals("")) ||
                (queryString != null && !queryString.equals("")))
            {
                com.esri.mo2.cs.geom.Envelope selectEnvelope = null;
                if (queryEnvelope != null && !queryEnvelope.trim().equals(""))
                    selectEnvelope = com.esri.mo2.cs.geom.Global.parseEnvelope(selectEnvelope, queryEnvelope);

                ss = getMap().select(selectedLayerIndex, selectEnvelope, queryString);

                if (ss != null && ss.size()>0) {
                    if (DEBUG) System.out.println("LayoutServiceBean: getLayoutImage() select set=" + ss.size());
                    DisplayList dl = (DisplayList)displayLists.get(getMap().getLayer(selectedLayerIndex).getName());
                    dl.setSelectionSet(ss);
                }
            }

            // do buffer
            com.esri.mo2.cs.geom.Polygon bufPolygon = null;
            if (bufferDistance > 0d && ss != null && ss.size()>0 )
            {
                int bufUnit = getBufferUnit(bufferUnit);
                bufPolygon = getMap().buffer(bufferDistance, bufUnit, selectedLayerIndex, ss);
            }
            if (DEBUG) System.out.println("LayoutServiceBean: getLayoutImage() Get buffer polygon ? " + (bufPolygon != null));

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

            return createLayoutImage(width, height, mapTitle, displayLists, acetateLayer);

        } catch (Exception ex) {
            return null;
        }
    }

    private int getBufferUnit(String bufferUnit) {
        if (bufferUnit==null)
            return getMap().getMapUnit();
        else
            return com.esri.mo2.util.Units.stringUnitToInt(bufferUnit.toLowerCase());
    }


//
// private methods
//

    private java.util.HashMap getDisplayLists(
            boolean[] layerVisList,
            int selectedLayerIndex)
    {
        if (selectedLayerIndex < 0) selectedLayerIndex = 0;

        // set layer visibility
        java.util.HashMap displayLists = new java.util.HashMap();
        if (layerVisList!=null && layerVisList.length > 0) {
            for (int i=0; i < getMap().getLayerCount(); i++) {
                DisplayList dl = new DisplayList();
                if (i<layerVisList.length)
                    dl.setVisible(layerVisList[i]);
                else
                    dl.setVisible(true);
                String name = getMap().getLayer(i).getName();
                displayLists.put(name, dl);
                if (DEBUG) System.out.println("LayoutServiceBean: getDisplayLists() layer name=" + name + " DisplayList " + dl.isVisible());
            }
        } else {
            for (int i=0; i<getMap().getLayerCount(); i++) {
                DisplayList dl = new DisplayList();
                dl.setVisible(true);
                displayLists.put(getMap().getLayer(i).getName(), dl);
            }
        }

        if (DEBUG) System.out.println("LayoutServiceBean: getDisplayLists() DisplayLists=" + displayLists.size() + " active layer index=" + selectedLayerIndex + " " + (layerVisList==null) + " " + layerVisList.length);

        return displayLists;
    }

    private com.esri.mo2.data.feat.SelectionSet findFeatures(int layerindex, String theFindValue) {
        com.esri.mo2.map.dpy.FeatureLayer flayer = (com.esri.mo2.map.dpy.FeatureLayer)getMap().getLayer(layerindex);
        com.esri.mo2.data.feat.Fields fields = flayer.getFeatureClass().getFields();
        com.esri.mo2.data.feat.SelectionSet selset = null;
        for (int i=0; i<fields.getFields().length; i++) {
            if (DEBUG) System.out.println("i=" + i + " name=" + fields.getField(i).getName() + " string type=" + fields.getField(i).isString());
            if (!fields.getField(i).isString()) continue;
            String whereClause = fields.getField(i).getName() + " cn " + theFindValue;
            com.esri.mo2.data.feat.SelectionSet ss  = getMap().select(layerindex, whereClause);

            if (selset == null) selset = ss;
            else selset.add(ss);
        }

        return selset;
    }

    private SelectionSet queryMap(int activeLayerIndex,  String queryEnvelope, String whereClause)
            throws Exception
    {
        Envelope extent = null;
        if (queryEnvelope != null) {
            extent = com.esri.mo2.cs.geom.Global.parseEnvelope(extent, queryEnvelope);
        }

        SelectionSet selset = getMap().select(activeLayerIndex, extent, whereClause);
        return selset;
    }

    private String createLayoutImage(int width, int height, String mapTitle, java.util.HashMap displayLists, com.esri.mo2.svr.map.AcetateLayer acetateLayer)
            throws java.io.IOException
    {
        if (DEBUG) System.out.println("LayoutServiceBean.createLayoutImage() called");
        for (int i=0; i<displayLists.size(); i++) {
            String name = getMap().getLayer(i).getName();
            DisplayList dl = (DisplayList)displayLists.get(name);
            getMap().addLayerDisplayList(name, dl);
        }

        if (acetateLayer != null) getMap().addAcetateLayer(acetateLayer);

        // draw layout image
        java.awt.Dimension newSize = addElementsToPageLayout(width, height, mapTitle);
        if (DEBUG) System.out.println("LayoutServiceBean.createLayoutImage() export to an image");

        String imagefile =  _layout.exportImage(new java.awt.Dimension(width, height));
        if (DEBUG) System.out.println("LayoutServiceBean.createLayoutImage() image file=" + imagefile + " map extent=" + getMap().getMapExtent());

        return imagefile;
    }

    private java.awt.Dimension addElementsToPageLayout(int width, int height, String mapTitle) {
        _layout.getPageLayout().removeAllElements();

        boolean addBorder=false;

        int dx=10, dy=10;
        int titleHeight = 20, titleWidth = 100;
        int legendWidth = 100, northArrowWidth = 100, northArrowHeight = 100;
        int legendHeight = height - northArrowHeight - dy;
        if (legendHeight > 50) legendHeight = 150;

        int scaleBarWidth = (width + northArrowWidth) / 2;
        int ovmapHeight = 100, scaleBarHeight = 100;
        int ovmapWidth = scaleBarWidth;//(int)( (double)ovmapHeight * ( (double)width/ (double)height) );

        // add a text/title element
        TextElement title = new TextElement(mapTitle);
        title.setTextSize(titleHeight);
        java.awt.Font font = new java.awt.Font(title.getTextFont().getFontName(), java.awt.Font.BOLD, titleHeight);
        title.setTextFont(font);

        int textWidth = titleHeight*mapTitle.length();
        if (titleWidth < textWidth) titleWidth = textWidth;

        // map element
        com.esri.mo2.map.elt.MapElement mapElement = new com.esri.mo2.map.elt.MapElement(getMap());

        // add border to MapElement
        //if (addBorder) {
            mapElement.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(mapElement));
            mapElement.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            mapElement.getBorderLayer().setPaint(java.awt.Color.black);
        //}
        // north arrow
        NorthArrowElement na = new NorthArrowElement();
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
        com.esri.mo2.map.core.Legend legend = new com.esri.mo2.map.elt.BaseLegend(getMap());
        LegendElement legendEle = new LegendElement(legend);
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
        ovmap.setMap(getMap());
        ovmap.addLayer(getMap().getLayer(0));
        OverviewMapElement ovmapElt = new OverviewMapElement(ovmap);
        // add border to overviewmap element
        //if (addBorder) {
            ovmapElt.setBorderLayer(new com.esri.mo2.map.elt.BaseBorderLayer(ovmapElt));
            ovmapElt.getBorderLayer().setStroke(
                com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.SOLID_LINE, 1)
            );
            ovmapElt.getBorderLayer().setPaint(java.awt.Color.black);
        //}

        // scale bar element
        ScaleBarElement sbElt = new ScaleBarElement();
        sbElt.setMap(getMap());
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

        com.esri.mo2.cs.geom.Envelope titleLoc = new com.esri.mo2.cs.geom.Envelope(dx+(width+legendWidth-titleWidth)/2, dy, titleWidth, titleHeight);
        com.esri.mo2.cs.geom.Envelope mapLoc = new com.esri.mo2.cs.geom.Envelope (dx, 2*dy+titleHeight, width, height);
        com.esri.mo2.cs.geom.Envelope legendLoc = new com.esri.mo2.cs.geom.Envelope (dx*2+width, 2*dy+titleHeight, legendWidth, legendHeight);
        com.esri.mo2.cs.geom.Envelope northArrowLoc = new com.esri.mo2.cs.geom.Envelope (dx*2+width, 2*dy+titleHeight+height-northArrowHeight, northArrowWidth, northArrowHeight);
        com.esri.mo2.cs.geom.Envelope ovmapLoc = new com.esri.mo2.cs.geom.Envelope (dx, 3*dy+titleHeight+height, ovmapWidth, ovmapHeight);

        int delta = ovmapWidth;
        if (ovmapWidth < scaleBarWidth) delta =scaleBarWidth;
        com.esri.mo2.cs.geom.Envelope scaleBarLoc = new com.esri.mo2.cs.geom.Envelope (dx*2+delta, 3*dy+titleHeight+height, scaleBarWidth, scaleBarHeight);

        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() Title loc=>" + titleLoc);
        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() map loc=>" + mapLoc);
        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() legned loc=>" + legendLoc);
        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() north arrow loc=>" + northArrowLoc);
        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() overviewmap loc=>" + ovmapLoc);
        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() scaleBar loc=>" + scaleBarLoc);

        java.awt.Dimension dim = new java.awt.Dimension(width+dx*3+legendWidth, 4*dy+height+titleHeight+ovmapHeight);
        com.esri.mo2.cs.geom.Envelope extent = new com.esri.mo2.cs.geom.Envelope(0, 0, dim.width, dim.height);

        title.setEnvelope(titleLoc);  // otherwise it will use default envelope
        _layout.getPageLayout().setExtent(extent);
        _layout.getPageLayout().addElement(title,      0, "Title1",       titleLoc);
        _layout.getPageLayout().addElement(legendEle,  1, "Legend",       legendLoc);
        _layout.getPageLayout().addElement(na,         2, "North Arrow",  northArrowLoc);
        _layout.getPageLayout().addElement(ovmapElt,   3, "OVMap",        ovmapLoc);
        _layout.getPageLayout().addElement(sbElt,      4, "ScaleBar",     scaleBarLoc);
        _layout.getPageLayout().addElement(mapElement, 5, "Map1",         mapLoc);

        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() title envelope=" + title.getEnvelope());
        if (DEBUG) System.out.println("LayoutServiceBean.addElementsToPageLayout() # of elements=" + _layout.getPageLayout().getElementCount());

        return dim;
    }

//
// local unit testing code
//

    public static void main(String[] args) throws Exception {
        LayoutServiceBean b = new LayoutServiceBean();
        Map map = new Map();
        //b.loadDataset(map);
        b.loadStandardAXL("D:/ESRI/MOJ20/Samples/Server/AXLFiles/usa.axl", map);
        b._map = map;
        try {
            long t1 = System.currentTimeMillis();
            b.createLayoutImage(647, 376, "New USA Map", new java.util.HashMap(), null);
            long t2 = System.currentTimeMillis();

            System.out.println("total => " + (t2-t1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadDataset(Map map) {
        try {
          String layername = "States";
          com.esri.mo2.file.shp.ShapefileFolder layersource =
          com.esri.mo2.file.shp.ShapefileFolder.getLayerSource("d:/esri/esriData/Usa/");

          com.esri.mo2.map.dpy.Layer[] layers = layersource.getLayers();
          com.esri.mo2.map.dpy.LayerInfo[] li = layersource.getLayerInfo();
          com.esri.mo2.map.dpy.Layer layer = null;

          for (int i=0;i<layers.length;i++)
          {
            //if (DEBUG) System.out.println("name="+li[i].getName()+" i="+i);
            if (li[i].getName().equalsIgnoreCase(layername)) {
              layer = layers[i];
              layer.setVisible(true);
              map.getLayerset().addLayer(layer);

              if (DEBUG) System.out.println("layer class=>" + layers[i].getClass().getName());
            }
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
    }

     private void loadStandardAXL(String axlFileName, Map map) throws Exception {
        com.esri.mo2.xml.prj.MapProject mp = new com.esri.mo2.xml.prj.MapProject();
        mp.setRegisteredEnvironment(new com.esri.mo2.util.Environment());
        mp.setRegisteredMap(map);
        mp.setFile(axlFileName);
        mp.load();

        System.out.println("# of layers=>" + map.getLayerCount());
    }

}