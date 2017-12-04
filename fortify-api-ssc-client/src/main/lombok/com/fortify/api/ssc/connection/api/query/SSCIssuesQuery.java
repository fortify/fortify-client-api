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
package com.fortify.api.ssc.connection.api.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.SSCIssueAPI.IssueSearchOptions;
import com.fortify.api.util.rest.json.AbstractJSONMapEnrich;
import com.fortify.api.util.rest.json.IJSONMapPreProcessor;
import com.fortify.api.util.rest.json.JSONMap;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Getter(AccessLevel.PROTECTED) @Builder
public class SSCIssuesQuery extends AbstractSSCApplicationVersionChildEntityQuery {
	public enum QueryMode {
		adv, issues
	}
	
	// Fields supported by AbstractRestConnectionWithCacheQuery
	private final SSCAuthenticatingRestConnection conn; 
	private final @Singular List<IJSONMapPreProcessor> preProcessors;
	private final boolean useCache;
	private final Integer maxResults;
	
	// Fields supported by AbstractSSCApplicationVersionChildEntityQuery
	private final String applicationVersionId;
	
	// Fields supported by AbstractSSCEntityQuery
	private final List<String> paramFields;
	private final String paramOrderBy;
	private final @Singular Map<String, String> paramQAnds;
	
	// Fields supported by this class
	private final String paramGroupId;
	private final String paramGroupingType;
	private final String paramFilterSetId;
	private final String paramFilter;
	private final QueryMode paramQm;
	@Builder.Default private final IssueSearchOptions issueSearchOptions = new IssueSearchOptions();
	
	public static class SSCIssuesQueryBuilder {
		public SSCIssuesQueryBuilder includeHidden() {
			issueSearchOptions.setIncludeHidden(true);
			return this;
		}
		
		public SSCIssuesQueryBuilder includeRemoved() {
			issueSearchOptions.setIncludeRemoved(true);
			return this;
		}
		
		public SSCIssuesQueryBuilder includeSuppressed() {
			issueSearchOptions.setIncludeSuppressed(true);
			return this;
		}
	}
	
	@Override
	protected String getChildEntityPath() {
		return "issues";
	}
	
	@Override
	protected List<IJSONMapPreProcessor> getDefaultPreProcessors() {
		return Arrays.asList((IJSONMapPreProcessor)new SSCJSONMapEnrichWithIssueDeepLink(getConn()));
	}

	@Override
	protected boolean isPagingSupported() {
		return true;
	}
	
	@Override
	protected WebTarget addExtraParameters(WebTarget target) {
		target = addParameterIfNotBlank(target, "groupid", paramGroupId);
		target = addParameterIfNotBlank(target, "groupingtype", paramGroupingType);
		target = addParameterIfNotBlank(target, "filterset", paramFilterSetId);
		target = addParameterIfNotBlank(target, "filter", paramFilter);
		target = addParameterIfNotBlank(target, "qm", paramQm==null?null:paramQm.name());
		return target;
	}
	
	@Override
	protected void initRequest() {
		getConn().api().issue().updateApplicationVersionIssueSearchOptions(getApplicationVersionId(), this.issueSearchOptions);
	}
	
	@RequiredArgsConstructor
	private static final class SSCJSONMapEnrichWithIssueDeepLink extends AbstractJSONMapEnrich {
		private final SSCAuthenticatingRestConnection conn;
		@Override
		public void enrich(JSONMap json) {
			json.put("deepLink", conn.api().issue().getIssueDeepLink(json));
		}
	}
}
