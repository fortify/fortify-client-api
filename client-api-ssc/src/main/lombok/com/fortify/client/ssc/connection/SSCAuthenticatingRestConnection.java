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
package com.fortify.client.ssc.connection;

import javax.ws.rs.client.Invocation.Builder;

import org.apache.commons.lang.StringUtils;

import com.fortify.util.rest.connection.AbstractRestConnection;
import com.fortify.util.rest.connection.AbstractRestConnectionConfig;
import com.fortify.util.rest.connection.IRestConnectionBuilder;

/**
 * This class provides an authenticated REST connection for SSC. Low-level API's are
 * available through the various executeRequest() methods provided by {@link AbstractRestConnection}.
 * Higher-level API's are available through the {@link #api(Class)} method. Instances of this class
 * can be created using the {@link #builder()} method.
 * 
 * @author Ruud Senden
 *
 */
public class SSCAuthenticatingRestConnection extends SSCBasicRestConnection {
	private final ISSCTokenFactory tokenFactory;
	
	/**
	 * Construct a new instance of this class based on the given {@link AbstractRestConnectionConfig}
	 * instance. 
	 * 
	 * @param config {@link SSCRestConnectionConfig} instance used to configure the SSC connection
	 */
	public SSCAuthenticatingRestConnection(SSCRestConnectionConfig<?> config) {
		super(config);
		this.tokenFactory = getTokenFactory(config);
	}

	/**
	 * Get a token factory based on the given {@link AbstractRestConnectionConfig}.
	 * Depending on the configured credentials, this will return either an
	 * {@link SSCTokenFactoryTokenCredentials} or {@link SSCTokenFactoryUserCredentials}
	 * instance.
	 *  
	 * @param config {@link SSCRestConnectionConfig} instance
	 * @return {@link ISSCTokenFactory} instance
	 */
	private ISSCTokenFactory getTokenFactory(SSCRestConnectionConfig<?> config) {
		if ( StringUtils.isNotBlank(config.getAuthToken()) ) {
			return new SSCTokenFactoryTokenCredentials(config.getAuthToken());
		} else if ( StringUtils.isNotBlank(config.getUserName()) && StringUtils.isNotBlank(config.getPassword()) ) {
			return new SSCTokenFactoryUserCredentials(config, config.getUserName(), config.getPassword(), config.getTokenDescription());
		} else {
			throw new RuntimeException("Either SSC authentication token, or user name and password need to be specified");
		}
	}
	
	@Override
	public void close() {
		super.close();
		this.tokenFactory.close();
	}
	
	/**
	 * Update the {@link Builder} to add the Authorization header.
	 */
	@Override
	public Builder updateBuilder(Builder builder) {
		// If the multiThreaded flag is enabled, use the
		// synchronized variant of getToken()
		String token = isMultiThreaded() 
				? tokenFactory.getTokenSynchronized()
				: tokenFactory.getToken();
		return super.updateBuilder(builder)
				.header("Authorization", "FortifyToken "+token);
	}
	
	/**
	 * This method returns an {@link SSCAuthenticatingRestConnectionBuilder} instance
	 * that allows for building {@link SSCAuthenticatingRestConnection} instances.
	 * @return New {@link SSCAuthenticatingRestConnectionBuilder} instance
	 */
	public static final SSCAuthenticatingRestConnectionBuilder builder() {
		return new SSCAuthenticatingRestConnectionBuilder();
	}
	
	/**
	 * This class provides a builder pattern for configuring an {@link SSCAuthenticatingRestConnection} instance.
	 * It re-uses builder functionality from {@link SSCRestConnectionConfig}, and adds a
	 * {@link #build()} method to build an {@link SSCAuthenticatingRestConnection} instance.
	 * 
	 * @author Ruud Senden
	 */
	public static class SSCAuthenticatingRestConnectionBuilder extends SSCRestConnectionConfig<SSCAuthenticatingRestConnectionBuilder> implements IRestConnectionBuilder<SSCAuthenticatingRestConnection> {
		@Override
		public SSCAuthenticatingRestConnection build() {
			return new SSCAuthenticatingRestConnection(this);
		}
	}
}
