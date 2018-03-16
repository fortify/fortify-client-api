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
package com.fortify.client.ssc.api.query.builder;

import java.util.List;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCCustomTagAPI;
import com.fortify.client.ssc.api.SSCIssueAPI;
import com.fortify.client.ssc.api.SSCIssueAPI.IssueSearchOptions;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.client.ssc.json.ondemand.SSCJSONMapOnDemandLoaderRest;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;
import com.fortify.util.rest.query.IRequestInitializer;
import com.fortify.util.spring.SpringExpressionUtil;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC application version issues.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionIssuesQueryBuilder extends AbstractSSCApplicationVersionChildEntityQueryBuilder<SSCApplicationVersionIssuesQueryBuilder> {
	public static enum QueryMode {
		adv, issues
	}
	
	private final IssueSearchOptions issueSearchOptions = new IssueSearchOptions();
	
	// TODO Can we propagate issueSearchOptions permissions from updateApplicationVersionIssueSearchOptions to this constructor,
	//      instead of specifying this explicitly?
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/issues", "PUT=/api/v\\\\d+/projectVersions/\\\\d+/issueSearchOptions"})
	public SSCApplicationVersionIssuesQueryBuilder(final SSCAuthenticatingRestConnection conn, final String applicationVersionId) {
		super(conn, applicationVersionId, true);
		appendPath("issues");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBaseUrl()+"/html/ssc/index.jsp#!/version/${projectVersionId}/fix/${id}/", "projectVersionId", "id"));
		setRequestInitializer(new IRequestInitializer() {
			@Override
			public void initRequest() {
				conn.api(SSCIssueAPI.class).updateApplicationVersionIssueSearchOptions(applicationVersionId, issueSearchOptions);
			}
		});
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramOrderBy(String orderBy, SSCOrderByDirection direction) {
		return super.paramOrderBy(orderBy, direction);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQ(String q) {
		return super.paramQ(q);
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramQAnd(String field, String value) {
		return super.paramQAnd(field, value);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupId(String groupId) {
		return super.queryParam("groupid", groupId);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupingType(String groupingType) {
		return super.queryParam("groupingtype", groupingType);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilterSet(String filterSetId) {
		return super.queryParam("filterset", filterSetId);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilter(String filter) {
		return super.queryParam("filter", filter);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQm(QueryMode queryMode) {
		return super.queryParam("qm", queryMode.name());
	}
	
	public SSCApplicationVersionIssuesQueryBuilder includeHidden(boolean includeHidden) {
		issueSearchOptions.setIncludeHidden(includeHidden); return _this();
	}
		
	public SSCApplicationVersionIssuesQueryBuilder includeRemoved(boolean includeRemoved) {
		issueSearchOptions.setIncludeRemoved(includeRemoved); return _this();
	}
	
	public SSCApplicationVersionIssuesQueryBuilder includeSuppressed(boolean includeSuppressed) {
		issueSearchOptions.setIncludeSuppressed(includeSuppressed); return _this();
	}
	
	public SSCApplicationVersionIssuesQueryBuilder onDemandDetails() {
		return onDemandDetails("details");
	}
	
	public SSCApplicationVersionIssuesQueryBuilder onDemandComments() {
		return onDemandComments("comments");
	}
	
	public SSCApplicationVersionIssuesQueryBuilder onDemandAuditHistory() {
		return onDemandAuditHistory("auditHistory");
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/issueDetails/\\d+"})
	public SSCApplicationVersionIssuesQueryBuilder onDemandDetails(String propertyName, String... fields) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderIssueDetailsWithCustomTagNames(getConn(), fields)));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/issues/\\d+/comments"})
	public SSCApplicationVersionIssuesQueryBuilder onDemandComments(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderRest(getConn(), "/api/v1/issues/${id}/comments")));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/issues/\\d+/auditHistory"})
	public SSCApplicationVersionIssuesQueryBuilder onDemandAuditHistory(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderRest(getConn(), "/api/v1/issues/${id}/auditHistory")));
	}
	
	private static final class SSCJSONMapOnDemandLoaderIssueDetailsWithCustomTagNames extends SSCJSONMapOnDemandLoaderRest {
		private static final long serialVersionUID = 1L;
		private final SSCAuthenticatingRestConnection conn;

		public SSCJSONMapOnDemandLoaderIssueDetailsWithCustomTagNames(SSCAuthenticatingRestConnection conn, String... fields) {
			super(conn, "/api/v1/issueDetails/${id}", fields);
			this.conn = conn;
		}
		
		@Override
		protected Object getResult(JSONMap restResult) {
			List<JSONMap> customTags = SpringExpressionUtil.evaluateExpression(restResult, "data.customTagValues", JSONList.class).asValueType(JSONMap.class);
			for ( JSONMap customTag : customTags ) {
				customTag.put("customTagName", conn.api(SSCCustomTagAPI.class).getCustomTagName(customTag.get("customTagGuid", String.class), true));
			}
			return super.getResult(restResult);
		}		
	}
}
