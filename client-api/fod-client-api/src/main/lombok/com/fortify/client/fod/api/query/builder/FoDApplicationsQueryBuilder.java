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
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoader;
import com.fortify.util.rest.json.preprocessor.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.JSONMapEnrichWithOnDemandProperty;

/**
 * This class allows for building an {@link FoDEntityQuery} instance that allows for
 * querying FoD applications.
 * 
 * @author Ruud Senden
 *
 */
public class FoDApplicationsQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDApplicationsQueryBuilder> {
	public FoDApplicationsQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/applications");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBrowserBaseUrl().toString()+"/redirect/Applications/${applicationId}"));
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramFilterAnd(String field, String... values) {
		return super.paramFilterAnd(field, values);
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramFilterAnd(String filter) {
		return super.paramFilterAnd(filter);
	}
	
	@Override
	public FoDApplicationsQueryBuilder paramOrderBy(String orderBy, FoDOrderByDirection orderByDirection) {
		return super.paramOrderBy(orderBy, orderByDirection);
	}
	
	public FoDApplicationsQueryBuilder applicationId(String applicationId) {
		return super.paramFilterAnd("applicationId", applicationId);
	}
	
	public FoDApplicationsQueryBuilder applicationName(String applicationName) {
		return super.paramFilterAnd("applicationName", applicationName);
	}
	
	public FoDApplicationsQueryBuilder applicationType(String applicationType) {
		return super.paramFilterAnd("applicationType", applicationType);
	}
	
	public FoDApplicationsQueryBuilder onDemandAttributesMap(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, new AttributesMapOnDemandLoader()));
	}
	
	public FoDApplicationsQueryBuilder onDemandReleases(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/applications/${applicationId}/releases")));
	}
	
	private static class AttributesMapOnDemandLoader extends AbstractJSONMapOnDemandLoader {
		private static final long serialVersionUID = 1L;
		public AttributesMapOnDemandLoader() {
			super(true);
		}
		
		@Override
		public Object getOnDemand(String propertyName, JSONMap parent) {
			return parent.getOrCreateJSONList("attributes")
					.filter("value!='(Not Set)'", true).toJSONMap("name", String.class, "value", String.class);
		}
	}
}
