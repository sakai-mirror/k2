package org.sakaiproject.kernel2.mp2;

import org.sakaiproject.kernel.api.ComponentActivator;
import org.sakaiproject.kernel.api.ComponentActivatorException;
import org.sakaiproject.kernel.api.Kernel;

public class Activator implements ComponentActivator {

  public void activate(Kernel kernel) throws ComponentActivatorException {
    // nothing to do since this project is just to export the model classes
  }

  public void deactivate() {
  }
}
