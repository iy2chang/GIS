package com.esri.svr.map;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import java.util.Vector;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import com.esri.mo2.cs.geom.Envelope;

import com.esri.svr.cmn.Feature;
import com.esri.svr.cmn.LayerInfo;
import com.esri.svr.cmn.MapResponse;
import com.esri.svr.cmn.MapInfo;
import com.esri.svr.cmn.ServiceInfo;


/**
 * BaseMOJMapper provides base functionlity to process a HTTPServletRequest
 * with map image generation left to the sub-classes.
 */
public abstract class BaseMOJMapper {
    protected java.util.HashMap _properties = null;
    protected ServletContext _servletContext = null;

    private java.text.NumberFormat _nf = java.text.NumberFormat.getInstance();
    private boolean DEBUG = true;

    /**
     * Process an HTTP request from a servlet and write output to the HTTP response object.
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws java.io.IOException
     */
    public void doGetPost(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException
    {
        String cmd = request.getParameter("Cmd");
        if (cmd == null) cmd = request.getParameter("cmd");
        if (DEBUG) System.out.println("================> BaseMOJMapper: doGetPost() cmd=" + cmd);

        if (cmd == null) {
            if (DEBUG) System.out.println("cmd not found");
            response.sendRedirect((String)_properties.get("ErrorPage"));
            return;
        }

        else if (cmd.equalsIgnoreCase("Ping")) {
            if (DEBUG) System.out.println("ping " + (String)_properties.get("PingPage"));
            response.sendRedirect((String)_properties.get("PingPage"));
            return;
        }

        // session
        HttpSession session = request.getSession(true);
        if (session == null) {
            if (DEBUG) System.out.println("session not found");
            response.sendRedirect((String)_properties.get("ErrorPage"));
            return;
        }

        Parameters parameters = new Parameters(request);
        response.setContentType("text/html");
        boolean[] layerVisibility = null;

        // if it's not the initial map
        if (!cmd.equalsIgnoreCase("InitMap")) {
            int k=0;
            Vector layerVisList = new Vector();
            Object[] obj = null;
            String layervisible = "layervis" + k;
            String visible = request.getParameter(layervisible);
            while (visible != null) {
                layerVisList.add(visible);
                layervisible = "layervis" + (++k);
                visible = request.getParameter(layervisible);
                //System.out.println("BaseMOJMapper: k="  + k + " visible=" + visible + " " + Thread.currentThread());
            }
            obj = new Object[layerVisList.size()];
            layerVisList.toArray(obj);
            layerVisibility = new boolean[obj.length];
            for (int i=0; i<obj.length; i++) layerVisibility[i] = Boolean.valueOf((String)obj[i]).booleanValue();

            if (DEBUG) System.out.println("layer visibility list=" + layerVisList);
        }

        // initial map
        if (cmd.equalsIgnoreCase("InitMap")) {
            MapInfo mapInfo = getMapInfo(parameters.serviceName);
            layerVisibility = mapInfo.getLayerVisibility();
            if (DEBUG) System.out.println("BaseMOJMapper # of layers=" + mapInfo.getLayerCount() + " " + layerVisibility.length);

            if (!parameters.validEnvelope) parameters.envelope = new com.esri.mo2.cs.geom.Envelope();
            generateResponsePage(request, response, parameters.serviceName, cmd, mapInfo.getMapExtent(), parameters.activeLayerIndex, layerVisibility, parameters.imageWidth, parameters.imageHeight, null);
        }

        /*
        else if (cmd.equalsIgnoreCase("FULLEXTENT")) {
            extent = getFullMapExtent(serviceName);
            generateResponsePage(request, response, request, serviceName, cmd, extent, active, layerVisList, imageWidth, imageHeight, queryEnvelope.toString());
        }*/

        // get identify features only
        else if (cmd.equalsIgnoreCase("IDENTIFY")) {
            if (DEBUG) System.out.println("### IDENTIFY map x=" + parameters.mapx + " y=" + parameters.mapy +  " tolerance=" + parameters.tolerance);
            if (parameters.validMapXY) {
                parameters.selectminx = parameters.mapx - parameters.tolerance/2d;
                parameters.selectminy = parameters.mapy - parameters.tolerance/2d;
                parameters.selectmaxx = parameters.mapx + parameters.tolerance/2d;
                parameters.selectmaxy = parameters.mapy + parameters.tolerance/2d;
                Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.tolerance, parameters.tolerance);
                String queryEnvelope = queryEnv.toString();
                select(parameters.serviceName, parameters.active, queryEnvelope, request);
                if (DEBUG) System.out.println("### IDENTIFY select minx=" + parameters.selectminx + " select miny=" + parameters.selectminy + " select maxx=" + parameters.selectmaxx + " select maxy=" + parameters.selectmaxy );
                redirectToSelectPage(request, response, parameters.serviceName, parameters.activeLayerIndex, cmd, parameters.selectminx, parameters.selectminy, parameters.selectmaxx, parameters.selectmaxy, "");
            }
        }

        // get select features only
        else if (cmd.equalsIgnoreCase("SELECT")) {
            if (DEBUG) System.out.println("### SELECT minx=" + parameters.selectminx + " miny=" + parameters.selectminy + " maxx=" + parameters.selectmaxx + " maxy=" + parameters.selectmaxy);
            if (parameters.validSelectMapXY) {
                Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                String queryEnvelope = queryEnv.toString();
                select(parameters.serviceName, parameters.active, queryEnvelope, request);
                redirectToSelectPage(request, response, parameters.serviceName, parameters.activeLayerIndex, cmd, parameters.selectminx, parameters.selectminy, parameters.selectmaxx, parameters.selectmaxy, "");
            }
        }

        // refresh the image with select options
        else if (cmd.equalsIgnoreCase("REFRESH_SELECT")) {
            if (DEBUG) System.out.println("### REFRESH_SELECT minx=" + parameters.selectminx + " miny=" + parameters.selectminy );
            String extent=null, queryEnvelope=null;
            if (parameters.validEnvelope && parameters.validImageSize && parameters.validSelectMapXY) {
                parameters.envelope = new Envelope(parameters.minx, parameters.miny, (parameters.maxx-parameters.minx), (parameters.maxy-parameters.miny));
                Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                extent = parameters.envelope.toString();
                queryEnvelope = queryEnv.toString();
                if (queryEnv.getArea() == 0d) queryEnvelope = null;
            } else {
                queryEnvelope = null;
                extent = getFullMapExtent(parameters.serviceName);
            }
            generateResponsePage(request, response, parameters.serviceName, cmd, extent, parameters.activeLayerIndex,
                layerVisibility, parameters.imageWidth, parameters.imageHeight, queryEnvelope, parameters.queryString);
        }

        // get image with a buffer polygon
        else if (cmd.equalsIgnoreCase("BUFFER")) {
            if (DEBUG) System.out.println("### BUFFER buffer distance=" + parameters.bufferDistance + " validEnvelope=" +
                parameters.validEnvelope + " validImageSize=" + parameters.validImageSize + " validSelectMapXY=" + parameters.validSelectMapXY +
                " bufferUnit="  + parameters.bufferUnit);

            if (parameters.validEnvelope && parameters.validImageSize && (parameters.validQueryString || parameters.validFindString || parameters.validSelectMapXY) && parameters.validBufferDistance) {
                parameters.envelope = new Envelope(parameters.minx, parameters.miny, (parameters.maxx-parameters.minx), (parameters.maxy-parameters.miny));
                String extent = parameters.envelope.toString();
                String queryEnvelope;
                if (parameters.validFindString) {
                    parameters.queryString = null;
                    queryEnvelope = null;
                } else if (parameters.validSelectMapXY) {
                    parameters.findString = null;
                    Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                    queryEnvelope = queryEnv.toString();
                    if (queryEnv.getArea() == 0d) queryEnvelope = null;
                } else {
                    queryEnvelope = null;
                }

                generateResponsePage(
                        request, response, parameters.serviceName,
                        cmd, extent, parameters.activeLayerIndex,
                        layerVisibility,
                        parameters.imageWidth, parameters.imageHeight, queryEnvelope, parameters.queryString, parameters.findString,
                        parameters.bufferDistance, parameters.bufferUnit, -1);
            }
        }

        // get buffer-select features only
        else if (cmd.equalsIgnoreCase("BUFFER_SELECT")) {
            if (DEBUG) System.out.println("### BUFFER_SELECT minx=" + parameters.selectminx + " miny=" + parameters.selectminy + " maxx=" + parameters.selectmaxx + " maxy=" + parameters.selectmaxy + " find string=" + parameters.findString);
            if ((parameters.validSelectMapXY || parameters.validFindString) && parameters.validBufferDistance && parameters.validTargetLayer) {
                if (parameters.validFindString) {
                    bufferSelect(parameters.serviceName, parameters.activeLayerIndex, null, null, parameters.findString, request, parameters.bufferDistance, parameters.bufferUnit, parameters.targetLayer);
                    redirectToBufferFindPage(request, response, parameters.serviceName, parameters.activeLayerIndex,
                        parameters.findString, parameters.bufferDistance, parameters.bufferUnit, parameters.targetLayer);
                } else {
                    Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                    String queryEnvelope = queryEnv.toString();
                    if (queryEnv.getArea() == 0d) queryEnvelope = null;
                    bufferSelect(parameters.serviceName, parameters.activeLayerIndex, queryEnvelope, parameters.queryString, null, request, parameters.bufferDistance, parameters.bufferUnit, parameters.targetLayer);
                    redirectToBufferSelectPage(request, response, parameters.serviceName, parameters.activeLayerIndex, cmd,
                        parameters.selectminx, parameters.selectminy, parameters.selectmaxx, parameters.selectmaxy, parameters.queryString, parameters.targetLayer);
                }
            }
        }

        // refresh the image with buffer-select options
        else if (cmd.equalsIgnoreCase("REFRESH_BUFFER_SELECT")) {
            if (DEBUG) System.out.println("### REFRESH_BUFFER_SELECT distance=" + parameters.bufferDistance + " unit=" + parameters.bufferUnit + " targetLayer=" + parameters.targetLayer);
            if (DEBUG) System.out.println("### REFRESH_BUFFER_SELECT query string=" + parameters.queryString + " find string=" + parameters.findString);
            String extent=null;
            if (parameters.validEnvelope && parameters.validImageSize) {
                parameters.envelope = new Envelope(parameters.minx, parameters.miny, (parameters.maxx-parameters.minx), (parameters.maxy-parameters.miny));
                extent = parameters.envelope.toString();
            } else {
                extent = getFullMapExtent(parameters.serviceName);
            }

            String queryEnvelope;
            if (parameters.validFindString) {
                parameters.queryString = null;
                queryEnvelope = null;
            } else if (parameters.validSelectMapXY) {
                Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                queryEnvelope = queryEnv.toString();
                if (queryEnv.getArea() == 0d) queryEnvelope = null;
            } else {
                queryEnvelope = null;
            }
            generateResponsePage(request, response, parameters.serviceName, cmd, extent, parameters.activeLayerIndex,
                layerVisibility, parameters.imageWidth, parameters.imageHeight, queryEnvelope, parameters.queryString, parameters.findString,
                parameters.bufferDistance, parameters.bufferUnit, parameters.targetLayer);
        }

        else if (cmd.equalsIgnoreCase("REFRESH")) {
            String extent=null;
            if (parameters.validEnvelope) {
                parameters.envelope = new Envelope(parameters.minx, parameters.miny, (parameters.maxx-parameters.minx), (parameters.maxy-parameters.miny));
                // due to the fact that the drawing is on off screen buffer
                // and a layer visibility change doesn't guarantee a redrawing to the buffer
                // and the image is copied from the buffer, so an artificial change of envelope
                // here is a necessary until we change the display manager
                parameters.envelope.grow(1.001d);
                extent = parameters.envelope.toString();
            } else {
                extent = getFullMapExtent(parameters.serviceName);
            }
            if (DEBUG) System.out.println("BaseMOJMapper: cmd=refresh extent=" + parameters.envelope);
            generateResponsePage(request, response, parameters.serviceName, cmd, extent, parameters.activeLayerIndex, layerVisibility, parameters.imageWidth, parameters.imageHeight, null);
        }

        else if (cmd.equalsIgnoreCase("QUERY")) {
            if (DEBUG) System.out.println("QueryString=" + parameters.queryString + " SampleField=" + parameters.sampleField);
            redirectToQueryPage(request, response, parameters.serviceName, parameters.activeLayerIndex, parameters.queryString, parameters.sampleField);
        }

        else if (cmd.equalsIgnoreCase("FIND")) {
            if (DEBUG) System.out.println("findString=" + parameters.findString);
            redirectToFindPage(request, response, parameters.serviceName, parameters.activeLayerIndex, parameters.findString);
        }

        // refresh the image with Find option
        else if (cmd.equalsIgnoreCase("REFRESH_FIND")) {
            if (DEBUG) System.out.println("### REFRESH_FIND find string=" + parameters.findString);
            String extent=null;
            if (parameters.validEnvelope && parameters.validImageSize && parameters.validFindString) {
                parameters.envelope = new Envelope(parameters.minx, parameters.miny, (parameters.maxx-parameters.minx), (parameters.maxy-parameters.miny));
                extent = parameters.envelope.toString();
            } else {
                extent = getFullMapExtent(parameters.serviceName);
            }

            generateFindResponsePage(request, response, parameters.serviceName, cmd,
                extent, parameters.activeLayerIndex, layerVisibility, parameters.imageWidth, parameters.imageHeight, parameters.findString);
        }

        else if (cmd.equalsIgnoreCase("ZOOMACTIVE")) {
            String extent=null;
            if (parameters.validEnvelope && parameters.validImageSize) {
                LayerInfo layerInfo = getLayerInfo(parameters.serviceName, parameters.activeLayerIndex);
                extent = layerInfo.getExtent();
            } else {
                extent = getFullMapExtent(parameters.serviceName);
            }

            if (DEBUG) System.out.println("### ZOOMACTIVE OptionCmd=" + parameters.optionCmd);

            if (parameters.optionCmd==null || parameters.optionCmd.trim().equals(""))
                generateResponsePage(request, response, parameters.serviceName, cmd,
                    extent,parameters. activeLayerIndex, layerVisibility, parameters.imageWidth, parameters.imageHeight, null);
            else {
                String queryEnvelope;
                if (parameters.optionCmd.equalsIgnoreCase("REFRESH_FIND")) {
                    if (!parameters.validFindString) parameters.findString = null;
                    generateFindResponsePage(request, response, parameters.serviceName, cmd,
                            extent, parameters.activeLayerIndex, layerVisibility, parameters.imageWidth, parameters.imageHeight, parameters.findString);
                } else if (parameters.optionCmd.equalsIgnoreCase("REFRESH_BUFFER_SELECT")) {
                    if (parameters.validSelectMapXY && parameters.validBufferDistance && parameters.validTargetLayer) {
                        Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                        queryEnvelope = queryEnv.toString();
                        if (queryEnv.getArea() == 0d) queryEnvelope = null;
                    } else {
                        queryEnvelope = null;
                    }
                    if (DEBUG) System.out.println("### ZOOMACTIVE findString=" + parameters.findString + " query string=" + parameters.queryString + " query envelope=" + queryEnvelope);
                    generateResponsePage(request, response, parameters.serviceName, cmd, extent, parameters.activeLayerIndex,
                        layerVisibility, parameters.imageWidth, parameters.imageHeight, queryEnvelope, parameters.queryString, parameters.findString,
                        parameters.bufferDistance, parameters.bufferUnit, parameters.targetLayer);
                } else if (parameters.optionCmd.equalsIgnoreCase("REFRESH_SELECT")) {
                    if (parameters.validSelectMapXY) {
                        Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                        queryEnvelope = queryEnv.toString();
                        if (queryEnv.getArea() == 0d) queryEnvelope = null;
                    } else {
                        queryEnvelope = null;
                    }
                    generateResponsePage(request, response, parameters.serviceName, cmd, extent, parameters.activeLayerIndex,
                        layerVisibility, parameters.imageWidth, parameters.imageHeight, queryEnvelope, parameters.queryString);
                }
            }
        }

        // list all services
        else if (cmd.equalsIgnoreCase("LISTSERVICES")) {
            try {
                ServiceInfo[] list = getAllServices();

                if (DEBUG) {
                    System.out.println("ListServices # =" + list.length);
                    for (int i=0; i<list.length; i++)
                        System.out.println("ListServices " + list[i].getServiceName());
                }

                listServices(request, response, list);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // get layout image
        else if (cmd.equalsIgnoreCase("PRINT")) {
            if (DEBUG) System.out.println("### PRINT minx=" + parameters.selectminx + " miny=" + parameters.selectminy );
            String extent=null, queryEnvelope;
            if (parameters.validEnvelope && parameters.validImageSize) {
                parameters.envelope = new Envelope(parameters.minx, parameters.miny, (parameters.maxx-parameters.minx), (parameters.maxy-parameters.miny));
                Envelope queryEnv = new Envelope(parameters.selectminx, parameters.selectminy, parameters.selectmaxx-parameters.selectminx, parameters.selectmaxy-parameters.selectminy);
                extent = parameters.envelope.toString();
                queryEnvelope = queryEnv.toString();
                if (queryEnv.getArea() == 0d) queryEnvelope = null;
            } else {
                queryEnvelope = null;
                extent = getFullMapExtent(parameters.serviceName);
            }
            if (DEBUG) System.out.println("### PRINT select minx=" + parameters.selectminx + " select miny=" + parameters.selectminy + " extent=" + extent + " buffer distance=" + parameters.bufferDistance);
            generateLayoutPrintPage(request, response, parameters.serviceName, extent, parameters.activeLayerIndex,
                layerVisibility, parameters.imageWidth, parameters.imageHeight, queryEnvelope, parameters.queryString,
                parameters.bufferDistance, parameters.bufferUnit, parameters.targetLayer, parameters.mapTitle);
        }

        // default action
        else {
            if (DEBUG) System.out.println("cmd not found");
            response.sendRedirect((String)_properties.get("ErrorPage"));
            return;
        }
    }

//
// protected abstract methods to be implemeneted by its sub-classes
//
    /**
     * Get layer info with given service name and layer index
     * @param serviceName the service name
     * @param selectedLayer the selected layer index
     * @return a LayerInfo object
     * @throws java.rmi.RemoteException
     */
    abstract protected LayerInfo getLayerInfo(String serviceName, int selectedLayer)
        throws java.rmi.RemoteException;

    /**
     * Get all of the service info
     * @return an array of ServiceInfo
     * @throws java.rmi.RemoteException
     */
    abstract protected ServiceInfo[] getAllServices()
        throws java.rmi.RemoteException;

    /**
     * Select features based on the given query string and map extent for a selected layer in a given service
     * @param serviceName the service name
     * @param selectedLayer the selected layer index
     * @param extent the query map extent
     * @param queryString the query string
     * @return a set of features
     * @throws java.rmi.RemoteException
     */
    abstract protected Feature[] select(String serviceName, int selectedLayer, String extent, String queryString)
        throws java.rmi.RemoteException;

    /**
     * Select features based on the given query string or find string or buffer distance,
     * and map extent for a selected layer in a given service. When buffer is applied,
     * the return features will be those from target layer.
     * @param serviceName the service name
     * @param selectedLayer selected layer index
     * @param extent the query map extent
     * @param queryString the query string
     * @param findString the find string
     * @param bufferDistance the buffer distance
     * @param bufferUnit the buffer unit
     * @param targetLayer the target layer that the buffer will apply to
     * @return a set of features
     * @throws java.rmi.RemoteException
     */
    abstract protected Feature[] select(
        String serviceName, int selectedLayer, String extent, String queryString, String findString,
        double bufferDistance, String bufferUnit, int targetLayer)
        throws java.rmi.RemoteException;

    /**
     * Find features based on the given find string
     * @param serviceName the service name
     * @param selectedLayer the selected layer index
     * @param findString the find string
     * @return a set of features
     * @throws java.rmi.RemoteException
     */
    abstract protected Feature[] find(String serviceName, int selectedLayer, String findString)
        throws java.rmi.RemoteException;

    /**
     * Get the full map extent for a given service
     * @param serviceName the service name
     * @return a string representation of map extent
     * @throws java.rmi.RemoteException
     */
    abstract protected String getFullExtent(String serviceName)
        throws java.rmi.RemoteException;

    /**
     * Get a response based on the given set of parameters. Only one of the three
     * functions (query, find and buffer) will apply in a single call.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisibility layer visibility array
     * @param selectedLayer the selected layer index
     * @param queryEnvelope query envelope
     * @param queryString query string
     * @param findString find string
     * @param bufferDistance buffer distance
     * @param bufferUnit buffer unit
     * @param targetLayer target layer to which the buffer will apply
     * @return a MapResponse object
     * @throws java.rmi.RemoteException
     */
    abstract protected MapResponse getResponse(
        String serviceName,
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
        throws java.rmi.RemoteException;

    /**
     * Get a response based on the given set of parameters.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisibility layer visibility array
     * @param selectedLayer selected layer index
     * @param queryEnvelope query envelope
     * @param queryString query string
     * @return a Map response object
     * @throws java.rmi.RemoteException
     */
    abstract protected MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayer,
        String queryEnvelope,
        String queryString)
        throws java.rmi.RemoteException;

    /**
     * Get a response based on the given set of parameters.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisibility layer visibility array
     * @param selectedLayer selected layer index
     * @param findString find string
     * @return a MapResponse object
     * @throws java.rmi.RemoteException
     */
    abstract protected MapResponse getResponse(
        String serviceName,
        int width, int height,
        String extent,
        boolean[] layerVisibility,
        int selectedLayer,
        String findString)
        throws java.rmi.RemoteException;

    /**
     * Get map infor for a given service name
     * @param serviceName the service name
     * @return a MapInfo object
     * @throws java.rmi.RemoteException
     */
    abstract protected MapInfo getMapInfo(String serviceName)
        throws java.rmi.RemoteException;

    /**
     * Get service info for a given service name
     * @param serviceName the service name
     * @return a string representation of service info
     * @throws java.rmi.RemoteException
     */
    abstract protected String getServiceInfo(String serviceName)
        throws java.rmi.RemoteException;


//
// private methods
//

    private void listServices(
        HttpServletRequest request,
        HttpServletResponse response,
        ServiceInfo[] list)
        throws java.io.IOException
    {
        try {
            RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("ServiceListPage"));
            request.setAttribute("service.list", list);
            rd.forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  new java.io.IOException(ex.getMessage());
        }
    }

    private void redirectToQueryPage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        int selectedLayer,
        String queryString,
        String sampleField
    ) throws java.io.IOException
    {
        LayerInfo layerInfo = null;
        String selectedFieldIndex = "0";
        int index = 0;
        boolean cont = true;

        try {
            if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 1 calling getLayerInfo()");
            layerInfo = getLayerInfo(serviceName, selectedLayer);

            if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 2 " + (layerInfo==null));
            request.setAttribute("layer.info", layerInfo);
            //LayerInfo layerinfo = getLayerInfo(serviceName, selectedLayer);

            if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 3 " + (layerInfo==null));
            request.setAttribute("layer.name", layerInfo.getName());

            if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 4 " + (layerInfo==null));
            request.setAttribute("sample.values", new String[0]);

            if (sampleField != null && !sampleField.trim().equals("")) {
                String[] names = layerInfo.getFieldNames();
                for (int i=0; i<names.length; i++) {
                    if (sampleField.equals(names[i])) {
                        selectedFieldIndex = String.valueOf(i);
                        index = i;
                        break;
                    }
                }

                // get sample values
                String extent = layerInfo.getExtent();

                if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 5 field index=" + index);

                Feature[] results = select(serviceName, selectedLayer, extent, null);

                if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 6 # of features==" + results.length);

                String sampleValues[] = new String[results.length];
                for (int i=0; i<results.length; i++) {
                    sampleValues[i]= (results[i].getFieldValues())[index];
                }
                request.setAttribute("sample.values", sampleValues);
                if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 7 sample.values=" + sampleValues.length);

                request.setAttribute("selected.fieldindex", selectedFieldIndex);

                if (DEBUG) System.out.println("BaseMOJMapper.redirectToQueryPage() 8 to JSP page");

                try {
                    RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("QueryPage"));
                    rd.forward(request, response);
                } catch (javax.servlet.ServletException ex) {
                    ex.printStackTrace();
                }
                cont = false;
            }

            else if (queryString != null && !queryString.trim().equals("") && cont) {
                // do query
                request.setAttribute("sample.values", new String[0]);
                request.setAttribute("selected.fieldindex", selectedFieldIndex);

                Feature[] results = select(serviceName, selectedLayer, null, queryString);
                request.setAttribute("identify.results", results);
                if (DEBUG) System.out.println("query results=" + results);

                request.setAttribute("cmd", "Query");
                request.setAttribute("query.string", queryString);
                try {
                    RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("SelectPage"));
                    rd.forward(request, response);
                } catch (javax.servlet.ServletException ex) {
                    ex.printStackTrace();
                }

            } else {
                request.setAttribute("selected.fieldindex", selectedFieldIndex);
                try {
                    RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("QueryPage"));
                    rd.forward(request, response);
                } catch (javax.servlet.ServletException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void redirectToFindPage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        int selectedLayer,
        String findString
    ) throws java.io.IOException
    {

        try {
            if (findString != null && !findString.trim().equals("")) {
                Feature[] results = find(serviceName, selectedLayer, findString);
                request.setAttribute("identify.results", results);
                if (DEBUG) System.out.println("find results=" + results);

                request.setAttribute("cmd", "Find");
                request.setAttribute("find.string", findString);

                LayerInfo layerinfo = getLayerInfo(serviceName, selectedLayer);
                request.setAttribute("layer.name", layerinfo.getName());

                try {
                    RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("SelectPage"));
                    rd.forward(request, response);
                } catch (javax.servlet.ServletException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void redirectToBufferFindPage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        int selectedLayer,
        String findString,
        double bufferDistance, String bufferUnit, int targetLayer
    ) throws java.io.IOException
    {
        request.setAttribute("cmd", "Buffer_Find");
        request.setAttribute("find.string", findString);

        request.setAttribute("buffer.distance", _nf.format(bufferDistance));
        request.setAttribute("buffer.unit", bufferUnit);
        request.setAttribute("target.layer", String.valueOf(targetLayer));

        try {
            RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("SelectPage"));
            rd.forward(request, response);
        } catch (javax.servlet.ServletException ex) {
            ex.printStackTrace();
        }
    }

    private void generateResponsePage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        String cmd,
        String extent,
        int selectedLayer,
        boolean[] layerVisibility,
        int imageWidth, int imageHeight,
        String queryEnvelope)
        throws java.io.IOException
     {
        generateResponsePage(
            request,
            response,
            serviceName,
            cmd,
            extent,
            selectedLayer,
            layerVisibility,
            imageWidth,
            imageHeight,
            queryEnvelope,
            null);
     }

