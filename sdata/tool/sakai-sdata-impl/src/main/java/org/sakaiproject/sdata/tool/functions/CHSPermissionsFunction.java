/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sdata.tool.functions;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.Kernel;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.model.CHSGroupMap;
import org.sakaiproject.sdata.tool.model.CHSNodeMap;

/**
 * <h3>Overview</h3>
 * <p>
 * This function provides a mechanism to set permissions on a CHS item. It will
 * only do this on a folder at this stage to avoid exponential increase in the
 * number of locations where permissions are created.
 * </p>
 * <h3>Request Parameters</h3>
 * <ul>
 * <li>Method: POST</li>
 * <li>Parameter Name: Description</li>
 * <li>f: <b>pm</b> Identifies the function</li>
 * <li>role: <b>access</b> The role to set the parameter on</li>
 * <li>perm: <b>read|write|delete|admin</b> The permission to set</li>
 * <li>set: <b>1|0</b> Set or Unset</li>
 * </ul>
 * <h3>Description</h3>
 * <p>
 * The Post is made with a function select parameter f=pm, and a three equal
 * length arrays of parameter values for each of role, perm, and set. Elements
 * are processed in sequence specifying the role , the permission (perm) to set
 * or unset on that role and if (set) to set or unset the permission.
 * </p>
 * <h3>Response</h3>
 * <p>
 * The current user must have admin permission on the folder,if they do and the
 * operation is successful in its entity a 200 will be returned. If the user is
 * not allowed to set, a 403 will be returned, if there is a problem with the
 * request (eg the role does not exist or the permission is not understood, then
 * a 500 will be returned
 * </p>
 * <p>
 * On a 200 the body will contain an updated set of metadata on the folder and
 * the metadata of the files and folders contained in the folder down to a depth
 * of 1
 * </p>
 * 
 * @author ieb
 */
public class CHSPermissionsFunction extends CHSSDataFunction {

	private static final Log log = LogFactory
			.getLog(CHSPermissionsFunction.class);

	public static final String ROLE = "role";

	public static final String PERM = "perm";

	public static final String SET = "set";

	public static final String READ = "read";

	public static final String WRITE = "write";

	public static final String REMOVE = "remove";

	public static final String ADMIN = "admin";

	public static final String SETVALUE = "1";

	public static final String UNSETVALUE = "0";

	private HashMap<String, String[]> permissionMap;

	private AuthzGroupService authzGroupService;

	private EntityManager entitiyManager;

	public CHSPermissionsFunction() {
		permissionMap = new HashMap<String, String[]>();
		permissionMap.put(READ,
				new String[] { ContentHostingService.AUTH_RESOURCE_READ });
		permissionMap.put(WRITE, new String[] {
				ContentHostingService.AUTH_RESOURCE_WRITE_ANY,
				ContentHostingService.AUTH_RESOURCE_ADD });
		permissionMap
				.put(
						REMOVE,
						new String[] { ContentHostingService.AUTH_RESOURCE_REMOVE_ANY });
		permissionMap.put(ADMIN,
				new String[] { AuthzGroupService.SECURE_UPDATE_AUTHZ_GROUP });
		authzGroupService = Kernel.authzGroupService();
		entitiyManager = Kernel.entityManager();
	}

