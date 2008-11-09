/**
 * 
 */
package org.sakaiproject.sdata.tool.test;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.HandlerSerialzer;

/**
 * @author ieb
 *
 */
public class NullHandlerSerializer  implements HandlerSerialzer{

	public void sendError(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Throwable ex) throws IOException {
		
	}

	public void sendMap(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> contetMap)
			throws IOException {
		
	}

}
