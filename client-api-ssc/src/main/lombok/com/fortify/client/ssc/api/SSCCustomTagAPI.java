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
import com.fortify.util.spring.expression.helper.DefaultExpressionHelperProvider;

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
	
	/**
	 * Get an {@link SSCCustomTagHelper} instance for efficiently
	 * working with custom tag data.
	 * @return
	 */
	public SSCCustomTagHelper getCustomTagHelper() {
		return new SSCCustomTagHelper();
	}
	
	/**
	 * This class provides various utility methods for working with SSC
	 * custom tag data. Data is loaded from SSC only once per 
	 * {@link SSCCustomTagHelper} instance. As such it is recommended to 
	 * store and re-use a single instance of this class where possible. 
	 * Keep in mind though that you need to get a fresh instance in order 
	 * to see any attribute definition changes on SSC. 
	 * 
	 * @author Ruud Senden
	 *
	 */
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
	
	/**
	 * Return an {@link SSCApplicationVersionCustomTagUpdater} instance to assist
	 * with updating custom tags. Don't forget to call {@link SSCApplicationVersionCustomTagUpdater#execute()} 
	 * to actually send the request to SSC. 
	 * @return
	 */
	public SSCApplicationVersionCustomTagUpdater updateCustomTags(String applicationVersionId) {
		return new SSCApplicationVersionCustomTagUpdater(applicationVersionId);
	}
	
	public final class SSCApplicationVersionCustomTagUpdater {
		private final String applicationVersionId;
		private SSCCustomTagHelper customTagHelper;
		private JSONList issues = new JSONList();
		private JSONList customTagAuditValues = new JSONList();
		
		/**
		 * Private constructor; instances can only be created through
		 * {@link SSCCustomTagAPI#updateCustomTags(String)}
		 * @param applicationVersionId
		 */
		private SSCApplicationVersionCustomTagUpdater(String applicationVersionId) {
			this.applicationVersionId = applicationVersionId;
		}
		
		/**
		 * Set the {@link SSCCustomTagHelper} instance to be used in case any mapping between
		 * custom tag names and id's needs to be done, for example when calling the {@link #byName(Map)}
		 * or {@link #byName(String, String)} methods. If not set, a new {@link SSCCustomTagHelper} instance
		 * will be created when needed. As this will potentially result in additional SSC API calls, it is
		 * recommended to re-use an existing {@link SSCCustomTagHelper} instance if possible. For optimal
		 * performance, this method should be called before calling any of the other methods.
		 * @param helper
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater withCustomTagHelper(SSCCustomTagHelper helper) {
			this.customTagHelper = helper;
			return this;
		}
		
		/**
		 * Specify a single vulnerability for which custom tags should be updated. The given 
		 * vulnerability is expected to contain 'id' and 'revision' properties; usually this
		 * information is returned by the SSC issues endpoint.
		 * @param vulnerability
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater forVulnerability(Object vulnerability) {
			JSONMap issue = new JSONMap();
			Long id = DefaultExpressionHelperProvider.get().evaluateSimpleExpression(vulnerability, "id", Long.class);
			Long revision = DefaultExpressionHelperProvider.get().evaluateSimpleExpression(vulnerability, "revision", Long.class);
			if ( revision == null ) { revision = 0L; }
			issue.put("id", id);
			issue.put("revision", revision);
			issues.add(issue);
			return this;
		}
		
		/**
		 * Specify a collection of vulnerabilities for which custom tags should be updated.
		 * See {@link #forVulnerability(Object)} for a description of expected format of the
		 * individual entries.
		 * @param vulnerabilities
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater forVulnerabilities(Collection<Object> vulnerabilities) {
			for ( Object vuln : vulnerabilities ) {
				forVulnerability(vuln);
			}
			return this;
		}
		
		/**
		 * Update the custom tag identified by the given custom tag guid with the given value.
		 * @param customTagGuid
		 * @param customTagValue
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater byGuid(String customTagGuid, String customTagValue) {
			JSONMap customTagAudit = new JSONMap();
			customTagAudit.put("customTagGuid", customTagGuid);
			customTagAudit.put("textValue", customTagValue);
			customTagAuditValues.add(customTagAudit);
			return this;
		}
		
		/**
		 * Update the custom tags identified by the given custom tag guid's in the given {@link Map}
		 * keys with the corresponding values.
		 * @param customTagGuid
		 * @param customTagValue
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater byGuid(Map<String, String> customTagGuidToValueMap) {
			for ( Map.Entry<String, String> customTagGuidAndValue : customTagGuidToValueMap.entrySet() ) {
				byGuid(customTagGuidAndValue.getKey(), customTagGuidAndValue.getValue());
			}
			return this;
		}
		
		/**
		 * Update the custom tag identified by the given custom tag name with the given value.
		 * @param customTagName
		 * @param customTagValue
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater byName(String customTagName, String customTagValue) {
			return byGuid(getCustomTagHelper().getCustomTagGuid(customTagName), customTagValue);
		}
		
		/**
		 * Update the custom tags identified by the given custom tag names in the given {@link Map}
		 * keys with the corresponding values.
		 * @param customTagNameToValueMap
		 * @return
		 */
		public SSCApplicationVersionCustomTagUpdater byName(Map<String, String> customTagNameToValueMap) {
			for ( Map.Entry<String, String> customTagNameAndValue : customTagNameToValueMap.entrySet() ) {
				byName(customTagNameAndValue.getKey(), customTagNameAndValue.getValue());
			}
			return this;
		}
		
		/**
		 * Send the attribute(s) update request to SSC.
		 * @return
		 */
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
		
		private SSCCustomTagHelper getCustomTagHelper() {
			if ( customTagHelper==null ) {
				customTagHelper = SSCCustomTagAPI.this.getCustomTagHelper();
			}
			return customTagHelper;
		}
		
	}
}
