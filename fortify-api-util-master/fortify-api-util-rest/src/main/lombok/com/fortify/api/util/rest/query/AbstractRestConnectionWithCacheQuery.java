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
package com.fortify.api.util.rest.query;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import com.fortify.api.util.rest.connection.RestConnectionWithCache;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * <p>This abstract class extends {@link AbstractRestConnectionQuery} by adding optional support for
 * caching, based on the caching functionality provided by {@link RestConnectionWithCache}. Based
 * on the Lombok annotations described in {@link AbstractRestConnectionQuery}, concrete implementations
 * can provide optional caching support by defining an instance field <code>private final boolean useCache;</code>.
 * Lombok will then override our {@link #isUseCache()} method to return the value configured through the
 * Builder.</p>  
 * 
 * @author Ruud Senden
 */
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractRestConnectionWithCacheQuery<ConnType 
	extends RestConnectionWithCache, ResponseType> extends AbstractRestConnectionQuery<ConnType, ResponseType>
{	
	protected boolean useCache() {
		return false;
	}

	@Override
	protected ResponseType executeRequest(WebTarget target) {
		return useCache() 
				? conn().executeRequest(HttpMethod.GET, target, getResponseTypeClass(), getCacheName())
				: conn().executeRequest(HttpMethod.GET, target, getResponseTypeClass());
	}
	
	protected String getCacheName() {
		return this.getClass().getName();
	}
}
