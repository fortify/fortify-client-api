/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates, a Micro Focus company
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

import org.apache.http.auth.UsernamePasswordCredentials;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This {@link AbstractRestConnectionConfig} implementation adds properties for
 * configuring user name and password, either explicitly or by parsing the 
 * user info provided in the base URL.
 * 
 * @author Ruud Senden
 *
 * @param <T>
 */
@Data @EqualsAndHashCode(callSuper=true) @ToString(callSuper=true)
public abstract class AbstractRestConnectionWithUsernamePasswordConfig<T extends AbstractRestConnectionWithUsernamePasswordConfig<T>> extends AbstractRestConnectionConfig<T> {
	private String userName;
	private String password;
	
	public T userName(String userName) {
		setUserName(userName); return getThis();
	}
	
	public T password(String password) {
		setPassword(password); return getThis();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void parseUriUserInfo(String userInfo) {
		UsernamePasswordCredentials c = new UsernamePasswordCredentials(userInfo);
		this.userName = c.getUserName();
		this.password = c.getPassword();
	}

}
