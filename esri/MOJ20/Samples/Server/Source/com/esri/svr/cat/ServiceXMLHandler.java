package com.esri.svr.cat;

import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/**
 * This class provides functionality for processing service XML document.
 */
public final class ServiceXMLHandler implements ContentHandler {
    private java.util.Stack startElements = new java.util.Stack();
    private java.util.Stack endElements = new java.util.Stack();
    private java.util.ArrayList serviceCatalogs = new java.util.ArrayList();

    private String content;
    private ServiceCatalog sc;
    private boolean hasError = false;

    /**
     * Default constructor
     */
    public ServiceXMLHandler() {
        super();
    }

    /**
     * Get a list of ServiceCatalog(s)
     * @return
     */
    public java.util.ArrayList getServiceCatalogs() {
        return serviceCatalogs;
    }

    /**
     * Start processing service XML element
     * @param uri uri
     * @param localName local name
     * @param qName qName
     * @param list list of attributes
     */
    public void startElement(String uri, String localName, String qName, Attributes list) {
        startElements.push(qName);
        System.out.println("startElement local name=" + localName + " qName=" + qName);

        if (qName.equals("service")) {
            try {
                sc = new ServiceCatalog();
            } catch (java.io.IOException ex)  {
                hasError = true;
                ex.printStackTrace();
            }
        }
    }

    /**
     * End processing of an element
     * @param uri uri
     * @param localName local name
     * @param qName qName
     */
    public void endElement(String uri, String localName, String qName) {
        String sEle = (String)startElements.peek();
        System.out.println("endElement=" + qName + " " + sEle + " " + qName.equals(sEle) + " content=>" + content);

        if (qName.equals("service")) {
            if (!hasError) {
                // add to the service catalog only when the service is valid/good
                serviceCatalogs.add(sc);
                /*
                System.out.println("Service Info===>");
                System.out.println("name=" + sc.getServiceName());
                System.out.println("type=" + sc.getServiceType());
                System.out.println("dir=" + sc.getOutputDir());
                System.out.println("url=" + sc.getOutputUrl());
                System.out.println("filename=" + sc.getAxlFileName());
                System.out.println("format=" + sc.getImageFormat());
                System.out.println("<==== Service Info");
                */
            }
            hasError = false;
            sc = null;
            startElements.pop();
        }

        else if (qName.equals(sEle)) {
            if (qName.equals("name")) {
                sc.setServiceName(content);
            } else if (qName.equals("type")) {
                sc.setServiceType(content);
            } else if (qName.equals("output_url")) {
                sc.setOutputUrl(content);
            } else if (qName.equals("output_dir")) {
                sc.setOutputDir(content);
            } else if (qName.equals("image_format")) {
                sc.setImageFormat(content);
            } else if (qName.equals("image_url_prefix")) {
                sc.setImageURLPrefix(content);
            } else if (qName.equals("axl_file")) {
                try {
                    sc.setAxlFileName(content);
                } catch (Exception ex) {
                    hasError = true;
                    ex.printStackTrace();
                }
            } else if (qName.equals("service_url")) {
                sc.setServiceURL(content);
            }
            startElements.pop();
        }

        else {
            endElements.push(qName);
        }
    }

    /**
     * Element's contents
     * @param chars contents' in char array
     * @param start starting location in the array
     * @param len end location in the array
     */
    public void characters(char[] chars, int start, int len) {
        content = new String(chars, start, len);
        System.out.println("content=" + (new String(chars, start, len)));
    }

    /**
     * Finish processing of the XML document
     */
    public void endDocument() {

        System.out.println(" # of services=" + serviceCatalogs.size());
        for (int i=0; i<serviceCatalogs.size(); i++) {
            ServiceCatalog sc1 = (ServiceCatalog)serviceCatalogs.get(i);
            System.out.println("name=" + sc1.getServiceName());
            System.out.println("type=" + sc1.getServiceType());
            System.out.println("dir=" + sc1.getOutputDir());
            System.out.println("url=" + sc1.getOutputUrl());
            System.out.println("filename=" + sc1.getAxlFileName());
            System.out.println("format=" + sc1.getImageFormat());
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int lenght) {
        System.out.println("ContentHandler.ignorableWhitespace()");
    }

    public void processingInstruction(String target, String data) {
        System.out.println("ContentHandler.processingInstruction()");
    }

    public void setDocumentLocator(Locator locator) {
        System.out.println("ContentHandler.setDocumentLocator()");
    }

    public void skippedEntity(String name) {
        System.out.println("ContentHandler.skippedEntity()");
    }

    public void startDocument() {
        System.out.println("ContentHandler.startDocument()");
    }

    public void startPrefixMapping(String prefix, String uri) {
        System.out.println("ContentHandler.startPrefixMapping()");
    }

    public void endPrefixMapping(String prefix) {
        System.out.println("ContentHandler.endPrefixMapping()");
    }
}
