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
package com.fortify.client.ssc.connection;

import com.fortify.util.rest.connection.AbstractRestConnectionWithUsernamePasswordConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class extends {@link AbstractRestConnectionWithUsernamePasswordConfig}, adding 
 * functionality for configuring an SSC authentication token (in addition to 
 * configuring SSC user name and password as provided by our superclass).
 * 
 * @author Ruud Senden
 *
 * @param <T>
 */
@Data @EqualsAndHashCode(callSuper=true) @ToString(callSuper=true)
public class SSCRestConnectionConfig<T extends SSCRestConnectionConfig<T>> extends AbstractRestConnectionWithUsernamePasswordConfig<T> {
	private String authToken;
	
	@Override
	protected void parseUriUserInfo(String userInfo) {
		if ( userInfo != null && userInfo.startsWith("authToken:") ) {
			setAuthToken(userInfo.substring("authToken:".length()));
		} else {
			super.parseUriUserInfo(userInfo);
		}
	}
	
	public T authToken(String authToken) {
		setAuthToken(authToken); return getThis();
	}
}
