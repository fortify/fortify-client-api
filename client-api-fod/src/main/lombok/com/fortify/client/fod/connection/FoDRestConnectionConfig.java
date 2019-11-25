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
package com.fortify.client.fod.connection;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Form;

import org.apache.commons.lang.StringUtils;

import com.fortify.util.rest.connection.AbstractRestConnectionWithUsernamePasswordConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class extends {@link AbstractRestConnectionWithUsernamePasswordConfig}, adding 
 * functionality for configuring an FoD tenant (in addition to 
 * configuring FoD user name and password as provided by our superclass),
 * or configuring FoD client id and client secret.
 * 
 * @author Ruud Senden
 *
 * @param <T>
 */
@Data @EqualsAndHashCode(callSuper=true)
public class FoDRestConnectionConfig<T extends FoDRestConnectionConfig<T>> extends AbstractRestConnectionWithUsernamePasswordConfig<T> {
	private String scope = "api-tenant";
	private String clientId;
	private String clientSecret;
	private String tenant;
	private URI    browserBaseUrl;
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
	
	public T rateLimitMaxRetries(int rateLimitMaxRetries) {
		setRateLimitMaxRetries(rateLimitMaxRetries);
		return getThis();
	}
	
	public String getUserNameWithTenant() {
		return getTenant() + "\\" + getUserName();
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
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error constructing URI");
		}
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