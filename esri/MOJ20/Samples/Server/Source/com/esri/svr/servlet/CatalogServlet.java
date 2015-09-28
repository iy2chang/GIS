package com.esri.svr.servlet;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */
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
import javax.ejb.EJBHome;

import com.esri.svr.cmn.ServiceInfo;
import com.esri.svr.ejb.Catalog;
import com.esri.svr.ejb.CatalogHome;

public class CatalogServlet extends HttpServlet
{
    private static CatalogHome _catalogHome;
    private static Catalog _catalog;
    private static java.util.HashMap _properties = new java.util.HashMap(5);
    private static InitialContext _jndiContext;

    /**
     * Override the HTTPServlet's init method
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
      super.init(config);
      //read properties
      try {
        java.io.InputStream fis = this.getClass().getResourceAsStream("/catalog.properties");
        System.out.println("CatalogServlet.init() input stream is null? " +(fis ==null) );

        if (fis !=null) {
          java.util.Properties p = new java.util.Properties();
          p.load(fis);
          _properties.put("ListServices", p.getProperty("ListServices"));
          _properties.put("AddService", p.getProperty("AddService"));
          _properties.put("RemoveService", p.getProperty("RemoveService"));
          _properties.put("RefreshService", p.getProperty("RefreshService"));
          _properties.put("CatalogInfoError", p.getProperty("CatalogInfoError"));
          _properties.put("ShowDetails", p.getProperty("ShowDetails"));
          _properties.put("InitialContextFactory", p.getProperty("InitialContextFactory"));
          _properties.put("ContextProviderURL", p.getProperty("ContextProviderURL"));
          _properties.put("ContextURLPKGPrefixes", p.getProperty("ContextURLPKGPrefixes"));
        } else {
          System.out.println("Cannot read prop file");
        }

        System.out.println("ListServices=>" + _properties.get("ListServices"));
        System.out.println("AddService=>" + _properties.get("AddService"));
        System.out.println("RemoveService=>" + _properties.get("RemoveService"));
        System.out.println("RefreshService=>" + _properties.get("RefreshService"));
        System.out.println("CatalogInfoError=>" + _properties.get("CatalogInfoError"));
        System.out.println("ShowDetails=>" + _properties.get("ShowDetails"));
        System.out.println("InitialContextFactory=>" + _properties.get("InitialContextFactory"));
        System.out.println("ContextProviderURL=>" + _properties.get("ContextProviderURL"));
        System.out.println("ContextURLPKGPrefixes=>" + _properties.get("ContextURLPKGPrefixes"));
      } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Error in reading property file");
      }

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
         _jndiContext = new InitialContext(env);

         System.out.println(" #######  looking for ejb/Catalog stateless session bean...");

         // get MapServiceIIHome
         Object ref  = _jndiContext.lookup("ejb/Catalog");

         System.err.println(" #######  " + ref.getClass().getName() + " " + (ref instanceof CatalogHome) + " " + (ref instanceof EJBHome));

         _catalogHome = (CatalogHome) PortableRemoteObject.narrow(ref, CatalogHome.class);
         _catalog = _catalogHome.create();

          /*
          try {
            Object obj = _jndiContext.lookup("java:comp/env/jdbc/OracleDS");
          } catch (Exception ex) {
              System.out.println("java:comp/env/jdbc/OracleDS name not found! (tested in CatalogServlet class)");
              ex.printStackTrace();
          } */
      }
      catch(Exception e)
      {
         e.printStackTrace();
         throw new ServletException("Failed to lookup Catalog", e);
      }
    }

    /**
     * Override HTTPServlet's doGet method
     * @param request the HTTP request
     * @param response the HTTP response
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
     * @param response the HTTP response
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
        throws ServletException, java.io.IOException
    {
        System.out.println("CatalogServlet: doGetPost() " + MultipartRequest.isMultipartData(request));
        if (MultipartRequest.isMultipartData(request))
            doGetPostMultipart(request, response);
        else
            doGetPostNormal(request, response);
    }

    private void doGetPostNormal(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        // cmd=Ping, ListAllServices, AddService, RemoveService or RefreshService
        String cmd = request.getParameter("cmd");
        System.out.println("CatalogServlet: doGetPostNormal() cmd=" + cmd);

        if (cmd == null) {
          System.out.println("cmd not found");
          writeResponse(request, response, "no cmd found");
          return;
        }

        else if (cmd.equalsIgnoreCase("Ping")) {
          System.out.println("ping ");
          writeResponse(request, response, "CatalogServlet version 1.0");
          return;
        }

        System.out.println("CatalogServlet: 2 doGetPostNormal() cmd=" + cmd);
        try {
            // list all services
            if (cmd.equalsIgnoreCase("ListServices")) {
                Object obj = _catalog.getAllServices();
                System.out.println("CatalogServlet: 3 doGetPostNormal() cmd=" + cmd + " " + obj.getClass().getName());

                ServiceInfo[] list = _catalog.getAllServices();
                System.out.println("ListServices # =" + list.length);
                for (int i=0; i<list.length; i++)
                    System.out.println("ListServices " + list[i].getServiceName());
                listServices(request, response, list);

            // show details
            } else if (cmd.equalsIgnoreCase("ShowDetails")) {
                String name = request.getParameter("name");
                if (name == null)
                    writeResponse(request, response, "service name is required for showing details");
                else {
                    getDetails(request, response,  name);
                }
            // add a service
            } else if (cmd.equalsIgnoreCase("AddService")) {
                String name = request.getParameter("name");
                String type = request.getParameter("type");
                String format = request.getParameter("image_format");
                String output_dir = request.getParameter("output_dir");
                String output_url = request.getParameter("output_url");
                String config = request.getParameter("config");
                _catalog.addService(name, type, output_dir, output_url, format, config);
                writeResponse(request, response, "The service '" + name + "' has been added successfully.");

            // remove a service
            } else if (cmd.equalsIgnoreCase("RemoveService")) {
                String name = request.getParameter("name");
                _catalog.removeService(name);
                writeResponse(request, response, "The service '" + name + "' has been removed successfully.");

            // refresh a service
            } else if (cmd.equalsIgnoreCase("RefreshService")) {
                String name = request.getParameter("name");
                String old_name = request.getParameter("old_name");
                String type = request.getParameter("type");
                String format = request.getParameter("image_format");
                String output_dir = request.getParameter("output_dir");
                String output_url = request.getParameter("output_url");
                String config = request.getParameter("config");

                System.out.println("old_name=" + old_name);

                _catalog.refreshService(name, old_name, type, output_dir, output_url, format, config);
                writeResponse(request, response, "The service '" + name + "' has been refreshed successfully.");

            // else command not found
            } else {
                writeResponse(request, response, "Command '"+ cmd + "' not found");
            }
        } catch (Exception ex) {
            writeResponse(request, response,
                "<b>Please read carefully.</b> <br>" +
                "The Exception is being thrown and the embedded info contains very important clue about what went wrong. <br>" +
                ex.getMessage());
        }
    }

    private void doGetPostMultipart(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        System.out.println("CatalogServlet: doGetPostMultipart() " + request.getContentType());

        MultipartRequest req = new MultipartRequest(request);
        System.out.println("File content=>" + req.getFileContent());

        // cmd=Ping, ListAllServices, AddService, RemoveService or RefreshService
        String cmd = req.getParameter("cmd");
        System.out.println("CatalogServlet: doGetPost() cmd=" + cmd);

        if (cmd == null) {
          System.out.println("cmd not found");
          writeResponse(request, response, "no cmd found");
          return;
        }

        else if (cmd.equalsIgnoreCase("Ping")) {
          System.out.println("ping ");
          writeResponse(request, response, "CatalogServlet version 1.0");
          return;
        }

        System.out.println("CatalogServlet: 2 doGetPostMultipart() cmd=" + cmd);
        try {
            // list all services
            if (cmd.equalsIgnoreCase("ListServices")) {
                System.out.println("CatalogServlet: 3 doGetPostMultipart() cmd=" + cmd);
                ServiceInfo[] list = _catalog.getAllServices();
                System.out.println("ListServices # =" + list.length);
                for (int i=0; i<list.length; i++)
                    System.out.println("ListServices " + list[i].getServiceName());
                listServices(request, response, list);

            // show details
            } else if (cmd.equalsIgnoreCase("ShowDetails")) {
                String name = request.getParameter("name");
                if (name == null)
                    writeResponse(request, response, "service name is required for showing details");
                else {
                    getDetails(request, response, name);
                }
            // add a service
            } else if (cmd.equalsIgnoreCase("AddService")) {
                String name = req.getParameter("name");
                String type = req.getParameter("type");
                String format = req.getParameter("image_format");
                String output_dir = req.getParameter("output_dir");
                String output_url = req.getParameter("output_url");
                String config = req.getParameter("config");
                if (config == null || config.equals(""))
                    config = req.getFileContent();
                _catalog.addService(name, type, output_dir, output_url, format, config);
                writeResponse(request, response, "The service '" + name + "' has been added successfully.");

            // remove a service
            } else if (cmd.equalsIgnoreCase("RemoveService")) {
                String name = req.getParameter("name");
                _catalog.removeService(name);
                writeResponse(request, response, "The service '" + name + "' has been removed successfully.");

            // refresh a service
            } else if (cmd.equalsIgnoreCase("RefreshService")) {
                String name = req.getParameter("name");
                String old_name = req.getParameter("old_name");
                String type = req.getParameter("type");
                String format = req.getParameter("image_format");
                String output_dir = req.getParameter("output_dir");
                String output_url = req.getParameter("output_url");
                String config = req.getParameter("config");

                System.out.println("old_name=" + old_name);

                _catalog.refreshService(name, old_name, type, output_dir, output_url, format, config);
                writeResponse(request, response, "The service '" + name + "' has been refreshed successfully.");

            // else command not found
            } else {
                writeResponse(request, response, "Command '"+ cmd + "' not found");
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            //throw new java.io.IOException(ex.getMessage());
            writeResponse(request, response,
                "<b>Please read carefully.</b> <br>" +
                "The Exception is being thrown and the embedded info contains very important clue about what went wrong. <br>" +
                ex.getMessage());
        }
    }

    private void writeResponse(HttpServletRequest request, HttpServletResponse response, String message)
        throws java.io.IOException, javax.servlet.ServletException
    {
        ServletContext sc =  getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher((String)_properties.get("CatalogInfoError"));
        //System.out.println("CatalogServlet:writeReponse() " + message);
        request.setAttribute("message", message);
        rd.forward(request, response);
    }

    private void listServices(
        HttpServletRequest request,
        HttpServletResponse response,
        ServiceInfo[] list)
        throws java.io.IOException, javax.servlet.ServletException
    {
        ServletContext sc =  getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher((String)_properties.get("ListServices"));
        request.setAttribute("servicelist", list);
        rd.forward(request, response);
    }

    private void getDetails(
        HttpServletRequest request,
        HttpServletResponse response,
        String name)
        throws java.io.IOException, javax.servlet.ServletException, javax.ejb.FinderException
    {
        String[] details = _catalog.getServiceInfo(name);
        ServletContext sc =  getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher((String)_properties.get("ShowDetails"));
        request.setAttribute("servicedetails", details);
        rd.forward(request, response);
    }

//
// testing code
//
    public static void main(String[] args) {
      try {
          Hashtable env = new Hashtable();
          env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
          env.put(Context.PROVIDER_URL, "localhost:1099");
          env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");

         // Get a naming context
         _jndiContext = new InitialContext(env);

         // get MapServiceIIHome
         Object ref  = _jndiContext.lookup("ejb/Catalog");
         _catalogHome = (CatalogHome) PortableRemoteObject.narrow(ref, CatalogHome.class);
         _catalog = _catalogHome.create();
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //throw new ServletException("Failed to lookup Catalog", e);
      }
    }
}