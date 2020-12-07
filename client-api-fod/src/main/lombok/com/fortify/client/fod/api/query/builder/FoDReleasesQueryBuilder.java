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

import org.apache.commons.lang.StringUtils;

import com.fortify.client.fod.api.FoDApplicationAPI;
import com.fortify.client.fod.api.json.embed.FoDEmbedConfig;
import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamFields;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamFilter;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamOrderByWithDirection;
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
public class FoDReleasesQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDReleasesQueryBuilder> 
	implements IFoDEntityQueryBuilderParamFields<FoDReleasesQueryBuilder>, 
	           IFoDEntityQueryBuilderParamFilter<FoDReleasesQueryBuilder>,
	           IFoDEntityQueryBuilderParamOrderByWithDirection<FoDReleasesQueryBuilder> 
{
	private static final String[] DEEPLINK_FIELDS = {"releaseId"};
	public FoDReleasesQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/releases");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBrowserBaseUrl().toString()+"/redirect/Releases/${releaseId}", DEEPLINK_FIELDS));
	}
	
	@Override
	public FoDReleasesQueryBuilder paramFields(boolean ignoreIfBlank, String... fields) {
		return super.paramFields(ignoreIfBlank, replaceField(JSONMapEnrichWithDeepLink.DEEPLINK_FIELD, DEEPLINK_FIELDS, fields));
	}
	
	@Override
	public FoDReleasesQueryBuilder paramFilterAnd(boolean ignoreIfBlank, String field, String... values) {
		return super.paramFilterAnd(ignoreIfBlank, field, values);
	}
	
	@Override
	public FoDReleasesQueryBuilder paramFilterAnd(boolean ignoreIfBlank, String filter) {
		return super.paramFilterAnd(ignoreIfBlank, filter);
	}
	
	public FoDReleasesQueryBuilder paramOrderBy(boolean ignoreIfBlank, FoDOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	public FoDReleasesQueryBuilder releaseId(boolean ignoreIfBlank, String releaseId) {
		return super.paramFilterAnd(ignoreIfBlank, "releaseId", releaseId);
	}
	
	public FoDReleasesQueryBuilder releaseName(boolean ignoreIfBlank, String releaseName) {
		return super.paramFilterAnd(ignoreIfBlank, "releaseName", releaseName);
	}
	
	public FoDReleasesQueryBuilder applicationAndOrReleaseName(boolean ignoreIfBlank, String applicationAndOrReleaseName) {
		return applicationAndOrReleaseName(ignoreIfBlank, applicationAndOrReleaseName, ":");
	}
	
	public FoDReleasesQueryBuilder applicationAndOrReleaseName(boolean ignoreIfBlank, String applicationAndOrReleaseName, String separator) {
		if ( !isBlank(!ignoreIfBlank, "applicationAndOrReleaseName", applicationAndOrReleaseName)) {
			String[] elts = applicationAndOrReleaseName.split(separator);
			if ( elts.length == 1 && StringUtils.isNotBlank(elts[0]) || elts.length == 2 && StringUtils.isBlank(elts[1]) ) {
				return applicationName(false, elts[0]);
			} else if ( elts.length == 2 && StringUtils.isBlank(elts[0]) ) {
				return releaseName(false, elts[1]);
			} else if ( elts.length == 2 ) {
				return applicationName(false, elts[0]).releaseName(false, elts[1]);
			} else {
				throw new IllegalArgumentException("Applications or releases containing a '"+separator+"' are unsupported");
			}
		}
		return _this();
	}
	
	public FoDReleasesQueryBuilder applicationAndReleaseName(boolean ignoreIfBlank, String applicationName, String releaseName) {
		return applicationName(ignoreIfBlank, applicationName).releaseName(ignoreIfBlank, releaseName);
	}
	
	public FoDReleasesQueryBuilder applicationId(boolean ignoreIfBlank, String applicationId) {
		return super.paramFilterAnd(ignoreIfBlank, "applicationId", applicationId);
	}
	
	public FoDReleasesQueryBuilder applicationName(boolean ignoreIfBlank, String applicationName) {
		return super.paramFilterAnd(ignoreIfBlank, "applicationName", applicationName);
	}
	
	public FoDReleasesQueryBuilder nameOrId(boolean ignoreIfBlank, String applicationAndReleaseNameOrId, String separator) {
		if ( !isBlank(!ignoreIfBlank, "applicationAndReleaseNameOrId", applicationAndReleaseNameOrId)) {
			String[] appVersionElements = applicationAndReleaseNameOrId.split(separator);
			if ( appVersionElements.length == 1 ) {
				return releaseId(ignoreIfBlank, appVersionElements[0]);
			} else if ( appVersionElements.length == 2 ) {
				return applicationAndReleaseName(ignoreIfBlank, appVersionElements[0], appVersionElements[1]);
			} else {
				throw new IllegalArgumentException("Applications or releases containing a '+separator+' can only be specified by id");
			}
		}
		return _this();
	}
	
	public FoDReleasesQueryBuilder nameOrId(boolean ignoreIfBlank, String applicationAndReleaseNameOrId) {
		return nameOrId(ignoreIfBlank, applicationAndReleaseNameOrId, ":");
	}
	
	public FoDReleasesQueryBuilder rating(boolean ignoreIfBlank, Integer rating) {
		return super.paramFilterAnd(ignoreIfBlank, "rating", rating+"");
	}
	
	public FoDReleasesQueryBuilder sdlcStatusType(boolean ignoreIfBlank, String sdlcStatusType) {
		return super.paramFilterAnd(ignoreIfBlank, "sdlcStatusType", sdlcStatusType);
	}
	
	public FoDReleasesQueryBuilder isPassed(boolean ignoreIfBlank, Boolean isPassed) {
		return super.paramFilterAnd(ignoreIfBlank, "isPassed", Boolean.toString(isPassed));
	}
	
	public FoDReleasesQueryBuilder embed(FoDEmbedConfig embedConfig) {
		if ( "application".equals(embedConfig.getSubEntity()) ) {
			return onDemandApplication(embedConfig.getPropertyName());
		} else {
			return super.embed(embedConfig);
		}
	}
	
	public FoDReleasesQueryBuilder onDemandAll() {
		return onDemandApplication();
	}
	
	public FoDReleasesQueryBuilder onDemandApplication() {
		return onDemandApplication("application");
	}
	
	public FoDReleasesQueryBuilder onDemandApplication(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new FoDApplicationOnDemandLoader(getConn())));
	}
	
	public FoDReleasesQueryBuilder onDemandSubEntity(String entityName) {
		return onDemandSubEntity(entityName, entityName);
	}
	
	public FoDReleasesQueryBuilder onDemandSubEntity(String propertyName, String subEntity) {
		switch (subEntity) {
		case "application": return onDemandApplication();
		default: return embedSubEntity(propertyName, subEntity); 
		}
	}
	
	private static class FoDApplicationOnDemandLoader extends AbstractJSONMapOnDemandLoaderWithConnection<FoDAuthenticatingRestConnection> {
		private static final long serialVersionUID = 1L;
		
		public FoDApplicationOnDemandLoader(FoDAuthenticatingRestConnection conn) {
			super(conn, true);
		}
		
		@Override
		public Object getOnDemand(FoDAuthenticatingRestConnection conn, String propertyName, JSONMap parent) {
			return conn.api(FoDApplicationAPI.class).queryApplications()
				.applicationId(false, parent.get("applicationId", String.class))
				.onDemandAttributesMap()
				.onDemandBugTracker()
				.build().getUnique();
		}
		
		@Override
		protected Class<FoDAuthenticatingRestConnection> getConnectionClazz() {
			return FoDAuthenticatingRestConnection.class;
		}
	}
}
