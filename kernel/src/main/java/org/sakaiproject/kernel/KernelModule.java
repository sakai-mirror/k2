/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import net.sf.ezmorph.Morpher;
import net.sf.json.JsonConfig;

import org.apache.jackrabbit.core.security.AccessManager;
import org.sakaiproject.kernel.api.ComponentManager;
import org.sakaiproject.kernel.api.Kernel;
import org.sakaiproject.kernel.api.Provider;
import org.sakaiproject.kernel.api.ServiceManager;
import org.sakaiproject.kernel.api.ShutdownService;
import org.sakaiproject.kernel.api.authz.ReferenceResolverService;
import org.sakaiproject.kernel.api.jcr.EventRegistration;
import org.sakaiproject.kernel.api.jcr.JCRService;
import org.sakaiproject.kernel.api.rest.RestProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.api.user.AuthenticationManagerService;
import org.sakaiproject.kernel.api.user.AuthenticationResolverService;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.authz.simple.JcrReferenceResolverService;
import org.sakaiproject.kernel.authz.simple.NullUserEnvironment;
import org.sakaiproject.kernel.authz.simple.PathReferenceResolverService;
import org.sakaiproject.kernel.component.core.guice.ServiceProvider;
import org.sakaiproject.kernel.initialization.InitializationActionProvider;
import org.sakaiproject.kernel.internal.api.InitializationAction;
import org.sakaiproject.kernel.jcr.api.JcrContentListener;
import org.sakaiproject.kernel.jcr.api.internal.StartupAction;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiAccessManager;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.SakaiJCRCredentials;
import org.sakaiproject.kernel.jcr.jackrabbit.sakai.StartupActionProvider;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConfig;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConverter;
import org.sakaiproject.kernel.serialization.json.BeanProcessor;
import org.sakaiproject.kernel.serialization.json.ValueProcessor;
import org.sakaiproject.kernel.site.SiteServiceImpl;
import org.sakaiproject.kernel.user.AuthenticationResolverServiceImpl;
import org.sakaiproject.kernel.user.ProviderAuthenticationResolverService;
import org.sakaiproject.kernel.user.UserFactoryService;
import org.sakaiproject.kernel.user.jcr.JcrUserFactoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.Credentials;
import javax.servlet.http.HttpSession;

/**
 * A Guice module used to create the kernel component.
 */
public class KernelModule extends AbstractModule {

  /**
   * Location of the kernel properties.
   */
  public static final String DEFAULT_PROPERTIES = "res://kernel-component.properties";

  /**
   * the environment variable that contains overrides to kernel properties
   */
  public static final String LOCAL_PROPERTIES = "SAKAI_KERNEL_COMPONENT_PROPERTIES";

  /**
   * The System property name that contains overrides to the kernel properties
   * resource
   */
  public static final String SYS_LOCAL_PROPERTIES = "sakai.kernel.component.properties";

  /**
   * The properties for the kernel
   */
  private final Properties properties;

  /**
   * The kernel which the bootstrap component exists within.
   */
  private final Kernel kernel;

  /**
   * Create the bootstrap module with a kernel and supplied properties.
   * 
   * @param kernel
   * @param properties
   */
  public KernelModule(Kernel kernel, Properties properties) {
    this.properties = properties;
    this.kernel = kernel;
  }

