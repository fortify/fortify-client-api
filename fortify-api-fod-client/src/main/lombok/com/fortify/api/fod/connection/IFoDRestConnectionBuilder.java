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
package com.fortify.api.fod.connection;

import com.fortify.api.util.rest.connection.IRestConnectionBuilder;

/**
 * This interface is used to implement the {@link FoDAuthenticatingRestConnection#builder()} method, and
 * is not actually implemented by any classes. See {@link IRestConnectionBuilder} for more details.
 * 
 * @author Ruud Senden
 *
 */
public interface IFoDRestConnectionBuilder extends IRestConnectionBuilder<FoDAuthenticatingRestConnection, IFoDRestConnectionBuilder> {
	public IFoDRestConnectionBuilder clientId(String clientId);
	public IFoDRestConnectionBuilder clientSecret(String clientSecret);
	public IFoDRestConnectionBuilder tenant(String tenant);
	public IFoDRestConnectionBuilder userName(String userName);
	public IFoDRestConnectionBuilder password(String password);
}
