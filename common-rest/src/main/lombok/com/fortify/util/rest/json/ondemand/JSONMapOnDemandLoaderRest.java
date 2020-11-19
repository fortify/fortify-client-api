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
package com.fortify.util.rest.json.ondemand;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import com.fortify.util.rest.connection.IRestConnection;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.expression.helper.DefaultExpressionHelperProvider;
import com.fortify.util.spring.expression.helper.IExpressionHelper;

/**
 * This {@link AbstractJSONMapOnDemandLoaderWithConnection} implementation allows for
 * dynamically loading on-demand data from the configured REST endpoint when a given 
 * JSON property is being accessed.
 *  
 * @author Ruud Senden
 *
 */
public class JSONMapOnDemandLoaderRest extends AbstractJSONMapOnDemandLoaderWithIRestConnection {
	private static final long serialVersionUID = 1L;
	private final IExpressionHelper expressionHelper = DefaultExpressionHelperProvider.get();
	private final String uriTemplateExpression;
	private final String resultExpression;
	
	public JSONMapOnDemandLoaderRest(IRestConnection conn, boolean storeValue, String uriTemplateExpression, String resultExpression) {
		super(conn, storeValue);
		this.uriTemplateExpression = uriTemplateExpression;
		this.resultExpression = resultExpression;
	}

	@Override
	public Object getOnDemand(IRestConnection conn, String propertyName, JSONMap parent) {
		return getResult(conn.executeRequest(HttpMethod.GET, getWebTarget(conn, parent), JSONMap.class));
	}

	protected Object getResult(JSONMap restResult) {
		if ( resultExpression == null ) {
			return restResult;
		} else {
			return expressionHelper.evaluateSimpleExpression(restResult, resultExpression, Object.class);
		}
	}

	protected WebTarget getWebTarget(IRestConnection conn, JSONMap parent) {
		String uri = expressionHelper.evaluateTemplateExpression(parent, uriTemplateExpression, String.class);
		return conn.getResource(uri);
	}

}
