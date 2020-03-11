/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC, a Micro Focus company
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.client.ssc.api;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsOfAuthEntityQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access SSC application version related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionAPI extends AbstractSSCAPI {
	public SSCApplicationVersionAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionsQueryBuilder queryApplicationVersions() {
		return new SSCApplicationVersionsQueryBuilder(conn());
	}
	
	public SSCApplicationVersionsOfAuthEntityQueryBuilder queryApplicationVersionsByAuthEntityId(String authEntityId) {
		return new SSCApplicationVersionsOfAuthEntityQueryBuilder(conn(), authEntityId);
	}
	
	public SSCApplicationVersionsOfAuthEntityQueryBuilder queryApplicationVersionsByAuthEntityName(String authEntityName) {
		return queryApplicationVersionsByAuthEntityId(
				conn().api(SSCAuthEntityAPI.class).getAuthEntityIdByEntityName(authEntityName));
				
	}
	
	public JSONMap getApplicationVersionById(String applicationVersionId, boolean useCache) {
		return queryApplicationVersions().id(applicationVersionId).useCache(useCache).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByName(String applicationName, String versionName, boolean useCache) {
		return queryApplicationVersions().applicationName(applicationName).versionName(versionName).useCache(useCache).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId, String separator, boolean useCache) {
		return queryApplicationVersions().nameOrId(nameOrId, separator).useCache(useCache).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId, boolean useCache) {
		return queryApplicationVersions().nameOrId(nameOrId).useCache(useCache).build().getUnique();
	}
	
	public void deleteApplicationVersion(JSONMap applicationVersion) {
		deleteApplicationVersionById(applicationVersion.get("id", String.class));
	}
	
	@SSCRequiredActionsPermitted({"DELETE=/api/v\\d+/projectVersions/\\d+"})
	public void deleteApplicationVersionById(String applicationVersionId) {
		conn().executeRequest(HttpMethod.DELETE, 
			conn().getBaseResource().path("/api/v1/projectVersions").path(applicationVersionId), Void.class);
	}

	public CreateApplicationVersionBuilder createApplicationVersion() {
		return new CreateApplicationVersionBuilder(conn());
	}
	
	// TODO Add support for defining application version team, copying state & other info from other version
	public static final class CreateApplicationVersionBuilder {
		private final SSCAuthenticatingRestConnection conn;
		private String applicationName;
		private String applicationId;
		private String applicationDescription;
		private String versionName;
		private String versionDescription;
		private String issueTemplateId;
		private boolean autoAddRequiredAttributes;
		private LinkedMultiValueMap<String, Object> attributeNameOrIdToValueMap = new LinkedMultiValueMap<>();

		public CreateApplicationVersionBuilder(SSCAuthenticatingRestConnection conn) {
			this.conn = conn;
		}
		
		public CreateApplicationVersionBuilder applicationId(String applicationId) {
			this.applicationId = applicationId;
			return this;
		}
		
		public CreateApplicationVersionBuilder applicationName(String applicationName) {
			this.applicationName = applicationName;
			return this;
		}
		
		public CreateApplicationVersionBuilder applicationDescription(String applicationDescription) {
			this.applicationDescription = applicationDescription;
			return this;
		}
		
		public CreateApplicationVersionBuilder versionName(String versionName) {
			this.versionName = versionName;
			return this;
		}
		
		public CreateApplicationVersionBuilder versionDescription(String versionDescription) {
			this.versionDescription = versionDescription;
			return this;
		}
		
		public CreateApplicationVersionBuilder autoAddRequiredAttributes(boolean autoAddRequiredAttributes) {
			this.autoAddRequiredAttributes = autoAddRequiredAttributes;
			return this;
		}
		
		public CreateApplicationVersionBuilder issueTemplateName(String issueTemplateName) {
			this.issueTemplateId = conn.api(SSCIssueTemplateAPI.class).getIssueTemplateIdForName(issueTemplateName);
			if ( this.issueTemplateId==null ) {
				throw new IllegalArgumentException("Unknown issue template "+issueTemplateName);
			}
			return this;
		}
		
		public CreateApplicationVersionBuilder attribute(String attributeNameOrId, String attributeValue) {
			attributeNameOrIdToValueMap.add(attributeNameOrId, attributeValue);
			return this;
		}
		
		public CreateApplicationVersionBuilder attributes(MultiValueMap<String, Object> values) {
			attributeNameOrIdToValueMap.addAll(values);
			return this;
		}
		
		public String execute() {
			MultiValueMap<String,Object> attributes = getApplicationVersionAttributes();
			String applicationVersionId = createNonCommittedApplicationVersiom().get("id", String.class);
			conn.api(SSCAttributeAPI.class).updateApplicationVersionAttributes(applicationVersionId, attributes);
			commitApplicationVersion(applicationVersionId);
			return applicationVersionId;
		}

		@SSCRequiredActionsPermitted({"POST=/api/v\\d+/projectVersions"})
		private JSONMap createNonCommittedApplicationVersiom() {
			// TODO Add checks that required properties are set
			JSONMap data = new JSONMap();
			data.put("name", versionName);
			data.put("description", versionDescription==null?"":versionDescription);
			data.put("project", getExistingOrNewApplicationData());
			data.put("active", true);
			data.put("committed", false);
			data.put("issueTemplateId", issueTemplateId);
			
			return conn.executeRequest(HttpMethod.POST, 
					conn.getBaseResource().path("/api/v1/projectVersions"), 
					Entity.entity(data, "application/json"), JSONMap.class).getOrCreateJSONMap("data");
		}
		
		private MultiValueMap<String, Object> getApplicationVersionAttributes() {
			LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
			if ( autoAddRequiredAttributes ) {
				data.putAll(conn.api(SSCAttributeAPI.class).getRequiredAttributesWithDefaultValues());
			}
			if ( attributeNameOrIdToValueMap != null ) {
				data.putAll(attributeNameOrIdToValueMap);
			}
			return data;
		}

		@SSCRequiredActionsPermitted({"PUT=/api/v\\d+/projectVersions/\\d+/action"})
		private void commitApplicationVersion(String applicationVersionId) {
			JSONMap data = new JSONMap();
			data.put("committed", true);
			
			conn.executeRequest(HttpMethod.PUT, 
					conn.getBaseResource().path("/api/v1/projectVersions").path(applicationVersionId), 
					Entity.entity(data, "application/json"), JSONMap.class);
		}

		private final JSONMap getExistingOrNewApplicationData() {
			JSONMap result = conn.api(SSCApplicationVersionAPI.class)
					.queryApplicationVersions().applicationName(applicationName)
					.applicationId(applicationId).maxResults(1).paramFields("project").build().getUnique();
			return result == null ? getNewApplicationData() : getExistingApplicationData(result.get("project", JSONMap.class));
		}

		private JSONMap getNewApplicationData() {
			JSONMap result = new JSONMap();
			result.put("name", applicationName);
			result.put("issueTemplateId", issueTemplateId);
			result.put("description", applicationDescription);
			return result;
		}
		
		private JSONMap getExistingApplicationData(JSONMap existingApplication) {
			return new JSONMap(existingApplication, "name", "id", "issueTemplateId", "description");
		}
		
		
	}
}
