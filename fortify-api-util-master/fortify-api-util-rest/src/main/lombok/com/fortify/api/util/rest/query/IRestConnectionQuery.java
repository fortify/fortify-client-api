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

import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;

/**
 * This interface allows for querying REST API's.
 * 
 * @author Ruud Senden
 *
 */
public interface IRestConnectionQuery {

	/**
	 * Process all results from the REST API call. For large result sets,
	 * this method usually provides better performance and requires less
	 * memory than the {@link #getAll()} method.
	 * 
	 * @param processor
	 */
	void processAll(IJSONMapProcessor processor);

	/**
	 * Get all results from the REST API call
	 * @return
	 */
	JSONList getAll();

	/**
	 * Get a unique result from the REST API call. If there are no
	 * results, null will be returned. If there is more than one result,
	 * an exception will be thrown.
	 * @return
	 */
	JSONMap getUnique();

}