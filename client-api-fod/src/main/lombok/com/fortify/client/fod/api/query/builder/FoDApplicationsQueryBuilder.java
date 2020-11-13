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
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoader;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;

/**
 * This class allows for building an {@link FoDEntityQuery} instance that allows for
 * querying FoD applications.
 * 
 * @author Ruud Senden
 *
 */
public class FoDApplicationsQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDApplicationsQueryBuilder> 
	implements IFoDEntityQueryBuilderParamFields<FoDApplicationsQueryBuilder>, 
	           IFoDEntityQueryBuilderParamFilter<FoDApplicationsQueryBuilder>,
	           IFoDEntityQueryBuilderParamOrderByWithDirection<FoDApplicationsQueryBuilder> 
{
	private static final String[] DEEPLINK_FIELDS = {"applicationId"};
	public FoDApplicationsQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/applications");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBrowserBaseUrl().toString()+"/redirect/Applications/${applicationId}", DEEPLINK_FIELDS));
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramFields(boolean ignoreIfBlank, String... fields) {
		return super.paramFields(ignoreIfBlank, replaceField(JSONMapEnrichWithDeepLink.DEEPLINK_FIELD, DEEPLINK_FIELDS, fields));
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramFilterAnd(boolean ignoreIfBlank, String field, String... values) {
		return super.paramFilterAnd(ignoreIfBlank, field, values);
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramFilterAnd(boolean ignoreIfBlank, String filter) {
		return super.paramFilterAnd(ignoreIfBlank, filter);
	}
	
	public FoDApplicationsQueryBuilder paramOrderBy(boolean ignoreIfBlank, FoDOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	public FoDApplicationsQueryBuilder applicationId(boolean ignoreIfBlank, String applicationId) {
		return super.paramFilterAnd(ignoreIfBlank, "applicationId", applicationId);
	}
	
	public FoDApplicationsQueryBuilder applicationName(boolean ignoreIfBlank, String applicationName) {
		return super.paramFilterAnd(ignoreIfBlank, "applicationName", applicationName);
	}
	
	public FoDApplicationsQueryBuilder applicationType(boolean ignoreIfBlank, String applicationType) {
		return super.paramFilterAnd(ignoreIfBlank, "applicationType", applicationType);
	}
	
	public FoDApplicationsQueryBuilder onDemandAll() {
		return onDemandAttributesMap().onDemandBugTracker().onDemandReleases();
	}
	
	public FoDApplicationsQueryBuilder onDemandAttributesMap() {
		return onDemandAttributesMap("attributesMap");
	}
	
	public FoDApplicationsQueryBuilder onDemandAttributesMap(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, new FoDApplicationAttributesMapOnDemandLoader()));
	}
	
	public FoDApplicationsQueryBuilder onDemandBugTracker() {
		return onDemandBugTracker("bugTracker");
	}
	
	public FoDApplicationsQueryBuilder onDemandBugTracker(String propertyName) {
		return onDemand(propertyName, "/api/v3/applications/${applicationId}/bug-tracker");
	}
	
	public FoDApplicationsQueryBuilder onDemandReleases() {
		return onDemandReleases("releases");
	}
	
	public FoDApplicationsQueryBuilder onDemandReleases(String propertyName) {
		return onDemand(propertyName, "/api/v3/applications/${applicationId}/releases");
	}
	
	private static class FoDApplicationAttributesMapOnDemandLoader extends AbstractJSONMapOnDemandLoader {
		private static final long serialVersionUID = 1L;
		public FoDApplicationAttributesMapOnDemandLoader() {
			super(true);
		}
		
		@Override
		public Object getOnDemand(String propertyName, JSONMap parent) {
			return parent.getOrCreateJSONList("attributes")
					.filter("value!='(Not Set)'", true).toJSONMap("name", String.class, "value", String.class);
		}
	}
}
