package org.sakaiproject.kernel.api.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Service to provide access to JPA entities and factories.
 */
public interface JpaService {
  /**
   * 
   * @return
   */
  EntityManagerFactory entityManagerFactory();

  /**
   * 
   * @return
   */
  EntityManager entityManager();
}
