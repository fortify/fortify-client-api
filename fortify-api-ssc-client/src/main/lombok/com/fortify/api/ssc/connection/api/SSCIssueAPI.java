/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
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
package com.fortify.api.ssc.connection.api;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.SSCIssueQuery;
import com.fortify.api.ssc.connection.api.query.SSCIssueQuery.SSCIssueQueryBuilder;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;

public class SSCIssueAPI extends AbstractSSCAPI {
	public SSCIssueAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCIssueQueryBuilder query(String applicationVersionId) {
		return SSCIssueQuery.builder().conn(conn()).applicationVersionId(applicationVersionId);
	}
	
	/**
	 * Update the issue search options for the given application version 
	 * @param applicationVersionId
	 * @param issueSearchOptions
	 */
	public void updateApplicationVersionIssueSearchOptions(String applicationVersionId, IssueSearchOptions issueSearchOptions) {
		conn().executeRequest(HttpMethod.PUT, 
				conn().getBaseResource().path("/api/v1/projectVersions")
				.path(applicationVersionId).path("issueSearchOptions"),
				Entity.entity(issueSearchOptions.getJSONRequestData(), "application/json"), JSONMap.class);
	}
	
	public static void main(String[] args) {
		SSCAuthenticatingRestConnection conn = new SSCAuthenticatingRestConnection("http://localhost:1710/ssc", "ssc",  "Admin123!", null);
		System.out.println(conn.api().issue().query("6").build().getAll());
		System.out.println(conn.api().issue().query("6").maxResults(2).build().getAll());
	}
	
	/**
	 * This class describes the SSC issue search options, allowing to either 
	 * include or exclude removed, hidden and suppressed issues.
	 * @author Ruud Senden
	 *
	 */
	public static class IssueSearchOptions {
		private Map<String, Boolean> searchOptions = new HashMap<String, Boolean>();
		
		public boolean isIncludeRemoved() {
			return searchOptions.getOrDefault("REMOVED", false);
		}
		public void setIncludeRemoved(boolean includeRemoved) {
			searchOptions.put("REMOVED", includeRemoved);
		}
		public boolean isIncludeSuppressed() {
			return searchOptions.getOrDefault("SUPPRESSED", false);
		}
		public void setIncludeSuppressed(boolean includeSuppressed) {
			searchOptions.put("SUPPRESSED", includeSuppressed);
		}
		public boolean isIncludeHidden() {
			return searchOptions.getOrDefault("HIDDEN", false);
		}
		public void setIncludeHidden(boolean includeHidden) {
			searchOptions.put("HIDDEN", includeHidden);
		}
		
		JSONList getJSONRequestData() {
			JSONList result = new JSONList();
			result.add(getOption("REMOVED"));
			result.add(getOption("SUPPRESSED"));
			result.add(getOption("HIDDEN"));
			return result;
		}
		private JSONMap getOption(String optionType) {
			JSONMap result = new JSONMap();
			result.put("optionType", optionType);
			result.put("optionValue", searchOptions.getOrDefault(optionType, false));
			return result;
		}
	}
}
