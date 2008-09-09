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
package org.apache.shindig.social.opensocial.sql.dao;

import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.sql.model.DbObject;

/**
 *
 */
public class DbListField extends ListFieldImpl implements DbObject, ListField {

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.model.DbObject#getId()
   */
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.model.DbObject#getObjectId()
   */
  public int getObjectId() {
    // TODO Auto-generated method stub
    return 0;
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.sql.model.DbObject#setObjectId(int)
   */
  public void setObjectId(int objectId) {
    // TODO Auto-generated method stub
  }

  /**
   * @param int1
   */
  public void setFkId(int int1) {
    // TODO Auto-generated method stub
    
  }

}
