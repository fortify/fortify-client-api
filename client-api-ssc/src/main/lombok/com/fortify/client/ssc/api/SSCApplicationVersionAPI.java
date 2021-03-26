/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates, a Micro Focus company
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

import org.apache.commons.lang.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI.SSCAttributeDefinitionHelper;
import com.fortify.client.ssc.api.SSCIssueTemplateAPI.SSCIssueTemplateHelper;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsOfAuthEntityQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.applier.ifblank.IfBlank;
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
	
	public JSONMap getApplicationVersionById(String applicationVersionId) {
		return queryApplicationVersions().id(IfBlank.ERROR(), applicationVersionId).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByName(String applicationName, String versionName) {
		return queryApplicationVersions().applicationName(IfBlank.ERROR(), applicationName).versionName(IfBlank.ERROR(), versionName).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId, String separator) {
		return queryApplicationVersions().nameOrId(IfBlank.ERROR(), nameOrId, separator).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId) {
		return queryApplicationVersions().nameOrId(IfBlank.ERROR(), nameOrId).build().getUnique();
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
		return new CreateApplicationVersionBuilder();
	}
	
	// TODO Add support for defining application version team, copying state & other info from other version
	public final class CreateApplicationVersionBuilder {
		private SSCAttributeDefinitionHelper attributeDefinitionHelper;
		private SSCIssueTemplateHelper issueTemplateHelper;
		private String applicationName;
		private String applicationId;
		private String applicationDescription;
		private String versionName;
		private String versionDescription;
		private String issueTemplateId;
		private boolean autoAddRequiredAttributes;
		private LinkedMultiValueMap<String, Object> attributeNameOrIdToValueMap = new LinkedMultiValueMap<>();
		
		private CreateApplicationVersionBuilder() {}

		public CreateApplicationVersionBuilder withAttributeDefinitionHelper(SSCAttributeDefinitionHelper attributeDefinitionHelper) {
			this.attributeDefinitionHelper = attributeDefinitionHelper;
			return this;
		}
		
		public CreateApplicationVersionBuilder withIssueTemplateHelper(SSCIssueTemplateHelper issueTemplateHelper) {
			this.issueTemplateHelper = issueTemplateHelper;
			return this;
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
			this.issueTemplateId = getIssueTemplateHelper().getIssueTemplateIdForName(issueTemplateName);
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
			conn().api(SSCApplicationVersionAttributeAPI.class).updateApplicationVersionAttributes(applicationVersionId)
				.withAttributeDefinitionHelper(getAttributeDefinitionHelper())
				.byNameOrId(attributes)
				.execute();
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
			data.put("issueTemplateId", getIssueTemplateId());
			
			return conn().executeRequest(HttpMethod.POST, 
					conn().getBaseResource().path("/api/v1/projectVersions"), 
					Entity.entity(data, "application/json"), JSONMap.class).getOrCreateJSONMap("data");
		}
		
		private String getIssueTemplateId() {
			issueTemplateId = issueTemplateId!=null ? issueTemplateId : getIssueTemplateHelper().getDefaultIssueTemplateId(); 
			if ( issueTemplateId == null ) {
				throw new IllegalStateException("No issue template specified, and no default issue template configured on SSC");
			}
			return issueTemplateId;
		}

		private MultiValueMap<String, Object> getApplicationVersionAttributes() {
			LinkedMultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
			if ( autoAddRequiredAttributes ) {
				data.putAll(getAttributeDefinitionHelper().getRequiredAttributesWithDefaultValues());
			}
			if ( attributeNameOrIdToValueMap != null ) {
				data.putAll(attributeNameOrIdToValueMap);
			}
			return data;
		}

		@SSCRequiredActionsPermitted({"PUT=/api/v\\d+/projectVersions/\\d+"})
		private void commitApplicationVersion(String applicationVersionId) {
			JSONMap data = new JSONMap();
			data.put("committed", true);
			
			conn().executeRequest(HttpMethod.PUT, 
					conn().getBaseResource().path("/api/v1/projectVersions").path(applicationVersionId), 
					Entity.entity(data, "application/json"), JSONMap.class);
		}

		private final JSONMap getExistingOrNewApplicationData() {
			if ( StringUtils.isBlank(applicationName) && StringUtils.isBlank(applicationId) ) {
				throw new IllegalStateException("Either application name or id must be specified");
			}
			JSONMap result = conn().api(SSCApplicationVersionAPI.class)
					.queryApplicationVersions().applicationName(IfBlank.SKIP(), applicationName)
					.applicationId(IfBlank.SKIP(), applicationId).maxResults(1).paramFields("project").build().getUnique();
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
		
		private SSCAttributeDefinitionHelper getAttributeDefinitionHelper() {
			if ( attributeDefinitionHelper==null ) {
				attributeDefinitionHelper = conn().api(SSCAttributeDefinitionAPI.class).getAttributeDefinitionHelper();
			}
			return attributeDefinitionHelper;
		}
		
		private SSCIssueTemplateHelper getIssueTemplateHelper() {
			if ( issueTemplateHelper==null ) {
				issueTemplateHelper = conn().api(SSCIssueTemplateAPI.class).getIssueTemplateHelper();
			}
			return issueTemplateHelper;
		}
	}
}
