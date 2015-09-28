package com.esri.svr.ejb;


import com.esri.svr.cmn.ServiceInfo;

import java.rmi.RemoteException;

/**
 * This interface defines an additional function for getting all service info.
 */
public interface MapServiceI extends MapServiceII {
     /**
     * Get all service info.
     * @return an arrary of ServiceInfo
     * @throws RemoteException
     */
     public ServiceInfo[] getAllServices() throws RemoteException;

}
