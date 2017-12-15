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
package com.fortify.api.fod.connection.api.query.builder;

import com.fortify.api.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.AbstractJSONMapOnDemandLoaderWithConnection;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapEnrichWithOnDemandProperty;

public class FoDReleaseQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDReleaseQueryBuilder> {
	public FoDReleaseQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/releases");
		// TODO Add PreProcessor to add deep link?
	}
	
	@Override
	public FoDReleaseQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}
	
	@Override
	public FoDReleaseQueryBuilder paramFilterAnd(String field, String value) {
		return super.paramFilterAnd(field, value);
	}
	
	@Override
	public FoDReleaseQueryBuilder paramOrderBy(String orderBy, OrderByDirection orderByDirection) {
		return super.paramOrderBy(orderBy, orderByDirection);
	}
	
	public FoDReleaseQueryBuilder releaseId(String releaseId) {
		return super.paramFilterAnd("releaseId", releaseId);
	}
	
	public FoDReleaseQueryBuilder releaseName(String releaseName) {
		return super.paramFilterAnd("releaseName", releaseName);
	}
	
	public FoDReleaseQueryBuilder applicationAndReleaseName(String applicationName, String releaseName) {
		return applicationName(applicationName).releaseName(releaseName);
	}
	
	public FoDReleaseQueryBuilder applicationId(String applicationId) {
		return super.paramFilterAnd("applicationId", applicationId);
	}
	
	public FoDReleaseQueryBuilder applicationName(String applicationName) {
		return super.paramFilterAnd("applicationName", applicationName);
	}
	
	public FoDReleaseQueryBuilder nameOrId(String applicationAndReleaseNameOrId, String separator) {
		String[] appVersionElements = applicationAndReleaseNameOrId.split(separator);
		if ( appVersionElements.length == 1 ) {
			return releaseId(appVersionElements[0]);
		} else if ( appVersionElements.length == 2 ) {
			return applicationAndReleaseName(appVersionElements[0], appVersionElements[1]);
		} else {
			throw new IllegalArgumentException("Applications or releases containing a '+separator+' can only be specified by id");
		}
	}
	
	public FoDReleaseQueryBuilder nameOrId(String applicationAndReleaseNameOrId) {
		return nameOrId(applicationAndReleaseNameOrId, ":");
	}
	
	public FoDReleaseQueryBuilder rating(int rating) {
		return super.paramFilterAnd("rating", rating+"");
	}
	
	public FoDReleaseQueryBuilder sdlcStatusType(String sdlcStatusType) {
		return super.paramFilterAnd("sdlcStatusType", sdlcStatusType);
	}
	
	public FoDReleaseQueryBuilder isPassed(boolean isPassed) {
		return super.paramFilterAnd("isPassed", Boolean.toString(isPassed));
	}
	
	public FoDReleaseQueryBuilder embedOnDemandObjects() {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty("application", new OnDemandApplication(getConn())));
	}
	
	private static class OnDemandApplication extends AbstractJSONMapOnDemandLoaderWithConnection<FoDAuthenticatingRestConnection> {
		private static final long serialVersionUID = 1L;
		public OnDemandApplication(FoDAuthenticatingRestConnection conn) {
			super(conn);
		}
		@Override
		public Object getOnDemand(JSONMap parent) {
			return conn().api().application().getApplicationById(parent.get("applicationId", String.class));
		}
	}
}
