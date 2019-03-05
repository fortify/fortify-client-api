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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
	
	public JSONList getCustomTags(boolean useCache) {
		return queryCustomTags().useCache(useCache).build().getAll();
	}

	public JSONList getApplicationVersionCustomTags(String applicationVersionId, boolean useCache) {
		return queryApplicationVersionCustomTags(applicationVersionId).useCache(useCache).build().getAll();
	}
	
	/**
	 * Get the list of custom tag names defined for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	public List<String> getApplicationVersionCustomTagNames(String applicationVersionId, boolean useCache) {
		return getApplicationVersionCustomTags(applicationVersionId, useCache).getValues("name", String.class);
	}
	
	/**
	 * Get the list of custom tag GUID's defined for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	public List<String> getApplicationVersionCustomTagGuids(String applicationVersionId, boolean useCache) {
		return getApplicationVersionCustomTags(applicationVersionId, useCache).getValues("guid", String.class);
	}
	
	/**
	 * Get the custom tag GUID for the given custom tag name
	 * @param customTagName
	 * @return
	 */
	public String getCustomTagGuid(String customTagName, boolean useCache) {
		return getCustomTags(useCache).mapValue("name.toLowerCase()", customTagName.toLowerCase(), "guid", String.class);
	}
	
	/**
	 * Get the custom tag name for the given custom tag GUID
	 * @param customTagGUID
	 * @return
	 */
	public String getCustomTagName(String customTagGUID, boolean useCache) {
		return getCustomTags(useCache).mapValue("guid", customTagGUID, "name", String.class);
	}
	
	/**
	 * Set a custom tag value for the given collection of vulnerabilities
	 * @param applicationVersionId
	 * @param customTagName
	 * @param value
	 * @param vulns
	 */
	public void setCustomTagValue(String applicationVersionId, String customTagName, String value, Collection<Object> vulns) {
		Map<String,String> customTagValues = new HashMap<String, String>(1);
		customTagValues.put(customTagName, value);
		setCustomTagValues(applicationVersionId, customTagValues, vulns);
	}

	
	/**
	 * Set multiple custom tag values for the given collection of vulnerabilities
	 * @param applicationVersionId
	 * @param customTagNamesAndValues
	 * @param vulns
	 */
	@SSCRequiredActionsPermitted({"POST=/api/v\\d+/projectVersions/\\d+/issues/action"})
	public void setCustomTagValues(String applicationVersionId, Map<String,String> customTagNamesAndValues, Collection<Object> vulns) {
		// TODO Simplify this code
		JSONMap request = new JSONMap();
		request.put("type", "AUDIT_ISSUE");

		List<JSONMap> issues = new ArrayList<JSONMap>();
		for ( Object vuln : vulns ) {
			JSONMap issue = new JSONMap();
			Long id = SpringExpressionUtil.evaluateExpression(vuln, "id", Long.class);
			Long revision = SpringExpressionUtil.evaluateExpression(vuln, "revision", Long.class);
			if ( revision == null ) { revision = 0L; }
			issue.put("id", id);
			issue.put("revision", revision);
			issues.add(issue);
		}
		request.putPath("values.issues", issues);
		JSONList customTagAuditValues = new JSONList(customTagNamesAndValues.size());
		for ( Map.Entry<String, String> customTagNameAndValue : customTagNamesAndValues.entrySet() ) {
			JSONMap customTagAudit = new JSONMap();
			customTagAudit.put("customTagGuid", getCustomTagGuid(customTagNameAndValue.getKey(), true));
			customTagAudit.put("textValue", customTagNameAndValue.getValue());
			customTagAuditValues.add(customTagAudit);
		}
		request.putPath("values.customTagAudit", customTagAuditValues);
		conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource().path("/api/v1/projectVersions").path(applicationVersionId).path("issues/action"),
				Entity.entity(request, "application/json"), JSONMap.class);
	}
}
