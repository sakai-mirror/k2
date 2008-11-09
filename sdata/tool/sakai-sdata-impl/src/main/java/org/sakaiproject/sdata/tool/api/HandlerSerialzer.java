/**
 * 
 */
package org.sakaiproject.sdata.tool.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ieb
 *
 */
public interface HandlerSerialzer {
	/**
	 * Sends an error to the client
	 * 
	 * @param ex
	 * @throws IOException
	 */
	void sendError(Handler handler, HttpServletRequest request, HttpServletResponse response, Throwable ex)
			throws IOException;

	/**
	 * Serailize a Map strucutre to the output stream
	 * 
	 * @param uploads
	 * @throws IOException
	 */
	void sendMap( HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> contetMap) throws IOException;

}
