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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCCustomTagAPI;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamFields;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamOrderBy;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamQ;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.client.ssc.json.ondemand.SSCJSONMapOnDemandLoaderRest;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;
import com.fortify.util.rest.query.IRestConnectionQuery;
import com.fortify.util.spring.SpringExpressionUtil;

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
		return super.paramFields(replaceField(JSONMapEnrichWithDeepLink.DEEPLINK_FIELD, DEEPLINK_FIELDS, fields));
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramOrderBy(String orderBy, SSCOrderByDirection direction) {
		return super.paramOrderBy(orderBy, direction);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQ(String q) {
		return super.paramQ(q);
	}

	public final SSCApplicationVersionIssuesQueryBuilder paramQAnd(String field, Object value) {
		return super.paramQAnd(field, value);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupId(String groupId) {
		return super.queryParam("groupid", groupId);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramGroupingType(String groupingType) {
		return super.queryParam("groupingtype", groupingType);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilterSet(String filterSetId) {
		this.filterSetId = filterSetId; // Used for building deep link pre-processor
		return super.queryParam("filterset", filterSetId);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramFilter(String filter) {
		return super.queryParam("filter", filter);
	}
	
	public final SSCApplicationVersionIssuesQueryBuilder paramQm(QueryMode queryMode) {
		return super.queryParam("qm", queryMode.name());
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
	
	protected SSCApplicationVersionIssuesQueryBuilder embedSubEntity(String propertyName, String entityName, EmbedType embedType, String... fields) {
		return embed(propertyName, "/api/v1/issues/${id}/"+entityName, embedType, fields);
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
		return embedSubEntity(propertyName, "Comments?limit=-1", embedType);
	}
	
	public SSCApplicationVersionIssuesQueryBuilder embedDetails(EmbedType embedType, String... fields) {
		return embedDetails("details", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/issuesDetails/\\d+" })
	public SSCApplicationVersionIssuesQueryBuilder embedDetails(String propertyName, EmbedType embedType, String... fields) {
		// TODO Add pre-load support / support for adding custom tag names
		//return embed(propertyName, "/api/v1/issueDetails/${id}", embedType);
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderIssueDetailsWithCustomTagNames(getConn())));
	}
	
	private static final class SSCJSONMapOnDemandLoaderIssueDetailsWithCustomTagNames extends SSCJSONMapOnDemandLoaderRest {
		private static final long serialVersionUID = 1L;

		public SSCJSONMapOnDemandLoaderIssueDetailsWithCustomTagNames(SSCAuthenticatingRestConnection conn) {
			super(conn, "/api/v1/issueDetails/${id}");
		}
		
		@Override
		protected Object getResult(JSONMap restResult) {
			List<JSONMap> customTags = SpringExpressionUtil.evaluateExpression(restResult, "data.customTagValues", JSONList.class).asValueType(JSONMap.class);
			for ( JSONMap customTag : customTags ) {
				// TODO Can we avoid SSCAuthenticatingRestConnection cast?
				customTag.put("customTagName", ((SSCAuthenticatingRestConnection)getConnection()).api(SSCCustomTagAPI.class).getCustomTagName(customTag.get("customTagGuid", String.class), true));
			}
			return super.getResult(restResult);
		}		
	}
}
