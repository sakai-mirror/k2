package org.sakaiproject.sdata.services.mpt;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.services.mra.MyRecentChangesBean;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;

public class MyPersonalToolsServiceDefinitionFactory implements ServiceDefinitionFactory  {

	public MyPersonalToolsServiceDefinitionFactory(){
		
		
	}
	public ServiceDefinition getSpec(HttpServletRequest request,
			HttpServletResponse response)
	{
		if (request.getRemoteUser() == null){
			try {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Logged In");
			} catch (IOException e) {}
		}
		return  new MyPersonalToolsBean(response);
	}

	public void init(Map<String, String> config)
	{
	}
	
}
