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
package org.sakaiproject.kernel.util;

import org.apache.jackrabbit.value.StringValue;
import org.sakaiproject.kernel.api.jcr.JCRConstants;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

/**
 *
 */
public class JcrUtils {
  /**
   * Add appropriate properties to a node to provide "smart" functionality.
   *
   * @param node
   * @param language
   * @param statement
   */
  public static void makeSmartNode(Node node, String language, String statement)
      throws RepositoryException {
    node.setProperty(JCRConstants.JCR_SMARTNODE, language + ":" + statement);
  }

  /**
   *
   * @param node
   * @param label
   * @throws RepositoryException
   * @throws IllegalArgumentException
   *           If label or node is null.
   */
  public static void addNodeLabel(Node node, String label)
      throws RepositoryException {
    // validate arguments
    if (node == null) {
      throw new IllegalArgumentException("Node must not be null.");
    }
    if (label == null) {
      throw new IllegalArgumentException("Node label must not be null.");
    }

    // get properties from node
    Value[] values = null;
    if (node.hasProperty(JCRConstants.JCR_LABELS)) {
      Property prop = node.getProperty(JCRConstants.JCR_LABELS);
      values = prop.getValues();
    } else {
      values = new Value[0];
    }

    // see if the label already exists
    boolean contains = false;
    for (Value v : values) {
      if (label.equals(v.getString())) {
        contains = true;
        break;
      }
    }

    // if not found, add new label
    if (!contains) {
      // creating array that is 1 larger than before
      Value[] newVals = new Value[values.length + 1];
      // copy old labels to new array
      for (int i = 0; i < values.length; i++) {
        newVals[i] = values[i];
      }
      // add new label
      newVals[newVals.length - 1] = new StringValue(label);
      // set values back to the node property.
      node.setProperty(JCRConstants.JCR_LABELS, newVals);
    }
  }

  public static void removeNodeLabel(Node node, String label)
      throws RepositoryException {
    // validate arguments
    if (node == null) {
      throw new IllegalArgumentException("Node must not be null.");
    }
    if (label == null) {
      throw new IllegalArgumentException("Node label must not be null.");
    }

    // get properties from node
    Value[] values = null;
    if (node.hasProperty(JCRConstants.JCR_LABELS)) {
      Property prop = node.getProperty(JCRConstants.JCR_LABELS);
      values = prop.getValues();
    } else {
      values = new Value[0];
    }

    // see if the label already exists
    boolean contains = false;
    for (Value v : values) {
      if (label.equals(v.getString())) {
        contains = true;
        break;
      }
    }

    // if found, remove label
    if (contains) {
      // creating array that is 1 larger than before
      Value[] newVals = new Value[values.length - 1];
      // copy old labels to new array, skipping label to be removed
      int newI = 0;
      for (Value value : values) {
        if (!label.equals(value.getString())) {
          newVals[newI++] = value;
        }
      }
      // set values back to the node property.
      node.setProperty(JCRConstants.JCR_LABELS, newVals);
    }
  }
}
