/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
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
package com.fortify.util.rest.connection;

import java.util.HashMap;
import java.util.Map;

public class Connections {
	private static final Map<String, IRestConnection> CONNECTIONS = new HashMap<>();

	private Connections() {}
	
	public static final void register(IRestConnection conn) {
		CONNECTIONS.put(conn.getConnectionId(), conn);
	}
	
	public static final void unRegister(IRestConnection conn) {
		CONNECTIONS.remove(conn.getConnectionId());
	}
	
	@SuppressWarnings("unchecked")
	public static final <C extends IRestConnection> C getOrNull(String connectionId, Class<C> clazz) {
		return (C)CONNECTIONS.get(connectionId);
	}
	
	public static final <C extends IRestConnection> C get(String connectionId, Class<C> clazz) {
		C result = getOrNull(connectionId, clazz);
		if ( result == null ) {
			throw new RuntimeException("Connection with id '"+connectionId+"' not available");
		}
		return result;
	}
}
