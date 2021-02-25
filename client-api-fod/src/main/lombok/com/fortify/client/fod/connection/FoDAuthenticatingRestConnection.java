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

import javax.ws.rs.client.Invocation.Builder;

import com.fortify.util.rest.connection.AbstractRestConnection;
import com.fortify.util.rest.connection.IRestConnectionBuilder;

/**
 * This class provides an authenticated REST connection for FoD. Low-level API's are
 * available through the various executeRequest() methods provided by {@link AbstractRestConnection}.
 * Higher-level API's are available through the {@link #api(Class)} method. Instances of this class
 * can be created using the {@link #builder()} method.
 * 
 * @author Ruud Senden
 *
 */
public class FoDAuthenticatingRestConnection extends FoDBasicRestConnection {
	private final FoDTokenFactory tokenProvider;
	private final URI browserBaseUrl;
	
	public FoDAuthenticatingRestConnection(FoDRestConnectionConfig<?> config) {
		super(config);
		this.browserBaseUrl = config.getBrowserBaseUrl();
		this.tokenProvider = new FoDTokenFactory(new FoDBasicRestConnection(config), config.getAuth());
	}
	
	public URI getBrowserBaseUrl() {
		return browserBaseUrl;
	}
	
	/**
	 * Update the {@link Builder} to add the Authorization header.
	 */
	@Override
	public Builder updateBuilder(Builder builder) {
		// If the multiThreaded flag is enabled, use the
		// synchronized variant of getToken()
		String token = isMultiThreaded() 
				? tokenProvider.getTokenSynchronized()
				: tokenProvider.getToken();
		return super.updateBuilder(builder)
				.header("Authorization", "Bearer "+token);
	}
	
	/**
	 * This method returns an {@link FoDAuthenticatingRestConnectionBuilder} instance
	 * that allows for building {@link FoDAuthenticatingRestConnection} instances.
	 * @return new {@link FoDAuthenticatingRestConnectionBuilder} instance
	 */
	public static final FoDAuthenticatingRestConnectionBuilder builder() {
		return new FoDAuthenticatingRestConnectionBuilder();
	}
	
	/**
	 * This class provides a builder pattern for configuring an {@link FoDAuthenticatingRestConnection} instance.
	 * It re-uses builder functionality from {@link FoDRestConnectionConfig}, and adds a
	 * {@link #build()} method to build an {@link FoDAuthenticatingRestConnection} instance.
	 * 
	 * @author Ruud Senden
	 */
	public static class FoDAuthenticatingRestConnectionBuilder extends FoDRestConnectionConfig<FoDAuthenticatingRestConnectionBuilder> implements IRestConnectionBuilder<FoDAuthenticatingRestConnection> {
		@Override
		public FoDAuthenticatingRestConnection build() {
			return new FoDAuthenticatingRestConnection(this);
		}
	}
}
