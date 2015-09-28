package com.esri.svr.lyt;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */

import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.map.img.ImageSupport;
import com.esri.svr.cmn.IOStreamProvider;
import com.esri.svr.cmn.IOStreamProviderLoader;

/**
 * This class provides functionality of generating layout images.
 */
public class LayoutService {

    private com.esri.mo2.svr.map.PageLayout _layout;

    /**
     * The default constructor for PageLayout.
     */
    public LayoutService() {
        _layout = new com.esri.mo2.svr.map.PageLayout();
        if (_outputDir == null)
            _outputDir = System.getProperty("user.dir");
    }

    /**
     * Get PageLayout object
     * @return the PageLayout object
     */
    public com.esri.mo2.svr.map.PageLayout getPageLayout() {
        return _layout;
    }

    /**
     * Set PageLayout object
     * @param layout the PageLayout to be set
     */
    public void setPageLayout(com.esri.mo2.svr.map.PageLayout layout) {
        if (layout!=null) _layout = layout;
    }


    /**
     * Export the layout as an image written to a persistent storage
     * @param imageSize
     * @return
     */
    public String exportImage(java.awt.Dimension imageSize) {
        System.out.println("   #########     exportImage(1) " + imageSize);
        return exportImage(imageSize, _imageFormat);
    }

    /**
     * Export the layout as an image written to a persistent storage
     * with given image format and full extent of the layout page
     * @param imageSize
     * @param imageFormat
     * @return
     */
    public String exportImage(java.awt.Dimension imageSize, String imageFormat) {

        System.out.println("   #########     exportImage(2) " + imageSize);

        return exportImage(imageSize, imageFormat, null);
    }

    /**
     * Export the layout as an image written to a persistent storage
     * with given image format and page extent
     * @param imageSize
     * @param imageFormat
     * @param pageExtent
     * @return
     */
    public String exportImage(
            java.awt.Dimension imageSize,
            String imageFormat,
            com.esri.mo2.cs.geom.Envelope pageExtent)
    {

        System.out.println("   #########     exportImage(3) " + imageSize);

        String fileName = null;
        try {
            fileName = exportImage(imageSize.width, imageSize.height, imageFormat, pageExtent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileName;
    }


    /**
     * Export the layout's thumbnail in the default image format
     * @return
     */
    public String exportThumbnail() {
        return exportThumbnail(_defaultImageFormat);
    }

    /**
     * Export the layout's thumbnail in the given image format
     * @param imageFormat
     * @return
     */
    public String exportThumbnail(String imageFormat) {
        String fileName = null;
        try {
            fileName = exportImage(_defaultThumbnailSize.width, _defaultThumbnailSize.height, imageFormat, _layout.getFullExtent());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileName;
    }

    /**
     *  Get image file format
     */
    public String getImageFormat() {
        return _imageFormat;
    }

    /**
     *  Set image file format. It must be one of the supported format, otherwise
     *  it will quietly ignore the unrecognized format
     *  @param format the image file format
     */
    public void setImageFormat(String format) {
        if (isImageFormatSupported(format))
          _imageFormat = format;
    }

    /**
     *  Set map image output directory. The default directory is user's directory
     *  defined in the JVM's system properties
     *  @param dir the image output directory
     */
    public void setOutputDir(String dir) {
        System.out.println("PageLayout: setOutputDir=" + dir);
        if (dir != null) {
           _outputDir = dir;
           _outputURL = null; // since output URL will take priority over the output directory
        }
    }

    /**
     * Get the image output directory
     */
    public String getOutputDir() {
        return _outputDir;
    }

    /**
     *  Set map image output URL in term of jCIFS. It takes priority over the output directory
     *  @param url the image output url
     */
    public void setOutputURL(String url) {
        System.out.println("PageLayout: setOutputURL=" + url);
        if (url != null && url.startsWith(_jCIFSPrefix))
           _outputURL = url;
    }

    /**
     * Get the image output URL
     */
    public String getOutputURL() {
        return _outputURL;
    }


    private boolean isImageFormatSupported(String format) {
        String formats[] = ImageSupport.getListOfWriterByTypeName();
        for (int i=0, n=formats.length; i<n; i++)
            if (format.compareToIgnoreCase(formats[i])==0) return true;
        return false;
    }


    private String exportImage(int width, int height, String imageFormat, Envelope pageExtent)
        throws java.io.IOException
    {
        System.out.println("   #########   exportImage(4) width=" + width + "  height=" + height);
        if (pageExtent == null) pageExtent = _layout.getFullExtent();
        // create an buffered mage
        java.awt.image.BufferedImage bufferedImage = _layout.getImage(new java.awt.Dimension(width, height), pageExtent);
        return exportImage(bufferedImage, imageFormat);
    }


    private String exportImage(java.awt.image.BufferedImage image, String imageFormat)
        throws java.io.IOException
    {
        com.esri.mo2.map.img.ImageWriter iw = ImageSupport.createWriterByType(imageFormat);
        if (iw == null)
            throw new java.io.IOException("No image writer for the specified image format '" + imageFormat + "'");

        String filename = getFileName();
        IOStreamProvider provider = null;
        if (_outputURL != null)
            provider = _providerLoader.getIOStreamProvider("com.esri.svr.plgin.JcifsIOStreamProvider");
        else
            provider = _providerLoader.getIOStreamProvider("com.esri.svr.plgin.FileIOStreamProvider");
        java.io.OutputStream os = provider.getOutputStream(filename);
        iw.exportImage(os, image);
        os.close();

        return filename;
    }

    private String getFileName()
        throws java.io.IOException
    {
        int number = (int)(Math.random() * 100000d);
        StringBuffer filename = new StringBuffer();

        if (_outputURL != null && !_outputURL.trim().equals("") && _outputURL.indexOf("://")>0) {
            filename.append(_outputURL);
            if (!_outputURL.endsWith("/")) filename.append("/");
        } else if (_outputDir!=null && !_outputDir.trim().equals("")) {
            filename.append(_outputDir);
            if (!_outputDir.endsWith(java.io.File.separator)) filename.append(java.io.File.separator);
        } else {
            throw new java.io.IOException("No vallid output directory or url.");
        }

        filename.append(_filenamePrefix);
        filename.append(number + ".");
        filename.append(_imageFormat);

        System.out.println("BaseMapper: getFileName() 2 filename=" + filename);

        return filename.toString();
    }


//
// private variables
//

    private String _outputDir = null; // for local disks or mapped disks
    private String _outputURL = null; // for jCIFS based output
    private String _imageFormat = "png";

    private final static String _filenamePrefix = "layout_image_";
    private final static String _jCIFSPrefix = "smb://";

    private final static java.awt.Dimension _defaultThumbnailSize = new java.awt.Dimension(100, 100);
    private final static String _defaultImageFormat = "jpg";
    private IOStreamProviderLoader _providerLoader = new IOStreamProviderLoader("com.esri.svr.cmn.IOStreamProvider");

}