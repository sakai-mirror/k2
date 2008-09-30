package org.sakaiporject.kernel.test;

import org.sakaiproject.id.api.IdManager; 

public class SimpleKernelServiceTest {

	IdManager idManager;
    public IdManager getIdManager() {
        return idManager;
    }
    public void setIdManager(IdManager idManager) {
    	System.out.println("IOC : setIdManager");
        this.idManager = idManager;
    }

	public void start() throws Exception {
        System.out.println("Hello OSGI Service Test!! "  );
        System.out.println("New ID = " + idManager.createUuid());
    }
	
    public void stop() throws Exception {
        System.out.println("Goodbye OSGI Service Test!!");
    }

}
