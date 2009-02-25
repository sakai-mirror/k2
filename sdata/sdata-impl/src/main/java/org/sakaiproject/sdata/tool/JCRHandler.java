/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */


package org.sakaiproject.sdata.tool;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.fileupload.sdata.FileItemIterator;
import org.apache.commons.fileupload.sdata.FileItemStream;
import org.apache.commons.fileupload.sdata.servlet.ServletFileUpload;
import org.apache.commons.fileupload.sdata.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.authz.PermissionDeniedException;
import org.sakaiproject.kernel.api.authz.UnauthorizedException;
import org.sakaiproject.kernel.api.jcr.JCRConstants;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryService;
import org.sakaiproject.kernel.api.jcr.support.JCRNodeFactoryServiceException;
import org.sakaiproject.kernel.webapp.RestServiceFaultException;
import org.sakaiproject.sdata.tool.api.HandlerSerialzer;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SDataFunction;
import org.sakaiproject.sdata.tool.model.JCRNodeMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * JCR Service is a servlet that gives access to the JCR returning the content
 * of files within the jcr or a map response (directories). The resource is
 * pointed to using the URI/URL requested (the path info part), and the standard
 * Http methods do what they are expected to in the http standard. GET gets the
 * content of the file, PUT put puts a new file, the content coming from the
 * stream of the PUT. DELETE deleted the file. HEAD gets the headers that would
 * come from a full GET.
 * </p>
 * <p>
 * The content type and content encoding headers are honored for GET,HEAD and
 * PUT, but other headers are not honored completely at the moment (range-*)
 * etc,
 * </p>
 * <p>
 * POST takes multipart uploads of content, the URL pointing to a folder and
 * each upload being the name of the file being uploaded to that folder. The
 * upload uses a streaming api, and expects that form fields are ordered, such
 * that a field starting with mimetype before the upload stream will specify the
 * mimetype associated with the stream.
 * </p>
 * 
 * @author ieb
 */
public class JCRHandler extends AbstractHandler {
  private static final Log log = LogFactory.getLog(JCRHandler.class);

  /**
   * Required for serialization... also to stop eclipse from giving me a
   * warning!
   */
  private static final long serialVersionUID = 676743152200357708L;

  private static final String LAST_MODIFIED = "Last-Modified";

  private static final String REAL_UPLOAD_NAME = "realname";

  private static final String BASE_NAME = "jcrhandler";

  public static final String BASE_REPOSITORY_PATH = BASE_NAME
      + ".baseRepositoryPath";

  public static final String RESOURCE_DEFINITION_FACTORY = BASE_NAME
      + ".resourceDefinitionFactory";

  public static final String RESOURCE_FUNCTION_FACTORY = BASE_NAME
      + ".resourceFuntionFactory";

  public static final String RESOURCE_SERIALIZER = BASE_NAME
      + ".resourceSerialzer";

  public static final String SECURITY_ASSERTION = BASE_NAME
      + ".securityAssertion";

  public static final String LOCK_DEFINITION = BASE_NAME + ".lockDefinition";

  public static final String BASE_SECURED_PATH = BASE_NAME + ".securedPath";

  private static final String KEY = "f";

  private transient JCRNodeFactoryService jcrNodeFactory;

  private transient ResourceDefinitionFactory resourceDefinitionFactory;

  private Map<String, SDataFunction> resourceFunctionFactory;

  /**
   * Create a JCRHandler and give it a resource definition factory that will
   * convert a URL into a location in the repository.
   * 
   * @param serializer
   * @param securityAssertion
   * 
   */
  @Inject
  public JCRHandler(
      JCRNodeFactoryService jcrNodeFactory,
      @Named(RESOURCE_DEFINITION_FACTORY) ResourceDefinitionFactory resourceDefinitionFactory,
      @Named(RESOURCE_FUNCTION_FACTORY) Map<String, SDataFunction> resourceFunctionFactory,
      @Named(RESOURCE_SERIALIZER) HandlerSerialzer serializer) {
    this.jcrNodeFactory = jcrNodeFactory;
    this.resourceDefinitionFactory = resourceDefinitionFactory;
    this.resourceFunctionFactory = resourceFunctionFactory;
    this.serializer = serializer;

  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {

      snoopRequest(request);

      ResourceDefinition rp = resourceDefinitionFactory.getSpec(request);
      Node n = jcrNodeFactory.getNode(rp.getRepositoryPath());
      if (n == null) {
        response.reset();
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
      NodeType nt = n.getPrimaryNodeType();

      long lastModifiedTime = -10;
      if (JCRConstants.NT_FILE.equals(nt.getName())) {

        Node resource = n.getNode(JCRConstants.JCR_CONTENT);
        Property lastModified = resource
            .getProperty(JCRConstants.JCR_LASTMODIFIED);
        lastModifiedTime = lastModified.getDate().getTimeInMillis();

        if (!checkPreconditions(request, response, lastModifiedTime, String
            .valueOf(lastModifiedTime))) {
          return;
        }
      }

      Session s = n.getSession();
      n.remove();
      s.save();
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);

    } catch (Exception e) {
      sendError(request, response, e);

      snoopRequest(request);
      log.error("Failed  TO service Request ", e);
    }
  }

