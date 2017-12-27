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

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import com.fortify.util.rest.connection.IRestConnection;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;

/**
 * This {@link AbstractJSONMapOnDemandLoaderWithConnection} implementation allows for
 * dynamically loading on-demand data from the configured REST endpoint when a given 
 * JSON property is being accessed.
 *  
 * @author Ruud Senden
 *
 */
public class JSONMapOnDemandLoaderRest extends AbstractJSONMapOnDemandLoaderWithConnection<IRestConnection> {
	private static final long serialVersionUID = 1L;
	private final String pathTemplateExpression;
	private final String resultExpression;
	
	public JSONMapOnDemandLoaderRest(IRestConnection conn, boolean storeValue, String pathTemplateExpression, String resultExpression) {
		super(conn, storeValue);
		this.pathTemplateExpression = pathTemplateExpression;
		this.resultExpression = resultExpression;
	}

	@Override
	public Object getOnDemand(String propertyName, JSONMap parent) {
		return getResult(conn().executeRequest(HttpMethod.GET, getWebTarget(parent), JSONMap.class));
	}

	protected Object getResult(JSONMap restResult) {
		if ( resultExpression == null ) {
			return restResult;
		} else {
			return SpringExpressionUtil.evaluateExpression(restResult, resultExpression, Object.class);
		}
	}

	protected WebTarget getWebTarget(JSONMap parent) {
		String path = SpringExpressionUtil.evaluateTemplateExpression(parent, pathTemplateExpression, String.class);
		return conn().getBaseResource().path(path);
	}

}
