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
package com.fortify.client.ssc.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.Expression;

import com.fortify.client.ssc.annotation.SSCCopyToConstructors;
import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.json.embed.SSCEmbedConfig;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.embed.StandardEmbedDefinition;
import com.fortify.util.rest.query.AbstractRestConnectionQueryBuilder;

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
	
	public SSCBulkRequestBuilder bulkRequestBuilder() {
		return new SSCBulkRequestBuilder(conn());
	}
	
	public SSCBulkEmbedder bulkEmbedder(SSCEmbedConfig embedConfig) {
		return new SSCBulkEmbedder(conn(), embedConfig);
	}
	
	/**
	 * This class allows for building and executing SSC bulk requests
	 * 
	 * @author Ruud Senden
	 * 
	 */
	public final class SSCBulkRequestBuilder {
		private final SSCAuthenticatingRestConnection conn;
		private final JSONList requests = new JSONList();
		
		/**
		 * Create instance using the given {@link SSCAuthenticatingRestConnection}
		 * @param conn
		 */
		public SSCBulkRequestBuilder(SSCAuthenticatingRestConnection conn) {
			this.conn = conn;
		}
		
		/**
		 * Add a request to the list of bulk requests to be executed.
		 * Similar to {@link #addBulkRequest(String, WebTarget)}, but this method 
		 * allows for adding data to be posted with the request.
		 * 
		 * @param httpMethod {@link HttpMethod} for the request
		 * @param webTarget {@link WebTarget} for the request
		 * @param postData Data to be posted with the request
		 * @return
		 */
		public SSCBulkRequestBuilder addBulkRequest(String httpMethod, WebTarget webTarget, Object postData) {
			JSONMap request = requests.addNewJSONMap();
			request.put("uri", webTarget.getUri().toString());
			request.put("httpVerb", httpMethod);
			if ( postData!=null ) {
				request.put("postData", postData);
			}
			return this;
		}
		
		/**
		 * Add a request to the list of bulk requests to be executed.
		 * Similar to {@link #addBulkRequest(String, WebTarget, Object)}, but this method
		 * doesn't post any data with the request.
		 * 
		 * @param httpMethod
		 * @param webTarget
		 * @return
		 */
		public SSCBulkRequestBuilder addBulkRequest(String httpMethod, WebTarget webTarget) {
			return addBulkRequest(httpMethod, webTarget, null);
		}
		
		/**
		 * Execute the bulk requests that were previously added using the 
		 * {@link #addBulkRequest(String, WebTarget)} or {@link #addBulkRequest(String, WebTarget, Object)}
		 * methods. In the order that requests were added, each entry in the 
		 * returned {@link JSONList} contains the result of that request.
		 * 
		 * @return
		 */
		@SSCRequiredActionsPermitted("POST=/api/v\\d+/bulk")
		public JSONList execute() {
			WebTarget bulkTarget = conn.getBaseResource().path("/api/v1/bulk");
			JSONMap bulkRequest = new JSONMap();
			bulkRequest.put("requests", this.requests);
			return conn
					.executeRequest(HttpMethod.POST, bulkTarget, Entity.entity(bulkRequest, MediaType.APPLICATION_JSON), JSONMap.class)
					.getOrCreateJSONList("data");
		}
	}
	
	/**
	 * This class allows for adding extra data to a given {@link JSONList}, using
	 * a single SSC bulk request to load the extra data for all {@link JSONList} 
	 * entries passed to the {@link SSCBulkEmbedder#addBulkData(JSONList)} method.
	 *  
	 * @author Ruud Senden
	 *
	 */
	@Setter @Accessors(fluent=true)
	public static final class SSCBulkEmbedder {
		private Map<String,JSONMap> uriToObjectMap = new HashMap<>();
		private final SSCAuthenticatingRestConnection conn;
		private final StandardEmbedDefinition embedDefinition;
		
		/**
		 * Create instance using the given {@link SSCAuthenticatingRestConnection}
		 * @param conn
		 */
		public SSCBulkEmbedder(SSCAuthenticatingRestConnection conn, SSCEmbedConfig embedConfig) {
			this.conn = conn;
			this.embedDefinition = new StandardEmbedDefinition(embedConfig);
		}
		
		/**
		 * Return the configured instance of this class as a
		 * page pre-processor to be used with the {@link AbstractRestConnectionQueryBuilder#pagePreProcessor(Consumer)}
		 * method.
		 * @return
		 */
		public Consumer<JSONList> asPagePreProcessor() {
			return jsonList->addBulkData(jsonList);
		}
		
		/**
		 * For each entry in the given {@link JSONList}, this method will build and execute an SSC bulk request 
		 * for retrieving embedded data according to the configured {@link SSCEmbedConfig} instance. The retrieved
		 * data is then added to each of the entries in the given {@link JSONList}, using the property name provided
		 * in the configured {@link SSCEmbedConfig} instance.
		 */
		@SSCCopyToConstructors
		public void addBulkData(JSONList jsonList) {
			SSCBulkRequestBuilder builder = conn.api(SSCBulkAPI.class).bulkRequestBuilder();
			addBulkRequests(builder, jsonList);
			try {
				JSONList bulkResults = builder.execute();
				bulkResults.asValueType(JSONMap.class).forEach(this::addResultToInputList);
			} catch (RuntimeException e) {
				embedDefinition.handleError(e);
			}
		}
		
		/**
		 * Given the request URI for the single bulk result that we are currently processing,
		 * this method looks up the corresponding {@link JSONMap} from {@link #uriToObjectMap}
		 * (which is the {@link JSONMap} entry that was used to generate the bulk request).
		 * The bulk result contents are then added to this {@link JSONMap} under the property
		 * that was configured using the {@link #targetProperty(String)} method.
		 *   
		 * @param singleBulkResult
		 */
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
				orgInput.put(embedDefinition.getPropertyName(), embedDefinition.getResult(responses.get(0, JSONMap.class).getPath("body", JSONMap.class)));
			}
		}
		
		/**
		 * For each entry in the given {@link JSONList}, this method calls 
		 * {@link #addBulkRequest(SSCBulkRequestBuilder, JSONMap)} to add 
		 * a corresponding request to the given {@link SSCBulkRequestBuilder}.
		 * 
		 * @param builder
		 * @param jsonList
		 */
		private void addBulkRequests(SSCBulkRequestBuilder builder, JSONList jsonList) {
			jsonList.asValueType(JSONMap.class).forEach(json->addBulkRequest(builder, json));
		}
		
		/**
		 * This method performs the following:
		 * <ul>
		 *  <li>Evaluate the configured URI {@link Expression} using the given {@link JSONMap} as input</li>
		 *  <li>Add a request for the evaluated URI to the given {@link SSCBulkRequestBuilder}</li>
		 *  <li>Store a mapping from the evaluated URI to the corresponding {@link JSONMap} in {@link #uriToObjectMap}</li>
		 * </ul>
		 * @param builder
		 * @param input
		 */
		private void addBulkRequest(SSCBulkRequestBuilder builder, JSONMap input) {
			if ( embedDefinition.isEnabled(input) ) {
				String uri = embedDefinition.buildUri(input);
				if ( StringUtils.isNotBlank(uri) ) {
					final WebTarget target = conn.getResource(uri);
					builder.addBulkRequest(HttpMethod.GET, target);
					uriToObjectMap.put(target.getUri().toString(), input);
				}
			}
		}
	}
}
