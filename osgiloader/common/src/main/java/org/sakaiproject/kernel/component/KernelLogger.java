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
package org.sakaiproject.kernel.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Logger;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

/**
 * 
 */
public class KernelLogger extends Logger {

  private static final Log LOG = LogFactory.getLog(KernelLogger.class);

  @Override
  protected void doLog(ServiceReference sr, int level, String msg, Throwable throwable) {

    // unwind throwable if it is a BundleException
    if ((throwable instanceof BundleException)
        && (((BundleException) throwable).getNestedException() != null)) {
      throwable = ((BundleException) throwable).getNestedException();
    }

    String s = (sr == null) ? null : "SvcRef " + sr;
    s = (s == null) ? msg : s + " " + msg;
    s = (throwable == null) ? s : s + " (" + throwable + ")";

    switch (level) {
    case LOG_DEBUG:
      LOG.debug(s,throwable);
      break;
    case LOG_ERROR:
      LOG.error(s, throwable);
      break;
    case LOG_INFO:
      LOG.info(s, throwable);
      break;
    case LOG_WARNING:
      LOG.warn(s, throwable);
      break;
    default:
      LOG.info(s, throwable);
    }
  }

}
