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
	
	public int getPageSize() {
		if ( this.max==-1 || this.currentCount < this.max ) {
			return pageSize; 
		} else if ( hasFilters) {
			return pageSize;
		} else {
			return Math.min(pageSize, max-start);
		}
	}
	
	public void addToCurrentCount(int count) {
		this.currentCount+=count;
	}
	
	public void setLastPageSize(int lastPageSize) {
		this.lastPageSize = lastPageSize;
		this.start = this.start + lastPageSize;
	}
	
	public PagingData max(int max) {
		setMax(max);
		return this;
	}
}