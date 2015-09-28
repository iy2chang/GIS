package com.esri.svr.plgin;

import com.esri.svr.cmn.IOStreamProvider;

import java.io.*;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/**
 * This class provides a CIFS-based InputStream and OutputStream.
 */
public class JcifsIOStreamProvider  implements IOStreamProvider {

    /**
     * Get an input stream with given CIFS based file name in form of
     * smb://username:password@hostname/foldername/filename.png etc.
     * @param fileName the smb file URL
     * @return an InputStream object
     * @throws IOException
     */
    public InputStream getInputStream(String fileName)
        throws IOException
    {
        return new SmbFileInputStream(fileName);
    }

    /**
     * Get an onput stream with given CIFS-based file name in form of
     * smb://username:password@hostname/foldername/filename.png etc.
     * @param fileName
     * @return an OutputStream object
     * @throws IOException
     */
    public OutputStream getOutputStream(String fileName)
        throws IOException
    {
        return new SmbFileOutputStream(fileName);
    }
}
