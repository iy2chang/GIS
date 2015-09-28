package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 *
 * CatalogHome interface
 */
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

/**
 * This interface defines functions to create a Catalog object.
 */
public interface CatalogHome extends EJBHome {

    /**
     * Create a Catalog object
     * @return a Catalog object
     * @throws RemoteException
     * @throws CreateException
     */
    public Catalog create() throws CreateException,java.rmi.RemoteException;
}