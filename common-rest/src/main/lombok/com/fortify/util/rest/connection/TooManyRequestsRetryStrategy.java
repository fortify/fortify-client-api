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
package com.fortify.util.rest.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * This class implements an Apache HttpClient 4.x {@link ServiceUnavailableRetryStrategy}
 * that will retry a request if the server responds with an HTTP 429 (TOO_MANY_REQUESTS)
 * response. This can be enabled on {@link AbstractRestConnection} instances by overriding
 * {@link AbstractRestConnection#getServiceUnavailableRetryStrategy()} as follows:
 * <code>
 *  protected ServiceUnavailableRetryStrategy getServiceUnavailableRetryStrategy() {
 *		return new TooManyRequestsRetryStrategy();
 *	}
 * </code>
 * 
 */
public final class TooManyRequestsRetryStrategy implements ServiceUnavailableRetryStrategy {
	private static final Log LOG = LogFactory.getLog(TooManyRequestsRetryStrategy.class);
	private String retryAfterHeaderName = "X-Retry-After";
	private String logPrefix = "";
	private int maxRetries = 1;
	private final ThreadLocal<Long> interval = new ThreadLocal<Long>();
	
	public TooManyRequestsRetryStrategy retryAfterHeaderName(String retryAfterHeaderName) {
		this.retryAfterHeaderName = retryAfterHeaderName;
		return this;
	}
	
	public TooManyRequestsRetryStrategy logPrefix(String logPrefix) {
		this.logPrefix = logPrefix+" ";
		return this;
	}
	
	public TooManyRequestsRetryStrategy maxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
		return this;
	}

	public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) { 
		if ( executionCount < maxRetries+1 && response.getStatusLine().getStatusCode()==429 ) {
			int retrySeconds = Integer.parseInt(response.getFirstHeader(retryAfterHeaderName).getValue());
			LOG.info(logPrefix+"Rate-limited request will be retried after "+retrySeconds+" seconds");
			interval.set((long)retrySeconds*1000);
			return true;
		}
		return false;
	}

	public long getRetryInterval() {
		Long result = interval.get();
		return result==null ? -1 : result;
	}
}
