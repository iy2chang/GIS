package com.esri.svr.cmn;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * This interface defines methods to get input/output streams.
 */
public interface IOStreamProvider {

    /**
     * Get an input stream based on the given file or URL name
     * @param fileName file or URL name
     * @return an InputStream
     * @throws IOException
     */
    public InputStream getInputStream(String fileName) throws IOException;

    /**
     * Get an output stream based the given file or URL name
     * @param fileName file or URL name
     * @return an OutputStream
     * @throws IOException
     */
    public OutputStream getOutputStream(String fileName)throws IOException;
}
