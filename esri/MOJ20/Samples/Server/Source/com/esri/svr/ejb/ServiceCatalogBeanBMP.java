package com.esri.svr.ejb;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ESRI
 * @version 2.0
 */

import java.util.Vector;
import java.util.Collection;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ejb.FinderException;
import javax.ejb.EJBException;

/**
 * This class provides full implementations of ServiceCatalogLocal and ServiceCatalogLocalHome interfaces
 * as a BMP (Bean Managed Persistence).
 */
public class ServiceCatalogBeanBMP extends ServiceCatalogBean {
    private boolean _isModified = false;
    private boolean _isLoaded = false;

    /**
     * The default consutrctor that will call its super-class's default constructor
     * @throws java.io.IOException
     */
    public ServiceCatalogBeanBMP()
        throws java.io.IOException
    {
        super();
        System.out.println("ServiceCatalogBeanBMP: ServiceCatalogBeanBMP() ... a new service bean created by EJB container");
    }

    //
    // getter/setter of attributes
    //

    /**
     * Set the service name
     * @param serviceName the service name
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
        _isModified = true;
    }

    /**
     * Get the service name
     * @return service name
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * Set the service type
     * @param serviceType the service type
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
        _isModified = true;
    }

    /**
     * Get service type
     * @return the service type
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * Set image output format
     * @param format the image format
     */
    public void setImageFormat(String format) {
        _imageFormat = format;
        _isModified = true;
    }

    /**
     * Get the output image format
     * @return the output image format
     */
    public String getImageFormat() {
        return _imageFormat;
    }

    /**
     * Set the serivce configuration
     * @param config the service configuration
     */
    public void setConfig(String config) {
        _axlConfig = config;
        _isModified = true;
    }

    /**
     * Get the service configuration
     * @return AXL document
     */
    public String getConfig() {
        return _axlConfig;
    }

    /**
     * Get the output URL
     * @return output URL
     */
    public String getOutputUrl() {
        return _outputUrl;
    }

    /**
     * Set output URL of Samba based jCIFS
     * @param outputJCIFSUrl the output URL
     */
    public void setOutputUrl(String outputJCIFSUrl) {
        _outputUrl = outputJCIFSUrl;
        _isModified = true;
    }

    /**
     * Get output directory
     * @return output directory
     */
    public String getOutputDir() {
        return _outputDir;
    }

    /**
     * Set output directory
     * @param outputDir the output directory
     */
    public void setOutputDir(String outputDir) {
        _outputDir = outputDir;
        _isModified = true;
    }

    //
    // other methods
    //

