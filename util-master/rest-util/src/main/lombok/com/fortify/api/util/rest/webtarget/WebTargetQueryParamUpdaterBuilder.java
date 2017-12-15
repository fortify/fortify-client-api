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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

/**
 * {@link IWebTargetUpdaterBuilder} implementation for building
 * {@link WebTargetQueryParamUpdater} instances. This builder 
 * allows request parameters to be added through the 
 * {@link #queryParam(String, String...)} method.
 * Please see the semantics for {@link WebTarget#queryParam(String, Object...)} 
 * to understand how multiple values for a single parameter are handled.
 * 
 * @author Ruud Senden
 *
 */
public class WebTargetQueryParamUpdaterBuilder implements IWebTargetUpdaterBuilder {
	private final Map<String, String[]> queryParams = new HashMap<>();

	/**
	 * Set zero, one or more values to be assigned to the given query parameter. 
	 * Note that this method replaces any previously set values for the given
	 * query parameter name. If no values are configured, or only a
	 * single blank value, then the {@link WebTargetQueryParamUpdater} 
	 * implementation will not add the query parameter.
	 * 
	 * @param paramValues
	 */
	public final WebTargetQueryParamUpdaterBuilder queryParam(String paramName, String... paramValues) {
		this.queryParams.put(paramName, paramValues); return this;
	}

	/**
	 * Build a new {@link WebTargetQueryParamUpdater} instance based on the
	 * parameter name and values configured on this {@link WebTargetQueryParamUpdaterBuilder}
	 * instance.
	 */
	@Override
	public IWebTargetUpdater build() {
		return new WebTargetQueryParamUpdater(queryParams);
	}

}
