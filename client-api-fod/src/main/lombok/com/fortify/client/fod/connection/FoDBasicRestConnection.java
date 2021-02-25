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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.apache.http.client.ServiceUnavailableRetryStrategy;

import com.fortify.util.rest.connection.AbstractRestConnection;
import com.fortify.util.rest.connection.TooManyRequestsRetryStrategy;

/**
 * This class provides a basic, non-authenticating REST connection
 * for FoD. It's main characteristics compared to a standard 
 * {@link AbstractRestConnection}:
 * <ul>
 *  <li>Add an <code>Accept: application/json</code> header</li>
 *  <li>Enable a 'service unavailable' strategy to retry requests 
 *      that fail due to FoD rate limiting</li>
 *  <li>Optimize the number of REST requests being sent to FoD
 *      in a multi-threaded application (don't send new request
 *      if another similar request is already waiting for a rate limit
 *      to expire)</li>
 * </ul>
 * 
 * Note that multi-threading optimization is only provided on the 
 * {@link WebTarget}-based execute methods. Clients calling the
 * {@link #executeRequest(String, Builder, Class)} or
 * {@link #executeRequest(String, WebTarget, Entity, Class)} methods
 * must provide their own thread synchronization if applicable.
 */
public class FoDBasicRestConnection extends AbstractRestConnection {
	private final Map<String, Object> pathMutexes = new HashMap<>();
	private final int rateLimitMaxRetries;

	protected FoDBasicRestConnection(FoDRestConnectionConfig<?> config) {
		super(config);
		this.rateLimitMaxRetries = config.getRateLimitMaxRetries();
	}
	
	protected final Object getMutex(final String path) {
		String pathWithoutIds = path.replaceAll("\\d", "x");
		return pathMutexes.computeIfAbsent(pathWithoutIds, key->new Object());
	}
	
	@Override
	protected <T> T executeRequestWithFinalizedWebTarget(String httpMethod, WebTarget webResource, Entity<?> entity, Class<T> returnType) {
		if ( !isMultiThreaded() ) {
			return super.executeRequestWithFinalizedWebTarget(httpMethod, webResource, entity, returnType);
		} else {
			synchronized (getMutex(webResource.getUri().getPath())) {
				return super.executeRequestWithFinalizedWebTarget(httpMethod, webResource, entity, returnType);
			}
		}
	}
	
	/**
	 * Update the {@link Builder} to add the Accept header.
	 */
	@Override
	public Builder updateBuilder(Builder builder) {
		return super.updateBuilder(builder)
				.accept("application/json");
	}
	
	@Override
	protected ServiceUnavailableRetryStrategy getServiceUnavailableRetryStrategy() {
		return new TooManyRequestsRetryStrategy()
				.retryAfterHeaderName("X-Rate-Limit-Reset")
				.logPrefix("[FoD]")
				.maxRetries(rateLimitMaxRetries);
	}
}
