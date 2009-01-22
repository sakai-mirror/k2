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
package org.sakaiproject.kernel.util;

import com.google.inject.CreationException;
import com.google.inject.spi.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Map.Entry;

/**
 */
public abstract class PropertiesLoader {
  private static final Log log = LogFactory.getLog(PropertiesLoader.class);

  /**
   * The properties for the kernel
   */
  public Properties properties;

  /**
   *
   */
  public PropertiesLoader() {
    InputStream is = null;
    try {
      is = ResourceLoader.openResource(getDefaultProperties(), this.getClass()
          .getClassLoader());
      properties = new Properties();
      properties.load(is);
      log.info("Loaded " + properties.size() + " properties from "
          + getDefaultProperties());
    } catch (IOException e) {
      throw new CreationException(Arrays.asList(new Message(
          "Unable to load properties: " + getDefaultProperties())));
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        // dont care about this.
      }
    }
    // load local properties if specified as a system property
    String localPropertiesLocation = System.getenv(getLocalProperties());
    String sysLocalPropertiesLocation = System
        .getProperty(getSysLocalProperties());
    if (sysLocalPropertiesLocation != null) {
      localPropertiesLocation = sysLocalPropertiesLocation;
    }
    try {
      if (localPropertiesLocation != null
          && localPropertiesLocation.trim().length() > 0) {
        is = ResourceLoader.openResource(localPropertiesLocation, this
            .getClass().getClassLoader());
        Properties localProperties = new Properties();
        localProperties.load(is);
        for (Entry<Object, Object> o : localProperties.entrySet()) {
          String k = o.getKey().toString();
          if (k.startsWith("+")) {
            String p = properties.getProperty(k.substring(1));
            if (p != null) {
              properties.put(k.substring(1), p + o.getValue());
            } else {
              properties.put(o.getKey(), o.getValue());
            }
          } else {
            properties.put(o.getKey(), o.getValue());
          }
        }
        log.info("Loaded " + localProperties.size() + " properties from "
            + localPropertiesLocation);
      } else {
        log.info("No Local Properties Override, set system property "
            + getLocalProperties()
            + " to a resource location to override kernel properties");
      }
    } catch (IOException e) {
      log.info("Failed to startup ", e);
      throw new CreationException(Arrays.asList(new Message(
          "Unable to load properties: " + localPropertiesLocation)));
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException e) {
        // dont care about this.
      }
    }
  }

  /**
   * Get all the loaded properties.
   *
   * @return
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * @see java.util.Properties#getProperty(String)
   * @param key
   * @return
   */
  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  /**
   * @see java.util.Properties#getProperty(String, String)
   * @param key
   * @param defaultValue
   * @return
   */
  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  /**
   * The location of the default properties file.
   * 
   * @return
   */
  protected abstract String getDefaultProperties();

  /**
   * The environment variable that contains overrides to kernel properties
   *
   * @return
   */
  protected abstract String getLocalProperties();

  /**
   * The System property name that contains overrides to the kernel properties
   * resource.
   *
   * @return
   */
  protected abstract String getSysLocalProperties();
}