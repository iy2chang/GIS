package com.esri.svr.servlet;

import javax.naming.Context;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;

import com.esri.svr.ejb.MapServiceI;
import com.esri.svr.ejb.MapServiceIHome;
import com.esri.svr.map.EJBMapperI;


/**
 * This is a controller servlet for SimpleEJB defined by MapServiceI, MapServiceIHome and MapServiceIBean.
 */
public class EJBControllerI  extends HttpServlet
{

    // EJB server related
    private static MapServiceIHome _mapServiceHome;
    private static MapServiceI _mapService;

    private static java.util.HashMap _properties = new java.util.HashMap(5);
    private static InitialContext _jndiContext;

    /**
     * Override HTTPServlet's init method
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        loadProperties();
        createEJBInitContext();
    }

    private void createEJBInitContext()
        throws ServletException
    {
        try {
            Hashtable env = new Hashtable();
            String factory = (String)_properties.get("InitialContextFactory");
            if (factory != null && !factory.trim().equals(""))
                env.put(Context.INITIAL_CONTEXT_FACTORY, factory);

            String url = (String)_properties.get("ContextProviderURL");
            if (url != null && !url.trim().equals(""))
                env.put(Context.PROVIDER_URL, url);

            String prefix = (String)_properties.get("ContextURLPKGPrefixes");
            if ( prefix != null && !prefix.trim().equals(""))
                env.put(Context.URL_PKG_PREFIXES, prefix);

            // Get a naming context
            _jndiContext = new InitialContext(env);  //new InitialContext();

            // map service
            Object ms  = _jndiContext.lookup("ejb/MapServiceI");
            _mapServiceHome = (MapServiceIHome) PortableRemoteObject.narrow(ms, MapServiceIHome.class);
            _mapService = _mapServiceHome.create();

        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to lookup MapServiceI", e);
        }
   }

   private void loadProperties() {
        //read properties
        try {
            java.io.InputStream fis = this.getClass().getResourceAsStream("/mojejbcontroller.properties");

            if (fis !=null) {
                java.util.Properties p = new java.util.Properties();
                p.load(fis);
                _properties.put("MapPage", p.getProperty("MapPage"));
                _properties.put("PrintPage", p.getProperty("PrintPage"));
                _properties.put("SelectPage", p.getProperty("SelectPage"));
                _properties.put("QueryPage", p.getProperty("QueryPage"));
                _properties.put("ErrorPage", p.getProperty("ErrorPage"));
                _properties.put("PingPage", p.getProperty("PingPage"));
                _properties.put("ServiceListPage", p.getProperty("ServiceListPage"));
                _properties.put("OutputURL", p.getProperty("OutputURL"));
                _properties.put("UseEJBContainer", p.getProperty("UseEJBContainer"));
                _properties.put("UseServletContainer", p.getProperty("UseServletContainer"));
                _properties.put("UseSOAPContainer", p.getProperty("UseSOAPContainer"));
                _properties.put("SOAPServerURL", p.getProperty("SOAPServerURL"));
                _properties.put("SOAPServerURI", p.getProperty("SOAPServerURI"));
                _properties.put("InitialContextFactory", p.getProperty("InitialContextFactory"));
                _properties.put("ContextProviderURL", p.getProperty("ContextProviderURL"));
                _properties.put("ContextURLPKGPrefixes", p.getProperty("ContextURLPKGPrefixes"));
            } else {
                System.out.println("Cannot read prop file");
            }

            System.out.println("map page=>" + _properties.get("MapPage"));
            System.out.println("select page=>" + _properties.get("SelectPage"));
            System.out.println("error page=>" + _properties.get("ErrorPage"));
            System.out.println("ping page=>" + _properties.get("PingPage"));
            System.out.println("ServiceListPage=>" + _properties.get("ServiceListPage"));
            System.out.println("InitialContextFactory=>" + _properties.get("InitialContextFactory"));
            System.out.println("ContextProviderURL=>" + _properties.get("ContextProviderURL"));
            System.out.println("ContextURLPKGPrefixes=>" + _properties.get("ContextURLPKGPrefixes"));

      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Error in reading property file");
      }
   }

   /**
    * Override HTTPServlet's doGet method
    * @param request the HTTP request
    * @param response the HTTP reponse
    * @throws ServletException
    * @throws IOException
    */
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      doGetPost(request, response);
   }

   /**
    * Override HTTPServlet's doPost method
    * @param request the HTTP request
    * @param response the HTTP reponse
    * @throws ServletException
    * @throws IOException
    */
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      doGetPost(request, response);
   }

//
// private methods
//

    private void doGetPost(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException
    {
        String cmd = request.getParameter("Cmd");
        if (cmd == null) cmd = request.getParameter("cmd");
        System.out.println("================> EJBControllerI: doGetPost() cmd=" + cmd);

        if (cmd == null) {
            System.out.println("cmd not found");
            response.sendRedirect((String)_properties.get("ErrorPage"));
            return;
        }

        else if (cmd.equalsIgnoreCase("Ping")) {
            System.out.println("ping " + (String)_properties.get("PingPage"));
            response.sendRedirect((String)_properties.get("PingPage"));
            return;
        }

        EJBMapperI ejbMapper = new EJBMapperI(_properties, getServletContext(), _mapService);
        ejbMapper.doGetPost(request, response);

    }
}
