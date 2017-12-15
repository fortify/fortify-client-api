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

import javax.ws.rs.core.Form;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.CredentialsProvider;

import com.fortify.api.util.rest.connection.RestConnectionConfig;
import com.fortify.api.util.rest.connection.RestConnectionConfigWithoutCredentialsProvider;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class extends {@link RestConnectionConfig} to add additional FoD-related
 * builder methods.
 * 
 * @author Ruud Senden
 *
 */
@Data @EqualsAndHashCode(callSuper=true)
public class FoDRestConnectionConfig<T extends FoDRestConnectionConfig<T>> extends RestConnectionConfigWithoutCredentialsProvider<T> {
	private String scope = "https://fod.fortify.com/";
	private String clientId;
	private String clientSecret;
	private String tenant;
	private String userName;
	private String password;
	
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
	
	public T userName(String userName) {
		setUserName(userName);
		return getThis();
	}
	
	public T password(String password) {
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