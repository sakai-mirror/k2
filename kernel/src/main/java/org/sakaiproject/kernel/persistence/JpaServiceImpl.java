package org.sakaiproject.kernel.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.sakaiproject.kernel.api.persistence.JpaService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JpaServiceImpl implements JpaService {
  private DataSource dataSource;

  private static EntityManagerFactory emf;
  private static EntityManager em;

  // Standard JPA JTA DataSource name.
  private static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

  // Standard JPA non-JTA DataSource name.
  private static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";

  // Default persistence unit name to retrieve
  private static final String DEFAULT_PERSISTENCE_UNIT = "org.sakaiproject";

  @Inject
  public JpaServiceImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public EntityManagerFactory entityManagerFactory() {
    HashMap<String, DataSource> props = new HashMap<String, DataSource>();
    props.put(NON_JTA_DATASOURCE, dataSource);
    EntityManagerFactory emf = entityManagerFactory(DEFAULT_PERSISTENCE_UNIT,
        props);
    return emf;
  }

  @SuppressWarnings("unchecked")
  protected EntityManagerFactory entityManagerFactory(
      String persistenceUnitName, Map props) {
    if (emf == null) {
      emf = Persistence.createEntityManagerFactory(persistenceUnitName, props);
    }
    return emf;
  }

  public EntityManager entityManager() {
    if (em == null) {
      em = entityManagerFactory().createEntityManager();
    }
    return em;
  }
}
