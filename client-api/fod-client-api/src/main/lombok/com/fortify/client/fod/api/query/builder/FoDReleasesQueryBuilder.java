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

import com.fortify.client.fod.api.FoDApplicationAPI;
import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoaderWithConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;

/**
 * This class allows for building an {@link FoDEntityQuery} instance that allows for
 * querying FoD releases.
 * 
 * @author Ruud Senden
 *
 */
public class FoDReleasesQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDReleasesQueryBuilder> {
	public FoDReleasesQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/releases");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBrowserBaseUrl().toString()+"/redirect/Releases/${releaseId}", "releaseId"));
	}
	
	@Override
	public FoDReleasesQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}
	
	@Override
	public FoDReleasesQueryBuilder paramFilterAnd(String field, String... values) {
		return super.paramFilterAnd(field, values);
	}
	
	@Override
	public FoDReleasesQueryBuilder paramFilterAnd(String filter) {
		return super.paramFilterAnd(filter);
	}
	
	@Override
	public FoDReleasesQueryBuilder paramOrderBy(String orderBy, FoDOrderByDirection orderByDirection) {
		return super.paramOrderBy(orderBy, orderByDirection);
	}
	
	public FoDReleasesQueryBuilder releaseId(String releaseId) {
		return super.paramFilterAnd("releaseId", releaseId);
	}
	
	public FoDReleasesQueryBuilder releaseName(String releaseName) {
		return super.paramFilterAnd("releaseName", releaseName);
	}
	
	public FoDReleasesQueryBuilder applicationAndReleaseName(String applicationName, String releaseName) {
		return applicationName(applicationName).releaseName(releaseName);
	}
	
	public FoDReleasesQueryBuilder applicationId(String applicationId) {
		return super.paramFilterAnd("applicationId", applicationId);
	}
	
	public FoDReleasesQueryBuilder applicationName(String applicationName) {
		return super.paramFilterAnd("applicationName", applicationName);
	}
	
	public FoDReleasesQueryBuilder nameOrId(String applicationAndReleaseNameOrId, String separator) {
		String[] appVersionElements = applicationAndReleaseNameOrId.split(separator);
		if ( appVersionElements.length == 1 ) {
			return releaseId(appVersionElements[0]);
		} else if ( appVersionElements.length == 2 ) {
			return applicationAndReleaseName(appVersionElements[0], appVersionElements[1]);
		} else {
			throw new IllegalArgumentException("Applications or releases containing a '+separator+' can only be specified by id");
		}
	}
	
	public FoDReleasesQueryBuilder nameOrId(String applicationAndReleaseNameOrId) {
		return nameOrId(applicationAndReleaseNameOrId, ":");
	}
	
	public FoDReleasesQueryBuilder rating(int rating) {
		return super.paramFilterAnd("rating", rating+"");
	}
	
	public FoDReleasesQueryBuilder sdlcStatusType(String sdlcStatusType) {
		return super.paramFilterAnd("sdlcStatusType", sdlcStatusType);
	}
	
	public FoDReleasesQueryBuilder isPassed(boolean isPassed) {
		return super.paramFilterAnd("isPassed", Boolean.toString(isPassed));
	}
	
	public FoDReleasesQueryBuilder onDemandApplication() {
		return onDemandApplication("application");
	}
	
	public FoDReleasesQueryBuilder onDemandApplication(String propertyName) {
		return onDemand(propertyName, "/api/v3/applications/${applicationId}");
	}
	
	public FoDReleasesQueryBuilder onDemandApplicationWithAttributesMap() {
		return onDemandApplicationWithAttributesMap("applicationWithAttributesMap");
	}
	
	public FoDReleasesQueryBuilder onDemandApplicationWithAttributesMap(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDApplicationWithAttributesMapOnDemandLoader(getConn())));
	}
	
	private static class FoDApplicationWithAttributesMapOnDemandLoader extends AbstractJSONMapOnDemandLoaderWithConnection<FoDAuthenticatingRestConnection> {
		private static final long serialVersionUID = 1L;
		
		public FoDApplicationWithAttributesMapOnDemandLoader(FoDAuthenticatingRestConnection conn) {
			super(conn, true);
		}
		
		@Override
		public Object getOnDemand(String propertyName, JSONMap parent) {
			return conn().api(FoDApplicationAPI.class).queryApplications()
				.applicationId(parent.get("applicationId", String.class))
				.onDemandAttributesMap().build().getUnique();
		}
	}
}
