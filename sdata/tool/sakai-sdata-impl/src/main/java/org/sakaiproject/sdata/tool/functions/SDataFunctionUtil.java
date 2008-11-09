package org.sakaiproject.sdata.tool.functions;

import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.api.SDataException;

public class SDataFunctionUtil
{
	public static void checkMethod(String method, String methods) throws SDataException
	{
		if (methods != null && methods.indexOf(method) < 0)
		{
			throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Function is not available for method " + method
							+ " only available on " + methods);
		}
	}

}
