package com.esri.svr.ejb;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: ESRI</p>
 * @version 2.0
 */
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
/**
 * This interface defines method for creating a MapSerivceII ojbect.
 */
public interface MapServiceIIHome extends EJBHome {

    /**
     * Create a MapServiceII object
     * @return a MapServiceII object
     * @throws RemoteException
     * @throws CreateException
     */
    public MapServiceII create() throws CreateException,java.rmi.RemoteException;

}