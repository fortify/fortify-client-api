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

import lombok.Data;

/**
 * This class is used to hold data related to paging, like current start position and
 * page size.
 * 
 * @author Ruud Senden
 *
 */
@Data 
public class PagingData {
	private int start = 0;
	private int pageSize = 50;
	private int max = -1;
	private int total = -1;
	private int lastPageSize = -1;
	private int currentCount = 0;
	private final boolean hasFilters;
	
	public PagingData(boolean hasFilters) {
		this.hasFilters = hasFilters;
	}
	
	/**
	 * This method returns the page size for the next paged query.
	 * If no maximum results haven been configured, this method
	 * simply returns the configured page size. Otherwise, if
	 * processing filters have been configured, this method either
	 * returns the current page size if we haven't reached the
	 * maximum results, or 0 if we have reached the maximum number
	 * of results. If no processing filters have been configured,
	 * we return either 0 if the last page size is smaller than the
	 * requested page size (meaning there are no more results), or
	 * the smaller number of the configured page size, or the number
	 * of remaining results to be loaded.
	 * @return
	 */
	public int getNextPageSize() {
		if ( this.max==-1 ) {
			return pageSize; 
		} else if ( hasFilters) {
			if ( this.currentCount < this.max ) {
				return pageSize;
			} else {
				return 0;
			}
		} else {
			if ( lastPageSize < pageSize ) {
				return 0;
			} else {
				return Math.min(pageSize, max-start);
			}
		}
	}
	
	/**
	 * Add the given count to the current count of
	 * total results loaded so far.
	 * @param count
	 */
	public void addToCurrentCount(int count) {
		this.currentCount+=count;
	}
	
	/**
	 * Set the number of results retrieved during
	 * the last paging request.
	 * @param lastPageSize
	 */
	public void setLastPageSize(int lastPageSize) {
		this.lastPageSize = lastPageSize;
		this.start = this.start + lastPageSize;
	}
	
	/**
	 * Configure the maximum number of results to
	 * be loaded.
	 * @param max
	 * @return
	 */
	public PagingData max(int max) {
		setMax(max);
		return this;
	}
}