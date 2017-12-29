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
package com.fortify.util.rest.query;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.IJSONMapPreProcessor;
import com.fortify.util.rest.json.processor.IJSONMapProcessor;

/**
 * This package-private class is used by {@link AbstractRestConnectionQuery} to
 * process results using the configured {@link IJSONMapPreProcessor} and
 * {@link IJSONMapProcessor} instances, updating the configured {@link PagingData}
 * instance, and handling the maximum number of results.
 * 
 * @author Ruud Senden
 *
 */
class JSONMapProcessorWithPreProcessorsAndPagingSupport implements IJSONMapProcessor {
	private final List<IJSONMapPreProcessor> preProcessors;
	private final IJSONMapProcessor processor;
	private final PagingData pagingData;
	
	/**
	 * Constructor for configuring a {@link List} of {@link IJSONMapPreProcessor} instances,
	 * an {@link IJSONMapProcessor} instance, and the {@link PagingData} for the current
	 * query.
	 * 
	 * @param preProcessors
	 * @param processor
	 * @param pagingData
	 */
	public JSONMapProcessorWithPreProcessorsAndPagingSupport(List<IJSONMapPreProcessor> preProcessors, IJSONMapProcessor processor, PagingData pagingData) {
		this.preProcessors = preProcessors;
		this.processor = processor;
		this.pagingData = pagingData;
	}

	/**
	 * Process the given {@link JSONMap} and update the configured {@link PagingData}
	 * accordingly. This method checks whether the maximum number of results has not 
	 * been reached yet, and if so, invokes all configured {@link IJSONMapPreProcessor} 
	 * instances to pre-process the given {@link JSONMap}. If all of the configured 
	 * {@link IJSONMapPreProcessor#preProcess(JSONMap)} invocations return true, the 
	 * configured {@link IJSONMapProcessor} is called to actually process the given 
	 * {@link JSONMap}.
	 * 
	 */
	@Override
	public void process(JSONMap json) {
		pagingData.addProcessed(1);
		if ( !pagingData.isMaxResultsReached() && preProcess(json) ) {
			pagingData.addProcessedNotFiltered(1);
			processor.process(json);
		}
	}
	
	/**
	 * Invoke all of the configured {@link IJSONMapPreProcessor} instances.
	 * If any of them returns false, this method stops processing any remaining
	 * {@link IJSONMapPreProcessor} instances and returns false as well, essentially
	 * filtering out/ignoring the given {@link JSONMap} instance.
	 * @param json
	 * @return
	 */
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

	/**
	 * This method simply invokes the {@link IJSONMapProcessor#notifyNextPage(PagingData)}
	 * of the configured {@link IJSONMapProcessor} instance.
	 */
	@Override
	public void notifyNextPage(PagingData pagingData) {
		processor.notifyNextPage(pagingData);
	}

	
	
	

}
