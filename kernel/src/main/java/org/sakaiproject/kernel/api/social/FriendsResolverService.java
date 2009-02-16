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
package org.sakaiproject.kernel.api.social;

import com.google.inject.ImplementedBy;

import org.sakaiproject.kernel.model.FriendsBean;
import org.sakaiproject.kernel.social.FriendsResolverServiceImpl;

/**
 * The Friends resolver service resolves a FriendsBean from a user id.
 */
@ImplementedBy(FriendsResolverServiceImpl.class)
public interface FriendsResolverService {

  /**
   * The name of the friends file 
   */
  public static final String FRIENDS_FILE = "fiends.json";
  
  /**
   * Get the FriendsBean for the user uuid.
   * @param uuid
   * @return
   */
  FriendsBean resolve(String uuid);
  
  
}
