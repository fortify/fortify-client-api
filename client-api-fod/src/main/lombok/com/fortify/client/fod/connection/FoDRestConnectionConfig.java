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
package com.fortify.client.fod.connection;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Form;

import org.apache.commons.lang.StringUtils;

import com.fortify.util.rest.connection.AbstractRestConnectionWithUsernamePasswordConfig;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * This class extends {@link AbstractRestConnectionWithUsernamePasswordConfig}, adding 
 * functionality for configuring an FoD tenant (in addition to 
 * configuring FoD user name and password as provided by our superclass),
 * or configuring FoD client id and client secret.
 * 
 * @author Ruud Senden
 *
 * @param <T> Concrete {@link FoDRestConnectionConfig} type
 */
@Data @EqualsAndHashCode(callSuper=true) @ToString(callSuper=true)
public class FoDRestConnectionConfig<T extends FoDRestConnectionConfig<T>> extends AbstractRestConnectionWithUsernamePasswordConfig<T> {
	private static final Map<String, ZoneId> instanceToZoneNames = _getInstanceToZoneNames();
	
	private String[] scopes = {"api-tenant"};
	private String clientId;
	private String clientSecret;
	private String tenant;
	@Setter(AccessLevel.PRIVATE) private URI browserBaseUrl;
	@Setter(AccessLevel.PRIVATE) private String instanceName;
	private ZoneId serverZoneId;
	private int    rateLimitMaxRetries = 1;
	
	public T clientId(String clientId) {
		setClientId(clientId);
		return getThis();
	}

	public T clientSecret(String clientSecret) {
		setClientSecret(clientSecret);
		return getThis();
	}
	
	public T tenant(String tenant) {
		setTenant(tenant);
		return getThis();
	}
	
	public T scopes(String... scopes) {
		setScopes(scopes);
		return getThis();
	}
	
	public T rateLimitMaxRetries(int rateLimitMaxRetries) {
		setRateLimitMaxRetries(rateLimitMaxRetries);
		return getThis();
	}
	
	public T serverZoneId(ZoneId zoneId) {
		setServerZoneId(zoneId);
		return getThis();
	}
	
	public String getUserNameWithTenant() {
		return getTenant() + "\\" + getUserName();
	}
	
	public void setScopes(String... scopes) {
		this.scopes = scopes;
	}
	
	@Override
	protected void setBaseUrl(URI uri) {
		URI apiUrl, browserUrl;
		try {
			if (uri.getHost().startsWith("api.") ) {
				apiUrl = uri;
				browserUrl = new URI(uri.getScheme(), null, uri.getHost().substring("api.".length()), uri.getPort(), uri.getPath(), uri.getQuery(), null);
			} else {
				apiUrl = new URI(uri.getScheme(), null, "api."+uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), null);
				browserUrl = uri;
			}
			super.setBaseUrl(apiUrl);
			setBrowserBaseUrl(browserUrl);
			String instanceName = StringUtils.substringBefore(browserUrl.getHost(), ".");
			ZoneId serverZoneId = this.serverZoneId!=null 
					? this.serverZoneId 
					: instanceToZoneNames.getOrDefault(instanceName, ZoneId.systemDefault());
			setInstanceName(instanceName);
			setServerZoneId(serverZoneId);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error constructing URI");
		}
	}
	
	public void setServerZoneId(ZoneId zoneId) {
		this.serverZoneId = zoneId;
	}
	
	public void setServerZoneId(String zoneId) {
		setServerZoneId(ZoneId.of(zoneId));
	}
	
	public ZoneId getServerZoneId() {
		return serverZoneId==null ? ZoneId.systemDefault() : serverZoneId;
	}

	Form getAuth() {
		if ( StringUtils.isNotBlank(getClientId()) && StringUtils.isNotBlank(getClientSecret()) ) {
			return getAuthClientCredentials();
		} else if ( StringUtils.isNotBlank(getTenant()) && StringUtils.isNotBlank(getUserName()) && StringUtils.isNotBlank(getPassword()) ) {
			return getAuthUserCredentials();
		} else {
			throw new RuntimeException("Either client id and secret, or tenant, user name and password must be specified");
		}
	}
	
	@Override
	protected void parseUriUserInfo(String userInfo) {
		if ( userInfo != null ) {
			String user = null;
			String password = null;
			
			String[] userInfoParts = userInfo.split(":", 2);
			
			if ( userInfoParts.length > 0 ) {
				user = userInfoParts[0];
			}
			if ( userInfoParts.length > 1 ) {
				password = userInfoParts[1];
			}
			
			if ( StringUtils.isNotBlank(user) ) {
				String[] userParts = user.split("\\\\", 2);
				if ( userParts.length == 2 ) {
					setTenant(userParts[0]);
					setUserName(userParts[1]);
					setPassword(password);
				} else {
					setClientId(user);
					setClientSecret(password);
				}
			}
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
	
	private String getScope() {
		return StringUtils.join(getScopes(), ' ');
	}
	
	private static Map<String, ZoneId> _getInstanceToZoneNames() {
		Map<String, ZoneId> result = new HashMap<>();
		result.put("ams", ZoneId.of("America/Los_Angeles"));
		result.put("emea", ZoneId.of("Europe/London"));
		result.put("apac", ZoneId.of("Etc/UTC"));
		result.put("fed", ZoneId.of("Etc/UTC"));
		return result;
	}
}