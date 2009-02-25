package org.sakaiproject.kernel.webapp;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.Application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.KernelManager;
import org.sakaiproject.kernel.api.Registry;
import org.sakaiproject.kernel.api.RegistryService;
import org.sakaiproject.kernel.api.rest.JaxRsPrototypeProvider;
import org.sakaiproject.kernel.api.rest.JaxRsSingletonProvider;

/**
 * Registers JAX-RS Resources from the {@link Application} specified by the
 * "sakai.jaxrs.application" context-param.
 */
public class JaxRsApplicationListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(JaxRsApplicationListener.class);
	
	protected Set<JaxRsSingletonProvider> jaxRsSingletonProviders =
		new HashSet<JaxRsSingletonProvider>();

	protected Set<JaxRsPrototypeProvider> jaxRsPrototypeProviders =
		new HashSet<JaxRsPrototypeProvider>();

	public void contextInitialized(ServletContextEvent event) {
		Registry<Class<?>, JaxRsSingletonProvider> singletonRegistry = getSingletonRegistry();
		Registry<Class<?>, JaxRsPrototypeProvider> prototypeRegistry = getPrototypeRegistry();
		String appClass = event.getServletContext().getInitParameter(Application.class.getName());
		Application app;
		try {
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(appClass.trim());
			app = (Application)clazz.newInstance();
		} catch (Exception e) {
			log.warn("Unable to instantiate JAX-RS Application " + appClass);
			e.printStackTrace();
			return;
		}
		for(final Object object : app.getSingletons()) {
			JaxRsSingletonProvider provider = new JaxRsSingletonProvider() {
				public Object getJaxRsSingleton() {
					return object;
				}
				public Class<?> getKey() {
					return object.getClass();
				}
				public int getPriority() {
					return 0;
				}
				public String toString() {
					return "Provider for: " + object.toString();
				}
			};
			jaxRsSingletonProviders.add(provider);
			singletonRegistry.add(provider);
			if(log.isInfoEnabled()) log.info("Added " + provider.getJaxRsSingleton() + " to JAX-RS registry " + singletonRegistry);
		}
		for(final Class<?> clazz : app.getClasses()) {
			JaxRsPrototypeProvider provider = new JaxRsPrototypeProvider() {
				public Class<?> getJaxRsPrototype() {
					return clazz;
				}
				public Class<?> getKey() {
					return clazz;
				}
				public int getPriority() {
					return 0;
				}
				public String toString() {
					return "Provider for: " + clazz.toString();
				}
			};
			jaxRsPrototypeProviders.add(provider);
			prototypeRegistry.add(provider);
			if(log.isInfoEnabled()) log.info("Added " + provider.getJaxRsPrototype() + " to JAX-RS registry " + prototypeRegistry);

		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		Registry<Class<?>, JaxRsSingletonProvider> singletonRegistry = getSingletonRegistry();
		for(JaxRsSingletonProvider provider : jaxRsSingletonProviders) {
			singletonRegistry.remove(provider);
		}
		Registry<Class<?>, JaxRsPrototypeProvider> prototypeRegistry = getPrototypeRegistry();
		for(JaxRsPrototypeProvider provider : jaxRsPrototypeProviders) {
			prototypeRegistry.remove(provider);
		}
	}

	protected Registry<Class<?>, JaxRsSingletonProvider> getSingletonRegistry() {
		return new KernelManager().
			getService(RegistryService.class).
			getRegistry(JaxRsSingletonProvider.JAXRS_SINGLETON_REGISTRY);
	}

	protected Registry<Class<?>, JaxRsPrototypeProvider> getPrototypeRegistry() {
		return new KernelManager().
			getService(RegistryService.class).
			getRegistry(JaxRsPrototypeProvider.JAXRS_PROTOTYPE_REGISTRY);
	}
}
