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
package com.fortify.api.util.rest.connection;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.http.auth.Credentials;

import com.fortify.api.util.rest.connection.RestConnection.RestConnectionBuilderInvocationHandler;

/**
 * <p>This interface is used to build connection instances. This interface is not actually implemented by any classes, 
 * but is used to instantiate a {@link Proxy} that proxies the chaining setters to the corresponding 
 * {@link RestConnectionConfig} instance, and the {@link #build()} method to a system-specific implementation
 * to actually instantiate the connection using the configured {@link RestConnectionConfig} instance.</p>
 * 
 * <p>System-specific {@link RestConnection} implementations will need to provide a {@link RestConnectionBuilderInvocationHandler}
 * implementation that defines the concrete {@link RestConnectionConfig} implementation to be used, as well as the
 * functionality for actually instantiating the connection. The concrete {@link RestConnection} implementation can 
 * then implement a builder() method by calling the {@link RestConnection#builder(RestConnectionBuilderInvocationHandler)}
 * method.</p>
 * 
 * <p>This interface provides all the chaining setter methods that are available in the {@link RestConnectionConfig}
 * base class. System-specific implementations will need to extend this interface with any additional chaining setter
 * methods provided by their {@link RestConnectionConfig} implementation. Note that the return type for each method should
 * be the corresponding {@link IRestConnectionBuilder} interface, not the {@link RestConnectionConfig} implementation.</p>
 *  
 * @author Ruud Senden
 *
 * @param <ConnType>
 * @param <T>
 */
public interface IRestConnectionBuilder<ConnType extends IRestConnection, T extends IRestConnectionBuilder<ConnType, T>> {
	T baseUrl(String baseUrl);

	T proxy(ProxyConfig proxy);

	T connectionProperties(String connectionProperties);

	T connectionProperties(Map<String, Object> connectionProperties);

	T credentials(Credentials credentials);

	T credentials(String credentials);

	T uri(String uriWithProperties);
	
	public ConnType build();
}