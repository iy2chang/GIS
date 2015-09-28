package com.esri.svr.cat;

import com.esri.mo2.map.prj.FileAccess;

/**
 * This class provides a simple implementation of FileAccess for a string based IO stream
 */
public class StringFileAccess implements FileAccess {
    private String axl;

    /**
     * constructs a FileAccess for given file path name string
     */
    public StringFileAccess(String axl) {
        this.axl = axl;
    }

    // FileAccess method implementations
   /**
    * Get an output stream
    * @return an OutputStream object
    * @throws java.io.IOException
    */
    public java.io.OutputStream getOutputStream() throws java.io.IOException {
        if (axl != null)
          return new java.io.ByteArrayOutputStream(axl.getBytes().length);
        else
          return null;
    }

    /**
     * Get an input stream
     * @return an InputStream
     * @throws java.io.IOException
     */
    public java.io.InputStream getInputStream() throws java.io.IOException {
        if (axl != null)
          return new java.io.ByteArrayInputStream(axl.getBytes());
        else
          return null;
    }
}