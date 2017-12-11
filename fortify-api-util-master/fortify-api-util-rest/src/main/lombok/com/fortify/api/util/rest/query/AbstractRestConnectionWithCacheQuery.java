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

import javax.ws.rs.client.WebTarget;

import com.fortify.api.util.rest.connection.AbstractRestConnectionWithCache;

import lombok.Getter;

/**
 * <p>This {@link AbstractRestConnectionQuery} extension adds support for optionally caching REST responses.
 * Instances of this class are configured using an {@link AbstractRestConnectionWithCacheQueryConfig}, which extends
 * from {@link AbstractRestConnectionQueryConfig} and adds a property for specifying whether caching should be 
 * enabled or disabled for this specific instance.</p>
 * 
 * <p>Please see {@link com.fortify.api.util.rest.query}, {@link AbstractRestConnectionQuery} and
 * {@link AbstractRestConnectionQueryConfig} for more details about using this API. Please see
 * {@link AbstractRestConnectionWithCache} for more details about caching.</p>
 * 
 * @author Ruud Senden
 */
@Getter
public abstract class AbstractRestConnectionWithCacheQuery<ConnType extends AbstractRestConnectionWithCache, ResponseType> 
	extends AbstractRestConnectionQuery<ConnType, ResponseType>
{	
	private final boolean useCache;
	
	protected AbstractRestConnectionWithCacheQuery(AbstractRestConnectionWithCacheQueryConfig<ConnType, ?> config) {
		super(config);
		this.useCache = config.isUseCache();
	}

	
	@Override
	protected ResponseType executeRequest(WebTarget target) {
		return useCache && getEntity()==null
				? getConn().executeRequest(getHttpMethod(), target, getResponseTypeClass(), getCacheName())
				: super.executeRequest(target);
	}
	
	protected String getCacheName() {
		return this.getClass().getName();
	}
}
