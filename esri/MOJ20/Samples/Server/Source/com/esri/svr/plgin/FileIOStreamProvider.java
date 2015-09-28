package com.esri.svr.plgin;

import com.esri.svr.cmn.IOStreamProvider;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * This class provides a file-based IO stream
 */
public class FileIOStreamProvider implements IOStreamProvider {

    /**
     * Gets a file InputStream
     * @param fileName the file name
     * @return an InputStream
     * @throws IOException
     */
    public InputStream getInputStream(String fileName)
        throws IOException
    {
        return new FileInputStream(new java.io.File(fileName));
    }

    /**
     * Gets a file OutputStream
     * @param fileName the file name
     * @return an OutputStream
     * @throws IOException
     */
    public OutputStream getOutputStream(String fileName)
        throws IOException
    {
        return new FileOutputStream(new java.io.File(fileName));
    }
}
