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
package com.fortify.util.rest.json.preprocessor.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.IJSONMapPreProcessor;

import lombok.Getter;

/**
 * <p>This abstract implementation for {@link IJSONMapPreProcessor} allows for
 * filtering {@link JSONMap} instances. Subclasses must provide an implementation
 * for the {@link #isMatching(JSONMap)} method; this abstract base class allows
 * for configuring whether matched {@link JSONMap} instances should be included
 * or excluded from further processing.</p>
 * 
 * <p>This class also allows for configuring {@link IJSONMapFilterListener}
 * instances to be notified about included and excluded {@link JSONMap} 
 * objects, for example for logging purposes.</p>
 * 
 * @author Ruud Senden
 *
 */
public abstract class AbstractJSONMapFilter implements IJSONMapPreProcessor {
	public enum MatchMode { INCLUDE, EXCLUDE }
	
	@Getter private final boolean includeMatching;
	private final List<IJSONMapFilterListener> filterListeners = new ArrayList<>();
	public AbstractJSONMapFilter(MatchMode matchMode) {
		this.includeMatching = MatchMode.INCLUDE.equals(matchMode);
	}

	@Override
	public final boolean preProcess(JSONMap json) {
		boolean isIncluded = includeMatching == isMatching(json);
		invokeFilterListeners(json, isIncluded);
		return isIncluded;
	}
	
	private final void invokeFilterListeners(JSONMap json, boolean isIncluded) {
		for ( IJSONMapFilterListener filterListener : filterListeners ) {
			if ( filterListener != null ) {
				filterListener.filtered(json, isIncluded, this);
			}
		}
	}
	
	public final void addFilterListeners(IJSONMapFilterListener... filterListeners) {
		this.filterListeners.addAll(Arrays.asList(filterListeners));
	}

	protected abstract boolean isMatching(JSONMap json);
}
