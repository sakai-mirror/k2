package org.sakaiproject.jpa.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestDirectLookup extends TestLookup
{
	private EntityManager em;

	public EntityManager entityManager()
	{
		if (em == null)
		{
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
			em = emf.createEntityManager();
		}
		return em;
	}
}
