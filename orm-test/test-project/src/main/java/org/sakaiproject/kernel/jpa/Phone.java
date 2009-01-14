package org.sakaiproject.kernel.jpa;

public class Phone
{
	private int phoneId;
	private String areaCode;
	private String prefix;
	private String number;

	public int getPhoneId()
	{
		return phoneId;
	}

	public void setPhoneId(int phoneId)
	{
		this.phoneId = phoneId;
	}

	public String getAreaCode()
	{
		return areaCode;
	}

	public void setAreaCode(String areaCode)
	{
		this.areaCode = areaCode;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}
}