	// FIXME: permissions are not getting set, and not getting set on the right
	// realm, we need to work out what is right
	@SuppressWarnings("unchecked")
	public void call(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Object target, ResourceDefinition rp)
			throws SDataException {
		SDataFunctionUtil.checkMethod(request.getMethod(), "GET|POST");
		if ("GET".equals(request.getMethod())) {

			ContentEntity ce = null;
			try {
				// check we can read the entity, but dont keep it
				ce = getEntity(handler, rp.getRepositoryPath());
			} catch (PermissionException e1) {
				throw new SDataException(HttpServletResponse.SC_FORBIDDEN,
						"User is not allowed to edit permissions on "
								+ rp.getRepositoryPath());
			}

			String ref = ce.getReference();
			if ( log.isDebugEnabled() ) 
			{
				log.debug("Got Reference " + ref);
			}
			Reference reference = entitiyManager.newReference(ref);
			Collection<?> groups = reference.getAuthzGroups();
			AuthzGroup authZGroup = null;
			String authZGroupId = null;
			for (Iterator<?> igroups = groups.iterator(); igroups.hasNext();) {
				String groupId = (String) igroups.next();
				try {
					if (authZGroupId == null) {
						authZGroup = authzGroupService.getAuthzGroup(groupId);
						authZGroupId = groupId;
					} else {
						if (authZGroupId.length() < groupId.length()) {
							authZGroup = authzGroupService
									.getAuthzGroup(groupId);
							authZGroupId = groupId;
						}
					}
				} catch (GroupNotDefinedException e1) {
					if ( log.isDebugEnabled() ) 
					{
						log.debug("Didnt get " + groupId);
					}
				}
			}

			if (authZGroup == null) {
				throw new SDataException(HttpServletResponse.SC_NOT_FOUND,
						"Realm " + ref + " does not exist for "
								+ rp.getRepositoryPath());
			}
			if ( log.isDebugEnabled() ) 
			{
				log.debug("Got " + authZGroup + " for " + authZGroupId+" as "+authZGroupToString(authZGroup));
			}
			try {
				handler.sendMap(request, response, new CHSGroupMap(authZGroup,
						permissionMap));
			} catch (IOException e) {
				throw new SDataException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"IO Error " + e.getMessage());
			}

		} else {
			// validate the request
			String[] roles = request.getParameterValues(ROLE);
			String[] permissions = request.getParameterValues(PERM);
			String[] sets = request.getParameterValues(SET);
			

			if (roles == null || permissions == null || sets == null
					|| roles.length != permissions.length
					|| roles.length != sets.length) {
				throw new SDataException(HttpServletResponse.SC_BAD_REQUEST,
						"Request must contain the same number of name, value, and action parameters ");
			}
			
			
			
			
			
			

			for (String perm : permissions) {
				
				if (!permissionMap.containsKey(perm)) {
					log.warn("The Permission "
							+ perm
							+ " does not exist as a permission on the folder ");
					throw new SDataException(
							HttpServletResponse.SC_BAD_REQUEST,
							"The Permission "
									+ perm
									+ " does not exist as a permission on the folder ");

				}
			}
			for (String set : sets) {
				if ((!SETVALUE.equals(set) && !UNSETVALUE.equals(set))) {
					log.warn("The request can only be set(1) or unset(0), found "+set);

					throw new SDataException(
							HttpServletResponse.SC_BAD_REQUEST,
							"The request can only be set(1) or unset(0), found "+set);

				}
			}

			ContentEntity ce = null;
			try {
				// check we can read the entity, but dont keep it
				ce = getEntity(handler, rp.getRepositoryPath());
			} catch (PermissionException ex1) {
				throw new SDataException(HttpServletResponse.SC_FORBIDDEN,
						"User is not allowed to edit permissions on "
								+ rp.getRepositoryPath());
			}
			String ref = ce.getReference();

			// get the roles
			Reference reference = entitiyManager.newReference(ref);
			if ( log.isDebugEnabled() ) 
			{
				log.debug("Got Reference "+reference+" for "+ref);
			}
			Collection<?> groups = reference.getAuthzGroups();

			// If this is the base group, then we want the site realm, otherwise
			// we want the longest realm
			String[] parts = ref.split("/");
			boolean base = parts.length == 4;

			

			
			// get the group relevant to this item
			String authZGroupId = null;
			for (Iterator<?> igroups = groups.iterator(); igroups.hasNext();) {
				String groupId = (String) igroups.next();
				if (base) {
					if (groupId.startsWith("/site")) {
						authZGroupId = groupId;
						break;
					}
				} else {
					if (authZGroupId == null) {
						authZGroupId = groupId;
					} else {
						if (authZGroupId.length() < groupId.length()) {
							authZGroupId = groupId;
						}
					}
				}
			}
			
			
			
			
			

			if (authZGroupId == null) {
				throw new SDataException(HttpServletResponse.SC_NOT_FOUND,
						"Realm " + ref + " does not exist for "
								+ rp.getRepositoryPath());
			}
			AuthzGroup authZGroup = null;

			if ( log.isDebugEnabled()  )
			{
				log.info("Selecting "+authZGroupId);
			}
			try {
				authZGroup = authzGroupService.getAuthzGroup(authZGroupId);
			} catch (GroupNotDefinedException e1) {
				try {
					authZGroup = authzGroupService.addAuthzGroup(authZGroupId);
				} catch (GroupIdInvalidException e) {
					throw new SDataException(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Denied create Permissions group  " + ref
									+ " id is invalid "
									+ rp.getRepositoryPath());
				} catch (GroupAlreadyDefinedException e) {
					throw new SDataException(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Denied create Permissions group  " + ref
									+ " already exists for "
									+ rp.getRepositoryPath());
				} catch (AuthzPermissionException e) {
					throw new SDataException(HttpServletResponse.SC_FORBIDDEN,
							"Denied create Permissions group  " + ref + " "
									+ rp.getRepositoryPath());
				}

			}
			if ( log.isDebugEnabled() )
			{
				log.debug("Got " + authZGroup + " for " + authZGroupId+" with "+roles.length+" permissions ");
				log.info(" Processing Roles "+roles.length);
			}
			
			// set the permissions
			for (int i = 0; i < roles.length; i++) {
				if ( log.isDebugEnabled() )
				{
					log.debug(" Processing "+roles[i]+" "+sets[i]+" "+permissions[i]);
				}
				Role r = authZGroup.getRole(roles[i]);
				if (r == null) {
					try {
						
						r = authZGroup.addRole(roles[i]);
						if ( log.isDebugEnabled() )
						{
							log.debug("Adding Role "
								+ roles[i]);
						}
					} catch (RoleAlreadyDefinedException e) {
						if ( log.isDebugEnabled() )
						{
							log.debug("Internal Error, adding role twice "
								+ roles[i]);
						}
					}

				}
				String[] functions = permissionMap.get(permissions[i]);
				if (SETVALUE.equals(sets[i])) {
					for (String function : functions) {
						log.info("Allow " + function + " on " + r.getId());
						r.allowFunction(function);
					}
				} else {
					for (String function : functions) {
						log.info("Dissalow " + function + " on " + r.getId());
						r.disallowFunction(function);
					}
				}
			}
			
			if ( log.isDebugEnabled() )
			{
				log.debug("After Edit AZG is "+authZGroupToString(authZGroup));
			}

			
			try {
				authzGroupService.save(authZGroup);
			} catch (GroupNotDefinedException e1) {
				throw new SDataException(HttpServletResponse.SC_NOT_FOUND,
						"Auth Group Does not exist " + rp.getRepositoryPath());
			} catch (AuthzPermissionException e1) {
				throw new SDataException(HttpServletResponse.SC_FORBIDDEN,
						"User is not allowed to edit permissions on "
								+ rp.getRepositoryPath());
			}

			try {
				handler.sendMap(request, response, new CHSGroupMap(authZGroup,
						permissionMap));
			} catch (IOException e) {
				throw new SDataException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"IO Error " + e.getMessage());
			}
		}

	}

	public static String authZGroupToString(AuthzGroup authZGroup) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		Set<?> roles = authZGroup.getRoles();
		for ( Iterator<?> i = roles.iterator(); i.hasNext();  ) {
			Role r = (Role) i.next();
			sb.append("\t").append(r.getId()).append(":{");
			Set<?> funcs = r.getAllowedFunctions();
			for ( Iterator<?> fi = funcs.iterator(); fi.hasNext();) {
				String f = (String) fi.next();
				sb.append(f).append(",");
			}
			sb.append("}\n");
		}
		return sb.toString();
	}
	
}
