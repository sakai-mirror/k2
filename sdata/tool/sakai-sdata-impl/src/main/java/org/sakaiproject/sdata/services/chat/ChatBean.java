package org.sakaiproject.sdata.services.chat;

import java.io.IOException;
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
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.email.cover.EmailService;
import org.sakaiproject.sdata.services.connections.ConnectionSqlreader;
import org.sakaiproject.sdata.services.connections.ConnectionSqlresult;
import org.sakaiproject.sdata.services.profile.ProfileSqlreader2;
import org.sakaiproject.sdata.services.profile.ProfileSqlresult2;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * A service definition bean for recent changes that the current user can see.
 * 
 * @author
 */
public class ChatBean implements ServiceDefinition
{

	private static final Log log = LogFactory.getLog(ChatBean.class);

	private SqlService sqlService = Kernel.sqlService();

	Map<String, Object> resultMap = new HashMap<String, Object>();

	private SessionManager sessionManager = Kernel.sessionManager();
	
	private UserDirectoryService userDirectoryService = Kernel.userDirectoryService();

	/**
	 * Create a recent changes bean with the number of pages.
	 * 
	 * @param paging
	 */
	public ChatBean(HttpServletRequest request, HttpServletResponse response)
	{
		try {
			if (request.getMethod().equalsIgnoreCase("get")){
				doList(request, response);
			} else if (request.getMethod().equalsIgnoreCase("post")){
				doPost(request, response);
			} 
		} catch (Exception ex){
			try {
				ex.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}  


	private void doList(HttpServletRequest request, HttpServletResponse response)
	{
		
		// Alles ophalen van gegeven vrienden
		
		String users = request.getParameter("users");
		String[] user = users.split("[|]");
		List<ChatSqlresult> lst = new ArrayList<ChatSqlresult>();
		String initial = request.getParameter("initial");
		
		log.error("User.length = " + user.length);
		
		if (users.length() > 0){
			
			if (initial.equalsIgnoreCase("true")){
			
				String currentUser = sessionManager.getCurrentSessionUserId();
				Object[] params = new Object[user.length * 4];
				Object[] params2 = new Object[user.length * 4 + 2];
				params2[0] = true;
				params2[1] = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				String sql = "SELECT * FROM sdata_chat WHERE";
				String sql2 = "UPDATE sdata_chat SET isread = ? , readwhen = ? WHERE";
				for (int i = 0; i < user.length; i++){
					String thisUser = user[i];
					if (i != 0){
						sql += " OR";
						sql2 += " OR";
					}
					sql += " ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?))";
					sql2 += " ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?))";
					params[i * 4] = currentUser;
					params[i * 4 + 1] = thisUser;
					params[i * 4 + 2] = thisUser;
					params[i * 4 + 3] = currentUser;
					params2[i * 4 + 2] = currentUser;
					params2[i * 4 + 3] = thisUser;
					params2[i * 4 + 4] = thisUser;
					params2[i * 4 + 5] = currentUser;
				}
				sql += " ORDER BY senttime ASC";
				
				lst = sqlService.dbRead(sql, params, new ChatSqlreader());
				
				// Gelezen berichten updaten
				
				sqlService.dbWrite(sql2, params2);
			
			} 
			
		}
		
		// Alle nieuwe ophalen
		
		String sql = "SELECT * FROM sdata_chat WHERE receiver = ? AND isread = ? ORDER BY senttime ASC";
		Object[] params = new Object[2];
		params[0] = sessionManager.getCurrentSessionUserId();
		params[1] = false;
		
		List<ChatSqlresult> lst2 = sqlService.dbRead(sql, params, new ChatSqlreader());
		
		// Gelezen berichten updaten
		
		String sql2 = "UPDATE sdata_chat SET isread = ? , readwhen = ? WHERE receiver = ? AND isread = ?";
		Object[] params2 = new Object[4];
		params2[0] = true;
		params2[1] = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		params2[2] = sessionManager.getCurrentSessionUserId();
		params2[3] = false;
		
		sqlService.dbWrite(sql2, params2);
		
		// Verwerk berichten
		
		ArrayList<String> finalUsers = new ArrayList<String>();
		for (int i = 0; i < lst.size(); i++){
			ChatSqlresult res = lst.get(i);
			if (res.getSender().equalsIgnoreCase(sessionManager.getCurrentSessionUserId())){
				if (! finalUsers.contains(res.getReceiver())){
					finalUsers.add(res.getReceiver());
				}
			} else {
				if (! finalUsers.contains(res.getSender())){
					finalUsers.add(res.getSender());
				}
			}
		}
		for (int i = 0; i < lst2.size(); i++){
			ChatSqlresult res = lst2.get(i);
			if (res.getSender().equalsIgnoreCase(sessionManager.getCurrentSessionUserId())){
				if (! finalUsers.contains(res.getReceiver())){
					finalUsers.add(res.getReceiver());
				}
			} else {
				if (! finalUsers.contains(res.getSender())){
					finalUsers.add(res.getSender());
				}
			}
		}
		
		Map<String, Map<String, Object>> finalMap = new HashMap<String, Map<String, Object>>();
		
		// Get list of users
		
		List<ProfileSqlresult2> lstProfiles = new ArrayList<ProfileSqlresult2>();
		
		if (finalUsers.size() > 0){
			
			Object[] params3 = new Object[finalUsers.size()];
			String sql3 = "";
			for (int i = 0; i < finalUsers.size(); i++){
				if (i != 0){
					sql3 += " OR";
				}
				sql3 += " new.USER_ID = ?";
				params3[i] = finalUsers.get(i);
			}
			
			lstProfiles = sqlService.dbRead("SELECT * FROM (SELECT *  FROM SAKAI_USER  LEFT OUTER JOIN sdata_profile ON SAKAI_USER.USER_ID = sdata_profile.userid) as new WHERE" + sql3, params3, new ProfileSqlreader2());
			
		}
		
		for (String s: finalUsers){
			// Create ArrayList
			
			ArrayList<ChatSqlresult> arl = new ArrayList<ChatSqlresult>();
			Map<String, Object> map = new HashMap<String, Object>();
			
			for (ChatSqlresult res : lst){
				if (res.getSender().equalsIgnoreCase(sessionManager.getCurrentSessionUserId())){
					if (res.getReceiver().equalsIgnoreCase(s)){
						arl.add(res);
					}
				} else {
					if (res.getSender().equalsIgnoreCase(s)){
						arl.add(res);
					}
				}
			}
			
			for (ChatSqlresult res : lst2){
				if (res.getSender().equalsIgnoreCase(sessionManager.getCurrentSessionUserId())){
					if (res.getReceiver().equalsIgnoreCase(s)){
						arl.add(res);
					}
				} else {
					if (res.getSender().equalsIgnoreCase(s)){
						arl.add(res);
					}
				}
			}
			
			// Fill Map
			
			map.put("items", arl);
			
			ProfileSqlresult2 res = new ProfileSqlresult2();
			for (int i = 0; i < lstProfiles.size(); i++){
				if (lstProfiles.get(i).getUserid().equalsIgnoreCase(s)){
					res = lstProfiles.get(i);
				}
			}
			
			map.put("profile", res);
			
			finalMap.put(s, map);
			
		}
		
		resultMap.put("items", finalMap);
		
	}
	
	private void doPost (HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		
		String text = request.getParameter("text");
		String sender = sessionManager.getCurrentSessionUserId();
		String receiver = request.getParameter("receiver");
		boolean isread = false;
		String senttime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		Object[] params = new Object[5];
		params[0] = text;
		params[1] = sender;
		params[2] = receiver;
		params[3] = isread;
		params[4] = senttime;
		
		sqlService.dbWrite("INSERT INTO sdata_chat (text, sender, receiver, isread, senttime) VALUES (?,?,?,?,?)", params);
		
	}
	
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
