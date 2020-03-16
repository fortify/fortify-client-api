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
package com.fortify.util.rest.json.preprocessor;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.enrich.AbstractJSONMapEnrich;
import com.fortify.util.rest.json.preprocessor.filter.AbstractJSONMapFilter;
import com.fortify.util.rest.json.processor.IJSONMapProcessor;

/**
 * <p>This interface is used to pre-process a given {@link JSONMap} instance
 * before {@link IJSONMapProcessor} is called. Implementations can modify
 * the given {@link JSONMap}, and/or indicate whether the given {@link JSONMap}
 * should be further processed or not.</p>
 * 
 * <p>Usually you will not implement this interface directly, but rather extend
 * from either {@link AbstractJSONMapEnrich} or {@link AbstractJSONMapFilter},
 * depending on whether you want to either enrich or filter {@link JSONMap}
 * instances.
 * 
 * @author Ruud Senden
 *
 */
public interface IJSONMapPreProcessor {
	/**
	 * This method allows for modifying the given {@link JSONMap} before it
	 * is processed by an {@link IJSONMapProcessor} instance, and/or exclude
	 * the given {@link JSONMap} instance from further processing.
	 * 
	 * @param json
	 * @return true if the given {@link JSONMap} should be included, false otherwise
	 */
	public boolean preProcess(JSONMap json);
}
