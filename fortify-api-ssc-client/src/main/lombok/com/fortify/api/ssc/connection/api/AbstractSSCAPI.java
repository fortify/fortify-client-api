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
package com.fortify.api.ssc.connection.api;

import javax.ws.rs.HttpMethod;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.spring.SpringExpressionUtil;

public class AbstractSSCAPI {
	private final SSCAuthenticatingRestConnection conn;

	protected AbstractSSCAPI(SSCAuthenticatingRestConnection conn) {
		this.conn = conn;
	}
	
	protected final SSCAuthenticatingRestConnection conn() {
		return conn;
	}

	/**
	 * Get a List<JSONMap> containing all entities for the given entityName
	 * @param entityName
	 * @return
	 */
	protected final JSONList getEntities(String entityName) {
		JSONMap data = conn().executeRequest(HttpMethod.GET, conn().getBaseResource().path("/api/v1").path(entityName), JSONMap.class);
		return SpringExpressionUtil.evaluateExpression(data, "data", JSONList.class);
	}

	/**
	 * Get a List<JSONMap> containing all entities for the given entityName for the given application version
	 * @param entityName
	 * @return
	 */
	protected final JSONList getApplicationVersionEntities(String applicationVersionId, String entityName) {
		return conn().executeRequest(HttpMethod.GET, conn().getBaseResource()
				.path("/api/v1/projectVersions/")
				.path(""+applicationVersionId)
				.path(entityName), JSONMap.class).get("data", JSONList.class);
	}

}