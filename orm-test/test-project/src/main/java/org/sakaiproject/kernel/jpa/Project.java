package org.sakaiproject.kernel.jpa;

import java.util.ArrayList;
import java.util.List;

public class Project
{
	private int projectId;
	private String name;
	private List<Employee> employees;

	public List<Employee> getEmployees()
	{
		return employees;
	}

	public void setEmployees(List<Employee> employees)
	{
		this.employees = employees;
	}

	public int getProjectId()
	{
		return projectId;
	}

	public void setProjectId(int projectId)
	{
		this.projectId = projectId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addEmployee(Employee e)
	{
		if (employees == null)
		{
			employees = new ArrayList<Employee>();
		}
		employees.add(e);
	}
}
