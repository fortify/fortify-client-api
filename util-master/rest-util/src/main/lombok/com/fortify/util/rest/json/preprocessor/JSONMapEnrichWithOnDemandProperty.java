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
package com.fortify.util.rest.json.preprocessor;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.IJSONMapOnDemandLoader;

import lombok.RequiredArgsConstructor;

/**
 * This {@link AbstractJSONMapEnrich} implementation allows for enriching a
 * given {@link JSONMap} instance with on-demand data by adding the configured
 * {@link IJSONMapOnDemandLoader} instance to the {@link JSONMap} under the
 * configured property name.
 * 
 * @author Ruud Senden
 *
 */
@RequiredArgsConstructor
public class JSONMapEnrichWithOnDemandProperty extends AbstractJSONMapEnrich {
	private final String propertyName;
	private final IJSONMapOnDemandLoader onDemandLoader;
	
	@Override
	protected void enrich(JSONMap json) {
		json.put(propertyName, onDemandLoader);
	}
}
