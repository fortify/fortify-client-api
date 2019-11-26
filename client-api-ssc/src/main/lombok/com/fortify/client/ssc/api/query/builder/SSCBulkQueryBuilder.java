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
package com.fortify.client.ssc.api.query.builder;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * bulk querying.
 * 
 * @author Ruud Senden
 * 
 */
public final class SSCBulkQueryBuilder {
	private final SSCAuthenticatingRestConnection conn;
	private final JSONList requests = new JSONList();
	
	public SSCBulkQueryBuilder(SSCAuthenticatingRestConnection conn) {
		this.conn = conn;
	}
	
	public SSCBulkQueryBuilder addBulkRequest(String httpMethod, WebTarget webTarget, Object postData) {
		JSONMap request = requests.addNewJSONMap();
		request.put("uri", webTarget.getUri().toString());
		request.put("httpVerb", httpMethod);
		if ( postData!=null ) {
			request.put("postData", postData);
		}
		return this;
	}
	
	public SSCBulkQueryBuilder addBulkRequest(String httpMethod, WebTarget webTarget) {
		return addBulkRequest(httpMethod, webTarget, null);
	}
	
	public JSONList execute() {
		WebTarget bulkTarget = conn.getBaseResource().path("/api/v1/bulk");
		JSONMap bulkRequest = new JSONMap();
		bulkRequest.put("requests", this.requests);
		return conn
				.executeRequest(HttpMethod.POST, bulkTarget, Entity.entity(bulkRequest, MediaType.APPLICATION_JSON), JSONMap.class)
				.getOrCreateJSONList("data");
	}
}