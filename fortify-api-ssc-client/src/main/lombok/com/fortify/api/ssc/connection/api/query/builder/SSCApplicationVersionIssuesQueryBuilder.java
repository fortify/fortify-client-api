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
package com.fortify.api.ssc.connection.api.query.builder;

import com.fortify.api.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.SSCIssueAPI.IssueSearchOptions;
import com.fortify.api.ssc.connection.api.query.SSCEntityQuery;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamFields;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamOrderBy;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamQ;
import com.fortify.api.util.rest.json.AbstractJSONMapEnrich;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.query.AbstractRestConnectionQuery.IRequestInitializer;
import com.fortify.api.util.rest.webtarget.WebTargetQueryParamUpdaterBuilder;

import lombok.RequiredArgsConstructor;

/**
 * This builder class can be used to build {@link SSCEntityQuery} instances
 * for querying application version vulnerabilities.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionIssuesQueryBuilder extends AbstractSSCApplicationVersionChildEntityQueryBuilder<SSCApplicationVersionIssuesQueryBuilder> {
	public static enum QueryMode {
		adv, issues
	}
	
	private final SSCParamFields paramFields = add(new SSCParamFields());
	private final SSCParamOrderBy paramOrderBy = add(new SSCParamOrderBy());
	private final SSCParamQ paramQ = add(new SSCParamQ());
	private final WebTargetQueryParamUpdaterBuilder paramGroupId = add(new WebTargetQueryParamUpdaterBuilder("groupid"));
	private final WebTargetQueryParamUpdaterBuilder paramGroupingType = add(new WebTargetQueryParamUpdaterBuilder("groupingtype"));
	private final WebTargetQueryParamUpdaterBuilder paramFilterSet = add(new WebTargetQueryParamUpdaterBuilder("filterset"));
	private final WebTargetQueryParamUpdaterBuilder paramFilter = add(new WebTargetQueryParamUpdaterBuilder("filter"));
	private final WebTargetQueryParamUpdaterBuilder paramQm = add(new WebTargetQueryParamUpdaterBuilder("qm"));
	private final IssueSearchOptions issueSearchOptions = new IssueSearchOptions();
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/issues"})
	public SSCApplicationVersionIssuesQueryBuilder(final SSCAuthenticatingRestConnection conn, final String applicationVersionId) {
		super(conn, applicationVersionId, true);
		preProcessor(new SSCJSONMapEnrichWithIssueDeepLink(conn));
		setRequestInitializer(new IRequestInitializer() {
			@Override
			public void initRequest() {
				conn.api().issue().updateApplicationVersionIssueSearchOptions(applicationVersionId, issueSearchOptions);
			}
		});
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramFields(String... fields) {
		paramFields.paramFields(fields); return _this();
	}

	public final SSCApplicationVersionIssuesQueryBuilder orderBy(String orderBy) {
		paramOrderBy.orderBy(orderBy); return _this();
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramQAnd(String field, String value) {
		paramQ.paramQAnd(field, value); return _this();
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupId(String groupId) {
		paramGroupId.paramValues(groupId); return _this();
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupingType(String groupingType) {
		paramGroupingType.paramValues(groupingType); return _this();
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilterSet(String filterSetId) {
		paramFilterSet.paramValues(filterSetId); return _this();
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilter(String filter) {
		paramFilter.paramValues(filter); return _this();
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQm(QueryMode queryMode) {
		paramQm.paramValues(queryMode.name()); return _this();
	}
	
	public SSCApplicationVersionIssuesQueryBuilder includeHidden() {
		issueSearchOptions.setIncludeHidden(true); return _this();
	}
		
	public SSCApplicationVersionIssuesQueryBuilder includeRemoved() {
		issueSearchOptions.setIncludeRemoved(true); return _this();
	}
	
	public SSCApplicationVersionIssuesQueryBuilder includeSuppressed() {
		issueSearchOptions.setIncludeSuppressed(true); return _this();
	}
	
	@Override
	protected String getChildEntityPath() {
		return "issues";
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
