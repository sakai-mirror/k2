package org.sakaiproject.sdata.tool.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupFullException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.javax.PagingPosition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MockAuthZGroupService  implements AuthzGroupService{

	private boolean pass;


	public AuthzGroup addAuthzGroup(String arg0)
			throws GroupIdInvalidException, GroupAlreadyDefinedException,
			AuthzPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup addAuthzGroup(String arg0, AuthzGroup arg1, String arg2)
			throws GroupIdInvalidException, GroupAlreadyDefinedException,
			AuthzPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAdd(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowJoinGroup(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemove(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUnjoinGroup(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdate(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public String authzGroupReference(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countAuthzGroups(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set getAllowedFunctions(String arg0, Collection arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup getAuthzGroup(String arg0)
			throws GroupNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getAuthzGroupIds(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAuthzGroups(String arg0, PagingPosition arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getAuthzGroupsIsAllowed(String arg0, String arg1, Collection arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getProviderIds(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserRole(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getUsersIsAllowed(String arg0, Collection arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getUsersRole(Collection arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAllowed(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllowed(String arg0, String arg1, Collection arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void joinGroup(String arg0, String arg1)
			throws GroupNotDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub
		
	}

	public void joinGroup(String arg0, String arg1, int arg2)
			throws GroupNotDefinedException, AuthzPermissionException,
			GroupFullException {
		// TODO Auto-generated method stub
		
	}

	public AuthzGroup newAuthzGroup(String arg0, AuthzGroup arg1, String arg2)
			throws GroupAlreadyDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshUser(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void removeAuthzGroup(AuthzGroup arg0)
			throws AuthzPermissionException {
		// TODO Auto-generated method stub
		
	}

	public void removeAuthzGroup(String arg0) throws AuthzPermissionException {
		// TODO Auto-generated method stub
		
	}

	public void save(AuthzGroup arg0) throws GroupNotDefinedException,
			AuthzPermissionException {
		// TODO Auto-generated method stub
		
	}

	public void unjoinGroup(String arg0) throws GroupNotDefinedException,
			AuthzPermissionException {
		// TODO Auto-generated method stub
		
	}

	public String archive(String arg0, Document arg1, Stack arg2, String arg3,
			List arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map arg4, Map arg5, Set arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String arg0, Reference arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public List getAuthzUserGroupIds(ArrayList arg0, String arg1) {
		return null;
	}

	public Map<String, Integer> getUserCountIsAllowed(String arg0,
			Collection<String> arg1) {
		return null;
	}

	public Set<String[]> getUsersIsAllowedByGroup(String arg0,
			Collection<String> arg1) {
		return null;
	}
 
}
