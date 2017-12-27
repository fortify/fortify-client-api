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

import com.fortify.util.rest.connection.IRestConnection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * This {@link AbstractJSONMapOnDemandLoader} extension allows for storing
 * and retrieving a reference to the current {@link IRestConnection} implementation
 * that was used to create this {@link IJSONMapOnDemandLoader} instance.
 * 
 * @author Ruud Senden
 *
 * @param <C>
 */
@Getter(AccessLevel.PROTECTED) @Accessors(fluent=true)
public abstract class AbstractJSONMapOnDemandLoaderWithConnection<C extends IRestConnection> extends AbstractJSONMapOnDemandLoader {
	private static final long serialVersionUID = 1L;
	private final C conn;
	
	public AbstractJSONMapOnDemandLoaderWithConnection(C conn, boolean storeValue) {
		super(storeValue);
		this.conn = conn;
	}
}
