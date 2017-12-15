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
import org.apache.http.auth.Credentials;

import com.fortify.api.ssc.connection.api.SSCAPI;
import com.fortify.api.util.rest.connection.AbstractRestConnection;
import com.fortify.api.util.rest.connection.IRestConnectionBuilder;
import com.fortify.api.util.rest.connection.RestConnectionConfig;
import com.fortify.api.util.rest.connection.RestConnectionConfigWithoutCredentialsProvider;

/**
 * This class provides an authenticated REST connection for SSC. Low-level API's are
 * available through the various executeRequest() methods provided by {@link AbstractRestConnection}.
 * Higher-level API's are available through the {@link #api()} method. Instances of this class
 * can be created using the {@link #builder()} method.
 * 
 * @author Ruud Senden
 *
 */
public class SSCAuthenticatingRestConnection extends SSCBasicRestConnection {
	private final ISSCTokenFactory tokenFactory;
	private final SSCAPI api = new SSCAPI(this);
	
	/**
	 * Get a more high-level API for accessing SSC. 
	 * @return
	 */
	public SSCAPI api() {
		return api;
	}
	
	/**
	 * Construct a new instance of this class based on the given {@link RestConnectionConfig}
	 * instance. 
	 * 
	 * @param config
	 */
	protected SSCAuthenticatingRestConnection(SSCRestConnectionConfig<?> config) {
		super(config);
		this.tokenFactory = getTokenFactory(config);
	}

	/**
	 * Get a token factory based on the given {@link RestConnectionConfig}.
	 * Depending on the configured credentials, this will return either an
	 * {@link SSCTokenFactoryTokenCredentials} or {@link SSCTokenFactoryUserCredentials}
	 * instance.
	 *  
	 * @param config
	 * @return
	 */
	private ISSCTokenFactory getTokenFactory(RestConnectionConfig<?> config) {
		Credentials credentials = config.getCredentials();
		String userName = credentials.getUserPrincipal()==null?null:credentials.getUserPrincipal().getName();
		String password = credentials.getPassword();
		if ( StringUtils.isBlank(userName) || "apitoken".equalsIgnoreCase(userName) ) {
			return new SSCTokenFactoryTokenCredentials(password);
		} else if ( StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) ) {
			return new SSCTokenFactoryUserCredentials(new SSCBasicRestConnection(config), userName, password);
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
	
	/**
	 * This method returns an {@link SSCAuthenticatingRestConnectionBuilder} instance
	 * that allows for building {@link SSCAuthenticatingRestConnection} instances.
	 * @return
	 */
	public static final SSCAuthenticatingRestConnectionBuilder builder() {
		return new SSCAuthenticatingRestConnectionBuilder();
	}
	
	/**
	 * This class provides a builder pattern for configuring an {@link SSCAuthenticatingRestConnection} instance.
	 * It re-uses builder functionality from {@link RestConnectionConfigWithoutCredentialsProvider}, and adds a
	 * {@link #build()} method to build an {@link SSCAuthenticatingRestConnection} instance.
	 * 
	 * @author Ruud Senden
	 */
	public static final class SSCAuthenticatingRestConnectionBuilder extends SSCRestConnectionConfig<SSCAuthenticatingRestConnectionBuilder> implements IRestConnectionBuilder<SSCAuthenticatingRestConnection> {
		@Override
		public SSCAuthenticatingRestConnection build() {
			return new SSCAuthenticatingRestConnection(this);
		}
	}
}
