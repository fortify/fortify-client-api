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
package com.fortify.client.fod.api.query.builder;

import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamFields;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamFilter;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamOrderByWithDirection;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;

/**
 * This class allows for building an {@link FoDEntityQuery} instance that allows for
 * querying FoD release vulnerabilities.
 * 
 * @author Ruud Senden
 *
 */
public class FoDReleaseVulnerabilitiesQueryBuilder extends AbstractFoDReleaseChildEntityQueryBuilder<FoDReleaseVulnerabilitiesQueryBuilder> 
	implements IFoDEntityQueryBuilderParamFields<FoDReleaseVulnerabilitiesQueryBuilder>, 
	           IFoDEntityQueryBuilderParamFilter<FoDReleaseVulnerabilitiesQueryBuilder>,
	           IFoDEntityQueryBuilderParamOrderByWithDirection<FoDReleaseVulnerabilitiesQueryBuilder> 
{
	private static final String[] DEEPLINK_FIELDS = {"vulnId"};
	public FoDReleaseVulnerabilitiesQueryBuilder(FoDAuthenticatingRestConnection conn, String releaseId) {
		super(conn, releaseId, true);
		appendPath("vulnerabilities");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBrowserBaseUrl().toString()+"/redirect/Issues/${vulnId}", DEEPLINK_FIELDS));
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramFields(boolean ignoreIfBlank, String... fields) {
		return super.paramFields(ignoreIfBlank, replaceField(JSONMapEnrichWithDeepLink.DEEPLINK_FIELD, DEEPLINK_FIELDS, fields));
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramFilterAnd(boolean ignoreIfBlank, String field, String... values) {
		return super.paramFilterAnd(ignoreIfBlank, field, values);
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramFilterAnd(boolean ignoreIfBlank, String filter) {
		return super.paramFilterAnd(ignoreIfBlank, filter);
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramOrderBy(boolean ignoreIfBlank, FoDOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramExcludeFilters(boolean ignoreIfBlank, Boolean excludeFilters) {
		return super.queryParam(ignoreIfBlank, "excludeFilters", Boolean.toString(excludeFilters));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramIncludeFixed(boolean ignoreIfBlank, Boolean includeFixed) {
		return super.queryParam(ignoreIfBlank, "includeFixed", Boolean.toString(includeFixed));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramIncludeSuppressed(boolean ignoreIfBlank, Boolean includeSuppressed) {
		return super.queryParam(ignoreIfBlank, "includeSuppressed", Boolean.toString(includeSuppressed));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramKeywordSearch(boolean ignoreIfBlank, String keywordSearch) {
		return super.queryParam(ignoreIfBlank, "keywordSearch", keywordSearch);
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandAll() {
		return onDemandAllData().onDemandAuditOptions().onDemandDetails().onDemandHeaders().onDemandHistory()
			.onDemandParameters().onDemandRecommendations().onDemandRequestResponse().onDemandScreenshots()
			.onDemandSummary().onDemandTraces();
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandAllData() {
		return onDemandAllData("allData");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandSummary() {
		return onDemandSummary("summary");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandDetails() {
		return onDemandDetails("details");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandRecommendations() {
		return onDemandRecommendations("recommendations");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandHistory() {
		return onDemandHistory("history");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandScreenshots() {
		return onDemandScreenshots("screenshots");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandRequestResponse() {
		return onDemandRequestResponse("requestResponse");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandHeaders() {
		return onDemandHeaders("headers");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandParameters() {
		return onDemandParameters("parameters");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandTraces() {
		return onDemandTraces("traces");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandAuditOptions() {
		return onDemandAuditOptions("auditOptions");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandAllData(String propertyName) {
		return onDemandSubEntity(propertyName, "all-data");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandSummary(String propertyName) {
		return onDemandSubEntity(propertyName, "summary");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandDetails(String propertyName) {
		return onDemandSubEntity(propertyName, "details");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandRecommendations(String propertyName) {
		return onDemandSubEntity(propertyName, "recommendations");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandHistory(String propertyName) {
		return onDemandSubEntity(propertyName, "history");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandScreenshots(String propertyName) {
		return onDemandSubEntity(propertyName, "screenshots");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandRequestResponse(String propertyName) {
		return onDemandSubEntity(propertyName, "response");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandHeaders(String propertyName) {
		return onDemandSubEntity(propertyName, "headers");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandParameters(String propertyName) {
		return onDemandSubEntity(propertyName, "parameters");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandTraces(String propertyName) {
		return onDemandSubEntity(propertyName, "traces");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandAuditOptions(String propertyName) {
		return onDemandSubEntity(propertyName, "auditOptions");
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandSubEntity(String propertyName, String entityName) {
		return onDemand(propertyName, "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/"+entityName);
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandSubEntity(String entityName) {
		return onDemandSubEntity(entityName, entityName);
	}
}
