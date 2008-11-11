package org.sakaiproject.sdata.tool.functions;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.Kernel;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;

public class CHSTagging implements Observer {

	private static final Log log = LogFactory.getLog(CHSTagging.class);
	private SqlService sqlService;
	private String[] indexFields = new String[] { "tag" };
	private EventTrackingService eventTrackingService;
	private EntityManager entityManager;
	private String propertiesVectorSQL;
	private String propertyMatchesSQL1;
	private String propertyMatchesSQL2;
	private String updateValueSQL;
	private String deleteValueSQL;

	public void init() {
		sqlService = Kernel.sqlService();
		boolean autoDDL = Kernel.serverConfigurationService().getBoolean("auto.ddl", false);
		if (autoDDL) {
			sqlService.ddl(this.getClass().getClassLoader(), "sdata_tagging");
		}
		
		
		// this should allow the SQL to go into property files.
		String vendor = sqlService.getVendor();
		try {
			Properties p = new Properties();
			p.load(this.getClass().getClassLoader().getResourceAsStream(vendor+"/sdata_tagging_queries.sql"));
			propertiesVectorSQL = p.getProperty("propertiesVectorSQL");
			propertyMatchesSQL1 = p.getProperty("propertyMatchesSQL1");
			propertyMatchesSQL2 = p.getProperty("propertyMatchesSQL2");
			updateValueSQL = p.getProperty("updateValueSQL");
			deleteValueSQL = p.getProperty("deleteValueSQL");
		} catch (Exception e) {
			propertiesVectorSQL = "select propertyvalue, count(*)  "
			+ "  from sdata_property_index " 
			+ " where context = ? "
			+ "   and propertyname = ? " 
			+ "  group by propertyvalue ";
			propertyMatchesSQL1 = "select reference  "
				+ "  from sdata_property_index " + " where context = ? "
				+ "   and propertyname = ? " + "   and propertyvalue in ( ";
			propertyMatchesSQL2 = ") LIMIT ? OFFSET ?  ";
			updateValueSQL = "insert into sdata_property_index ( context, reference, propertyname, propertyvalue )"
				+ "values ( ?,?,?,? ) ";
			deleteValueSQL = "delete " 
				+ "  from sdata_property_index "
				+ " where context = ? " 
				+ "  and reference = ? ";
		}
		
		
		entityManager = Kernel.entityManager();
		eventTrackingService = Kernel.eventTrackingService();
		eventTrackingService.addLocalObserver(this);
		log.info(" Registring "+this);
		
		
	}
	public void destroy() {
		log.info(" Deregistering "+this);
		eventTrackingService.deleteObserver(this);
		log.info(" DONE Deregistering "+this);
	}

	public Map<String, Integer> getPropertyVector(String context,
			String propertyName) {
		List<?> results = sqlService.dbRead(
				propertiesVectorSQL,
				new Object[] { context, propertyName }, new SqlReader() {

					public Object readSqlResultRecord(ResultSet result) {
						try {
							return new Object[] { result.getString(1),
									result.getInt(2) };
						} catch (Exception ex) {
							return new Object[] { "null", 0 };
						}
					}

				});
		
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (Iterator<?> i = results.iterator(); i.hasNext();) {
			Object[] r = (Object[]) i.next();
			;
			m.put((String) r[0], (Integer) r[1]);
		}
		return m;
	}

	public List<String> getPropertyMatches(String context, String propertyName,
			String[] propertyValue, int start, int nresults) {
		Object[] params = new Object[propertyValue.length + 4];
		params[0] = context;
		params[1] = propertyName;
		StringBuilder inTerm = new StringBuilder();
		params[2] = propertyValue[0];
		inTerm.append("?");
		for (int i = 1; i < propertyValue.length; i++) {
			params[i + 2] = propertyValue[i];
			inTerm.append(",?");
		}
		params[propertyValue.length+2] = nresults;
		params[propertyValue.length+3] = start;

		List<?> results = sqlService.dbRead(propertyMatchesSQL1
				+ inTerm.toString() + propertyMatchesSQL2, params, new SqlReader() {

			public Object readSqlResultRecord(ResultSet result) {
				try {
					return result.getString(1);
				} catch (Exception ex) {
					return "";
				}
			}

		});
		return (List<String>) results;
	}

	public void update(String reference, Entity entity) {
		ResourceProperties properties = entity.getProperties();
		String context = getContext(reference);
		remove(context,reference);
		if ( log.isDebugEnabled() ) 
		{
			log.info("Adding Properties for "+context+" "+reference);
		}
		for (String indexField : indexFields) {
			List<?> property = properties.getPropertyList(indexField);
			if ( property != null ) 
			{
				for (Iterator<?> p = property.iterator(); p.hasNext();) {
					String prop = (String) p.next();
					if ( prop != null && prop.length() > 0) {
					sqlService
							.dbWrite(
									updateValueSQL, new Object[] {
											context, reference, indexField,
											prop });
					}
				}
			} 
		}

	}

	public void remove(String reference) {
		remove(getContext(reference), reference);
	}
	private String getContext(String reference) {
		String[] parts = reference.split("/");
		String context = "";
		if ( parts.length > 3 ) {
			context = parts[3];
		}
		return context;
	}
	public void remove(String context, String reference) 
	{
		if ( log.isDebugEnabled() ) 
		{
			log.debug("Removing Properties for "+context+" "+reference);
		}
		
		sqlService.dbWrite(deleteValueSQL, new Object[] {
				context, reference });
	}
	public void update(Observable o, Object evt) {

		Event event = (org.sakaiproject.event.api.Event) evt;
		if (event.getEvent().startsWith("content")
					&& !event.getEvent().equals("content.read")) {
			
			Reference r = entityManager.newReference(event.getResource());
			if ( event.getEvent().equals("content.delete") ) 
			{
				
				remove(event.getResource());
			} 
			else 
			{
				Entity e = r.getEntity();
				if ( e != null )
				{
					update(event.getResource(),e);	
				}
				else 
				{
					log.warn(" Event Resource "+r+" generated null entity");
				}
			}
		} 
	}

}
