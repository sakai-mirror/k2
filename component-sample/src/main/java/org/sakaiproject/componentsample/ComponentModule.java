package org.sakaiproject.componentsample;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.componentsample.api.HelloWorldService;
import org.sakaiproject.componentsample.api.InternalDateService;
import org.sakaiproject.componentsample.core.HelloWorldServiceGuicedImpl;
import org.sakaiproject.componentsample.core.InternalDateServiceImpl;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.component.core.guice.ServiceProvider;

import javax.persistence.EntityManager;

public class ComponentModule extends AbstractModule {

  private static final Log LOG = LogFactory.getLog(ComponentModule.class);
  private final Kernel kernel;
  
  public ComponentModule(Kernel kernel) {
    this.kernel = kernel;
  }
  
  @Override
  protected void configure() {
    // First bind external services to this injector
    ServiceManager serviceManager = kernel.getServiceManager();
    bind(JCRService.class).toProvider(new ServiceProvider<JCRService>(serviceManager, JCRService.class));
    bind(EntityManager.class).toProvider(new ServiceProvider<EntityManager>(serviceManager, EntityManager.class));

    // Now bind local services
    bind(InternalDateService.class).to(InternalDateServiceImpl.class).in(Scopes.SINGLETON);
    bind(HelloWorldService.class).to(HelloWorldServiceGuicedImpl.class).in(Scopes.SINGLETON);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Bound HelloWorldService");
    }
  }
}
