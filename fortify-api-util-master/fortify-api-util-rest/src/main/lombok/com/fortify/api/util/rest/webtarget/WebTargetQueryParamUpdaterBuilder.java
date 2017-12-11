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

import javax.ws.rs.client.WebTarget;

import com.fortify.api.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.api.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.api.util.rest.webtarget.WebTargetQueryParamUpdater;

/**
 * {@link IWebTargetUpdaterBuilder} implementation for building
 * {@link WebTargetQueryParamUpdater} instances. This builder 
 * is configured with the query parameter name, and allows corresponding
 * values to be set through the {@link #paramValues(String...)} method.
 * Please see the semantics for {@link WebTarget#queryParam(String, Object...)} 
 * to understand how multiple values for a single parameter are handled.
 * 
 * @author Ruud Senden
 *
 */
public class WebTargetQueryParamUpdaterBuilder implements IWebTargetUpdaterBuilder {
	private final String paramName;
	private String[] paramValues;
	
	/**
	 * Create a new instance of this {@link IWebTargetUpdaterBuilder}
	 * with the given query parameter name.
	 * @param paramName
	 */
	public WebTargetQueryParamUpdaterBuilder(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * Set zero, one or more values to be assigned to the query parameter
	 * as configured through the constructor. Note that this method replaces
	 * any previously set values. If no values are configured, or only a
	 * single blank value, then the {@link WebTargetQueryParamUpdater} 
	 * implementation will not add the query parameter.
	 * 
	 * @param paramValues
	 */
	public final void paramValues(String... paramValues) {
		this.paramValues = paramValues;
	}

	/**
	 * Build a new {@link WebTargetQueryParamUpdater} instance based on the
	 * parameter name and values configured on this {@link WebTargetQueryParamUpdaterBuilder}
	 * instance.
	 */
	@Override
	public IWebTargetUpdater build() {
		return new WebTargetQueryParamUpdater(paramName, paramValues);
	}

}
