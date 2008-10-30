package org.sakaiproject.jpa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.jpa.Address;
import org.sakaiproject.jpa.Employee;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpringLookup
{
	static EntityManagerFactory emf;
	static EntityManager em;

	@BeforeClass
	public static void oneTimeSetup() throws Exception
	{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:applicationContext.xml" });
		emf = (EntityManagerFactory) context
				.getBean("org.sakaiproject.springframework.orm.jpa.EntityManagerFactory");
		em = emf.createEntityManager();

		em.getTransaction().begin();
		// create some addresses
		ArrayList<Address> adds = new ArrayList<Address>();

		Address add = new Address();
		add.setStreet("123 Main St.");
		add.setCity("Mainville");
		add.setState("AA");
		em.persist(add);
		add = new Address();
		add.setStreet("128 Main St.");
		add.setCity("Maintown");
		add.setState("AB");
		em.persist(add);

		Employee emp = new Employee();
		emp.setFirstName("Carl");
		emp.setLastName("Hall");
		// adds.add(add);
		emp.setAddresses(adds);
		em.persist(emp);

		emp = new Employee();
		emp.setFirstName("Michelle");
		emp.setLastName("Hall");
		em.persist(emp);

		emp = new Employee();
		emp.setFirstName("Stuart");
		emp.setLastName("Freeman");
		em.persist(emp);

		emp = new Employee();
		emp.setFirstName("Clay");
		emp.setLastName("Fenlason");
		em.persist(emp);

		emp = new Employee();
		emp.setFirstName("Clay");
		emp.setLastName("Smith");
		em.persist(emp);

		em.flush();
	}

	@AfterClass
	public static void oneTimeTearDown()
	{
		em.getTransaction().rollback();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAllEmployees()
	{
		Query selectAllEmployees = em.createQuery("select e from Employee e");
		List<Employee> employees = (List<Employee>) selectAllEmployees.getResultList();
		assertNotNull(employees);
		assertTrue(employees.size() > 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSimpleSelect()
	{
		//
		// FIRST NAME
		//
		// single first name entry
		Query selectByFirstName = em.createQuery("select e from Employee e where e.firstName = ?1");
		selectByFirstName.setParameter(1, "Carl");
		List<Employee> employees = (List<Employee>) selectByFirstName.getResultList();
		assertNotNull(employees);
		assertEquals(1, employees.size());

		// multiple first name entries
		selectByFirstName.setParameter(1, "Clay");
		employees = (List<Employee>) selectByFirstName.getResultList();
		assertNotNull(employees);
		assertEquals(2, employees.size());

		// non-existent entry
		selectByFirstName.setParameter(1, "Bobby");
		employees = (List<Employee>) selectByFirstName.getResultList();
		assertNotNull(employees);
		assertEquals(0, employees.size());

		//
		// LAST NAME
		//
		// single last name entry
		Query selectByLastName = em.createQuery("select e from Employee e where e.lastName = ?1");
		selectByLastName.setParameter(1, "Freeman");
		employees = (List<Employee>) selectByLastName.getResultList();
		assertNotNull(employees);
		assertEquals(1, employees.size());

		// multiple last name entries
		selectByLastName.setParameter(1, "Hall");
		employees = (List<Employee>) selectByLastName.getResultList();
		assertNotNull(employees);
		assertEquals(2, employees.size());

		// non-existent entry
		selectByLastName.setParameter(1, "Jones");
		employees = (List<Employee>) selectByLastName.getResultList();
		assertNotNull(employees);
		assertEquals(0, employees.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMultiSelect()
	{
		Query selectByFirstLastName = em
				.createQuery("select e from Employee e where e.firstName = ?1 and e.lastName = ?2");
		selectByFirstLastName.setParameter(1, "Carl").setParameter(2, "Hall");
		List<Employee> employees = (List<Employee>) selectByFirstLastName.getResultList();
		assertNotNull(employees);
		assertEquals(1, employees.size());

		selectByFirstLastName = em
				.createQuery("select e from Employee e where e.firstName = ?1 and e.lastName = ?2");
		selectByFirstLastName.setParameter(1, "Carl").setParameter(2, "Johnson");
		employees = (List<Employee>) selectByFirstLastName.getResultList();
		assertNotNull(employees);
		assertEquals(0, employees.size());

		selectByFirstLastName = em
				.createQuery("select e from Employee e where e.firstName = ?1 and e.lastName = ?2");
		selectByFirstLastName.setParameter(1, "Tommy").setParameter(2, "Hall");
		employees = (List<Employee>) selectByFirstLastName.getResultList();
		assertNotNull(employees);
		assertEquals(0, employees.size());

		selectByFirstLastName = em
				.createQuery("select e from Employee e where e.firstName = ?1 and e.lastName = ?2");
		selectByFirstLastName.setParameter(1, "Wrong").setParameter(2, "Dude");
		employees = (List<Employee>) selectByFirstLastName.getResultList();
		assertNotNull(employees);
		assertEquals(0, employees.size());
	}
}
