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
package org.sakaiproject.kernel.api.messaging;

import java.io.Serializable;
import java.util.Map;

/**
 * Base interface for all message objects. Can be used as a simple message
 * itself or extended.
 */
public interface Message extends Serializable {

  public static final String TYPE = "Message";

  /**
   * An enumeration of field names in a message.
   */
  public static enum Field {

    /** the field name for the sender. */
    FROM("From"),
    /** the field name for body. */
    BODY("Body"),
    /** the field name for title. */
    TITLE("Title"),
    /** the field name for type. */
    TYPE("Type"),
    /** headers for the message */
    HEADERS("Headers");
    /**
     * the name of the field.
     */
    private final String niceName;

    /**
     * Create a field based on a name.
     * 
     * @param jsonString
     *          the name of the field
     */
    private Field(String jsonString) {
      this.niceName = jsonString;
    }

    /**
     * @return a string representation of the enum.
     */
    @Override
    public String toString() {
      return this.niceName;
    }
  }

  /**
   * The type of a message.
   */
  public static enum Type {

    /** An email. */
    EMAIL("email"),
    /** A short private message. */
    NOTIFICATION("notification"),
    /** A message to a specific user that can be seen only by that user. */
    PRIVATE_MESSAGE("privateMessage"),
    /** A message to a specific user that can be seen by more than that user. */
    PUBLIC_MESSAGE("publicMessage");
    /**
     * The type of message.
     */
    private final String niceName;

    /**
     * Create a message type based on a string token.
     * 
     * @param jsonString
     *          the type of message
     */
    private Type(String jsonString) {
      this.niceName = jsonString;
    }

    /**
     * @return a string representation of the enum.
     */
    @Override
    public String toString() {
      return this.niceName;
    }
  }

  /**
   * Generic getter for a field.
   * 
   * @param key
   *          the key of the field to get.
   * @return the value found for the requested field. null if not found.
   */
  Serializable getField(String key);

  /**
   * Generic getter for a field. Equivalent to getField(key.toString()).
   * 
   * @param <T>
   *          the type to be returned.
   * @param key
   *          the key of the field to get.
   * @return the value found for the requested field. null if not found.
   */
  Serializable getField(Enum<?> key);

  /**
   * Retrieves all fields stored on the message.
   * 
   * @return {@link java.util.Map}<String, Object> of fields with non-null keys
   *         and values.
   */
  Map<String, Serializable> getFields();

  /**
   * Generic setter for a field.
   * 
   * @param <T>
   *          the type of the value being set.
   * @param key
   *          the field to set a value to.
   * @param value
   *          the value to set.
   */
  void setField(String key, Serializable value);

  /**
   * Generic setter for a field. Equivalent to setField(key.toString, value).
   * 
   * @param key
   *          the field to set a value to.
   * @param value
   *          the value to set.
   */
  void setField(Enum<?> key, Serializable value);

  /**
   * Add a header to the message.
   * 
   * @param key
   *          key of the header.
   * @param value
   *          value of the header.
   */
  void setHeader(String key, String value);

  /**
   * Remove a header from the message.
   * 
   * @param key
   *          key of header to remove.
   */
  void removeHeader(String key);

  /**
   * Removes a field from the message.
   * 
   * @param key
   *          the key of the field to be removed.
   */
  void removeField(String key);

  /**
   * Removes a field from the message.
   * 
   * @param key
   *          the key of the field to be removed.
   */
  void removeField(Enum<?> key);

  /**
   * Gets the main text of the message.
   * 
   * @return the main text of the message
   */
  Serializable getBody();

  /**
   * Sets the main text of the message. HTML attributes are allowed and are
   * sanitized by the container
   * 
   * @param newBody
   *          the main text of the message
   */
  void setBody(Serializable newBody);

  /**
   * Gets the title of the message.
   * 
   * @return the title of the message
   */
  String getTitle();

  /**
   * Sets the title of the message. HTML attributes are allowed and are
   * sanitized by the container.
   * 
   * @param newTitle
   *          the title of the message
   */
  void setTitle(String newTitle);

  /**
   * Gets the type of the message.
   * 
   * @return the type of message
   * @see Type
   */
  String getType();

  /**
   * Sets the type of the message.
   * 
   * @param newType
   *          the type of message (enum Message.Type)
   * @see Type
   */
  void setType(String newType);

  void send();
}
