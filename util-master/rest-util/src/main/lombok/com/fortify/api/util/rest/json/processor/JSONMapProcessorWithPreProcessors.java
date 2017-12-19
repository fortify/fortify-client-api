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
package com.fortify.api.util.rest.json.processor;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.preprocessor.IJSONMapPreProcessor;
import com.fortify.api.util.rest.query.PagingData;

public class JSONMapProcessorWithPreProcessors implements IJSONMapProcessor {
	private final List<IJSONMapPreProcessor> preProcessors;
	private final IJSONMapProcessor processor;
	
	public JSONMapProcessorWithPreProcessors(List<IJSONMapPreProcessor> preProcessors, IJSONMapProcessor processor) {
		this.preProcessors = preProcessors;
		this.processor = processor;
	}

	@Override
	public void process(JSONMap json) {
		if ( preProcess(json) ) {
			processor.process(json);
		}
	}
	
	private boolean preProcess(JSONMap json) {
		boolean result = true;
		if ( CollectionUtils.isNotEmpty(preProcessors) ) {
			for ( IJSONMapPreProcessor preProcessor : preProcessors ) {
				result &= preProcessor.preProcess(json);
				if ( !result ) { break; }
			}
		}
		return result;
	}

	@Override
	public void nextPage(PagingData pagingData) {
		processor.nextPage(pagingData);
	}

	
	
	

}
