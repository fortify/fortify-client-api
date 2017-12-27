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

import com.fortify.util.rest.json.JSONMap;

/**
 * This abstract {@link IJSONMapOnDemandLoader} implementation allows
 * for optionally replacing the on-demand loader with the actual on-demand
 * data upon first access to the on-demand property.
 * 
 * @author Ruud Senden
 *
 */
public abstract class AbstractJSONMapOnDemandLoader implements IJSONMapOnDemandLoader {
	private static final long serialVersionUID = 1L;
	private final boolean storeValue;
	public AbstractJSONMapOnDemandLoader(boolean storeValue) {
		this.storeValue = storeValue;
	}

	@Override
	public final Object getAndStoreOnDemand(String propertyName, JSONMap parent) {
		Object result = getOnDemand(propertyName, parent);
		if ( storeValue ) {
			parent.put(propertyName, result);
		}
		return result;
	}
	
	public abstract Object getOnDemand(String propertyName, JSONMap parent);

}
