package org.sakaiproject.sdata.tool.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;

public class MockReference implements Reference {

	private String reference;

	public MockReference(String reference) {
		this.reference = reference;
	}
	public void addSiteContextAuthzGroup(Collection arg0) {
		// TODO Auto-generated method stub

	}

	public void addUserAuthzGroup(Collection arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void addUserTemplateAuthzGroup(Collection arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public Collection getAuthzGroups() {
		List<String> l = new ArrayList<String>();
		l.add(reference);
		return l;
	}

	public Collection getAuthzGroups(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityProducer getEntityProducer() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReference() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSubType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isKnownType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean set(String arg0, String arg1, String arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateReference(String arg0) {
		// TODO Auto-generated method stub

	}

}
