package org.sakaiproject.sdata.services.mpt;

import java.util.Map;

import javax.servlet.ServletException;

import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.sdata.tool.json.JSONServiceHandler;

public class MyPersonalToolsHandler extends JSONServiceHandler {
	private static final long serialVersionUID = 1L;

	protected ServiceDefinitionFactory getServiceDefinitionFactory()
			throws ServletException {
		return new MyPersonalToolsServiceDefinitionFactory();
	}
	
	protected ServiceDefinitionFactory getServiceDefinitionFactory(
			Map<String, String> config) throws ServletException
	{
		return new MyPersonalToolsServiceDefinitionFactory();
	}
}
