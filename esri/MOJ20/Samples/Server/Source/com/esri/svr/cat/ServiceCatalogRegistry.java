package com.esri.svr.cat;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import com.esri.mo2.svr.map.Map;
import com.esri.svr.cmn.ServiceInfo;

/**
 * A ServiceCatalogRegistry provides a placeholder for all the map services loaded
 * automatically from a mapservices.xml file or added by users through its APIs.
 */
public class ServiceCatalogRegistry {

    private static java.util.HashMap _serviceList = new java.util.HashMap();

    /**
     * Default constructor
     */
    private ServiceCatalogRegistry(){}


    /**
     * Get all services in this service catalog
     * @return  an array of ServiceInfo
     */
    public static ServiceInfo[] getAllServices() {
        ServiceInfo[] services = new ServiceInfo[_serviceList.values().size()];
        java.util.Iterator it = _serviceList.values().iterator();
        int k=0;
        while (it.hasNext()) {
            ServiceCatalog catalog = (ServiceCatalog)it.next();
            ServiceInfo info = new ServiceInfo();

            info.setAxlFileName(catalog.getAxlFileName());
            info.setConfig(catalog.getConfig());
            info.setImageFormat(catalog.getImageFormat());
            info.setImageURLPrefix(catalog.getImageURLPrefix());
            info.setOutputDir(catalog.getOutputDir());
            info.setOutputURL(catalog.getOutputUrl());
            info.setServiceName(catalog.getServiceName());
            info.setServiceType(catalog.getServiceType());
            info.setServiceURL(catalog.getServiceURL());

            services[k++] = info;
        }

        //debug
        for (int i=0; i<services.length; i++)
          System.out.println("ServiceCatalogRegistry.getAllServices() i=" + i + " name=" + services[i].getServiceName() + " url=" + services[i].getServiceURL());

        return services;
    }



    /**
     * Add map services into the service catalog through an input stream such as file input stream
     * @param is the input stream containing an XML document that describes the services
     */
    public static void addServices(java.io.InputStream is) {
        //parse the services xml document
        ServiceXMLHandler handler = new ServiceXMLHandler();
        try {
            javax.xml.parsers.SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);

            InputSource isrc = new InputSource(is);
            reader.parse(isrc);

            java.util.ArrayList list = handler.getServiceCatalogs();
            for (int i=0; i<list.size(); i++) addServiceCatalog((ServiceCatalog)list.get(i));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add an individual service to the service catalog
     * @param service the service name
     */
    public static void addServiceCatalog(ServiceCatalog service) {
        if (service != null)
            _serviceList.put(service.getServiceName(), service);
    }

    /**
     * Remove a service catalog from the service catalog
     * @param serviceName  the service name
     */
    public static void removeServiceCatalog(String serviceName) {
        Object obj = _serviceList.get(serviceName);
        if (obj != null)
            _serviceList.remove(obj);
    }

    /**
     * Get a service catalog by its name
     * @param serviceName the service name
     * @return a ServiceCatalog object
     */
    public static ServiceCatalog getServiceCatalog(String serviceName) {
        return (ServiceCatalog)_serviceList.get(serviceName);
    }

    // load all the default services defined in the mapservices.xml file if there is one
    static {
        try {
            java.io.InputStream fis = ServiceCatalogRegistry.class.getResourceAsStream("/mapservices.xml");
            if (fis != null)  addServices(fis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Adds layers to a given map from given service name
     * @param serviceName the service name
     * @param map map object
     */
    public static void addLayersToMap(String serviceName, Map map) {
        if (_serviceList.get(serviceName) != null && map != null) {
            ServiceCatalog sc = (ServiceCatalog)_serviceList.get(serviceName);
            com.esri.mo2.map.dpy.Layerset lset = sc.getLayers();

            System.out.println("ServiceCatalogRegistry:addLayersToMap() # of layers=" + lset.size() + " service Name=" + serviceName);
            for (int i=0; i<lset.size(); i++) {
                map.getLayerset().addLayer(lset.layerAt(i));
            }

            // set map units here
            map.setMapUnit(sc.getLayers().getDisplayManager().getMapUnit());

            // set coordinate system
            map.setCoordinateSystem(sc.getCoordinateSystem());
        }
    }

//
// unit testing
//
    public static void main(String[] args) {
        try {
            Map map = new Map();
            ServiceCatalogRegistry sc = new ServiceCatalogRegistry();
            ServiceCatalogRegistry.addLayersToMap("world2", map);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}