  /**
   * Configure the guice bindings.
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Names.bindProperties(this.binder(), properties);
    ServiceManager serviceManager = kernel.getServiceManager();
    bind(Kernel.class).toInstance(kernel);
    bind(ServiceManager.class).toInstance(serviceManager);
    bind(ComponentManager.class).toInstance(kernel.getComponentManager());

    bind(ShutdownService.class).toProvider(
        new ServiceProvider<ShutdownService>(serviceManager,
            ShutdownService.class));

    // JCR setup
    TypeLiteral<List<StartupAction>> startupActionType = new TypeLiteral<List<StartupAction>>() {
    };
    bind(startupActionType).toProvider(StartupActionProvider.class);

    bind(Credentials.class).annotatedWith(
        Names.named(JCRService.NAME_CREDENTIALS)).to(SakaiJCRCredentials.class);

    // Kernel initialization
    TypeLiteral<List<InitializationAction>> initializationActionType = new TypeLiteral<List<InitializationAction>>() {
    };
    bind(initializationActionType).toProvider(
        InitializationActionProvider.class);
    
    bind(AccessManager.class).to(SakaiAccessManager.class);
    // Secure bind(AccessManager.class).to(SecureSakaiAccessManager.class);

    TypeLiteral<Map<String, ReferenceResolverService>> resolverMap = new TypeLiteral<Map<String, ReferenceResolverService>>() {
    };
    bind(resolverMap).toProvider(ReferenceResolverServiceProvider.class);

    bind(ReferenceResolverService.class).annotatedWith(
        Names.named(PathReferenceResolverService.DEFAULT_RESOLVER)).to(
        JcrReferenceResolverService.class);

    bind(BeanConverter.class).annotatedWith(
        Names.named(BeanConverter.REPOSITORY_BEANCONVETER)).to(
        BeanJsonLibConverter.class).in(Scopes.SINGLETON);

    // site service
    bind(SiteService.class).to(SiteServiceImpl.class).in(Scopes.SINGLETON);

    // config for the bean converter
    bind(Map.class).to(HashMap.class);
    bind(List.class).to(ArrayList.class);
    bind(Map[].class).to(HashMap[].class);
    bind(JsonConfig.class).annotatedWith(Names.named("SakaiKernelJsonConfig"))
        .to(BeanJsonLibConfig.class);

    bind(UserFactoryService.class).to(JcrUserFactoryService.class).in(
        Scopes.SINGLETON);

    bind(UserEnvironment.class).annotatedWith(
        Names.named(UserEnvironment.NULLUSERENV)).to(NullUserEnvironment.class)
        .in(Scopes.SINGLETON);

    TypeLiteral<Map<String, HttpSession>> sessionMap = new TypeLiteral<Map<String, HttpSession>>() {
    };
    bind(sessionMap).toProvider(SessionMapProvider.class);

    TypeLiteral<List<EventRegistration>> eventList = new TypeLiteral<List<EventRegistration>>() {
    };
    bind(eventList).toProvider(EventRegistrationProvider.class);

    TypeLiteral<List<JcrContentListener>> contentListeners = new TypeLiteral<List<JcrContentListener>>() {
    };
    bind(contentListeners).toProvider(JcrContentListenerProviders.class);

    TypeLiteral<List<ValueProcessor>> valueProcessors = new TypeLiteral<List<ValueProcessor>>() {
    };
    bind(valueProcessors).toProvider(ValueProcessorsProvider.class);

    TypeLiteral<List<BeanProcessor>> beanProcessors = new TypeLiteral<List<BeanProcessor>>() {
    };
    bind(beanProcessors).toProvider(BeanProcessorProvider.class);

    TypeLiteral<Map<String, Object>> jsonClassMap = new TypeLiteral<Map<String, Object>>() {
    };
    bind(jsonClassMap).annotatedWith(
        Names.named(BeanJsonLibConfig.JSON_CLASSMAP)).toProvider(
        JsonClassMapProvider.class);

    TypeLiteral<List<Morpher>> jsonMorpherList = new TypeLiteral<List<Morpher>>() {
    };
    bind(jsonMorpherList).toProvider(JsonMorpherListProvider.class);

    // bind in the cached version
    bind(AuthenticationResolverService.class).to(
        AuthenticationResolverServiceImpl.class).in(Scopes.SINGLETON);

    // then bind the provider container to the head
    bind(AuthenticationResolverService.class).annotatedWith(
        Names.named(AuthenticationResolverServiceImpl.RESOLVER_CHAIN_HEAD)).to(
        ProviderAuthenticationResolverService.class).in(Scopes.SINGLETON);

    bind(AuthenticationManagerService.class).annotatedWith(
        Names.named(AuthenticationResolverServiceImpl.RESOLVER_CHAIN_HEAD)).to(
        ProviderAuthenticationResolverService.class).in(Scopes.SINGLETON);

    // bring this list up early so it can register itself
    TypeLiteral<List<RestProvider>> restProviderList = new TypeLiteral<List<RestProvider>>() {
    };
    bind(restProviderList).toProvider(RestProviderListProvider.class)
        .asEagerSingleton();

    // this is the list of all integrtion parts, annotated to avoid it being
    // used elsewhere by mistake.
    TypeLiteral<List<Provider<String>>> integrationProviderList = new TypeLiteral<List<Provider<String>>>() {
    };
    bind(integrationProviderList).annotatedWith(
        Names.named("forced-internal-1")).toProvider(
        IntegrationProviderListProvider.class).asEagerSingleton();

  }
}
