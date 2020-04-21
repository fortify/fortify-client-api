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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionCustomTagsQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCCustomTagsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;

/**
 * This class is used to access SSC custom tag related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCCustomTagAPI extends AbstractSSCAPI {
	
	public SSCCustomTagAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionCustomTagsQueryBuilder queryApplicationVersionCustomTags(String applicationVersionId) {
		return new SSCApplicationVersionCustomTagsQueryBuilder(conn(), applicationVersionId);
	}
	
	public SSCCustomTagsQueryBuilder queryCustomTags() {
		return new SSCCustomTagsQueryBuilder(conn());
	}
	
	public JSONList getCustomTags() {
		return queryCustomTags().build().getAll();
	}
	
	public SSCCustomTagHelper getCustomTagHelper() {
		return new SSCCustomTagHelper();
	}
	
	public final class SSCCustomTagHelper {
		private JSONList customTags;

		/**
		 * Lazy-load the list of custom tags
		 * @return
		 */
		public JSONList getCustomTags() {
			if ( customTags==null ) {
				customTags = SSCCustomTagAPI.this.getCustomTags();
			}
			return customTags;
		}
		
		/**
		 * Get the custom tag GUID for the given custom tag name
		 * @param customTagName
		 * @return
		 */
		public String getCustomTagGuid(String customTagName) {
			return getCustomTags().mapValue("name.toLowerCase()", customTagName.toLowerCase(), "guid", String.class);
		}
		
		/**
		 * Get the custom tag name for the given custom tag GUID
		 * @param customTagGUID
		 * @return
		 */
		public String getCustomTagName(String customTagGUID) {
			return getCustomTags().mapValue("guid", customTagGUID, "name", String.class);
		}
	}

	public JSONList getApplicationVersionCustomTags(String applicationVersionId) {
		return queryApplicationVersionCustomTags(applicationVersionId).build().getAll();
	}
	
	/**
	 * Get the list of custom tag names defined for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	public List<String> getApplicationVersionCustomTagNames(String applicationVersionId) {
		return getApplicationVersionCustomTags(applicationVersionId).getValues("name", String.class);
	}
	
	/**
	 * Get the list of custom tag GUID's defined for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	public List<String> getApplicationVersionCustomTagGuids(String applicationVersionId) {
		return getApplicationVersionCustomTags(applicationVersionId).getValues("guid", String.class);
	}
	
	public SSCApplicationVersionCustomTagUpdater updateCustomTags(String applicationVersionId) {
		return new SSCApplicationVersionCustomTagUpdater(applicationVersionId);
	}
	
	public final class SSCApplicationVersionCustomTagUpdater {
		private final String applicationVersionId;
		private SSCCustomTagHelper helper;
		private JSONList issues = new JSONList();
		private JSONList customTagAuditValues = new JSONList();
		
		public SSCApplicationVersionCustomTagUpdater(String applicationVersionId) {
			this.applicationVersionId = applicationVersionId;
		}
		
		public SSCApplicationVersionCustomTagUpdater withHelper(SSCCustomTagHelper helper) {
			this.helper = helper;
			return this;
		}
		
		public SSCApplicationVersionCustomTagUpdater forVulnerability(Object vulnerability) {
			JSONMap issue = new JSONMap();
			Long id = SpringExpressionUtil.evaluateExpression(vulnerability, "id", Long.class);
			Long revision = SpringExpressionUtil.evaluateExpression(vulnerability, "revision", Long.class);
			if ( revision == null ) { revision = 0L; }
			issue.put("id", id);
			issue.put("revision", revision);
			issues.add(issue);
			return this;
		}
		
		
		public SSCApplicationVersionCustomTagUpdater forVulnerabilities(Collection<Object> vulnerabilities) {
			for ( Object vuln : vulnerabilities ) {
				forVulnerability(vuln);
			}
			return this;
		}
		
		public SSCApplicationVersionCustomTagUpdater byGuid(String customTagGuid, String customTagValue) {
			JSONMap customTagAudit = new JSONMap();
			customTagAudit.put("customTagGuid", customTagGuid);
			customTagAudit.put("textValue", customTagValue);
			customTagAuditValues.add(customTagAudit);
			return this;
		}
		
		public SSCApplicationVersionCustomTagUpdater byGuid(Map<String, String> customTagGuidToValueMap) {
			for ( Map.Entry<String, String> customTagGuidAndValue : customTagGuidToValueMap.entrySet() ) {
				byGuid(customTagGuidAndValue.getKey(), customTagGuidAndValue.getValue());
			}
			return this;
		}
		
		public SSCApplicationVersionCustomTagUpdater byName(String customTagName, String customTagValue) {
			return byGuid(getHelper().getCustomTagGuid(customTagName), customTagValue);
		}
		
		public SSCApplicationVersionCustomTagUpdater byName(Map<String, String> customTagNameToValueMap) {
			for ( Map.Entry<String, String> customTagNameAndValue : customTagNameToValueMap.entrySet() ) {
				byName(customTagNameAndValue.getKey(), customTagNameAndValue.getValue());
			}
			return this;
		}
		
		private SSCCustomTagHelper getHelper() {
			if ( helper==null ) {
				helper = SSCCustomTagAPI.this.getCustomTagHelper();
			}
			return helper;
		}
		
		@SSCRequiredActionsPermitted({"POST=/api/v\\d+/projectVersions/\\d+/issues/action"})
		public void execute() {
			if ( issues.size()>0 && customTagAuditValues.size()>0 ) {
				JSONMap request = new JSONMap();
				request.put("type", "AUDIT_ISSUE");
				request.putPath("values.issues", issues);
				request.putPath("values.customTagAudit", customTagAuditValues);
				conn().executeRequest(HttpMethod.POST, 
						conn().getBaseResource().path("/api/v1/projectVersions").path(applicationVersionId).path("issues/action"),
						Entity.entity(request, "application/json"), JSONMap.class);
			}
		}
		
	}
}
