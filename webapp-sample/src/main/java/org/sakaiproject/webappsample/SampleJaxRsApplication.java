package org.sakaiproject.webappsample;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class SampleJaxRsApplication extends Application {
	@Override
	public Set<Object> getSingletons() {
		// Create JAX-RS annotated objects you like, injecting whatever
		// collaborating services you like, by any means you like.
		Set<Object> set = new HashSet<Object>();
		set.add(new SingletonHelloWorld());
		set.add(new SingletonGoodbyeWorld());
		return set;
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(PrototypeHelloWorld.class);
		classes.add(PrototypeGoodbyeWorld.class);
		return classes;
	}
}
