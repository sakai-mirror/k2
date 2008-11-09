package org.sakaiproject.sdata.tool.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.Kernel;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;

public class CHSGroupMap extends HashMap<String, Object>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -721518028272586576L;

	public CHSGroupMap(AuthzGroup authZGroup, Map<String, String[]> permissionMap) {
		put("id", authZGroup.getId());
		put("createdBy", authZGroup.getCreatedBy().getEid());
		put("createdTime", authZGroup.getCreatedTime().getTime());
		put("description", authZGroup.getDescription());
		put("maintain", authZGroup.getMaintainRole());
		put("modifiedBy", authZGroup.getModifiedBy().getEid());
		put("modifiedTime", authZGroup.getModifiedTime().getTime());
		put("providerGroupId", authZGroup.getProviderGroupId());
		Map<String,Map<String, String>> roles = new HashMap<String,Map<String, String>>();
		
		Set<?> azgR = authZGroup.getRoles();
		for ( Object roleO : azgR) {
			Role r = (Role)roleO;
			Map<String , String> rm = new HashMap<String, String>();
			rm.put("description",r.getDescription());
			rm.put("role", r.getId());
			Set<?> functions = r.getAllowedFunctions();
			for ( String permission : permissionMap.keySet())  {
				String[] pf = permissionMap.get(permission);
				rm.put(permission, "false");
				for ( String f : pf  ) {
					if ( functions.contains(f) ) {
						rm.put(permission, "true");
					}
				}
			}
			roles.put(r.getId(), rm);
		}
		put("roles",roles);
		
		
		
		if (Kernel.authzGroupService().allowUpdate(authZGroup.getId()))
		{
			List<Map<String, String>> members = new ArrayList<Map<String, String>>();
			Set<?> azgM = authZGroup.getMembers();
			for ( Object memberO : azgM) {
				Member m = (Member)memberO;
				Map<String , String> mm = new HashMap<String, String>();
				mm.put("eid",m.getUserEid());
				mm.put("role", m.getRole().getId());
				members.add(mm);
			}
			put("members",members);
		}
	}

}
