package org.sakaiproject.kernel.api.rest;

import org.sakaiproject.kernel.api.Provider;

/**
 * Provides a JAX-RS prototype resource
 */
public interface JaxRsPrototypeProvider extends Provider<Class<?>>{
  public static final String JAXRS_PROTOTYPE_REGISTRY = "jaxrs.prototype.registry";
  public Class<?> getJaxRsPrototype();
}
