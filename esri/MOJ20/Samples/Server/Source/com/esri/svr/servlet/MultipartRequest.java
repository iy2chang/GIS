
package com.esri.svr.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.FilePart;


/**
 * A utility class to handle <code>multipart/form-data</code> requests,
 * the kind of requests that support file uploads.  This class emulates the
 * interface of <code>HttpServletRequest</code>, making it familiar to use.
 * It uses a "push" model where any incoming files are read and saved directly
 * to disk in the constructor. If you wish to have more flexibility, e.g.
 * write the files to a database, use the "pull" model
 * <code>MultipartParser</code> instead.
 * <p>
 * This class can receive arbitrarily large files (up to an artificial limit
 * you can set), and fairly efficiently too.
 * It cannot handle nested data (multipart content within multipart content)
 * or internationalized content (such as non Latin-1 filenames).
 * <p>
 * See the included <a href="upload.war">upload.war</a>
 * for an example of how to use this class.
 * <p>
 */
public class MultipartRequest {

  private static final int DEFAULT_MAX_POST_SIZE = 1024 * 1024;  // 1 Meg

  protected Hashtable parameters = new Hashtable();  // name - Vector of values

  private String fileContent;

  /**
   * Constructor with an old signature, kept for backward compatibility.
   * Without this constructor, a servlet compiled against a previous version
   * of this class (pre 1.4) would have to be recompiled to link with this
   * version.  This constructor supports the linking via the old signature.
   * Callers must simply be careful to pass in an HttpServletRequest.
   *
   */
  public MultipartRequest(ServletRequest request) throws IOException {
    this((HttpServletRequest)request, DEFAULT_MAX_POST_SIZE);
  }

  /**
   * Constructs a new MultipartRequest to handle the specified request,
   * saving any uploaded files to the given directory, and limiting the
   * upload size to the specified length.  If the content is too large, an
   * IOException is thrown.  This constructor actually parses the
   * <tt>multipart/form-data</tt> and throws an IOException if there's any
   * problem reading or parsing the request.
   *
   * @param request the servlet request.
   * @param maxPostSize the maximum size of the POST content.
   * @exception IOException if the uploaded content is larger than
   * <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
   */
  public MultipartRequest(HttpServletRequest request,
                          int maxPostSize) throws IOException {
    // Sanity check values
    if (request == null)
      throw new IllegalArgumentException("request cannot be null");

    if (maxPostSize <= 0) {
      throw new IllegalArgumentException("maxPostSize must be positive");
    }

    // Parse the incoming multipart, storing files in the dir provided,
    // and populate the meta objects which describe what we found
    MultipartParser parser = new MultipartParser(request, maxPostSize);

    Part part;
    while ((part = parser.readNextPart()) != null) {
      String name = part.getName();
      if (part.isParam()) {
        ParamPart paramPart = (ParamPart) part;
        String value = paramPart.getStringValue();
        Vector existingValues = (Vector)parameters.get(name);
        if (existingValues == null) {
          existingValues = new Vector();
          parameters.put(name, existingValues);
        }
        existingValues.addElement(value);
        //System.out.println("name=" + name + " value=" + value);
        System.out.println("variable name=" + name);
      }
      else if (part.isFile()) {
        // It's a file part
        FilePart filePart = (FilePart) part;
        String fileName = filePart.getFileName();

        System.out.println("filename=" + fileName + " file value=" + filePart);

        if (fileName != null) {
          fileContent = getFileAsString(filePart);
        }
      }
    }
  }

  /**
   * Get the constents of the file as string
   * @return String
   */
  public String getFileAsString(FilePart filePart)
    throws java.io.IOException
  {
    StringBuffer buffer = new StringBuffer();
    int read;
    byte[] buf = new byte[8 * 1024];
    java.io.InputStream is = filePart.getInputStream();
    while((read = is.read(buf)) != -1) {
      buffer.append(new String(buf, 0, read));
    }
    return buffer.toString().trim();
  }

  /**
   * Returns the names of all the parameters as an Enumeration of
   * Strings.  It returns an empty Enumeration if there are no parameters.
   *
   * @return the names of all the parameters as an Enumeration of Strings.
   */
  public Enumeration getParameterNames() {
    return parameters.keys();
  }

  /**
   * Returns the value of the named parameter as a String, or null if
   * the parameter was not sent or was sent without a value.  The value
   * is guaranteed to be in its normal, decoded form.  If the parameter
   * has multiple values, only the last one is returned (for backward
   * compatibility).  For parameters with multiple values, it's possible
   * the last "value" may be null.
   *
   * @param name the parameter name.
   * @return the parameter value.
   */
  public String getParameter(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String value = (String)values.elementAt(values.size() - 1);
      return value;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the values of the named parameter as a String array, or null if
   * the parameter was not sent.  The array has one entry for each parameter
   * field sent.  If any field was sent without a value that entry is stored
   * in the array as a null.  The values are guaranteed to be in their
   * normal, decoded form.  A single value is returned as a one-element array.
   *
   * @param name the parameter name.
   * @return the parameter values.
   */
  public String[] getParameterValues(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String[] valuesArray = new String[values.size()];
      values.copyInto(valuesArray);
      return valuesArray;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   */
   public String getFileContent() {
        return fileContent;
   }

   /**
    * Is it multipart form data
    */
   public static boolean isMultipartData(HttpServletRequest req) {
    // Check the content type to make sure it's "multipart/form-data"
    // Access header two ways to work around WebSphere oddities
    String type = null;
    String type1 = req.getHeader("Content-Type");
    String type2 = req.getContentType();
    // If one value is null, choose the other value
    if (type1 == null && type2 != null) {
      type = type2;
    }
    else if (type2 == null && type1 != null) {
      type = type1;
    }
    // If neither value is null, choose the longer value
    else if (type1 != null && type2 != null) {
      type = (type1.length() > type2.length() ? type1 : type2);
    }

    if (type == null || !type.toLowerCase().startsWith("multipart/form-data")) {
      return false;
    }

    return true;
  }
}
