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

import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.sql.model.DbObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class DbList extends ArrayList<ListField> implements DbObject {

  /**
   * 
   */
  private static final long serialVersionUID = -1023085046211648515L;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.model.DbObject#getId()
   */
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.model.DbObject#getObjectId()
   */
  public int getObjectId() {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shindig.social.opensocial.sql.model.DbObject#setObjectId(int)
   */
  public void setObjectId(int objectId) {
    // TODO Auto-generated method stub

  }

  /**
   * @return
   */
  public String toSingleValue() {
    if ( size() == 0 ) {
      return null;
    } else {
      return get(0).getValue();
    }
  }
  

  /**
   * @return
   */
  public List<String> toValueList() {
    List<String> vlist = new ArrayList<String>();
    for ( ListField l : this) {
      vlist.add(l.getValue());
    }
    return vlist;
  }


}
