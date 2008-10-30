package org.sakaiproject.jpa.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpringLookup extends TestLookup
{
	private EntityManager em;

	public EntityManager entityManager()
	{
		if (em == null)
		{
			ApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "classpath:applicationContext.xml" });
			EntityManagerFactory emf = (EntityManagerFactory) context
					.getBean("org.sakaiproject.springframework.orm.jpa.EntityManagerFactory");
			em = emf.createEntityManager();
		}
		return em;
	}
}