  /**
   * Snoop on the request if the request parameter snoop=1 output appears in the
   * log, at level INFO
   * 
   * @param request
   */
  private void snoopRequest(HttpServletRequest request) {
    boolean snoop = "1".equals(request.getParameter("snoop"));
    if (snoop) {
      StringBuilder sb = new StringBuilder("SData Request :");
      sb.append("\n\tRequest Path :").append(request.getPathInfo());
      sb.append("\n\tMethod :").append(request.getMethod());
      for (Enumeration<?> hnames = request.getHeaderNames(); hnames
          .hasMoreElements();) {
        String name = (String) hnames.nextElement();
        sb.append("\n\tHeader :").append(name).append("=[").append(
            request.getHeader(name)).append("]");
      }
      if (request.getCookies() != null) {
        for (Cookie c : request.getCookies()) {
          sb.append("\n\tCookie:");
          sb.append("name[").append(c.getName());
          sb.append("]path[").append(c.getPath());
          sb.append("]value[").append(c.getValue());
        }
      }
      sb.append("]");
      log.info(sb.toString());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.sdata.tool.api.Handler#doHead(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doHead(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      log.info("Doing Head ");
      snoopRequest(request);

      ResourceDefinition rp = resourceDefinitionFactory.getSpec(request);
      Node n = jcrNodeFactory.getNode(rp.getRepositoryPath());
      if (n == null) {
        response.reset();
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      Node resource = n.getNode(JCRConstants.JCR_CONTENT);
      Property lastModified = resource
          .getProperty(JCRConstants.JCR_LASTMODIFIED);
      Property mimeType = resource.getProperty(JCRConstants.JCR_MIMETYPE);
      Property content = resource.getProperty(JCRConstants.JCR_DATA);

      response.setContentType(mimeType.getString());
      if (mimeType.getString().startsWith("text")) {
        if (resource.hasProperty(JCRConstants.JCR_ENCODING)) {
          Property encoding = resource.getProperty(JCRConstants.JCR_ENCODING);
          response.setCharacterEncoding(encoding.getString());
        }
      }
      response.setDateHeader(LAST_MODIFIED, lastModified.getDate()
          .getTimeInMillis());
      // we need to do something about huge files
      response.setContentLength((int) content.getLength());
      response.setStatus(HttpServletResponse.SC_OK);

    } catch (Exception e) {
      sendError(request, response, e);

      snoopRequest(request);
      log.error("Failed  TO service Request ", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.sdata.tool.api.Handler#doPut(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      snoopRequest(request);


      ResourceDefinition rp = resourceDefinitionFactory.getSpec(request);
      String mimeType = ContentTypes.getContentType(rp.getRepositoryPath(), request
          .getContentType());
      String charEncoding = null;
      if (mimeType.startsWith("text")) {
        charEncoding = request.getCharacterEncoding();
      }
      Node n = jcrNodeFactory.getNode(rp.getRepositoryPath());
      boolean created = false;
      if (n == null) {
        n = jcrNodeFactory.createFile(rp.getRepositoryPath(),mimeType);
        created = true;
        if (n == null) {
          throw new RuntimeException("Failed to create node at "
              + rp.getRepositoryPath() + " type " + JCRConstants.NT_FILE);
        }
      } else {
        NodeType nt = n.getPrimaryNodeType();
        if (!JCRConstants.NT_FILE.equals(nt.getName())) {
          response.reset();
          response.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "Content Can only be put to a file, resource type is "
                  + nt.getName());
          return;
        }
      }

      GregorianCalendar gc = new GregorianCalendar();
      long lastMod = request.getDateHeader(LAST_MODIFIED);
      if (lastMod > 0) {
        gc.setTimeInMillis(lastMod);
      } else {
        gc.setTime(new Date());
      }

      InputStream in = request.getInputStream();
      saveStream(n, in, mimeType, charEncoding, gc);

      in.close();
      if (created) {
        response.setStatus(HttpServletResponse.SC_CREATED);
      } else {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
      }
    } catch (UnauthorizedException ape) {
      // catch any Unauthorized exceptions and send a 401
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ape.getMessage());
    } catch (PermissionDeniedException pde) {
      // catch any permission denied exceptions, and send a 403
      response.reset();
      response.sendError(HttpServletResponse.SC_FORBIDDEN, pde.getMessage());
    } catch (SDataException e) {
      sendError(request, response, e);
      log.error("Failed  To service Request " + e.getMessage());
    } catch (Exception e) {
      sendError(request, response, e);
      snoopRequest(request);
      log.error("Failed  TO service Request ", e);
    }
  }