    private void generateResponsePage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        String cmd,
        String extent,
        int selectedLayer,
        boolean[] layerVisibility,
        int imageWidth, int imageHeight,
        String queryEnvelope,
        String queryString
        )
        throws java.io.IOException
     {
        generateResponsePage(
            request,
            response,
            serviceName,
            cmd,
            extent,
            selectedLayer,
            layerVisibility,
            imageWidth,
            imageHeight,
            queryEnvelope,
            queryString,
            null,
            0d, null, -1);
     }

    private void generateResponsePage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        String cmd,
        String extent,
        int selectedLayer,
        boolean[] layerVisibility,
        int imageWidth, int imageHeight,
        String queryEnvelope,
        String queryString,
        String findString,
        double bufferDistance,
        String bufferUnit,
        int targetLayer)
        throws java.io.IOException
     {
        if (DEBUG) System.out.println("BaseMOJMapper.getResponse() " + (extent != null && imageWidth > 0 && imageHeight > 0));
        if (DEBUG) System.out.println("BaseMOJMapper.getResponse() " + extent);

        // generate a map image here
        if (extent != null && imageWidth > 0 && imageHeight > 0) {
            if (DEBUG) System.out.println("BaseMOJMapper: new extent=" + extent);
            if (DEBUG) System.out.println("BaseMOJMapper: image size=" + imageWidth + " " + imageHeight);
            if (DEBUG) System.out.println("BaseMOJMapper: active layer index=" + selectedLayer);

            MapResponse mapresponse =
                getResponse(
                    serviceName,
                    imageWidth,
                    imageHeight,
                    extent,
                    layerVisibility,
                    selectedLayer,
                    queryEnvelope,
                    queryString,
                    findString,
                    bufferDistance,
                    bufferUnit,
                    targetLayer);

            if (DEBUG) System.out.println("BaseMOJMapper: new extent=" + mapresponse);

            // get file name and generate URL
            String filename = mapresponse.getImageFile();
            if (DEBUG) System.out.println("BaseMOJMapper: file name=>" + filename + " file separator=" + java.io.File.separator);

            if (filename.indexOf("://") > -1)
                filename = filename.substring(filename.lastIndexOf("/") +1);
            else {
                if (filename.lastIndexOf(java.io.File.separator) > -1)
                    filename = filename.substring(filename.lastIndexOf(java.io.File.separator)+1);
            }
            String url = (String)_properties.get("OutputURL") + "/" + filename;
            if (DEBUG) System.out.println("BaseMOJMapper: doGetPost() url=" + url);

            // get active layer
            int selectedLayerIndex = mapresponse.getSelectedLayerIndex();
            if (DEBUG) System.out.println("BaseMOJMapper: doGetPost() active layer=" + selectedLayer);

            // get extent
            String newextent = mapresponse.getMapExtent();
            if (DEBUG) System.out.println("BaseMOJMapper: doGetPost() extent=" + newextent);

            // set layer names and visibilities
            if (DEBUG) System.out.println("BaseMOJMapper: calling setLayerList() ... envelope=" + newextent + " " + Thread.currentThread());
            request.setAttribute("layer.visible.list", mapresponse.getLayerVisibilities());
            request.setAttribute("layer.name.list", mapresponse.getLayerNames());

            redirectToMapPage(request, response, serviceName, selectedLayerIndex, cmd, url, newextent, imageWidth, imageHeight);
            if (DEBUG) System.out.println("BaseMOJMapper: doGetPost() url=" + url + " extent=" +  newextent);
        }
    }

    private void generateFindResponsePage(
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        String cmd,
        String extent,
        int selectedLayer,
        boolean[] layerVisibility,
        int imageWidth, int imageHeight,
        String findString)
        throws java.io.IOException
     {
        // generate a map image here
        if (extent != null && imageWidth > 0 && imageHeight > 0) {
            if (DEBUG) System.out.println("BaseMOJMapper: new extent=" + extent);
            if (DEBUG) System.out.println("BaseMOJMapper: image size=" + imageWidth + " " + imageHeight);
            if (DEBUG) System.out.println("BaseMOJMapper: layer vis list=" + layerVisibility);
            if (DEBUG) System.out.println("BaseMOJMapper: active layer index=" + selectedLayer);

            MapResponse mapresponse = null;
            try {
                mapresponse =
                    getResponse(serviceName, imageWidth, imageHeight, extent, layerVisibility,
                            selectedLayer, findString);
            } catch (Exception ex) {
                //ex.printStackTrace();
                throw new java.io.IOException(ex.getLocalizedMessage());
            }
            if (DEBUG) System.out.println("generateFindResponsePage() results=" + mapresponse);

            // get file name and generate URL
            String filename = mapresponse.getImageFile();
            if (filename.lastIndexOf(java.io.File.separator) > -1)
                filename = filename.substring(filename.lastIndexOf(java.io.File.separator)+1);

            String url = (String)_properties.get("OutputURL") + "/" + filename;
            if (DEBUG) System.out.println("BaseMOJMapper: generateFindResponsePage() url=" + url);

            // get active layer
            int selectedLayerIndex = mapresponse.getSelectedLayerIndex();
            if (DEBUG) System.out.println("BaseMOJMapper: generateFindResponsePage() active layer=" + selectedLayer);

            // get extent
            String newextent = mapresponse.getMapExtent();
            if (DEBUG) System.out.println("BaseMOJMapper: generateFindResponsePage() extent=" + newextent);

            // set layer names and visibilities
            if (DEBUG) System.out.println("BaseMOJMapper: calling setLayerList() ... envelope=" + newextent + " " + Thread.currentThread());
            request.setAttribute("layer.visible.list", mapresponse.getLayerVisibilities());
            request.setAttribute("layer.name.list", mapresponse.getLayerNames());

            redirectToMapPage(request, response, serviceName, selectedLayerIndex, cmd, url, newextent, imageWidth, imageHeight);
            if (DEBUG) System.out.println("BaseMOJMapper: generateFindResponsePage() url=" + url + " extent=" +  newextent);
        }
    }

    private void redirectToMapPage (
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        int selectedLayer,
        String cmd,
        String url,
        String extent,
        int imageWidth,
        int imageHeight)
        throws java.io.IOException
    {
        try {

            if (DEBUG) System.out.println("BaseMOJMapper.redirectToMapPage() extent=" + extent);

            StringTokenizer st = new StringTokenizer(extent);
            if (st.countTokens()!=4)
                throw new java.io.IOException("The map extent is not valid.");

            String minx = st.nextToken();
            String miny = st.nextToken();
            String maxx = st.nextToken();
            String maxy = st.nextToken();

            RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("MapPage"));
            request.setAttribute("active", String.valueOf(selectedLayer));
            request.setAttribute("image.url", url);
            request.setAttribute("cmd", cmd);
            request.setAttribute("service.name", serviceName);
            request.setAttribute("min.x", minx);
            request.setAttribute("min.y", miny);
            request.setAttribute("max.x", maxx);
            request.setAttribute("max.y", maxy);
            request.setAttribute("image.width", String.valueOf(imageWidth));
            request.setAttribute("image.height", String.valueOf(imageHeight));
            rd.forward(request, response);
        } catch (javax.servlet.ServletException ex) {
            ex.printStackTrace();
            throw new java.io.IOException(ex.getMessage());
        }
    }

    private void redirectToSelectPage (
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        int selectedLayer,
        String cmd,
        double selectminx,
        double selectminy,
        double selectmaxx,
        double selectmaxy,
        String queryString
    )
        throws java.io.IOException
    {
        try {
            RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("SelectPage"));
            request.setAttribute("active", String.valueOf(selectedLayer));
            request.setAttribute("cmd", cmd);
            request.setAttribute("service.name", serviceName);
            request.setAttribute("select.minx", _nf.format(selectminx));
            request.setAttribute("select.miny", _nf.format(selectminy));
            request.setAttribute("select.maxx", _nf.format(selectmaxx));
            request.setAttribute("select.maxy", _nf.format(selectmaxy));
            request.setAttribute("query.string", queryString);
            LayerInfo layerinfo = getLayerInfo(serviceName, selectedLayer);
            request.setAttribute("layer.name", layerinfo.getName());
            rd.forward(request, response);

            if (DEBUG) System.out.println("BaseMOJMapper.redirectToSelectPage() select minx=" + selectminx + " miny=" + selectminy + " maxx=" + selectmaxx + " maxy=" + selectmaxy + " String of minx=>" + String.valueOf(selectminx) + " format of minx=>" + _nf.format(selectminx));

        } catch (javax.servlet.ServletException ex) {
            ex.printStackTrace();
        }
    }

    private void redirectToBufferSelectPage (
        HttpServletRequest request,
        HttpServletResponse response,
        String serviceName,
        int selectedLayer,
        String cmd,
        double selectminx,
        double selectminy,
        double selectmaxx,
        double selectmaxy,
        String queryString,
        int targetLayer
    )
        throws java.io.IOException
    {
        try {
            RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("SelectPage"));
            request.setAttribute("active", String.valueOf(selectedLayer));
            request.setAttribute("cmd", cmd);
            request.setAttribute("service.name", serviceName);
            request.setAttribute("select.minx", _nf.format(selectminx));
            request.setAttribute("select.miny", _nf.format(selectminy));
            request.setAttribute("select.maxx", _nf.format(selectmaxx));
            request.setAttribute("select.maxy", _nf.format(selectmaxy));
            request.setAttribute("query.string", queryString);
            LayerInfo layerinfo = getLayerInfo(serviceName, targetLayer);
            request.setAttribute("layer.name", layerinfo.getName());
            rd.forward(request, response);

            if (DEBUG) System.out.println("BaseMOJMapper.redirectToSelectPage() select minx=" + selectminx + " miny=" + selectminy + " maxx=" + selectmaxx + " maxy=" + selectmaxy + " String of minx=>" + String.valueOf(selectminx) + " format of minx=>" + _nf.format(selectminx));

        } catch (javax.servlet.ServletException ex) {
            ex.printStackTrace();
        }
    }

    private String getFullMapExtent(String serviceName)
    {
        try {
            String results = getFullExtent(serviceName);
            return results;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void select(String serviceName, String layerindex, String selectExtent, HttpServletRequest request)
    {
        try {
            int index = 0;
            index = Integer.valueOf(layerindex).intValue();
            Feature[] results = select(serviceName, index, selectExtent, null);
            request.setAttribute("identify.results", results);
            if (DEBUG) System.out.println("identify() results=" + results);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bufferSelect(
        String serviceName, int selectedLayerIndex,
        String queryEnvelope, String queryString, String findString, HttpServletRequest request,
        double bufferDistance, String bufferUnit, int targetLayer)
    {
        try {
            Feature[] results = select(serviceName, selectedLayerIndex, queryEnvelope, queryString, findString, bufferDistance, bufferUnit, targetLayer);
            request.setAttribute("identify.results", results);
            if (DEBUG) System.out.println("identify() results=" + results);
        } catch (Exception ex) {
            ex.printStackTrace();
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
     * @param findString the find string
     * @param mapTitle the map title string text
     */
    abstract public String getLayoutImage(
            String serviceName,
            int width, int height,
            String extent,
            boolean[] layerVisibility,
            int selectedLayerIndex,
            String findString,
            String mapTitle);

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
     * @param mapTitle the map title text
     */
    abstract public String getLayoutImage(
            String serviceName,
            int width, int height,
            String extent,
            boolean[] layerVisibility,
            int selectedLayerIndex,
            String queryEnvelope,
            String queryString,
            String mapTitle);

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
    abstract public String getLayoutImage(
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
            String mapTitle);

    private void generateLayoutPrintPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String serviceName,
            String extent,
            int selectedLayer,
            boolean[] layerVisibility,
            int width, int height,
            String queryEnvelope,
            String queryString,
            double bufferDistance,
            String bufferUnit,
            int targetLayer,
            String mapTitle)
            throws java.io.IOException
    {
        try {
            String filename = null;
            if (bufferDistance==0d)
                filename = getLayoutImage(serviceName, width, height, extent, layerVisibility, selectedLayer, queryEnvelope, queryString, mapTitle);
            else
                filename = getLayoutImage(serviceName, width, height, extent, layerVisibility, selectedLayer, queryEnvelope, queryString, bufferDistance, bufferUnit, targetLayer, mapTitle);

            if (DEBUG) System.out.println("generatePrintPage: file name=>" + filename + " file separator=" + java.io.File.separator);
            if (filename.lastIndexOf(java.io.File.separator) > -1)
                filename = filename.substring(filename.lastIndexOf(java.io.File.separator)+1);

            String url = (String)_properties.get("OutputURL") + "/" + filename;

            if (DEBUG) System.out.println("generatePrintPage: doGetPost() url=" + url);
            request.setAttribute("image.width", String.valueOf(width));
            request.setAttribute("image.height", String.valueOf(height));
            request.setAttribute("image.url", url);
            RequestDispatcher rd = _servletContext.getRequestDispatcher((String)_properties.get("PrintPage"));
            rd.forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class Parameters {
        //private HttpServletRequest request;

        // service name
        String serviceName = null;
        String queryString = null;
        String findString = null;
        String bufferUnit = null;
        String active = null;
        String sampleField = null;
        String optionCmd = null;
        String mapTitle = null;

        Envelope envelope=null;
        int clickx=0, clicky=0;
        int imageWidth=0, imageHeight=0, targetLayer=0;
        int activeLayerIndex = 0;
        double mapx=0d, mapy=0d, tolerance=0d, bufferDistance=0d;
        double dx=0d, dy=0d;
        double xp=0d, yp=0d;
        double minx=0d, miny=0d, maxx=0d, maxy=0d;
        double selectminx=0d, selectminy=0d, selectmaxx=0d, selectmaxy=0d;
        boolean validEnvelope=false, validImageSize=false;
        boolean validClickXY=false, validMapXY=false, validSelectMapXY=false;
        boolean validTargetLayer=false, validBufferDistance=false;
        boolean validQueryString = false;
        boolean validFindString = false;


        public Parameters(HttpServletRequest request) {
            //this.request = request;

            // service name
            serviceName = request.getParameter("ServiceName");
            active = request.getParameter("Active");
            queryString = request.getParameter("QueryString");
            findString = request.getParameter("FindString");
            bufferUnit = request.getParameter("BufferUnit");
            sampleField = request.getParameter("SampleField");
            optionCmd = request.getParameter("OptionCmd");
            mapTitle = request.getParameter("MapTitle");
            if (mapTitle == null) mapTitle = "A Map";

            // set default tolerance
            tolerance = 1d/10000d;

            // get first set of parameters
            String MinX = request.getParameter("MinX");
            String MinY = request.getParameter("MinY");
            String MaxX = request.getParameter("MaxX");
            String MaxY = request.getParameter("MaxY");
            String Width = request.getParameter("Width");
            String Height = request.getParameter("Height");
            String MapX = request.getParameter("MapX"); // map coordinate x
            String MapY = request.getParameter("MapY");
            String ClickX = request.getParameter("ClickX"); // screen image coordinate x
            String ClickY = request.getParameter("ClickY");

            String Tolerance =request.getParameter("Tolerance");
            String Extent = MinX + " " + MinY + " " + MaxX + " " + MaxY;

            String SelectMinX = request.getParameter("SelectMinX");
            String SelectMinY = request.getParameter("SelectMinY");
            String SelectMaxX = request.getParameter("SelectMaxX");
            String SelectMaxY = request.getParameter("SelectMaxY");

            String BufferDistance = request.getParameter("BufferDistance");
            String TargetLayer = request.getParameter("TargetLayer");

            if (DEBUG) System.out.println("BaseMOJMapper: 1 map extent=" + Extent + " click point=" + MapX + "," + MapY + " Active=" + active + " service name=" + serviceName);

            validQueryString =(queryString!=null && !queryString.trim().equals("") && !queryString.trim().equalsIgnoreCase("null"));
            validFindString = (findString!=null && !findString.trim().equals("") && !findString.trim().equalsIgnoreCase("null"));

            try {
                if (!Width.equals("") && !Height.equals("")) {
                    imageWidth = Integer.valueOf(Width).intValue();
                    imageHeight = Integer.valueOf(Height).intValue();
                    validImageSize = true;
                }
            } catch (Exception ex) {}
            if (DEBUG) System.out.println("BaseMOJMapper: image width=" + Width + " height=" + Height + " " + imageWidth + " " + imageHeight);

            try {
                if (!MinX.equals("") && !MinY.equals("") && !MaxX.equals("") && !MaxY.equals("")) {
                    minx = _nf.parse(MinX).doubleValue();
                    miny = _nf.parse(MinY).doubleValue();
                    maxx = _nf.parse(MaxX).doubleValue();
                    maxy = _nf.parse(MaxY).doubleValue();
                    validEnvelope = true;
                    envelope = new Envelope(minx, miny, maxx-minx, maxy-miny);
                    dx = (maxx-minx); dy = (maxy-miny);
                }
            } catch (Exception ex) {}

            try {
                    tolerance = _nf.parse(Tolerance).doubleValue();
            } catch (Exception ex) {}

            try {
                if (!ClickX.equals("") && !ClickY.equals("")) {
                    clickx = Integer.valueOf(ClickX).intValue();
                    clicky = Integer.valueOf(ClickY).intValue();
                    int dcx = clickx - imageWidth/2, dcy = clicky - imageHeight/2;
                    xp = (double)dcx/(double)imageWidth; yp = (double)dcy/(double)imageHeight;
                    validClickXY = true;
                }
            } catch (Exception ex) {}

            try {
                if (!MapX.equals("") && !MapY.equals(""))
                {
                    mapx = _nf.parse(MapX).doubleValue();
                    mapy = _nf.parse(MapY).doubleValue();
                    validMapXY = true;
                }
            } catch (Exception ex) {}

            try {
                activeLayerIndex = Integer.valueOf(active).intValue();
            }
            catch (Exception ex){}

            try {
                if (!SelectMinX.equals("") && !SelectMinY.equals("") && !SelectMaxX.equals("") && !SelectMaxY.equals("")) {
                    selectminx = _nf.parse(SelectMinX).doubleValue();
                    selectminy = _nf.parse(SelectMinY).doubleValue();
                    selectmaxx = _nf.parse(SelectMaxX).doubleValue();
                    selectmaxy = _nf.parse(SelectMaxY).doubleValue();
                    validSelectMapXY = true;
                }
            } catch (Exception ex) {}

            try {
                if (!BufferDistance.equals(""))  {
                    bufferDistance = _nf.parse(BufferDistance).doubleValue();
                    validBufferDistance = true;
                }
            } catch (Exception ex) {}

            try {
                if (!TargetLayer.equals(""))  {
                    targetLayer = Integer.valueOf(TargetLayer).intValue();
                    validTargetLayer = true;
                }
            } catch (Exception ex) {}
            if (DEBUG) System.out.println("BaseMOJMapper: 2 map extent=" + minx + " " + miny + " " + maxx + " " + maxy + " active=" + activeLayerIndex);

            if (imageWidth == 0 || imageHeight == 0) {
                imageWidth = 400;
                imageHeight = 300;
            }
        }
    }
}
