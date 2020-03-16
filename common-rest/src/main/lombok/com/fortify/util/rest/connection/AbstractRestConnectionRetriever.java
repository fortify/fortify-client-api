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

import lombok.Getter;

/**
 * This abstract {@link IRestConnectionRetriever} implementation can be used as a base
 * class for {@link IRestConnectionRetriever} implementations. Based on the configured
 * {@link AbstractRestConnectionConfig} instance that also implements the 
 * {@link IRestConnectionBuilder} interface, it allows for configuring REST connection
 * properties, building the connection, and caching the configured connection.
 * 
 * @author Ruud Senden
 *
 * @param <ConnType> Concrete connection type, implementing {@link IRestConnection}
 * @param <BuilderType> Concrete connection builder type, extending from {@link AbstractRestConnectionConfig} and implementing {@link IRestConnectionBuilder}
 */
public abstract class AbstractRestConnectionRetriever<ConnType extends IRestConnection, BuilderType extends AbstractRestConnectionConfig<?> & IRestConnectionBuilder<ConnType>> implements IRestConnectionRetriever<ConnType> {
	private ConnType connection;
	@Getter private final BuilderType config = createConfig();
	
	public final ConnType getConnection() {
		if ( connection == null ) {
			connection = config.build();
		}
		return connection;
	}
	
	protected abstract BuilderType createConfig();
	
}
