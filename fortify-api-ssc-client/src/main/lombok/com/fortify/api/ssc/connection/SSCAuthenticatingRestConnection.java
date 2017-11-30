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
package com.fortify.api.ssc.connection;

import javax.ws.rs.client.Invocation.Builder;

import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.api.SSCAPI;
import com.fortify.api.util.rest.connection.ProxyConfiguration;

/**
 * This class provides an authenticated REST connection for SSC.
 * @author Ruud Senden
 *
 */
public class SSCAuthenticatingRestConnection extends SSCBasicRestConnection {
	private final ISSCTokenFactory tokenFactory;
	private final SSCAPI api = new SSCAPI(this);
	
	public final SSCAPI api() {
		return api;
	}
	
	

	/**
	 * Constructor for connecting to SSC using an authentication token
	 * @param baseUrl
	 * @param token
	 * @param proxyConfig
	 */
	public SSCAuthenticatingRestConnection(String baseUrl, String token, ProxyConfiguration proxyConfig) {
		super(baseUrl, proxyConfig);
		if ( StringUtils.isBlank(token) ) {
			throw new RuntimeException("SSC authentication token cannot be blank");
		}
		this.tokenFactory = new SSCTokenFactoryTokenCredentials(token);
	}

	/**
	 * Constructor for connecting to SSC using username and password
	 * @param baseUrl
	 * @param userName
	 * @param password
	 * @param proxyConfig
	 */
	public SSCAuthenticatingRestConnection(String baseUrl, String userName, String password, ProxyConfiguration proxyConfig) {
		super(baseUrl, proxyConfig);
		if ( StringUtils.isBlank(userName) ) {
			throw new RuntimeException("SSC username cannot be blank");
		}
		if ( StringUtils.isBlank(password) ) {
			throw new RuntimeException("SSC password cannot be blank");
		}
		this.tokenFactory = new SSCTokenFactoryUserCredentials(baseUrl, userName, password, proxyConfig);
	}
	
	/**
	 * Update the {@link Builder} to add the Authorization header.
	 */
	@Override
	public Builder updateBuilder(Builder builder) {
		return super.updateBuilder(builder)
				.header("Authorization", "FortifyToken "+tokenFactory.getToken());
	}
}
