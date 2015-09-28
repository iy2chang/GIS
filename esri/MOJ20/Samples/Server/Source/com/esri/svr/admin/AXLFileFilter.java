package com.esri.svr.admin;


import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.filechooser.*;

/**
 * A FileFilter for selecting ArcXML configuration file
 */
public class AXLFileFilter extends FileFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";

    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    /**
     * Default constructor.
     */
    public AXLFileFilter() {
	    this.filters = new Hashtable();
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new AXLFileFilter("axl");
     *
     * @see #addExtension
     */
    public AXLFileFilter(String extension) {
	    this(extension,null);
    }

    /**
     * Creates an AXL file filter that accepts files with the given extension.
     * @param extension AXL file extension
     * @param description AXL file extension description
     */
    public AXLFileFilter(String extension, String description) {
	    this();
	    if(extension!=null) addExtension(extension);
 	    if(description!=null) setDescription(description);
    }

    /**
     * Creates a AXL filter that accepts files with given filters
     * @param filters file filters
     */
    public AXLFileFilter(String[] filters) {
	    this(filters, null);
    }

    /**
     * Creates a AXL filter that accepts files with given filters
     * @param filters file filters
     * @param description AXL extension description
     */
    public AXLFileFilter(String[] filters, String description) {
	    this();
	    for (int i = 0; i < filters.length; i++) {
	        // add filters one by one
	        addExtension(filters[i]);
	    }
 	    if(description!=null) setDescription(description);
    }

    /**
     * Check to see if the given file can be accpeted or not
     * @param f file to be checked
     * @return boolean
     */
    public boolean accept(File f) {
	    if(f != null) {
	        if(f.isDirectory()) {
		        return true;
	        }
	        String extension = getExtension(f);
	        if(extension != null && filters.get(getExtension(f)) != null) {
		        return true;
	        }
	    }
	    return false;
    }

    /**
     * Get a file extension
     * @param f file to be checked
     * @return file's extension
     */
    public String getExtension(File f) {
	    if(f != null) {
	        String filename = f.getName();
	        int i = filename.lastIndexOf('.');
	        if(i>0 && i<filename.length()-1) {
		    return filename.substring(i+1).toLowerCase();
	    }
	    }
	    return null;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     *
     * Note that the "." before the extension is not needed and will be ignored.
     */
    public void addExtension(String extension) {
	    if(filters == null) {
	        filters = new Hashtable(5);
	    }
	    filters.put(extension.toLowerCase(), this);
	    fullDescription = null;
    }


    /**
     * Returns the human readable description of this filter.
     */
    public String getDescription() {
	    if(fullDescription == null) {
	        if(description == null || isExtensionListInDescription()) {
 		        fullDescription = description==null ? "(" : description + " (";
		        // build the description from the extension list
		        Enumeration extensions = filters.keys();
		        if(extensions != null) {
		            fullDescription += "." + (String) extensions.nextElement();
		            while (extensions.hasMoreElements()) {
			            fullDescription += ", ." + (String) extensions.nextElement();
		            }
		        }
		        fullDescription += ")";
	        } else {
		        fullDescription = description;
	        }
	    }
	    return fullDescription;
    }

    /**
     * Sets the human readable description of this filter.
     */
    public void setDescription(String description) {
	    this.description = description;
	    fullDescription = null;
    }

    /**
     * Determines whether the extension list should
     * show up in the human readable description.
     */
    public void setExtensionListInDescription(boolean b) {
	    useExtensionsInDescription = b;
	    fullDescription = null;
    }

    /**
     * Returns whether the extension list should
     * show up in the human readable description.
     */
    public boolean isExtensionListInDescription() {
	    return useExtensionsInDescription;
    }
}
