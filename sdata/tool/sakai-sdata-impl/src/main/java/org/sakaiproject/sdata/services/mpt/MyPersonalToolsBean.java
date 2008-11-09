package org.sakaiproject.sdata.services.mpt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.Kernel;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.sdata.services.me.MeBean;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
//import org.sakaiproject.s
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.SortedIterator;
public class MyPersonalToolsBean implements ServiceDefinition{

	//private static MyPersonalToolsBean.getLog(MyPersonalToolsBean.class);

	private SiteService siteService;

	private SqlService sqlService;
	
	private SessionManager sessionManager;
	
	private ToolManager toolManager;
	
	private static final String NULL_STRING = "";
	
	private Map<String, Object> map2 = new HashMap<String, Object>();;

	private Map<String, Object> map = new HashMap<String, Object>();
	
	private List<Tool> listOfTools = null;
	
	private List<Map> myMappedTools = new ArrayList<Map>();
	
	private static final Log log = LogFactory.getLog(MyPersonalToolsBean.class);
	
	public MyPersonalToolsBean(HttpServletResponse response){
				
		this.sqlService = Kernel.sqlService();
		this.siteService = Kernel.siteService();
		this.sessionManager = Kernel.sessionManager();
		this.toolManager = new ToolManager();
	}
	
	
	public Map<String, Object> getResponseMap() {
		// TODO Auto-generated method stub
		this.listOfTools = this.getListOfAddableTools();

			for(Tool t: this.listOfTools){
			
			map = new HashMap<String, Object>();
			
			Tool tool = (Tool) t;
			map.put("toolName", tool.getTitle());
			map.put("toolDescription", tool.getDescription());
			map.put("toolId", tool.getId());
			//map.put("toolAccessSecurity", tool.getAccessSecurity());
			//map.put("toolHome", tool.getHome());
			myMappedTools.add(map);
			
			}
			map2.put("items", this.myMappedTools);
		return map2;
	}
	
	public List<Tool> getListOfAddableTools(){
		Set categories = new HashSet();
		categories.add("myworkspace");
		Set toolRegistrations = ToolManager.findTools(categories, null);
		List<Tool> tools = new ArrayList<Tool>();
		SortedIterator i = new SortedIterator(toolRegistrations.iterator(),
				new ToolComparator());
		for (; i.hasNext();) {
			// form a new Tool
			Tool tr = (Tool) i.next();
			//Tool newTool = new MyTool();
			//newTool.title = tr.getTitle();
			//newTool.id = tr.getId();
			//newTool.description = tr.getDescription();
			//log.error("TOOL TITLE GETTING IN: " + tr.getTitle());
			tools.add(tr);
		}
		
		return tools;
		
	}
	
	// ToolComparator
	private class ToolComparator implements Comparator {
		/**
		 * implementing the Comparator compare function
		 * 
		 * @param o1
		 *            The first object
		 * @param o2
		 *            The second object
		 * @return The compare result. 1 is o1 < o2; 0 is o1.equals(o2); -1
		 *         otherwise
		 */
		public int compare(Object o1, Object o2) {
			try {
				return ((Tool) o1).getTitle().compareTo(((Tool) o2).getTitle());
			} catch (Exception e) {
			}
			return -1;

		} // compare

	} // ToolComparator

	// a utility class for working with ToolConfigurations and ToolRegistrations
	// %%% convert featureList from IdAndText to Tool so getFeatures item.id =
	// chosen-feature.id is a direct mapping of data
	public class MyTool {
		public String id = NULL_STRING;

		public String title = NULL_STRING;

		public String description = NULL_STRING;

		public boolean selected = false;

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public boolean getSelected() {
			return selected;
		}

	}
}
