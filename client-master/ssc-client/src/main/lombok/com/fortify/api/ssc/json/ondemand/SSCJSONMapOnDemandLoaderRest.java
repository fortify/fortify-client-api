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
package com.fortify.api.ssc.json.ondemand;

import javax.ws.rs.client.WebTarget;

import com.fortify.api.util.rest.connection.IRestConnection;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.ondemand.JSONMapOnDemandLoaderRest;

public class SSCJSONMapOnDemandLoaderRest extends JSONMapOnDemandLoaderRest {
	private static final long serialVersionUID = 1L;
	private final String[] fields;

	public SSCJSONMapOnDemandLoaderRest(IRestConnection conn, String pathTemplateExpression, String... fields) {
		super(conn, true, pathTemplateExpression, "data");
		this.fields = fields;
	}
	
	@Override
	protected WebTarget getWebTarget(JSONMap parent) {
		WebTarget result = super.getWebTarget(parent);
		if ( fields!=null && fields.length > 0 ) {
			result = result.queryParam("fields", String.join(",", fields));
		}
		return result;
	}
}
