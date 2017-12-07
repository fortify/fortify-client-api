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
package com.fortify.api.fod.connection;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.fortify.api.util.rest.connection.IRestConnection;
import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapsToJSONListProcessor;
import com.fortify.api.util.spring.SpringExpressionUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This class provides a token-authenticated REST connection
 * for FoD.
 */
public class FoDAuthenticatingRestConnection extends FoDBasicRestConnection {
	private final FoDTokenFactory tokenProvider;
	
	public FoDAuthenticatingRestConnection(FoDRestConnectionConfig config) {
		super(config);
		this.tokenProvider = new FoDTokenFactory(config);
	}

	/** Cache for applications */
	private final LoadingCache<String, JSONMap> applicationsCache = CacheBuilder.newBuilder().maximumSize(10)
			.build(new CacheLoader<String, JSONMap>() {
				@Override
				public JSONMap load(String applicationId) {
					return getApplication(applicationId);
				}
			});
	
	/**
	 * Update the {@link Builder} to add the Authorization header.
	 */
	@Override
	public Builder updateBuilder(Builder builder) {
		return super.updateBuilder(builder)
				.header("Authorization", "Bearer "+tokenProvider.getToken());
	}
	
	// TODO Allow extra request parameters to be set by caller, like filters and ordering 
	//      (order by application id for optimal applicationsCache use)
	public void processReleases(IJSONMapProcessor processor) {
		process(getBaseResource().path("/api/v3/releases").queryParam("orderBy", "applicationId"), processor);
	}
	
	public void processReleases(String applicationId, IJSONMapProcessor processor) {
		process(getBaseResource().path("/api/v3/applications/{id}/releases").resolveTemplate("id", applicationId), processor);
	}
	
	public JSONList getReleases(String applicationId) {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processReleases(applicationId, processor);
		return processor.getJsonList();
	}
	
	public JSONMap getRelease(String releaseId) {
		return executeRequest(HttpMethod.GET, getBaseResource().path("/api/v3/releases/{id}").resolveTemplate("id", releaseId), JSONMap.class);
	}
	
	public JSONMap getRelease(String applicationName, String releaseName) {
		String filter = String.format("applicationName:%s+releaseName:%s", applicationName, releaseName);
		JSONList releases = executeRequest(HttpMethod.GET, getBaseResource().path("/api/v3/releases")
				.queryParam("filters", filter)
				.resolveTemplate("appName", applicationName)
				.resolveTemplate("releaseName", releaseName), JSONMap.class).get("items", JSONList.class);
		if ( releases==null || releases.size()!=1 ) {
			return null;
		} else {
			return releases.asValueType(JSONMap.class).get(0);
		}
	}
	
	public JSONMap getApplication(String applicationId) {
		return executeRequest(HttpMethod.GET, getBaseResource().path("/api/v3/applications/{id}").resolveTemplate("id", applicationId), JSONMap.class);
	}
	
	public JSONMap getCachedApplication(String applicationId) {
		return applicationsCache.getUnchecked(applicationId);
	}
	
	protected void process(WebTarget target, IJSONMapProcessor processor) {
		int start=0;
		int count=50;
		while ( start < count ) {
			target = target.queryParam("limit", "50").queryParam("offset", start);
			JSONMap data = executeRequest(HttpMethod.GET, target, JSONMap.class);
			count = SpringExpressionUtil.evaluateExpression(data, "totalCount", Integer.class);
			JSONList list = SpringExpressionUtil.evaluateExpression(data, "items", JSONList.class);
			start += list.size();
			for ( JSONMap obj : list.asValueType(JSONMap.class) ) {
				processor.process(obj);
			}
		}
	}
	
	public void addCommentToVulnerabilities(String releaseId, String comment, Collection<String> vulnIds) {
		JSONMap data = new JSONMap();
		data.put("comment", comment);
		data.put("vulnerabilityIds", vulnIds);
		bulkEdit(releaseId, data);
	}
	
	public void addBugLinkToVulnerabilities(String releaseId, String bugLink, Collection<String> vulnIds) {
		String path = String.format("/api/v3/releases/%s/vulnerabilities/bug-link", releaseId);
		JSONMap data = new JSONMap();
		data.put("bugLink", bugLink);
		data.put("vulnerabilityIds", vulnIds);
		postBulk(path, data);
	}
	
	public void bulkEdit(String releaseId, JSONMap bulkEditData) {
		postBulk(String.format("/api/v3/releases/%s/vulnerabilities/bulk-edit", releaseId), bulkEditData);
	}

	private void postBulk(String path, JSONMap data) {
		executeRequest(HttpMethod.POST, getBaseResource().path(path), Entity.entity(data,MediaType.APPLICATION_JSON), JSONMap.class);
	}

	public Map<String, String> getApplicationAttributeValuesByName(String applicationId) {
		JSONMap application = getCachedApplication(applicationId);
		JSONList attributes = application.get("attributes", JSONList.class);
		return attributes.filter("value!='(Not Set)'", true).toMap("name", String.class, "value", String.class);
	}
	
	public static final IFoDRestConnectionBuilder builder() {
		return (IFoDRestConnectionBuilder)builder(new FoDRestConnectionBuilderInvocationHandler());
	}
	
	private static final class FoDRestConnectionBuilderInvocationHandler extends RestConnectionBuilderInvocationHandler<FoDRestConnectionConfig> {
		public FoDRestConnectionBuilderInvocationHandler() {
			super(new FoDRestConnectionConfig());
		}
		
		@Override
		public IRestConnection build(FoDRestConnectionConfig config) {
			return new FoDAuthenticatingRestConnection(config);
		}
		
		@Override
		protected Class<?> getInterfaceType() {
			return IFoDRestConnectionBuilder.class;
		}
	}
}
