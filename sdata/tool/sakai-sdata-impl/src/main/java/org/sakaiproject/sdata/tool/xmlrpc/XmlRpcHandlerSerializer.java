package org.sakaiproject.sdata.tool.xmlrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.TypeFactory;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.serializer.DefaultXMLWriterFactory;
import org.apache.xmlrpc.serializer.XmlRpcWriter;
import org.apache.xmlrpc.serializer.XmlWriterFactory;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.HandlerSerialzer;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class XmlRpcHandlerSerializer implements HandlerSerialzer {


	
	private XmlWriterFactory writerFactory = new DefaultXMLWriterFactory();

	private XmlRpcStreamRequestConfig pConfig = new XmlRpcStreamRequestConfigImpl();

	private TypeFactory typeFactory = new XmlRpcTypeFactory(pConfig);

	public void sendError(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Throwable ex) throws IOException {
		if (ex instanceof SDataException)
		{

			SDataException sde = (SDataException) ex;
			response.reset();
			handler.setHandlerHeaders(request, response);
			response.sendError(sde.getCode(), sde.getMessage());
		}
		else
		{
			XmlRpcWriter xw;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try
			{
				xw = getXmlRpcWriter(pConfig, baos);
			}
			catch (XmlRpcException e)
			{
				throw new IOException("Failed to get RpcWriter  " + e.getMessage());
			}
			try
			{
				xw.write(pConfig, 500, ex.getMessage(), ex);
				baos.flush();
				byte[] out = baos.toByteArray();
				baos.close();
				response.setContentLength(out.length);
				response.setContentType("text/xml");
				response.setCharacterEncoding("UTF-8");
				response.getOutputStream().write(out);

			}
			catch (SAXException e)
			{
				throw new IOException("Failed to write response " + e.getMessage());
			}
		}
	}

	public void sendMap(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> contentMap)
			throws IOException {
		XmlRpcWriter xw;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			xw = getXmlRpcWriter(pConfig, baos);
		}
		catch (XmlRpcException e)
		{
			throw new IOException("Failed to get RpcWriter  " + e.getMessage());
		}
		try
		{
			xw.write(pConfig, contentMap);
			baos.flush();
			byte[] out = baos.toByteArray();
			baos.close();
			response.setContentLength(out.length);
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			response.getOutputStream().write(out);
		}
		catch (SAXException e)
		{
			throw new IOException("Failed to write response " + e.getMessage());
		}

	}
	
	/**
	 * TODO Javadoc
	 * 
	 * @param pConfig
	 * @param pStream
	 * @return
	 * @throws XmlRpcException
	 */
	protected XmlRpcWriter getXmlRpcWriter(XmlRpcStreamRequestConfig pConfig,
			OutputStream pStream) throws XmlRpcException
	{
		ContentHandler w = getXMLWriterFactory().getXmlWriter(pConfig, pStream);
		return new XmlRpcWriter(pConfig, w, getTypeFactory());
	}

	/**
	 * TODO Javadoc
	 * 
	 * @return
	 */
	protected XmlWriterFactory getXMLWriterFactory()
	{
		return writerFactory;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @return
	 */
	protected TypeFactory getTypeFactory()
	{
		return typeFactory;
	}


}
