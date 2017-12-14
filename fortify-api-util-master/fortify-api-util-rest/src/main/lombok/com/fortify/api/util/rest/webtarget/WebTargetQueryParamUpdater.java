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
package com.fortify.api.util.rest.webtarget;

import java.util.Map;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

/**
 * This {@link IWebTargetUpdater} implementation allows for adding
 * a query parameter (as configured through the constructor) to a given 
 * {@link WebTarget} instance. Please see the semantics for 
 * {@link WebTarget#queryParam(String, Object...)} to understand
 * how multiple values for a single parameter are handled.
 * 
 * @author Ruud Senden
 *
 */
public class WebTargetQueryParamUpdater implements IWebTargetUpdater {
	private Map<String, String[]> queryParams;
	
	/**
	 * Create a new instance for adding the given query parameter with
	 * the given values to {@link WebTarget} instances. If no values are
	 * given, or only a single blank value, then the query parameter will
	 * not be added.
	 * 
	 * @param name
	 * @param values
	 */
	public WebTargetQueryParamUpdater(String name, String... values) {
		this.queryParams = ImmutableMap.of(name, values);
	}
	
	/**
	 * Create a new instance for adding the query parameter names with
	 * corresponding values from the given {@link Map} to {@link WebTarget} 
	 * instances. For any parameter, if no values are given, or only a single 
	 * blank value, then the query parameter will not be added.
	 * 
	 * @param name
	 * @param values
	 */
	public WebTargetQueryParamUpdater(Map<String, String[]> queryParams) {
		this.queryParams = ImmutableMap.copyOf(queryParams);
	}

	/**
	 * Add the configured query parameter to the given {@link WebTarget}
	 * instance. If no values are configured, or only a single blank value, 
	 * then the query parameter will not be added.
	 */
	@Override
	public WebTarget update(WebTarget target) {
		for ( Map.Entry<String, String[]> entry : queryParams.entrySet() ) {
			String name = entry.getKey();
			String[] values = entry.getValue();
			if ( values != null && values.length>0 && !(values.length==1 && StringUtils.isBlank(values[0])) ) {
				target = target.queryParam(name, (Object[])values);
			}
		}
		return target;
	}

}
