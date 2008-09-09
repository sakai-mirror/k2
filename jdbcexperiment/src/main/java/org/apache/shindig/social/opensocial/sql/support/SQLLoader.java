/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.apache.shindig.social.opensocial.sql.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads a set of SQL statements from a properties file
 */
public class SQLLoader {
  private ConcurrentHashMap<String, MessageFormat> formatCache = new ConcurrentHashMap<String, MessageFormat>();
  private HashMap<String, String> defaultSet = new HashMap<String, String>();
  private HashMap<String, String> dialectSet = new HashMap<String, String>();
  /**
   * 
   */
  private static final long serialVersionUID = 543823707223956086L;
  private static final Log LOG = LogFactory.getLog(SQLLoader.class);

  public SQLLoader(DbModule module) throws IOException, SQLException {
    try {
      InputStream in = this.getClass().getClassLoader().getResourceAsStream(module.getSqlQueries());
      Properties p = new Properties();
      p.load(in);
      for (Entry<Object, Object> o : p.entrySet()) {
        dialectSet.put(String.valueOf(o.getKey()), String.valueOf(o.getValue()));
      }
    } catch (Exception ex) {
      LOG.warn("Failed to load dialect specific SQL from " + module.getSqlQueries());
    }
    try {
      InputStream in = this.getClass().getClassLoader().getResourceAsStream(
          module.getDefaultQueries());
      Properties p = new Properties();
      p.load(in);
      for (Entry<Object, Object> o : p.entrySet()) {
        defaultSet.put(String.valueOf(o.getKey()), String.valueOf(o.getValue()));
      }
    } catch (Exception ex) {
      LOG.warn("Failed to load generic SQL from " + module.getSqlQueries());
    }

  }

  /**
   * Format a sql string with the set of replacements.
   * 
   * @param name the name of the sql string
   * @param replacements the replacements
   * @return a formatted sql string
   */
  public String get(String name, Object[] replacements) {
    if (replacements == null) {
      return get(name);
    } else {
      MessageFormat f = formatCache.get(name);

      if (f == null) {
        String template = get(name);
        if (template == null) {
          return null;
        }
        f = new MessageFormat(template);
        formatCache.put(name, f);
      }

      MessageFormat mf = (MessageFormat) f.clone();
      return mf.format(replacements);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.HashMap#get(java.lang.Object)
   */
  private String get(Object key) {
    String s = dialectSet.get(key);
    if (s == null) {
      s = defaultSet.get(key);
    }
    return s;
  }

}
