package com.esri.svr.cmn;

import com.esri.mo2.util.ReadOnlyIterator;
import com.esri.svr.cmn.IOStreamProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.io.IOException;


/**
 * This class loads IOStreamProvider(s) for the given provider's interface name. It uses MOJ20's plug-in
 * architecture to load those IOStreamProvider(s) developed by users or ESRI.
 */
public class IOStreamProviderLoader {
    private LinkedList providerList;
    private String providerInterface;

    /**
     * Construct a loader with given interface name
     * @param interfaceName IOStreamProvider interface name
     */
    public IOStreamProviderLoader(String interfaceName) {
        this.providerInterface = interfaceName;

        try {
            Iterator iterator = com.esri.mo2.sys.spi.Provider.getProviderObjects(interfaceName);
            providerList = new LinkedList();
            while(iterator.hasNext()) {
                IOStreamProvider provider = (IOStreamProvider)iterator.next();
                providerList.add(provider);
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ioex){
            ioex.printStackTrace();
        }
    }

    /**
     * Obtains a read-only Iterator over the collection of MenuProviders
     * that was created within this MenuManager's constructor
     * @return an iterator of MenuProviders
     */
    public Iterator getProviders() {
        return new ReadOnlyIterator(providerList.iterator());
    }

    /**
     * Obtains the full class name of the interface used to construct this MenuManager
     */
    public String getProviderInterfaceName() {
        return providerInterface;
    }

    /**
     * Get am IOStreamProvider based on the given class name
     * @param classname IOStreamProvider's class name
     * @return an IOStreamProvider object
     */
    public IOStreamProvider getIOStreamProvider(String classname) {
        IOStreamProvider provider = null;
        for (int i=0; i<providerList.size(); i++) {
            if (providerList.get(i).getClass().getName().compareTo(classname)==0) {
                provider = (IOStreamProvider)providerList.get(i);
                break;
            }
        }
        return provider;
    }

    /**
     * Get the first available IOStreamProvider
     * @return an IOStreamProvider object
     */
    public IOStreamProvider getAvailableIOStreamProvider() {
        IOStreamProvider provider = null;
        if (providerList.size()>0) provider = (IOStreamProvider)providerList.get(0);
        return provider;
    }
}