  /**
   * Save the input stream into the JCR storage,
   * 
   * @param n
   *          the target node
   * @param in
   *          the input stream from the request.
   * @param mimeType
   *          the mime type of the content
   * @param charEncoding
   *          the character encoding of the content
   * @param lastModified
   *          the time when the content was last modified
   * @throws RepositoryException
   */
  private long saveStream(Node n, InputStream in, String mimeType,
      String charEncoding, Calendar lastModified) throws RepositoryException {
    Node resource = n.getNode(JCRConstants.JCR_CONTENT);
    resource.setProperty(JCRConstants.JCR_LASTMODIFIED, lastModified);
    resource.setProperty(JCRConstants.JCR_MIMETYPE, mimeType);
    resource.setProperty(JCRConstants.JCR_ENCODING, charEncoding);

    Property content = resource.getProperty(JCRConstants.JCR_DATA);

    content.setValue(in);

    n.save();

    return content.getLength();
  }

  /*
   * private Map<String, Object> createProgressMap(String progressID) { if
   * (progressID == null) { return null; } Map<String, Object> progressMap =
   * ProgressHandler.getMap(progressID); if ( progressMap == null ) {
   * synchronized (newProgressMapMutex ) { progressMap =
   * ProgressHandler.getMap(progressID); if ( progressMap == null ) {
   * progressMap = new ConcurrentHashMap<String, Object>();
   * ProgressHandler.setMap(progressID, progressMap); } } } return progressMap;
   * } private void clearProgress() { ProgressHandler.clearMap(); } private
   * Map<String, Object> getProgress(String progressID) { return
   * ProgressHandler.getMap(progressID); }
   */
  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.sdata.tool.api.Handler#doGet(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doGet(final HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    OutputStream out = null;
    InputStream in = null;
    try {
      snoopRequest(request);

      ResourceDefinition rp = resourceDefinitionFactory.getSpec(request);

      Node n = jcrNodeFactory.getNode(rp.getRepositoryPath());
      if (n == null) {
        response.reset();
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      NodeType nt = n.getPrimaryNodeType();

      SDataFunction m = resourceFunctionFactory.get(rp.getFunctionDefinition());
      if (m != null) {
        m.call(this, request, response, n, rp);
      } else {

        if (JCRConstants.NT_FILE.equals(nt.getName())) {

          Node resource = n.getNode(JCRConstants.JCR_CONTENT);
          Property lastModified = resource
              .getProperty(JCRConstants.JCR_LASTMODIFIED);
          Property mimeType = resource.getProperty(JCRConstants.JCR_MIMETYPE);
          Property content = resource.getProperty(JCRConstants.JCR_DATA);

          response.setContentType(mimeType.getString());
          if (mimeType.getString().startsWith("text")) {
            if (resource.hasProperty(JCRConstants.JCR_ENCODING)) {
              Property encoding = resource
                  .getProperty(JCRConstants.JCR_ENCODING);
              response.setCharacterEncoding(encoding.getString());
            }
          }
          response.setDateHeader(LAST_MODIFIED, lastModified.getDate()
              .getTimeInMillis());
          setGetCacheControl(response, rp.isPrivate());

          String currentEtag = String.valueOf(lastModified.getDate()
              .getTimeInMillis());
          response.setHeader("ETag", currentEtag);

          long lastModifiedTime = lastModified.getDate().getTimeInMillis();

          if (!checkPreconditions(request, response, lastModifiedTime,
              currentEtag)) {
            return;
          }
          long totallength = content.getLength();
          long[] ranges = new long[2];
          ranges[0] = 0;
          ranges[1] = totallength;
          if (!checkRanges(request, response, lastModifiedTime, currentEtag,
              ranges)) {
            return;
          }

          long length = ranges[1] - ranges[0];

          if (totallength != length) {
            response.setHeader("Accept-Ranges", "bytes");
            response.setDateHeader("Last-Modified", lastModifiedTime);
            response.setHeader("Content-Range", "bytes " + ranges[0] + "-"
                + (ranges[1] - 1) + "/" + totallength);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

            log.info("Partial Content Sent "
                + HttpServletResponse.SC_PARTIAL_CONTENT);
          } else {
            response.setStatus(HttpServletResponse.SC_OK);
          }

          response.setContentLength((int) length);

          out = response.getOutputStream();

          in = content.getStream();
          long loc = in.skip(ranges[0]);
          if (  loc != ranges[0] ) {
            throw new RestServiceFaultException(HttpServletResponse.SC_BAD_REQUEST,"Range specified is invalid asked for "+ranges[0]+" got "+loc);
          }
          byte[] b = new byte[10240];
          int nbytes = 0;
          while ((nbytes = in.read(b)) > 0 && length > 0) {
            if (nbytes < length) {
              out.write(b, 0, nbytes);
              length = length - nbytes;
            } else {
              out.write(b, 0, (int) length);
              length = 0;
            }
          }
        } else {
          setGetCacheControl(response, rp.isPrivate());

          // Property lastModified =
          // n.getProperty(JCRConstants.JCR_LASTMODIFIED);
          // response.setHeader("ETag",
          // String.valueOf(lastModified.getDate()
          // .getTimeInMillis()));

          JCRNodeMap outputMap = new JCRNodeMap(n, rp.getDepth(), rp);
          sendMap(request, response, outputMap);
        }
      }
    } catch (UnauthorizedException ape) {
      // catch any Unauthorized exceptions and send a 401
      response.reset();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ape.getMessage());
    } catch (PermissionDeniedException pde) {
      // catch any permission denied exceptions, and send a 403
      response.reset();
      response.sendError(HttpServletResponse.SC_FORBIDDEN, pde.getMessage());

    } catch (SDataException e) {
      log.error("Failed  To service Request " + e.getMessage());
      e.printStackTrace();
      sendError(request, response, e);
    } catch (Exception e) {
      log.error("Failed  TO service Request ", e);
      sendError(request, response, e);
      snoopRequest(request);
    } finally {

      try {
        out.close();
      } catch (Exception ex) {
      }
      try {
        in.close();
      } catch (Exception ex) {
      }
    }
  }

  /**
   * Check the ranges requested in the request headers, this conforms to the RFC
   * on the range, if-range headers. On return, it the request is to be
   * processed, true will be returned, and ranges[0] will the the start byte of
   * the response stream and ranges[1] will be the end byte.
   * 
   * @param request
   *          the request object from the Servlet Container.
   * @param response
   *          the response object from the servlet container.
   * @param lastModifiedTime
   *          the last modified time from target object
   * @param currentEtag
   *          the Etag
   * @param ranges
   *          ranges setup to contain the start and end byte offsets
   * @return true if the response is to contain data, false if not.
   * @throws IOException
   */
  private boolean checkRanges(HttpServletRequest request,
      HttpServletResponse response, long lastModifiedTime, String currentEtag,
      long[] ranges) throws IOException {

    String range = request.getHeader("range");
    long ifRangeDate = request.getDateHeader("if-range");
    String ifRangeEtag = request.getHeader("if-range");

    if (ifRangeDate != -1 && lastModifiedTime > ifRangeDate) {
      // the entity has been modified, ignore and send the whole lot
      return true;
    }
    if (ifRangeEtag != null && !currentEtag.equals(ifRangeEtag)) {
      // the entity has been modified, ignore and send the whole lot
      return true;
    }

    if (range != null) {
      String[] s = range.split("=");
      if (!"bytes".equals(s[0])) {
        response.reset();
        response.sendError(416,
            "System only supports single range responses, specified in bytes");
        return false;
      }
      range = s[1];
      String[] r = range.split(",");
      if (r.length > 1) {
        response.reset();
        response.sendError(416, "System only supports single range responses");
        return false;
      }
      range = range.trim();
      if (range.startsWith("-")) {
        ranges[1] = Long.parseLong(range.substring(1));
      } else if (range.endsWith("-")) {
        ranges[0] = Long.parseLong(range.substring(0, range.length() - 1));
      } else {
        r = range.split("-");
        ranges[0] = Long.parseLong(r[0]);
        ranges[1] = Long.parseLong(r[1]);
      }
    }
    return true;
  }

  /**
   * Evaluate pre-conditions, based on the request, as per the http rfc.
   * 
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  private boolean checkPreconditions(HttpServletRequest request,
      HttpServletResponse response, long lastModifiedTime, String currentEtag)
      throws IOException {
    lastModifiedTime = lastModifiedTime - (lastModifiedTime % 1000);
    long ifUnmodifiedSince = request.getDateHeader("if-unmodified-since");
    if (ifUnmodifiedSince > 0 && (lastModifiedTime >= ifUnmodifiedSince)) {
      response.reset();
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return false;
    }

    String ifMatch = request.getHeader("if-match");
    if (ifMatch != null && ifMatch.indexOf(currentEtag) < 0) {
      // ifMatch was present, but the currentEtag didnt match
      response.reset();
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return false;
    }
    String ifNoneMatch = request.getHeader("if-none-match");
    if (ifNoneMatch != null && ifNoneMatch.indexOf(currentEtag) >= 0) {
      if ("GET|HEAD".indexOf(request.getMethod()) >= 0) {
        response.reset();
        response.sendError(HttpServletResponse.SC_NOT_MODIFIED);

      } else {
        // ifMatch was present, but the currentEtag didnt match
        response.reset();
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      }
      return false;
    }
    long ifModifiedSince = request.getDateHeader("if-modified-since");
    if ((ifModifiedSince > 0) && (lastModifiedTime <= ifModifiedSince)) {
      response.reset();
      response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
      return false;
    }
    return true;
  }

  /**
   * Set the cache control headers suitable for all HTTP protocol versions
   * 
   * @param response
   */
  private void setGetCacheControl(HttpServletResponse response,
      boolean isprivate) {
    if (isprivate) {
      response.addHeader("Cache-Control", "must-revalidate");
      response.addHeader("Cache-Control", "private");
      response.addHeader("Cache-Control", "no-store");
    } else {
      // response.addHeader("Cache-Control", "must-revalidate");
      response.addHeader("Cache-Control", "public");
    }
    response.addHeader("Cache-Control", "max-age=600");
    response.addHeader("Cache-Control", "s-maxage=600");
    response.setDateHeader("Date", System.currentTimeMillis());
    response.setDateHeader("Expires", System.currentTimeMillis() + 600000);

  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.sakaiproject.sdata.tool.api.Handler#doPost(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getRemoteUser() == null) {
      log.info("No User, denied ");
      response.sendError(401);
    } else {
      snoopRequest(request);
      try {
        ResourceDefinition rp = resourceDefinitionFactory.getSpec(request);
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        // multiparts are always streamed uploads
        if (isMultipart) {
          doMumtipartUpload(request, response, rp);
        } else {

          Node n = jcrNodeFactory.getNode(rp.getRepositoryPath());

          SDataFunction m = resourceFunctionFactory.get(rp
              .getFunctionDefinition());
          if (m != null) {
            m.call(this, request, response, n, rp);
          } else {
            log.info("NOP Post performed");
            throw new SDataException(HttpServletResponse.SC_NOT_FOUND,
                "Method not found ");
          }

        }
      } catch (UnauthorizedException ape) {
        // catch any Unauthorized exceptions and send a 401
        log.info(ape);
        response.reset();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ape
            .getMessage());
      } catch (PermissionDeniedException pde) {
        // catch any permission denied exceptions, and send a 403
        log.info(pde);
        response.reset();
        response.sendError(HttpServletResponse.SC_FORBIDDEN, pde.getMessage());
      } catch (SDataException sde) {
        log.info(sde);
        sendError(request, response, sde);

      } catch (RepositoryException rex) {
        log.info(rex);
        sendError(request, response, rex);
      } catch (JCRNodeFactoryServiceException jfe) {
        log.info(jfe);
        sendError(request, response, jfe);
      }
    }

  }

  /**
   * Perform a mime multipart upload into the JCR repository based on a location
   * specified by the rp parameter. The parts of the multipart upload are
   * relative to the current request path
   * 
   * @param request
   *          the request object of the current request.
   * @param response
   *          the response object of the current request
   * @param rp
   *          the resource definition for the current request
   * @throws ServletException
   * @throws IOException
   */
  private void doMumtipartUpload(HttpServletRequest request,
      HttpServletResponse response, ResourceDefinition rp)
      throws ServletException, IOException {
    try {
      try {
        Node n = jcrNodeFactory.createFolder(rp.getRepositoryPath());
        if (n == null) {
          response.reset();
          response.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "Unable to uplaod to location " + rp.getRepositoryPath());
          return;
        }
      } catch (Exception ex) {
        sendError(request, response, ex);
        snoopRequest(request);
        log.error("Failed  TO service Request ", ex);
        return;
      }

      // Check that we have a file upload request

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload();
      List<String> errors = new ArrayList<String>();

      // Parse the request
      FileItemIterator iter = upload.getItemIterator(request);
      Map<String, Object> responseMap = new HashMap<String, Object>();
      Map<String, Object> uploads = new HashMap<String, Object>();
      Map<String, List<String>> values = new HashMap<String, List<String>>();
      int uploadNumber = 0;
      while (iter.hasNext()) {
        FileItemStream item = iter.next();
        log.debug("Got Upload through Uploads");
        String name = item.getName();
        String fieldName = item.getFieldName();
        log.info("    Name is " + name + " field Name " + fieldName);
        for (String headerName : item.getHeaderNames()) {
          log
              .info("Header " + headerName + " is "
                  + item.getHeader(headerName));
        }
        InputStream stream = item.openStream();
        if (!item.isFormField()) {
          try {
            if (name != null && name.trim().length() > 0) {

              List<String> realNames = values.get(REAL_UPLOAD_NAME);
              String finalName = name;
              if (realNames != null && realNames.size() > uploadNumber) {
                finalName = realNames.get(uploadNumber);
              }

              String mimeType = ContentTypes.getContentType(finalName, item
                  .getContentType());
              Node target = jcrNodeFactory.createFile(rp
                  .convertToAbsoluteRepositoryPath(finalName),mimeType);
              GregorianCalendar lastModified = new GregorianCalendar();
              lastModified.setTime(new Date());
              long size = saveStream(target, stream, mimeType, "UTF-8",
                  lastModified);
              Map<String, Object> uploadMap = new HashMap<String, Object>();
              if (size > Integer.MAX_VALUE) {
                uploadMap.put("contentLength", String.valueOf(size));
              } else {
                uploadMap.put("contentLength", (int) size);
              }
              uploadMap.put("name", finalName);
              uploadMap.put("url", rp.convertToExternalPath(rp
                  .convertToAbsoluteRepositoryPath(finalName)));
              uploadMap.put("mimeType", mimeType);
              uploadMap.put("lastModified", lastModified.getTime());
              uploadMap.put("status", "ok");

              uploads.put(fieldName, uploadMap);
            }
          } catch (Exception ex) {
            log.error("Failed to Upload Content", ex);
            Map<String, Object> uploadMap = new HashMap<String, Object>();
            uploadMap.put("mimeType", "text/plain");
            uploadMap.put("encoding", "UTF-8");
            uploadMap.put("contentLength", -1);
            uploadMap.put("lastModified", 0);
            uploadMap.put("status", "Failed");
            uploadMap.put("cause", ex.getMessage());
            List<String> stackTrace = new ArrayList<String>();
            for (StackTraceElement ste : ex.getStackTrace()) {
              stackTrace.add(ste.toString());
            }
            uploadMap.put("stacktrace", stackTrace);
            uploads.put(fieldName, uploadMap);
            uploadMap = null;

          }

        } else {
          String value = Streams.asString(stream);
          List<String> valueList = values.get(name);
          if (valueList == null) {
            valueList = new ArrayList<String>();
            values.put(name, valueList);

          }
          valueList.add(value);
        }
      }
      
      responseMap.put("success", true);
      responseMap.put("errors", errors.toArray(new String[1]));
      responseMap.put("uploads", uploads);
      sendMap(request, response, responseMap);
      log.info("Response Complete Saved to " + rp.getRepositoryPath());
    } catch (Throwable ex) {
      log.error("Failed  TO service Request ", ex);
      sendError(request, response, ex);
      return;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sakaiproject.sdata.tool.api.Handler#setHandlerHeaders(javax.servlet
   * .http.HttpServletResponse)
   */
  public void setHandlerHeaders(HttpServletRequest request,
      HttpServletResponse response) {
    response.setHeader("x-sdata-handler", this.getClass().getName());
    response.setHeader("x-sdata-url", request.getPathInfo());
  }

  /**
   * @return
   */
  public String getKey() {
    return KEY;
  }
}
