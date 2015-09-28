package com.esri.svr.ejb;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * This interface defines funtion to create a MapServiceI object.
 */
public interface MapServiceIHome extends EJBHome {

    /**
     * Create a MapServiceI object
     * @return a MapServiceII object
     * @throws java.rmi.RemoteException
     * @throws CreateException
     */
    public MapServiceI create() throws CreateException,java.rmi.RemoteException;

}
