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
package com.fortify.client.ssc.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import org.springframework.expression.Expression;

import com.fortify.client.ssc.api.query.builder.SSCBulkQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * This class is used to access the SSC bulk API endpoint.
 * 
 * @author Ruud Senden
 *
 */
public class SSCBulkAPI extends AbstractSSCAPI {
	public SSCBulkAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCBulkQueryBuilder queryBulk() {
		return new SSCBulkQueryBuilder(conn());
	}
	
	public SSCAddBulkData addBulkData() {
		return new SSCAddBulkData(conn());
	}
	
	// TODO Clean up this class
	@Setter @Accessors(fluent=true)
	public static final class SSCAddBulkData {
		private String targetProperty;
		private Expression pathExpression;
		private Map<String,String> queryParamExpressions;
		private Map<String,JSONMap> uriToObjectMap = new HashMap<>();
		private final SSCAuthenticatingRestConnection conn;
		
		public SSCAddBulkData(SSCAuthenticatingRestConnection conn) {
			this.conn = conn;
		}
		
		public Consumer<JSONList> consumer() {
			return jsonList->addBulkData(jsonList);
		}
		
		public SSCAddBulkData pathExpression(Expression pathExpression) {
			this.pathExpression = pathExpression;
			return this;
		}
		
		public SSCAddBulkData pathExpression(String pathExpression) {
			return pathExpression(SpringExpressionUtil.parseTemplateExpression(pathExpression));
		}
		
		public void addBulkData(JSONList jsonList) {
			SSCBulkQueryBuilder builder = conn.api(SSCBulkAPI.class).queryBulk();
			addBulkRequests(builder, jsonList);
			JSONList bulkResults = builder.execute();
			bulkResults.asValueType(JSONMap.class).forEach(this::addResultToInputList);
		}
		
		private void addResultToInputList(JSONMap singleBulkResult) {
			String uri = singleBulkResult.getPath("request.uri", String.class);
			JSONMap orgInput = uriToObjectMap.get(uri);
			if ( orgInput==null ) {
				throw new RuntimeException("Unable to find original input object for bulk URI "+uri);
			}
			JSONList responses = singleBulkResult.getPath("responses", JSONList.class);
			if ( responses.size()>1 ) {
				throw new RuntimeException("Looping not supported");
			}
			if ( responses.size()>0 ) {
				orgInput.put(targetProperty, responses.get(0, JSONMap.class).getPath("body.data"));
			}
		}
		
		private void addBulkRequests(SSCBulkQueryBuilder builder, JSONList jsonList) {
			jsonList.asValueType(JSONMap.class).forEach(json->addBulkRequest(builder, json));
		}
		
		private void addBulkRequest(SSCBulkQueryBuilder builder, JSONMap input) {
			String path = SpringExpressionUtil.evaluateExpression(input, pathExpression, String.class);
			final WebTarget target = conn.getBaseResource().path(path);
			addQueryParams(target, input);
			builder.addBulkRequest(HttpMethod.GET, target);
			uriToObjectMap.put(target.getUri().toString(), input);
		}

		private void addQueryParams(final WebTarget webTarget, JSONMap input) {
			if ( queryParamExpressions!=null ) {
				queryParamExpressions.entrySet().forEach(addQueryParam(webTarget, input));
			}
		}

		private Consumer<? super Entry<String, String>> addQueryParam(final WebTarget webTarget, JSONMap input) {
			return e->webTarget.queryParam(e.getKey(), 
					SpringExpressionUtil.evaluateTemplateExpression(input, e.getValue(), String.class));
		}
	}
}
