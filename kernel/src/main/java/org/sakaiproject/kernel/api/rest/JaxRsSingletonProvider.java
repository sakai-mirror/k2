package org.sakaiproject.kernel.api.rest;

import org.sakaiproject.kernel.api.Provider;

/**
 * Provides a JAX-RS singleton resource
 */
public interface JaxRsSingletonProvider extends Provider<Class<?>>{
  public static final String JAXRS_SINGLETON_REGISTRY = "jaxrs.singleton.registry";
  public Object getJaxRsSingleton();
}
