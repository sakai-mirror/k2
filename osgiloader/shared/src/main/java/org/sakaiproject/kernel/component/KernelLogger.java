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
 * The KernelLogger unwinds bundle exceptions to make them more readable.
 */
public class KernelLogger extends Logger {

  /**
   * The Logger.
   */
  private static final Log LOG = LogFactory.getLog(KernelLogger.class);

  /**
   * Log a message.
   *
   * @param sr The service reference where the message came from
   * @param msg the message
   * @param level the level of logging.
   * @param throwable an optional throwable
   */
  @Override
  protected void doLog(final ServiceReference sr, final int level, final String msg,
      final Throwable throwable) {

    // unwind throwable if it is a BundleException
    Throwable t = throwable;
    if ((throwable instanceof BundleException)
        && (((BundleException) throwable).getNestedException() != null)) {
      t = ((BundleException) throwable).getNestedException();
    }

    String s = null;
    if (sr != null) {
      s = "SvcRef " + sr;
    }
    if (s == null) {
      s = msg;
    } else {
      s = s + " " + msg;
    }
    if (t != null) {
      s = s + " (" + t + ")";
    }

    switch (level) {
    case LOG_DEBUG:
      LOG.debug(s, t);
      break;
    case LOG_ERROR:
      LOG.error(s, t);
      break;
    case LOG_INFO:
      LOG.info(s, t);
      break;
    case LOG_WARNING:
      LOG.warn(s, t);
      break;
    default:
      LOG.info(s, t);
    }
  }

}
