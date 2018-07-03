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

import com.fortify.client.ssc.annotation.SSCCopyToConstructors;
import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCAttributeAPI;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.client.ssc.json.preprocessor.filter.SSCJSONMapFilterApplicationVersionNamesOrIds;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoaderWithConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;
import com.fortify.util.rest.json.preprocessor.filter.AbstractJSONMapFilter.MatchMode;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC application versions.
 * 
 * @author Ruud Senden
 * 
 */
public final class SSCApplicationVersionsQueryBuilder extends AbstractSSCEntityQueryBuilder<SSCApplicationVersionsQueryBuilder> {
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions"})
	public SSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v1/projectVersions");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBaseUrl()+"/html/ssc/index.jsp#!/version/${id}/fix", "id"));
	}

	public final SSCApplicationVersionsQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}

	public final SSCApplicationVersionsQueryBuilder paramOrderBy(String orderBy, SSCOrderByDirection direction) {
		return super.paramOrderBy(orderBy, direction);
	}

	public final SSCApplicationVersionsQueryBuilder paramQAnd(String field, String value) {
		return super.paramQAnd(field, value);
	}

	public SSCApplicationVersionsQueryBuilder id(String id) {
		return super.paramQAnd("id", id);
	}
	
	public SSCApplicationVersionsQueryBuilder applicationId(String applicationId) {
		return super.paramQAnd("project.id", applicationId);
	}

	public SSCApplicationVersionsQueryBuilder applicationName(String applicationName) {
		return super.paramQAnd("project.name", applicationName);
	}

	public SSCApplicationVersionsQueryBuilder versionName(String versionName) {
		return super.paramQAnd("name", versionName);
	}
	
	public SSCApplicationVersionsQueryBuilder nameOrId(String applicationVersionNameOrId, String separator) {
		String[] appVersionElements = applicationVersionNameOrId.split(separator);
		if ( appVersionElements.length == 1 ) {
			return id(appVersionElements[0]);
		} else if ( appVersionElements.length == 2 ) {
			return applicationName(appVersionElements[0]).versionName(appVersionElements[1]);
		} else {
			throw new IllegalArgumentException("Applications or versions containing a '+separator+' can only be specified by id");
		}
	}
	
	public SSCApplicationVersionsQueryBuilder nameOrId(String applicationVersionNameOrId) {
		return nameOrId(applicationVersionNameOrId, ":");
	}
	
	public SSCApplicationVersionsQueryBuilder namesOrIds(String applicationVersionNamesOrIds) {
		return preProcessor(new SSCJSONMapFilterApplicationVersionNamesOrIds(MatchMode.INCLUDE, applicationVersionNamesOrIds));
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandAttributes() {
		return onDemandAttributes("attributes");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandAttributeValuesByName() {
		return onDemandAttributeValuesByName("attributeValuesByName");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandBugTracker() {
		return onDemandBugTracker("bugTracker");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandCustomTags() {
		return onDemandCustomTags("customTags");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandFilterSets() {
		return onDemandFilterSets("filterSets");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandIssueSearchOptions() {
		return onDemandIssueSearchOptions("issueSearchOptions");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandPerformanceIndicatorHistories() {
		return onDemandPerformanceIndicatorHistories("performanceHistories");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandVariableHistories() {
		return onDemandVariableHistories("variableHistories");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandResponsibilities() {
		return onDemandResponsibilities("responsibilities");
	}
	
	public SSCApplicationVersionsQueryBuilder onDemandResultProcessingRules() {
		return onDemandResultProcessingRules("processingRules");
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/attributes"})
	public SSCApplicationVersionsQueryBuilder onDemandAttributes(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/attributes");
	}
	
	/**
	 * Add on-demand attribute for all application version attribute values by name.
	 * Attributes without any value will be excluded from the result.
	 * @param propertyName
	 * @return
	 */
	public SSCApplicationVersionsQueryBuilder onDemandAttributeValuesByName(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderAttributeValuesByName(getConn())));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/bugtracker"})
	public SSCApplicationVersionsQueryBuilder onDemandBugTracker(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/bugtracker", fields));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/customTags"})
	public SSCApplicationVersionsQueryBuilder onDemandCustomTags(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/customTags", fields));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/filterSets"})
	public SSCApplicationVersionsQueryBuilder onDemandFilterSets(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/filterSets");
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/issueSearchOptions"})
	public SSCApplicationVersionsQueryBuilder onDemandIssueSearchOptions(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/issueSearchOptions", fields));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/performanceIndicatorHistories"})
	public SSCApplicationVersionsQueryBuilder onDemandPerformanceIndicatorHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/performanceIndicatorHistories", fields));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/variableHistories"})
	public SSCApplicationVersionsQueryBuilder onDemandVariableHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/variableHistories", fields));
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/responsibilities"})
	public SSCApplicationVersionsQueryBuilder onDemandResponsibilities(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/responsibilities");
	}
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/resultProcessingRules"})
	public SSCApplicationVersionsQueryBuilder onDemandResultProcessingRules(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/resultProcessingRules", fields));
	}
	
	private static final class SSCJSONMapOnDemandLoaderAttributeValuesByName extends AbstractJSONMapOnDemandLoaderWithConnection<SSCAuthenticatingRestConnection> {
		private static final long serialVersionUID = 1L;

		public SSCJSONMapOnDemandLoaderAttributeValuesByName(SSCAuthenticatingRestConnection conn) {
			super(conn, true);
		}
		
		@Override @SSCCopyToConstructors
		public Object getOnDemand(String propertyName, JSONMap parent) {
			return conn().api(SSCAttributeAPI.class).getApplicationVersionAttributeValuesByName(parent.get("id",String.class));
		}
		
	}
}