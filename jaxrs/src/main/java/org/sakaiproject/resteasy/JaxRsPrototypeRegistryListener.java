package org.sakaiproject.resteasy;

import org.jboss.resteasy.spi.Registry;
import org.sakaiproject.kernel.api.RegistryListener;
import org.sakaiproject.kernel.api.rest.JaxRsPrototypeProvider;

/**
 * Listens for changes to Sakai's JAX-RS prototype registry, and updates
 * RestEasy's internal resource registry.
 */
public class JaxRsPrototypeRegistryListener implements RegistryListener<JaxRsPrototypeProvider> {
	protected Registry registry;
	
	public JaxRsPrototypeRegistryListener(Registry jaxRsRegistry) {
		this.registry = jaxRsRegistry;
	}
	
	public void added(JaxRsPrototypeProvider wasAdded) {
		registry.addPerRequestResource(wasAdded.getJaxRsPrototype());
	}

	public void removed(JaxRsPrototypeProvider wasRemoved) {
		registry.removeRegistrations(wasRemoved.getJaxRsPrototype());
	}

	public void updated(JaxRsPrototypeProvider wasUpdated) {
		registry.removeRegistrations(wasUpdated.getJaxRsPrototype());
		registry.addPerRequestResource(wasUpdated.getJaxRsPrototype());
	}

}
