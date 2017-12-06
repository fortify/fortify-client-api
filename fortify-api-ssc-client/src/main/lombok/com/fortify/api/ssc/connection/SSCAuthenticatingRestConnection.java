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

import java.util.Map;

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

	@lombok.Builder
	private SSCAuthenticatingRestConnection(String baseUrl, ProxyConfiguration proxy, Map<String, Object> connectionProperties,
			String authToken, String userName, String password) {
		super(baseUrl, proxy, connectionProperties);
		tokenFactory = getTokenFactory(authToken, userName, password);
	}

	private ISSCTokenFactory getTokenFactory(String authToken, String userName, String password) {
		if ( StringUtils.isNotBlank(authToken) ) {
			return new SSCTokenFactoryTokenCredentials(authToken);
		} else if ( StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) ) {
			return new SSCTokenFactoryUserCredentials(getBaseUrl(), userName, password, getProxy(), getConnectionProperties());
		} else {
			throw new RuntimeException("Either SSC authentication token, or user name and password need to be specified");
		}
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
