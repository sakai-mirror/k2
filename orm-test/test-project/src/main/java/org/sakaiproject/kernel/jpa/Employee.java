package org.sakaiproject.kernel.jpa;

import java.util.ArrayList;
import java.util.List;

public class Employee
{
	private int employeeId;
	private String firstName;
	private String lastName;
	private List<Address> addresses;
	private List<Phone> phones;
	private List<Project> projects;

	public List<Project> getProjects()
	{
		return projects;
	}

	public void setProjects(List<Project> projects)
	{
		this.projects = projects;
	}

	public int getEmployeeId()
	{
		return employeeId;
	}

	public void setEmployeeId(int employeeId)
	{
		this.employeeId = employeeId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public List<Address> getAddresses()
	{
		return addresses;
	}

	public void setAddresses(List<Address> addresses)
	{
		this.addresses = addresses;
	}

	public List<Phone> getPhones()
	{
		return phones;
	}

	public void setPhones(List<Phone> phones)
	{
		this.phones = phones;
	}

	public void addAddress(Address a)
	{
		if (addresses == null)
		{
			addresses = new ArrayList<Address>();
		}
		addresses.add(a);
	}

	public void addProject(Project p)
	{
		if (projects == null)
		{
			projects = new ArrayList<Project>();
		}
		projects.add(p);
	}
}
