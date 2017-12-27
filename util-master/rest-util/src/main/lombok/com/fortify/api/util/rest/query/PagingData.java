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

import com.fortify.api.util.rest.json.processor.IJSONMapProcessor;

import lombok.Getter;
import lombok.ToString;

/**
 * This class is used to hold and calculate data related to paging.
 * 
 * @author Ruud Senden
 *
 */
@Getter @ToString
public class PagingData {
	private int processedTotal = 0;
	private int processedCurrentPage = -1;
	private int processedTotalNotFiltered = 0;
	private int totalAvailable = -1;
	private int pageSize = 50;
	private int maxResults = -1;
	private int nextPageSize = -1;
	
	/**
	 * Get the start position for the next page to be loaded.
	 * {@link AbstractRestConnectionQuery} implementations will
	 * usually add this information to paged REST requests.
	 * @return
	 */
	public int getNextPageStart() {
		return processedTotal;
	}
	
	/**
	 * Get the size for the next page to be loaded.
	 * {@link AbstractRestConnectionQuery} implementations will
	 * usually add this information to paged REST requests.
	 * @return
	 */
	public int getNextPageSize() {
		return nextPageSize;
	}
	
	/**
	 * Indicate whether we've already loaded the maximum number of results
	 * (if configured).
	 * @return
	 */
	public boolean isMaxResultsReached() {
		return maxResults != -1 && processedTotalNotFiltered >= maxResults;
	}
	
	/**
	 * Set the available total number of results. This is used for
	 * information purposes only (for example to be printed/logged
	 * in the {@link IJSONMapProcessor#notifyNextPage(PagingData)}
	 * method).
	 * 
	 * @param totalAvailable
	 */
	public void setTotalAvailable(int totalAvailable) {
		this.totalAvailable = totalAvailable;
	}
	
	/**
	 * Package-private method for calculating next page size, returning
	 * the newly calculated page size. This method must be called before
	 * any call to {@link #getNextPageSize()}, and after processing each
	 * page.
	 */
	int calculateNextPageSize() {
		if ( isMaxResultsReached() || (processedCurrentPage>-1 && processedCurrentPage < pageSize) ) {
			// If we've loaded all required results, or the current page size was smaller than expected 
			// (meaning no more results), return 0.
			nextPageSize = 0;
		} else if ( maxResults < 0 || processedTotalNotFiltered < processedTotal ) {
			// If no max results is configured, or if results are being filtered, simply return configured page size
			nextPageSize = pageSize;
		} else {
			// For non-filtered results, return either configured page size, or remaining
			// number of results to be loaded if this is smaller than configured page
			// size.
			nextPageSize = Math.min(pageSize, maxResults - processedTotalNotFiltered );
		}
		return nextPageSize;
	}
	
	/**
	 * Package-private method for updating the number
	 * of processed results.
	 * @param count
	 */
	void addProcessed(int count) {
		processedTotal += count; processedCurrentPage += count;
	}
	
	/**
	 * Package-private method for updating the number
	 * of non-filtered processed results.
	 * @param count
	 */
	void addProcessedNotFiltered(int count) {
		processedTotalNotFiltered += count;
	}
	
	/**
	 * Configure the maximum number of results to
	 * be loaded.
	 * @param max
	 * @return
	 */
	public PagingData maxResults(int max) {
		this.maxResults  = max;
		return this;
	}
	
	/**
	 * Configure the page size; default is 50
	 */
	public PagingData pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}
}