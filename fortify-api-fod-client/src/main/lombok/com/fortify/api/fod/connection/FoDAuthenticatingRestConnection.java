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
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.CredentialsProvider;

import com.fortify.api.fod.connection.api.FoDAPI;
import com.fortify.api.util.rest.connection.AbstractRestConnection;
import com.fortify.api.util.rest.connection.IRestConnectionBuilder;
import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapsToJSONListProcessor;
import com.fortify.api.util.spring.SpringExpressionUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class provides an authenticated REST connection for FoD. Low-level API's are
 * available through the various executeRequest() methods provided by {@link AbstractRestConnection}.
 * Higher-level API's are available through the {@link #api()} method. Instances of this class
 * can be created using the {@link #builder()} method.
 * 
 * @author Ruud Senden
 *
 */
public class FoDAuthenticatingRestConnection extends FoDBasicRestConnection {
	private final FoDTokenFactory tokenProvider;
	private final FoDAPI api = new FoDAPI(this);
	
	/**
	 * Get a more high-level API for accessing SSC. 
	 * @return
	 */
	public FoDAPI api() {
		return api;
	}
	
	protected FoDAuthenticatingRestConnection(FoDRestConnectionConfig config) {
		super(config);
		this.tokenProvider = new FoDTokenFactory(new FoDBasicRestConnection(config), config.getAuth());
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
	
	/**
	 * This method returns an {@link FoDAuthenticatingRestConnectionBuilder} instance
	 * that allows for building {@link FoDAuthenticatingRestConnection} instances.
	 * @return
	 */
	public static final FoDAuthenticatingRestConnectionBuilder builder() {
		return new FoDAuthenticatingRestConnectionBuilder();
	}
	
	/**
	 * This class extends {@link RestConnectionConfig} to add additional FoD-related
	 * builder methods.
	 * 
	 * @author Ruud Senden
	 *
	 */
	@Data @EqualsAndHashCode(callSuper=true)
	public static class FoDRestConnectionConfig extends RestConnectionConfig<FoDRestConnectionConfig> {
		private String scope = "https://fod.fortify.com/";
		private String clientId;
		private String clientSecret;
		private String tenant;
		private String userName;
		private String password;
		
		public FoDRestConnectionConfig clientId(String clientId) {
			setClientId(clientId);
			return getThis();
		}
		
		public FoDRestConnectionConfig clientSecret(String clientSecret) {
			setClientSecret(clientSecret);
			return getThis();
		}
		
		public FoDRestConnectionConfig tenant(String tenant) {
			setTenant(tenant);
			return getThis();
		}
		
		public FoDRestConnectionConfig userName(String userName) {
			setUserName(userName);
			return getThis();
		}
		
		public FoDRestConnectionConfig password(String password) {
			setPassword(password);
			return getThis();
		}
		
		
		
		/**
		 * For FoD we require our own credentials handling, so this method returns null
		 */
		@Override
		public CredentialsProvider getCredentialsProvider() {
			return null;
		}
		
		public String getUserNameWithTenant() {
			return getTenant() + "\\" + getUserName();
		}
		
		public Form getAuth() {
			if ( StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret) ) {
				return getAuthClientCredentials();
			} else if ( StringUtils.isNotBlank(tenant) && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) ) {
				return getAuthUserCredentials();
			} else {
				throw new RuntimeException("Either client id and secret, or tenant, user name and password must be specified");
			}
		}
		
		private Form getAuthClientCredentials() {
			Form form = new Form();
			form.param("scope", getScope());
			form.param("grant_type", "client_credentials");
			form.param("client_id", getClientId());
			form.param("client_secret", getClientSecret());
			return form;
		}
		
		private Form getAuthUserCredentials() {
			Form form = new Form();
			form.param("scope", getScope());
			form.param("grant_type", "password");
			form.param("username", getUserNameWithTenant());
			form.param("password", getPassword());
			return form;
		}
	}
	
	/**
	 * This class provides a builder pattern for configuring an {@link FoDAuthenticatingRestConnection} instance.
	 * It re-uses builder functionality from {@link FoDRestConnectionConfig}, and adds a
	 * {@link #build()} method to build an {@link FoDAuthenticatingRestConnection} instance.
	 * 
	 * @author Ruud Senden
	 */
	public static final class FoDAuthenticatingRestConnectionBuilder extends FoDRestConnectionConfig implements IRestConnectionBuilder<FoDAuthenticatingRestConnection> {
		@Override
		public FoDAuthenticatingRestConnection build() {
			return new FoDAuthenticatingRestConnection(this);
		}
	}
}
