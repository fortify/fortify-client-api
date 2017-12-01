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
import com.fortify.api.util.rest.json.IJSONMapFilter;
import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapsToJSONListProcessor;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * <p>This abstract class can be used as a base class for querying data from a REST API. 
 * 
 * TODO Update JavaDoc
 * 
 * @author Ruud Senden
 */
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractRestConnectionQuery<ConnType extends RestConnection, ResponseType> {
	private final ConnType conn;
	private List<IJSONMapFilter> filters;
	private Integer maxResults;
	
	protected AbstractRestConnectionQuery(ConnType conn) {
		this.conn = conn;
	}
	
	/**
	 * Process all results from the REST API call
	 * @param processor
	 */
	public void processAll(IJSONMapProcessor processor) {
		processAll(getWebTarget(), new PagingData().max(this.maxResults==null?-1:this.maxResults), processor);
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
		processAll(getWebTarget(), new PagingData().max(Math.min(2, maxResults)), processor);
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
	
	protected final ConnType conn() {
		return conn;
	}
	
	protected final WebTarget getWebTarget() {
		return updateBaseWebTarget(getBaseWebTarget());
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
	 * Subclasses can override this method to update the base target, for
	 * example by adding query-specific request parameters.
	 * 
	 * @param webTarget
	 * @return
	 */
	protected WebTarget updateBaseWebTarget(WebTarget webTarget) {
		return webTarget;
	}
	
	protected ResponseType executeRequest(WebTarget target) {
		return conn().executeRequest(HttpMethod.GET, target, getResponseTypeClass());
	}
	
	/**
	 * Subclasses can override this method to do some initialization before calling the target endpoint,
	 * for example to perform some additional REST requests to modify target system settings required for correct
	 * target endpoint invocation.
	 */
	protected void initRequest() {}
	
	/**
	 * Get the base web target identifying the resource to use.
	 * Usually this should return something like
	 * conn().getBaseResource().path("/api/entity") for top-level
	 * entities, or 
	 * conn().getBaseResource().path("api/parentEntity").path(parentId).path("childEntity")
	 * @return
	 */
	protected abstract WebTarget getBaseWebTarget();
	
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
				if ( isIncluded(obj) ) {
					processor.process(obj);
				}
			}
		}
		return data;
	}

	private boolean isIncluded(JSONMap json) {
		boolean result = true;
		if ( CollectionUtils.isNotEmpty(filters) ) {
			for ( IJSONMapFilter filter : filters ) {
				result &= filter.include(json);
				if ( !result ) { break; }
			}
		}
		return result;
	}
}
