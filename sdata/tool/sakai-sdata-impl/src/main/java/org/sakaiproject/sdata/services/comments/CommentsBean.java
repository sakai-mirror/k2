package org.sakaiproject.sdata.services.comments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.Kernel;
import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.message.api.MessageChannel;
import org.sakaiproject.sdata.services.mra.MyRecentChangesSqlreader;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.SearchResult;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

/**
 * A service definition bean for recent changes that the current user can see.
 * 
 * @author
 */
public class CommentsBean implements ServiceDefinition
{

	private static final Log log = LogFactory.getLog(CommentsBean.class);

	private SqlService sqlService;

	Map<String, Object> resultMap = new HashMap<String, Object>();

	private SessionManager sessionManager;

	/**
	 * Create a recent changes bean with the number of pages.
	 * 
	 * @param paging
	 */
	public CommentsBean(HttpServletRequest request, HttpServletResponse response)
	{
	
		this.sqlService = Kernel.sqlService();
		this.sessionManager = Kernel.sessionManager();
		UserDirectoryService userDirectoryService = Kernel.userDirectoryService();
		
		String commentsInsertSQL="insert into sdata_comments (time, comment, placement, userid) values( ?,?,?,? )";
		String commentsCheckSQL= "select * from sdata_comments where placement = ? ";

		if (request.getMethod().toLowerCase().equals("get")){
			
			String placement = request.getParameter("placement");
			
			Object[] params = new Object[1];
			params[0] = placement;
			
			List<CommentsSqlresult> lst = sqlService.dbRead(commentsCheckSQL, params,
					new CommentsSqlreader());
			
			ArrayList<Map<String,String>> results = new ArrayList<Map<String,String>>();
			
			for (CommentsSqlresult res: lst){
				Map<String,String> item = new HashMap<String, String>();
				item.put("id", "" + res.getId());
				item.put("time", res.getTime());
				item.put("placement", res.getPlacement());
				item.put("comment", res.getComment());
				item.put("userid", res.getUserid());
				results.add(item);
			}
			
			resultMap.put("items",results);
			
		} else {
			
			String placement = request.getParameter("placement");
			String comment = request.getParameter("comment");
			
			Session session = sessionManager.getCurrentSession();
			SqlService sqlService = Kernel.sqlService();
			String user = "Unknown";
			try {
				user = userDirectoryService.getUser(session.getUserId()).getDisplayId();
			} catch (Exception ex){}
			
			//time, comment, placement, userid

			Object[] params = new Object[4];
			params[0] = new java.text.SimpleDateFormat(
			           "yyyy-MM-dd HH:mm")
			        .format(new java.util.Date(System
					     .currentTimeMillis()));
			params[1] = comment;
			params[2] = placement;
			params[3] = user;
			
			sqlService.dbWrite(commentsInsertSQL, params);
			
			resultMap.put("status","success");
			
		}

	} // END CONSTRUCTOR

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap()
	{
		return resultMap;
	}

}
