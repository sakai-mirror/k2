package org.sakaiproject.sdata.tool.test;

import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

public class MockSessionManager implements SessionManager {

	public int getActiveUserCount(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Session getCurrentSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentSessionUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	public ToolSession getCurrentToolSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCurrentSession(Session arg0) {
		// TODO Auto-generated method stub

	}

	public void setCurrentToolSession(ToolSession arg0) {
		// TODO Auto-generated method stub

	}

	public Session startSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session startSession(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
