package com.esri.svr.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import com.esri.svr.map.BaseMapper;
import com.esri.svr.map.ServletMapper;
import com.esri.svr.cat.ServiceCatalogRegistry;

/**
 * This Servlet is a controller to servlet-based mapper, i.e, JspServletViewer sample.
 */
public class ServletController extends HttpServlet
{
    private static java.util.HashMap _properties = new java.util.HashMap(5);
    private BaseMapper _mapper;

    /**
     * Override HTTPServlet's init method
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        loadProperties();
        createMapServerContext();
    }

    private void createMapServerContext() {
        _mapper = new BaseMapper();

        /*// read map services
        try {
            java.io.InputStream fis = this.getClass().getResourceAsStream("/mapservices.xml");
            if (fis ==null) {
                java.io.FileInputStream stream = new java.io.FileInputStream("mapservices.xml");
                fis = stream;
            }
            if (fis != null) {
                ServiceCatalogRegistry.addServices(fis);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error in reading map service file");
        } */
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
        System.out.println("================> ServletController: doGetPost() cmd=" + cmd);

        if (cmd == null) {
            System.out.println("cmd not found");
            response.getWriter().println("Cmd not found!");
            response.getWriter().flush();
            return;
        }

        else if (cmd.equalsIgnoreCase("Ping")) {
            System.out.println("ping " + (String)_properties.get("PingPage"));
            response.getWriter().println("ServletController 1.0");
            response.getWriter().flush();
            return;
        }

        String serviceName = request.getParameter("ServiceName");
        ServletMapper servletMapper = new ServletMapper(_properties, getServletContext(), _mapper, serviceName);
        servletMapper.doGetPost(request, response);
    }


    private void loadProperties() {
        //read properties
        try {
            java.io.InputStream fis = this.getClass().getResourceAsStream("/mojservletcontroller.properties");
            if (fis ==null) {
                java.io.FileInputStream stream = new java.io.FileInputStream("mojservletcontroller.properties");
                fis = stream;
            }

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
                _properties.put("OutputDirectory", p.getProperty("OutputDirectory"));
                _properties.put("OutputURL", p.getProperty("OutputURL"));
            } else {
                System.out.println("Cannot read prop file");
            }

            System.out.println("map page=>" + _properties.get("MapPage"));
            System.out.println("select page=>" + _properties.get("SelectPage"));
            System.out.println("error page=>" + _properties.get("ErrorPage"));
            System.out.println("ping page=>" + _properties.get("PingPage"));
            System.out.println("ServiceListPage=>" + _properties.get("ServiceListPage"));
            System.out.println("output dir=>" + _properties.get("OutputDirectory"));
      } catch (Exception ex) {
          ex.printStackTrace();
          System.out.println("Error in reading property file");
      }
   }
}
