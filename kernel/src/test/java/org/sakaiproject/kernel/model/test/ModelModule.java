/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.kernel.model.test;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import net.sf.ezmorph.Morpher;
import net.sf.json.JsonConfig;

import org.easymock.EasyMock;
import org.sakaiproject.kernel.BeanProcessorProvider;
import org.sakaiproject.kernel.JsonClassMapProvider;
import org.sakaiproject.kernel.JsonMorpherListProvider;
import org.sakaiproject.kernel.KernelModule;
import org.sakaiproject.kernel.ValueProcessorsProvider;
import org.sakaiproject.kernel.api.serialization.BeanConverter;
import org.sakaiproject.kernel.api.userenv.UserEnvironment;
import org.sakaiproject.kernel.authz.simple.NullUserEnvironment;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConfig;
import org.sakaiproject.kernel.serialization.json.BeanJsonLibConverter;
import org.sakaiproject.kernel.serialization.json.BeanProcessor;
import org.sakaiproject.kernel.serialization.json.ValueProcessor;
import org.sakaiproject.kernel.util.PropertiesLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;

/**
 * A Guice module used for testing model manipulation.
 */
public class ModelModule extends AbstractModule {


  /**
   * The properties for the kernel
   */
  private final Properties properties;

  /**
   * Create a Guice module for testing purposes. Only provides beans necessary
   * to model manipulation.
   * 
   */
  public ModelModule() {
    properties = PropertiesLoader.load(this.getClass().getClassLoader(),
        KernelModule.DEFAULT_PROPERTIES, KernelModule.LOCAL_PROPERTIES,
        KernelModule.SYS_LOCAL_PROPERTIES);
  }

  /**
   * Configure the guice bindings.
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Names.bindProperties(this.binder(), properties);

    bind(BeanConverter.class).annotatedWith(
        Names.named(BeanConverter.REPOSITORY_BEANCONVETER)).to(
        BeanJsonLibConverter.class).in(Scopes.SINGLETON);

    // config for the bean converter
    bind(Map.class).to(HashMap.class);
    bind(List.class).to(ArrayList.class);
    bind(Map[].class).to(HashMap[].class);
    bind(JsonConfig.class).annotatedWith(Names.named("SakaiKernelJsonConfig"))
        .to(BeanJsonLibConfig.class);

    bind(UserEnvironment.class).annotatedWith(
        Names.named(UserEnvironment.NULLUSERENV)).to(NullUserEnvironment.class)
        .in(Scopes.SINGLETON);

    // create some mocks to fill out the necessary classes
    bind(EntityManager.class).toInstance(
        EasyMock.createMock(EntityManager.class));

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

  }
}
