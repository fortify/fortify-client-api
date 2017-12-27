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
package com.fortify.util.rest.json.ondemand;

import java.io.Serializable;

import com.fortify.util.rest.json.JSONMap;

/**
 * Interface to support on-demand loading of properties in JSONMap instances.
 * Note that implementations should usually be {@link Serializable} to allow
 * the {@link JSONMap} instance to be serialized if necessary.
 * 
 * @author Ruud Senden
 *
 */
public interface IJSONMapOnDemandLoader extends Serializable {
	/**
	 * This method retrieves the value for the given property name
	 * in the given parent {@link JSONMap}. Implementations can optionally
	 * replace the on-demand loader in the parent with the retrieved
	 * property value to avoid re-loading the property value whenever
	 * the property is requested.
	 * 
	 * @param propertyName
	 * @param parent
	 * @return
	 */
	public Object getAndStoreOnDemand(String propertyName, JSONMap parent);
}
