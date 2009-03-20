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
package org.sakaiproject.kernel.locking;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.locking.Lock;
import org.sakaiproject.kernel.api.locking.LockManager;
import org.sakaiproject.kernel.api.locking.LockTimeoutException;
import org.sakaiproject.kernel.api.memory.Cache;
import org.sakaiproject.kernel.api.memory.CacheManagerService;
import org.sakaiproject.kernel.api.memory.CacheScope;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A lock manager that uses a cluster replicated cache to manage the locks
 */
public class LockManagerImpl implements LockManager {

  /**
   * The name of the cluster replicated cache. This cache must be configured with a
   * suitable expiry to allow removal o stale locks and configured with a cluster wide
   * replication.
   */
  private static final String LOCKMAP = "lockmanager.lockmap";
  /**
   *
   */
  private static final String REQUEST_LOCKS = "lockmanager.requestmap";
  private CacheManagerService cacheManagerService;
  private Cache<LockImpl> lockMap;
  private long instanceId;
  private SecureRandom random;
  private ThreadLocal<Long> threadId = new ThreadLocal<Long>() {
    /**
     * {@inheritDoc}
     *
     * @see java.lang.ThreadLocal#initialValue()
     */
    @Override
    protected Long initialValue() {
      return random.nextLong();
    }
  };
  private Object monitor = new Object();

  /**
   * @throws NoSuchAlgorithmException
   *
   */
  @Inject
  public LockManagerImpl(CacheManagerService cacheManagerService)
      throws NoSuchAlgorithmException {
    this.cacheManagerService = cacheManagerService;
    lockMap = cacheManagerService.getCache(LOCKMAP, CacheScope.CLUSTERREPLICATED);
    random = SecureRandom.getInstance("SHA1PRNG");
    instanceId = random.nextLong();
  }

  public Lock getLock(String id) {
    return getLock(id, true);
  }

  /**
   * @param id
   * @return
   */
  public Lock getLock(String id, boolean create) {
    LockImpl lock = lockMap.get(id);
    if (create) {
      if (lock == null || !lock.isLocked()) {
        synchronized (monitor) {
          lock = lockMap.get(id);
          if (lock == null || !lock.isLocked()) {
            Cache<LockImpl> requestLocks = cacheManagerService.getCache(REQUEST_LOCKS,
                CacheScope.REQUEST);
            lock = new LockImpl(id, random.nextLong(), threadId.get(), instanceId);
            lockMap.put(id, lock);
            requestLocks.put(id, lock);
          }
        }
      }
    }
    lock.bind(this);
    return lock;
  }

  /**
   * Unlock only if the current thread is the owner.
   *
   * @param lock
   */
  protected void unlock(LockImpl lock) {
    if (lock.isOwner() && lock.isLocked()) {
      lock.setLocked(false);
      synchronized (monitor) {
        lockMap.remove(lock.getLocked());
      }
    }
  }

  /**
   * @return
   */
  protected long getInstanceId() {
    return instanceId;
  }

  /**
   * @return
   */
  public long getThreadId() {
    return threadId.get();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sakaiproject.kernel.api.locking.LockManager#lock(java.lang.String)
   */
  public Lock waitForLock(String id) throws LockTimeoutException {
    long sleepTime = 100;
    int tries = 0;
    while (tries++ < 300) {
      Lock lock = getLock(id);
      if (lock != null && lock.isOwner()) {
        return lock;
      }
      if (sleepTime < 500) {
        sleepTime = sleepTime + 10;
      }
      try {
        if (tries % 100 == 0) {
          System.err.println(Thread.currentThread() + " Waiting for " + sleepTime
              + " ms " + tries);
        }
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
      }
    }
    throw new LockTimeoutException("Failed to lock node "+id);
  }


}
