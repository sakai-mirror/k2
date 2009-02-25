package org.sakaiproject.resteasy;

import org.jboss.resteasy.spi.Registry;
import org.sakaiproject.kernel.api.RegistryListener;
import org.sakaiproject.kernel.api.rest.JaxRsSingletonProvider;

/**
 * Listens for changes to Sakai's JAX-RS registry, and updates RestEasy's
 * internal resource registry.
 */
public class JaxRsSingletonRegistryListener implements RegistryListener<JaxRsSingletonProvider> {
	protected Registry registry;
	
	public JaxRsSingletonRegistryListener(Registry jaxRsRegistry) {
		this.registry = jaxRsRegistry;
	}
	
	public void added(JaxRsSingletonProvider wasAdded) {
		registry.addSingletonResource(wasAdded.getJaxRsSingleton());
	}

	public void removed(JaxRsSingletonProvider wasRemoved) {
		registry.removeRegistrations(wasRemoved.getJaxRsSingleton().getClass());
	}

	public void updated(JaxRsSingletonProvider wasUpdated) {
		registry.removeRegistrations(wasUpdated.getJaxRsSingleton().getClass());
		registry.addSingletonResource(wasUpdated.getJaxRsSingleton());
	}

}
