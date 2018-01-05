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
package com.fortify.client.fod.api.query.builder;

import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.client.fod.json.ondemand.FoDJSONMapOnDemandLoaderRest;
import com.fortify.util.rest.json.preprocessor.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.JSONMapEnrichWithOnDemandProperty;

/**
 * This class allows for building an {@link FoDEntityQuery} instance that allows for
 * querying FoD release vulnerabilities.
 * 
 * @author Ruud Senden
 *
 */
public class FoDReleaseVulnerabilitiesQueryBuilder extends AbstractFoDReleaseChildEntityQueryBuilder<FoDReleaseVulnerabilitiesQueryBuilder> {
	public FoDReleaseVulnerabilitiesQueryBuilder(FoDAuthenticatingRestConnection conn, String releaseId) {
		super(conn, releaseId, true);
		appendPath("vulnerabilities");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBrowserBaseUrl().toString()+"/redirect/Issues/${vulnId}"));
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramFilterAnd(String field, String... values) {
		return super.paramFilterAnd(field, values);
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramFilterAnd(String filter) {
		return super.paramFilterAnd(filter);
	}
	
	@Override
	public FoDReleaseVulnerabilitiesQueryBuilder paramOrderBy(String orderBy, FoDOrderByDirection orderByDirection) {
		return super.paramOrderBy(orderBy, orderByDirection);
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramExcludeFilters(boolean excludeFilters) {
		return super.queryParam("excludeFilters", Boolean.toString(excludeFilters));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramIncludeFixed(boolean includeFixed) {
		return super.queryParam("includeFixed", Boolean.toString(includeFixed));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramIncludeSuppressed(boolean includeSuppressed) {
		return super.queryParam("includeSuppressed", Boolean.toString(includeSuppressed));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder paramKeywordSearch(String keywordSearch) {
		return super.queryParam("keywordSearch", keywordSearch);
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
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/all-data")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandSummary(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/summary")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandDetails(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/details")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandRecommendations(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/recommendations")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandHistory(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/history")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandScreenshots(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/screenshots")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandRequestResponse(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/response")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandHeaders(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/headers")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandParameters(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/parameters")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandTraces(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/traces")));
	}
	
	public FoDReleaseVulnerabilitiesQueryBuilder onDemandAuditOptions(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/releases/${releaseId}/vulnerabilities/${vulnId}/auditOptions")));
	}
}