    /**
     * Get a JDBC connection from connection pool
     * @return a JDBC connection
     * @throws Exception
     */
    public Connection getConnection() throws Exception {
        // In a production system you should use pooled connection
        // when the pooled connection is supported
        try {
            Context ctxt = new InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource)ctxt.lookup("java:comp/env/jdbc/MapServiceDS");
            return ds.getConnection();
        } catch (Exception ex) {
            System.out.println("failed to get a connection wihtout user name and password. Try again with predefined username and password.");
            //ex.printStackTrace();

            // try again with user name and password
            // for SQL Server in WebSphere 5
            try {
                Context ctxt = new InitialContext();
                javax.sql.DataSource ds = (javax.sql.DataSource)ctxt.lookup("java:comp/env/jdbc/MapServiceDS");
                return ds.getConnection("mojejb","mojejb");
            } catch (Exception e) {
                System.out.println("failed to get a connection in second try");
                ex.printStackTrace();
            }

            throw ex;
        }
    }

    //
    // EJB-related methods
    //

    /**
     * Create a service catalog bean instance with given parameters
     * @param serviceName the service name
     * @param serviceType the service type
     * @param output_dir the output directory
     * @param output_url the output URL
     * @param image_format the output image format
     * @param config the service configuration
     * @return service name as primary key
     */
    public String ejbCreate(
        String serviceName,
        String serviceType,
        String output_dir,
        String output_url,
        String image_format,
        String config) throws javax.ejb.CreateException
    {
        System.out.println("ServiceCatalogBeanBMP:ejbCreate(String, ...) called. serviceName='" + serviceName + "'");
        //System.out.println("ServiceCatalogBeanBMP:ejbCreate(String, ...) called. serviceType='" + serviceType + "'");
        //System.out.println("ServiceCatalogBeanBMP:ejbCreate(String, ...) called. output_url=" + output_url);
        //System.out.println("ServiceCatalogBeanBMP:ejbCreate(String, ...) called. output_dir=" + output_dir);
        //System.out.println("ServiceCatalogBeanBMP:ejbCreate(String, ...) called. image_format=" + image_format);
        //System.out.println("ServiceCatalogBeanBMP:ejbCreate(String, ...) called. config='" + config + "'");

        super.ejbCreate(serviceName, serviceType, output_dir, output_url, image_format, config);

        PreparedStatement pstmt = null;
        Connection conn = null;

        // in this way, the backslash problem will be a non-issue.
        //String sql = "INSERT INTO mapservice (name, type, config, output_dir, output_url, image_format) VALUES (?, ?, ?, ?, ?, ?)";
        String sql = "INSERT INTO mapservice VALUES (?, ?, ?, ?, ?, ?)";

        if (output_dir == null) output_dir="";
        if (output_url == null) output_url="";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serviceName);
            pstmt.setString(2, serviceType);
            pstmt.setString(3, config);
            pstmt.setString(4, output_dir);
            pstmt.setString(5, output_url);
            pstmt.setString(6, image_format);
            pstmt.execute();
        } catch (Exception ex) {
            throw new EJBException(ex.toString());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        return serviceName;
    }

    /**
     * Remove a service from the system
     */
    public void ejbRemove() {
        System.out.println("ServiceCatalogBeanBMP:ejbRemove() called.");

        String servicename = (String)ctx.getPrimaryKey();
        ejbRemove(servicename);
    }

    /**
     * Remove a given service
     * @param serviceName the service to be removed
     */
    public void ejbRemove(String serviceName) {
        if (serviceName==null)
            throw new EJBException("service name can't be null.");

        String sql = "DELETE FROM mapservice WHERE name='" + serviceName + "'";

        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.execute();
        } catch (Exception ex) {
            throw new EJBException(ex.toString());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }
    }

    /**
     * Load the service from database to the EJB container
     */
    public void ejbLoad() {
        String servicename = (String)ctx.getPrimaryKey();

        boolean isSameService = ((_serviceName!=null) && (servicename.compareTo(_serviceName)==0));
        if (isSameService && _isLoaded && !_isModified) return;

        System.out.println("ServiceCatalogBeanBMP: ejbLoad() called. is the same service=" + isSameService + " isLoaded=" + _isLoaded + " isModified=" + _isModified);

        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM mapservice WHERE name = '" + servicename + "'");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            _serviceName = rs.getString("name");
            _serviceType = rs.getString("type");
            _axlConfig = getAXL(rs.getCharacterStream("config")); //getAXL(rs.getClob("config")); //rs.getString("config");
            _outputUrl = rs.getString("output_url");
            _outputDir = rs.getString("output_dir");
            _imageFormat = rs.getString("image_format");

            _isLoaded = true;
            _isModified = false;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EJBException(ex.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        super.ejbLoad();
    }

    private static String getAXL(java.io.Reader reader) throws Exception {
        StringBuffer strbuf = new StringBuffer();
        char[] buf = new char[1024];
        int n = reader.read(buf);
        while (n > 0) {
            strbuf.append(buf, 0, n);
            n = reader.read(buf);
        }
        return strbuf.toString();
    }

    /**
     * Store the service catalog bean
     */
    public void ejbStore() {
        if (!_isModified) return;

        System.out.println("ServiceCatalogBeanBMP: ejbStore() called. name=" + ctx.getPrimaryKey());
        System.out.println("ServiceCatalogBeanBMP: ejbStore() called. type=" + _serviceType);
        System.out.println("ServiceCatalogBeanBMP: ejbStore() called. output_dir=" + _outputDir);
        System.out.println("ServiceCatalogBeanBMP: ejbStore() called. output_url=" + _outputUrl);
        System.out.println("ServiceCatalogBeanBMP: ejbStore() called. image_format=" + _imageFormat);
        //System.out.println("ServiceCatalogBeanBMP: ejbStore() called. " + getConfig());

        String servicename = (String)ctx.getPrimaryKey();
        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(
                "UPDATE mapservice SET type = ?, config = ?, " +
                "output_dir = ?, output_url = ?, image_format = ? " +
                "WHERE name = '" + servicename + "'");
            pstmt.setString(1, _serviceType);
            pstmt.setString(2, _axlConfig);
            pstmt.setString(3, _outputDir);
            pstmt.setString(4, _outputUrl);
            pstmt.setString(5, _imageFormat);
            pstmt.executeUpdate();
            _isModified = false;
        } catch (Exception ex) {
            throw new EJBException(ex.toString());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }
    }

    /**
     * Find a service based on a service primary key (i.e. service name)
     * @param key the service primary key
     * @return a service name as primary key
     * @throws FinderException
     */
    public String ejbFindByPrimaryKey(String key)
      throws FinderException
    {
        System.out.println("ServiceCatalogBeanBMP: ejbFindByPrimaryKey() called. ");

        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM mapservice WHERE name='" + key + "'");
            ResultSet rs = pstmt.executeQuery();
            //int k=1;
            while (rs.next()) {
                String name = rs.getString("name");
                //String type = rs.getString("type");
                //String config = rs.getString("config");
                //System.out.println("k=" + (k++) + " " + name + "   " + config);
                if (name.equals(key)) {
                   return key;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new javax.ejb.FinderException(e.getMessage());
        } finally {
            try {
                if (pstmt!= null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {}
        }
        throw new javax.ejb.FinderException("Service not found.");
    }

    /**
     * Find a service based the given service name
     * @param serviceName the service name
     * @return a service name as primary key
     * @throws FinderException
     */
    public String ejbFindByServiceName(String serviceName)
      throws FinderException
    {
        System.out.println("ServiceCatalogBeanBMP: ejbFindByServiceName() called. '" + serviceName + "'");

        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM mapservice WHERE name='" + serviceName + "'");
            ResultSet rs = pstmt.executeQuery();
            boolean hasNext = rs.next();
            if (hasNext) {
                String name = rs.getString("name");
                //String type = rs.getString("type");
                //String config = rs.getString("config");

                System.out.println("name='" + name + "'");
                //System.out.println("type=" + rs.getString("type"));
                //System.out.println("output_dir=" + rs.getString("output_dir"));
                //System.out.println("output_url=" + rs.getString("output_url"));
                //System.out.println("image_format=" + rs.getString("image_format"));

                //System.out.println("config=" + rs.getString("config"));

                if (name.equals(serviceName)) {
                   return serviceName;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new javax.ejb.FinderException(e.getMessage());
        } finally {
           try {
                if (pstmt!= null) pstmt.close();
                if (conn != null) conn.close();
           } catch (Exception ex) {}
        }
        throw new javax.ejb.FinderException("Service not found.");
    }

    /**
     * Find all the service names
     * @return a collection that contains all the service names
     */
    public Collection ejbFindAllServices()
        throws FinderException
    {
        Collection set = new Vector();
        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("SELECT name FROM mapservice ORDER BY name");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String serviceName = rs.getString("name");
                set.add(serviceName);
            }
        } catch (Exception ex) {
            throw new FinderException(ex.toString());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        System.out.println("ServiceCatalogBeanBMP:ejbFindAllServices() " + set);
        return set;
    }
}