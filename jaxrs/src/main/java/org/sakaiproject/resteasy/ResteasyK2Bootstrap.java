package org.sakaiproject.resteasy;

import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.rest.JaxRsPrototypeProvider;
import org.sakaiproject.kernel.api.rest.JaxRsSingletonProvider;

import static org.sakaiproject.kernel.api.rest.JaxRsSingletonProvider.JAXRS_SINGLETON_REGISTRY;
import static org.sakaiproject.kernel.api.rest.JaxRsPrototypeProvider.JAXRS_PROTOTYPE_REGISTRY;

/**
 * Bootstraps the RestEasy JAX-RS implementation, using resources registered
 * with the sakai kernel's JaxRsResourceProvider.JAXRS_REGISTRY registry.
 */
public class ResteasyK2Bootstrap extends ResteasyBootstrap {
	private static final Log log = LogFactory.getLog(ResteasyBootstrap.class);
	protected JaxRsSingletonRegistryListener singletonListener;
	protected JaxRsPrototypeRegistryListener prototypeListener;
	
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		Registry restEasyRegistry = (Registry) event.getServletContext().getAttribute(Registry.class.getName());
		
		// Add listeners to keep the kernel registries aligned with the resteasy registry
		singletonListener = new JaxRsSingletonRegistryListener(restEasyRegistry);
		prototypeListener = new JaxRsPrototypeRegistryListener(restEasyRegistry);
		
		// Add all of the resources in our JAX-RS providers to the resteasy registry
		KernelManager km = new KernelManager();
		RegistryService registryService = km.getService(RegistryService.class);
		org.sakaiproject.kernel.api.Registry<Class<?>, JaxRsSingletonProvider> jaxRsSingletonRegistry =
			registryService.getRegistry(JAXRS_SINGLETON_REGISTRY);
		org.sakaiproject.kernel.api.Registry<Class<?>, JaxRsPrototypeProvider> jaxRsPrototypeRegistry =
			registryService.getRegistry(JAXRS_PROTOTYPE_REGISTRY);

		// Sync the JAX-RS implementation's registry with the kernel's registry
		syncRestEasyRegistry(restEasyRegistry, jaxRsSingletonRegistry, jaxRsPrototypeRegistry);
		
		// Listen for changes to the kernel registry
		jaxRsSingletonRegistry.addListener(singletonListener);
		jaxRsPrototypeRegistry.addListener(prototypeListener);
		
		if(log.isInfoEnabled()) log.info("Added JAX-RS registry listener for updates to " + jaxRsSingletonRegistry);
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

	protected void syncRestEasyRegistry(Registry restEasyRegistry,
			org.sakaiproject.kernel.api.Registry<Class<?>, JaxRsSingletonProvider> jaxRsSingletonRegistry,
			org.sakaiproject.kernel.api.Registry<Class<?>, JaxRsPrototypeProvider> jaxRsPrototypeRegistry) {
		log.info("Updating " + restEasyRegistry);
		Map<Class<?>, JaxRsSingletonProvider> singletonProvidersMap = jaxRsSingletonRegistry.getMap();
		for(JaxRsSingletonProvider provider : singletonProvidersMap.values()) {
			try {
				restEasyRegistry.removeRegistrations(provider.getJaxRsSingleton().getClass());
			} catch(Exception e) {
				log.warn(e);
			}
			if(log.isInfoEnabled()) log.info("Added JAX-RS singleton: " + provider.getJaxRsSingleton());
			restEasyRegistry.addSingletonResource(provider.getJaxRsSingleton());
		}
		Map<Class<?>, JaxRsPrototypeProvider> prototypeProvidersMap = jaxRsPrototypeRegistry.getMap();
		for(JaxRsPrototypeProvider provider : prototypeProvidersMap.values()) {
			try {
				restEasyRegistry.removeRegistrations(provider.getJaxRsPrototype());
			} catch(Exception e) {
				log.warn(e);
			}
			if(log.isInfoEnabled()) log.info("Added JAX-RS prototype: " + provider.getJaxRsPrototype());
			restEasyRegistry.addPerRequestResource(provider.getJaxRsPrototype());
		}
	}
}