package com.esri.svr.ejb;

import com.esri.svr.map.BaseMapper;
import com.esri.svr.cmn.*;
import com.esri.svr.cat.ServiceXMLHandler;
import com.esri.svr.cat.ServiceCatalog;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

/**
 * This class provides implementation of MapServiceI and MapServiceIHome interfaces
 * in addition to SessionBean interface.
 */
public class MapServiceIBean implements SessionBean {

    protected BaseMapper baseMapper;
    protected SessionContext ctx;

    private java.util.HashMap _serviceList = new java.util.HashMap();
    private ServiceInfo[] _serviceInfo = new ServiceInfo[0];

//
// default constructor
//
    /**
     * The Default consutrctor that will create a server Map object
     */
    public MapServiceIBean()
        throws RemoteException
    {
        baseMapper = new BaseMapper();
        loadServiceInfo();
    }

    private void loadServiceInfo()
        throws RemoteException
    {
        try {
            java.io.InputStream fis = MapServiceIBean.class.getResourceAsStream("/META-INF/mapservices.xml");
            if (fis != null) {
                //parse the services xml document
                ServiceXMLHandler handler = new ServiceXMLHandler();
                javax.xml.parsers.SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setContentHandler(handler);

                InputSource isrc = new InputSource(fis);
                reader.parse(isrc);

                java.util.ArrayList list = handler.getServiceCatalogs();
                System.out.println("ImageServiceBean: # of services=" + list.size());

                // each bean will serve only one service, so rest of the services will be ignored
                if (list.size()>=1) {
                    _serviceInfo = new ServiceInfo[list.size()];
                    ServiceInfo info;
                    ServiceCatalog catalog;
                    for (int i=0; i<list.size(); i++) {
                        catalog = (ServiceCatalog)list.get(i);
                        _serviceList.put(catalog.getServiceName(), catalog);
                        info = new ServiceInfo();
                        info.setServiceName(catalog.getServiceName());
                        info.setServiceType(catalog.getServiceType());
                        info.setOutputDir(catalog.getOutputDir());
                        info.setOutputURL(catalog.getOutputUrl());
                        info.setImageFormat(catalog.getImageFormat());
                        info.setConfig(catalog.getConfig());
                        _serviceInfo[i] = info;
                    }
                }
            }
            if (_serviceList.size()==0)
                throw new RemoteException("No service info has been found!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
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
        /*System.out.println("1 MapServiceIBean:addLayers() before adding # of layers=>" + getMap().getLayerCount());
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
            ServiceCatalog service = (ServiceCatalog)_serviceList.get(servicename);
            com.esri.mo2.map.dpy.Layerset lset = service.getLayers();
            System.out.println("@@@ MapServiceIBean:addLayers() # of layers=" + lset.size());

            //for (int i=lset.getSize()-1; i>=0; i--) {
            for (int i=0; i<lset.size(); i++) {
              baseMapper.getMap().getLayerset().addLayer(lset.layerAt(i));
              System.out.println("i=" + i + " Layer Name=" + lset.layerAt(i).getName() + " visible=" + lset.layerAt(i).isVisible());
            }

            // set output url or directory
            String dir = service.getOutputDir();
            String url = service.getOutputUrl();
            if (url != null && !url.trim().equals(""))
                baseMapper.setOutputUrl(url);
            else if (dir != null && !dir.equals(""))
                baseMapper.setOutputDir(dir);

            // set map units here
            baseMapper.getMap().setMapUnit(service.getLayers().getDisplayManager().getMapUnit());
            // set map coordinate system
            baseMapper.getMap().setCoordinateSystem(service.getCoordinateSystem());

        } catch (Exception fex) {
            fex.printStackTrace();
        }

        System.out.println("6 ########## service name=" + servicename);

        baseMapper.setMapName(servicename.trim());
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
            ServiceCatalog service = (ServiceCatalog)_serviceList.get(serviceName);
            StringBuffer strbuf = new StringBuffer();

            strbuf.append(service.getServiceName()+",");
            strbuf.append(service.getServiceType()+",");
            strbuf.append(service.getConfig()+",");
            strbuf.append(service.getOutputDir()+",");
            strbuf.append(service.getOutputUrl()+",");
            strbuf.append(service.getImageFormat());

            return strbuf.toString();
        } catch (Exception ex) {
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
            return baseMapper.getImage(width, height, extent);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Create a MapResponse object based on the given parameters.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisList layer visibility array
     * @param selectedLayerIndex selected layer index
     * @param findString find string
     * @return a MapResponse object
     * @throws java.rmi.RemoteException
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
            return baseMapper.getResponse(width, height, extent, layerVisList, selectedLayerIndex, findString);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
     * Create a MapResponse object based on the given parameters.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisList layer visibility array
     * @param selectedLayerIndex selected layer index
     * @param queryEnvelope query envelope
     * @param queryString query string
     * @return a MapResponse object
     * @throws java.rmi.RemoteException
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
            return baseMapper.getResponse(width, height, extent, layerVisList, selectedLayerIndex, queryEnvelope, queryString);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException(ex.getMessage());
        }
    }

     /**
     * Create a MapResponse object based on the given parameters.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisList layer visibility array
     * @param selectedLayerIndex selected layer index
     * @param queryEnvelope query envelope
     * @param queryString query string
     * @param findString find string
     * @param bufferDistance buffer distance
     * @param bufferUnit buffer unit
     * @return a MapResponse object
     * @throws java.rmi.RemoteException
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
            return baseMapper.getResponse(width, height, extent, layerVisList, selectedLayerIndex, queryEnvelope, queryString, findString, bufferDistance, bufferUnit);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new java.rmi.RemoteException(ex.getMessage());
        }
    }

    /**
     * Create a MapResponse object based on the given parameters.
     * @param serviceName the service name
     * @param width map image width
     * @param height map image height
     * @param extent map extent
     * @param layerVisList layer visibility array
     * @param selectedLayerIndex selected layer index
     * @param queryEnvelope query envelope
     * @param queryString query string
     * @param findString find string
     * @param bufferDistance buffer distance
     * @param bufferUnit buffer unit
     * @param targetLayerIndex a layer to which the buffer polygon will apply a selection
     * @return a MapResponse object
     * @throws java.rmi.RemoteException
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
        return getFullMapExtent(serviceName);
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
     * Select feature(s) based on given parameters.
     * @param serviceName the service name
     * @param activeLayerIndex the selected/active layer index within the service
     * @param queryEnvelope
     * @param queryString
     * @return an array of Feature(s)
     * @throws java.rmi.RemoteException
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
     * @param serviceName service name
     * @param selectedLayerIndex selected layer index
     * @param queryEnvelope query envelope
     * @param bufferDistance buffer distance
     * @param targetLayerIndex target layer index
     * @return a vector of selected features
     * @throws java.rmi.RemoteException
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
           throws RemoteException
    {
        try {
            addLayers(serviceName);
            return baseMapper.select(selectedLayerIndex, queryEnvelope, queryString, findString, bufferDistance, bufferUnit, targetLayerIndex);
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }

    /**
      * Buffer a set of selected features for the selected/active layer with given buffering distance.
      * @param serviceName service name
      * @param selectedLayerIndex selected layer index
      * @param queryEnvelope query envelope
      * @param queryString query string
      * @param findString find string
      * @param distance buffer distance
      * @param unit buffer unit
      * @return an array of Ring(s) that constitute a polygon
      * @throws java.rmi.RemoteException
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
     * Get layer info
     * @param serviceName service name
     * @param layerName layer name
     * @return a LayerInfo object
     * @throws java.rmi.RemoteException
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
     * Get layer info
     * @param serviceName service name
     * @param layerIndex layer index
     * @return a LayerInfo object
     * @throws java.rmi.RemoteException
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

    /**
     * Get all service info
     * @return an array of ServiceInfo objects
     * @throws RemoteException
     */
    public ServiceInfo[] getAllServices()
        throws RemoteException
    {
            return _serviceInfo;
    }
//
// session bean required methods
//

    /**
     * Set session context
     * @param ctx the session bean context
     */
    public void setSessionContext(SessionContext ctx) throws javax.ejb.EJBException {
        System.out.println("MapServiceIBean:setSessionContext()");
        this.ctx = ctx;
    }

    /**
     * Remove the session bean (empty implementation)
     */
    public void ejbRemove() throws javax.ejb.EJBException {
        System.out.println("MapServiceIBean:ejbRemove()");
    }

    /**
     * Activate the session bean (empty implementation)
     */
    public void ejbActivate() throws javax.ejb.EJBException {
        System.out.println("MapServiceIBean:ejbActivate()");
    }

    /**
     * Passivate the session bean (empty implementation)
     */
    public void ejbPassivate() throws javax.ejb.EJBException {
        System.out.println("MapServiceIBean:ejbPassivate()");
    }

    /**
     * Create the session bean (empty implementation)
     */
    public void ejbCreate() throws javax.ejb.CreateException {
        System.out.println("MapServiceIBean:ejbCreate()");
    }
}
