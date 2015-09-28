package com.esri.svr.ejb;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

/**
 * This interface defines functions to create a LayoutService object.
 */
public interface LayoutServiceHome extends EJBHome {

    /**
     * Create a LayoutService object
     * @return a LayoutService object
     * @throws RemoteException
     * @throws CreateException
     */
    public LayoutService create() throws CreateException,java.rmi.RemoteException;
}