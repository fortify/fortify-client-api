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

import org.apache.commons.lang.StringUtils;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.json.embed.SSCEmbedConfig.EmbedType;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamFields;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamOrderBy;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamQ;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.query.IRestConnectionQuery;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC application version issues.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionIssuesQueryBuilder 
	extends AbstractSSCApplicationVersionChildEntityQueryBuilder<SSCApplicationVersionIssuesQueryBuilder> 
	implements ISSCEntityQueryBuilderParamFields<SSCApplicationVersionIssuesQueryBuilder>,
           ISSCEntityQueryBuilderParamOrderBy<SSCApplicationVersionIssuesQueryBuilder>,
           ISSCEntityQueryBuilderParamQ<SSCApplicationVersionIssuesQueryBuilder>
{
	private static final String[] DEEPLINK_FIELDS = {"projectVersionId", "id", "engineType", "issueInstanceId"};
	public static enum QueryMode {
		adv, issues
	}
	
	private String filterSetId = null;
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/issues"})
	public SSCApplicationVersionIssuesQueryBuilder(final SSCAuthenticatingRestConnection conn, final String applicationVersionId) {
		super(conn, applicationVersionId, true);
		appendPath("issues");
	}
	
	@Override
	public IRestConnectionQuery build() {
		String deepLinkExpression = getConn().getBaseUrlStringWithoutTrailingSlash()
				+"/html/ssc/version/${projectVersionId}/fix/${id}/?engineType=${engineType}&issue=${issueInstanceId}";
		if ( StringUtils.isNotBlank(filterSetId) ) {
			deepLinkExpression += "&filterSet="+filterSetId;
		}
		preProcessor(new JSONMapEnrichWithDeepLink(deepLinkExpression, DEEPLINK_FIELDS));
		return super.build();
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramOrderBy(boolean ignoreIfBlank, SSCOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQ(boolean ignoreIfBlank, String q) {
		return super.paramQ(ignoreIfBlank, q);
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramQAnd(boolean ignoreIfBlank, String field, Object value) {
		return super.paramQAnd(ignoreIfBlank, field, value);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupId(boolean ignoreIfBlank, String groupId) {
		return super.queryParam(ignoreIfBlank, "groupid", groupId);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupingType(boolean ignoreIfBlank, String groupingType) {
		return super.queryParam(ignoreIfBlank, "groupingtype", groupingType);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilterSetId(boolean ignoreIfBlank, String filterSetId) {
		this.filterSetId = filterSetId; // Used for building deep link pre-processor
		return super.queryParam(ignoreIfBlank, "filterset", filterSetId);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilter(boolean ignoreIfBlank, String filter) {
		return super.queryParam(ignoreIfBlank, "filter", filter);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQm(boolean ignoreIfBlank, QueryMode queryMode) {
		return super.queryParam(ignoreIfBlank, "qm", queryMode.name());
	}
	
	public SSCApplicationVersionIssuesQueryBuilder paramShowHidden(boolean showHidden) {
		return super.queryParam("showhidden", ""+showHidden);
	}
		
	public SSCApplicationVersionIssuesQueryBuilder paramShowRemoved(boolean showRemoved) {
		return super.queryParam("showremoved", ""+showRemoved);
	}
	
	public SSCApplicationVersionIssuesQueryBuilder paramShowSuppressed(boolean showSuppressed) {
		return super.queryParam("showsuppressed", ""+showSuppressed);
	}
	
	public SSCApplicationVersionIssuesQueryBuilder embedSubEntity(String entityName, EmbedType embedType, String... fields) {
		return embedSubEntity(entityName, entityName, embedType, fields);
	}
	
	public SSCApplicationVersionIssuesQueryBuilder embedAuditHistory(EmbedType embedType, String... fields) {
		return embedAuditHistory("auditHistory", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/issues/\\d+/auditHistory" })
	public SSCApplicationVersionIssuesQueryBuilder embedAuditHistory(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "auditHistory", embedType);
	}
	
	public SSCApplicationVersionIssuesQueryBuilder embedComments(EmbedType embedType, String... fields) {
		return embedComments("comments", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/issues/\\d+/comments" })
	public SSCApplicationVersionIssuesQueryBuilder embedComments(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "comments", embedType);
	}
	
	public SSCApplicationVersionIssuesQueryBuilder embedDetails(EmbedType embedType) {
		return embedDetails("details", embedType);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/issuesDetails/\\d+" })
	public SSCApplicationVersionIssuesQueryBuilder embedDetails(String propertyName, EmbedType embedType) {
		return embedSubEntity(propertyName, "details", embedType);
	}
}
