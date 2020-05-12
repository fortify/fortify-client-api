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
package com.fortify.client.ssc.api.query.builder;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamFields;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamQ;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC application version filter sets.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionIssueGroupsQueryBuilder 
	extends AbstractSSCApplicationVersionChildEntityQueryBuilder<SSCApplicationVersionIssueGroupsQueryBuilder>
	implements ISSCEntityQueryBuilderParamFields<SSCApplicationVersionIssueGroupsQueryBuilder>,
           ISSCEntityQueryBuilderParamQ<SSCApplicationVersionIssueGroupsQueryBuilder>
{
	public static enum QueryMode {
		adv, issues
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/issueGroups"})
	public SSCApplicationVersionIssueGroupsQueryBuilder(SSCAuthenticatingRestConnection conn, String applicationVersionId) {
		super(conn, applicationVersionId, false);
		appendPath("issueGroups");
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}
	
	@Deprecated
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramQ(String q) {
		return paramQ(false, q);
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramQ(boolean ignoreIfBlank, String q) {
		return super.paramQ(ignoreIfBlank, q);
	}

	@Deprecated
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramQAnd(String field, Object value) {
		return paramQAnd(false, field, value);
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramQAnd(boolean ignoreIfBlank, String field, Object value) {
		return super.paramQAnd(ignoreIfBlank, field, value);
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramGroupingType(String groupingType) {
		return super.queryParam("groupingtype", groupingType);
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramFilterSet(String filterSetId) {
		return super.queryParam("filterset", filterSetId);
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramFilter(String filter) {
		return super.queryParam("filter", filter);
	}
	
	public final SSCApplicationVersionIssueGroupsQueryBuilder paramQm(QueryMode queryMode) {
		return super.queryParam("qm", queryMode.name());
	}
	
	public SSCApplicationVersionIssueGroupsQueryBuilder paramShowHidden(boolean showHidden) {
		return super.queryParam("showhidden", ""+showHidden);
	}
		
	public SSCApplicationVersionIssueGroupsQueryBuilder paramShowRemoved(boolean showRemoved) {
		return super.queryParam("showremoved", ""+showRemoved);
	}
	
	public SSCApplicationVersionIssueGroupsQueryBuilder paramShowSuppressed(boolean showSuppressed) {
		return super.queryParam("showsuppressed", ""+showSuppressed);
	}
	
	
}
