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
package com.fortify.api.util.rest.query;

import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.fortify.api.util.rest.connection.RestConnection;
import com.fortify.api.util.rest.json.IJSONMapPreProcessor;
import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapsToJSONListProcessor;

/**
 * <p>This abstract class can be used as a base class for querying data from a REST API.
 * It optionally supports paging and filtering results, depending on concrete implementations
 * of this class. Concrete implementations of this class will need to implement the various
 * abstract methods, for example for defining the base {@link WebTarget} to be invoked, updating
 * the base {@link WebTarget} to generate the actual request, and handling system-specific paging 
 * functionality.</p>
 * 
 * <p>Implementations for the {@link #getConn()}, {@link #getFilters()} and {@link #getMaxResults()} 
 * are usually generated using Lombok annotations as follows:
 * <pre>
 * {@literal @}Getter(AccessLevel.PROTECTED)
 * {@literal @}Builder
 * public final class MyConcreteQuery extends AbstractRestConnectionQuery {
 *		// Fields supported by AbstractRestConnectionWithCacheQuery
 *		private final SSCAuthenticatingRestConnection conn;
 *		private final @Singular List<IJSONMapFilter> filters;
 *		private final Integer maxResults;
 *
 * 		...
 * }
 * </pre>
 * The Getter annotation will override the corresponding methods in this abstract class, and the
 * Builder annotation will generate a builder implementation that allows for configuring these
 * fields. This approach allows us to:
 * <ul>
 *  <li>Easily apply the Builder pattern to fields required by this abstract base class and abstract child classes.</li>
 *  <li>Allows the concrete implementation to only provide Builder methods for functionality that is actually supported by the endpoint,
 *      by simply not defining any fields for unsupported functionality.</li>
 * </ul></p> 
 * 
 * @author Ruud Senden
 */
public abstract class AbstractRestConnectionQuery<ConnType extends RestConnection, ResponseType> {
	protected abstract ConnType conn();
	protected List<IJSONMapPreProcessor> preProcessors() { return null; }
	protected List<IJSONMapPreProcessor> getDefaultPreProcessors() { return null; }
	protected Integer getMaxResults() { return -1; }
	
	/**
	 * Process all results from the REST API call
	 * @param processor
	 */
	public void processAll(IJSONMapProcessor processor) {
		processAll(getWebTarget(), new PagingData().max(getMaxResults()==null?-1:getMaxResults()), processor);
	}

	/**
	 * Get all results from the REST API call
	 * @return
	 */
	public JSONList getAll() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(processor);
		return processor.getJsonList();
	}
	
	/**
	 * Get a unique result from the REST API call. If there are no
	 * results, null will be returned. If there is more than one result,
	 * an exception will be thrown.
	 * @return
	 */
	public JSONMap getUnique() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(getWebTarget(), new PagingData().max(Math.min(2, getMaxResults()==null?-1:getMaxResults())), processor);
		JSONList list = processor.getJsonList();
		if ( list == null || list.size() == 0 ) {
			return null;
		}
		if ( list.size() > 1 ) {
			throw new RuntimeException("More than one object found: "+list); // TODO Use less generic exception type
		}
		return list.asValueType(JSONMap.class).get(0);
	}
	
	public int getCount() {
		return -1; // TODO
	}
	
	protected final WebTarget getWebTarget() {
		return resolveTemplateParams(getUpdatedBaseWebTarget(conn().getBaseResource()));
	}
	
	protected WebTarget resolveTemplateParams(WebTarget webTarget) {
		return webTarget;
	}
	
	/**
	 * Utility method for adding request parameters if the given value is not blank.
	 * @param target
	 * @param name
	 * @param value
	 * @return
	 */
	protected final WebTarget addParameterIfNotBlank(WebTarget target, String name, String value) {
		if ( StringUtils.isNotBlank(value) ) {
			target = target.queryParam(name, value);
		}
		return target;
	}
	
	/**
	 * Subclasses need to implement this method to add the actual request path and
	 * optionally query-specific request parameters.
	 * 
	 * @param connectionBaseTarget Base {@link WebTarget} from the current connection instance
	 * @return
	 */
	protected abstract WebTarget getUpdatedBaseWebTarget(WebTarget connectionBaseTarget);
	
	protected ResponseType executeRequest(WebTarget target) {
		return conn().executeRequest(getHttpMethod(), target, getResponseTypeClass());
	}
	
	protected String getHttpMethod() {
		return HttpMethod.GET;
	}
	
	/**
	 * Subclasses can override this method to do some initialization before calling the target endpoint,
	 * for example to perform some additional REST requests to modify target system settings required for correct
	 * target endpoint invocation.
	 */
	protected void initRequest() {}
	
	/**
	 * Identify whether paging is supported by the resource 
	 * returned by {@link #getBaseWebTarget()}.
	 * @return
	 */
	protected abstract boolean isPagingSupported();

	protected abstract void updatePagingDataFromResponse(PagingData pagingData, ResponseType data);

	protected abstract WebTarget updateWebTargetWithPagingData(WebTarget target, PagingData pagingData);
	
	protected abstract Class<ResponseType> getResponseTypeClass();

	protected abstract JSONList getJSONListFromResponse(ResponseType data);
	
	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 * Depending on the return value of {@link #isPagingSupported()}, this method will either directly invoke
	 * the given web target (paging not supported), or retrieve all data page by page (paging is supported).
	 * 
	 */
	private void processAll(WebTarget target, PagingData pagingData, IJSONMapProcessor processor) {
		initRequest();
		if ( !isPagingSupported() ) {
			processAll(target, processor);
		} else {
			do {
				processor.nextPage(pagingData);
				WebTarget pagingTarget = updateWebTargetWithPagingData(target, pagingData);
				ResponseType response = processAll(pagingTarget, processor);
				updatePagingDataFromResponse(pagingData, response);
			} while ( pagingData.getStart() < pagingData.getTotal() && pagingData.getPageSize()>0 );
		}
	}
	
	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 */
	private ResponseType processAll(WebTarget target, IJSONMapProcessor processor) {
		ResponseType data = executeRequest(target);
		JSONList list = getJSONListFromResponse(data);
		if ( processor != null ) {
			for ( JSONMap obj : list.asValueType(JSONMap.class) ) {
				if ( preProcess(getDefaultPreProcessors(), obj) && preProcess(preProcessors(), obj) ) {
					processor.process(obj);
				}
			}
		}
		return data;
	}

	private boolean preProcess(List<IJSONMapPreProcessor> preProcessors, JSONMap json) {
		boolean result = true;
		if ( CollectionUtils.isNotEmpty(preProcessors) ) {
			for ( IJSONMapPreProcessor preProcessor : preProcessors ) {
				result &= preProcessor.preProcess(json);
				if ( !result ) { break; }
			}
		}
		return result;
	}
}
