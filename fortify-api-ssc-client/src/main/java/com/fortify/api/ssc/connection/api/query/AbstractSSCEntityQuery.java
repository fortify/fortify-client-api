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
package com.fortify.api.ssc.connection.api.query;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapsToJSONListProcessor;

/**
 * This abstract class is used to query entity data from SSC. It supports the following
 * standard SSC query parameters:
 * <ul>
 *  <li>q: {@link #queryAnd(String, String)} and {@link #queryReplace(String, String)}</li>
 *  <li>fields: {@link #fields(String...)}</li>
 *  <li>orderby: {@link #orderBy(String)}</li>
 *  <li>groupby: {@link #groupBy(String)}</li>
 *  <li>embed: {@link #embed(String)}</li>
 * </ul>
 * All of these methods are defined as 'protected' in this class. Depending on the query
 * parameters that are supported for a specific SSC entity, the corresponding subclass
 * can override these methods to make them public.
 * 
 * TODO: Describe Paging
 * TODO: Describe methods for retrieving/processing results
 * 
 * @author Ruud Senden
 *
 * @param <Q> Subclass type
 */
public abstract class AbstractSSCEntityQuery<Q extends AbstractSSCEntityQuery<Q>> {
	private final SSCAuthenticatingRestConnection conn;
	private int maxResults = -1;
	private String query;
	private String[] fields;
	private String orderBy;
	private String groupBy;
	private String embed;

	protected AbstractSSCEntityQuery(SSCAuthenticatingRestConnection conn) {
		this.conn = conn;
	}
	
	protected final SSCAuthenticatingRestConnection conn() {
		return conn;
	}

	protected Q maxResults(int maxResults) {
		this.maxResults = maxResults;
		return getThis();
	}

	protected Q queryReplace(String queryReplace) {
		this.query = queryReplace;
		return getThis();
	}
	
	protected Q queryAnd(String field, String value) {
		String queryAppend = field+":\""+value+"\"";
		if ( this.query == null || StringUtils.isBlank(this.query) ) {
			this.query = queryAppend;
		} else {
			this.query += "+and+"+queryAppend;
		}
		return getThis();
	}

	protected Q fields(String... fields) {
		this.fields = fields;
		return getThis();
	}
	
	protected Q orderBy(String orderByField) {
		this.orderBy = orderByField;
		return getThis();
	}
	
	protected Q groupBy(String groupByField) {
		this.groupBy = groupByField;
		return getThis();
	}
	
	protected Q embed(String embed) {
		this.embed = embed;
		return getThis();
	}
	
	protected WebTarget getWebTargetWithFieldsAndQueryParam() {
		return addParameters(getBaseWebTarget());
	}
	
	protected WebTarget addParameters(WebTarget webTarget) {
		webTarget = addParameterFields(webTarget);
		webTarget = addParameterQuery(webTarget);
		webTarget = addParameterOrderBy(webTarget);
		webTarget = addParameterGroupBy(webTarget);
		webTarget = addParameterEmbed(webTarget);
		webTarget = addExtraParameters(webTarget);
		return webTarget;
	}

	protected WebTarget addParameterQuery(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(this.query) ) {
			webTarget = webTarget.queryParam("q", this.query);
		}
		return webTarget;
	}

	protected WebTarget addParameterFields(WebTarget webTarget) {
		if ( this.fields != null && this.fields.length > 0 ) {
			webTarget = webTarget.queryParam("fields", String.join(",", this.fields));
		}
		return webTarget;
	}
	
	protected WebTarget addParameterOrderBy(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(this.orderBy) ) {
			webTarget = webTarget.queryParam("orderby", this.orderBy);
		}
		return webTarget;
	}
	
	protected WebTarget addParameterGroupBy(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(this.groupBy) ) {
			webTarget = webTarget.queryParam("groupby", this.groupBy);
		}
		return webTarget;
	}
	
	protected WebTarget addParameterEmbed(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(this.embed) ) {
			webTarget = webTarget.queryParam("embed", this.embed);
		}
		return webTarget;
	}
	
	/**
	 * Subclasses can override this method to add any additional
	 * request parameters that are not supported by this base class.
	 * 
	 * @param webTarget
	 * @return
	 */
	protected WebTarget addExtraParameters(WebTarget webTarget) {
		return webTarget;
	}
	
	/**
	 * Get the base web target identifying the SSC resource to use.
	 * Usually this should return something like
	 * conn().getBaseResource().path("api/v1/entity") for top-level
	 * entities, or 
	 * conn().getBaseResource().path("api/v1/parentEntity").path(parentId).path("childEntity")
	 * @return
	 */
	protected abstract WebTarget getBaseWebTarget();
	
	/**
	 * Identify whether paging is supported by the current SSC resource.
	 * @return
	 */
	protected abstract boolean isPagingSupported();

	public void processAll(IJSONMapProcessor processor) {
		processAll(getWebTargetWithFieldsAndQueryParam(), new PagingData().max(this.maxResults), processor);
	}

	public JSONList getAll() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(processor);
		return processor.getJsonList();
	}
	
	public JSONMap getUnique() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(getWebTargetWithFieldsAndQueryParam(), new PagingData().max(1), processor);
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
	
	@SuppressWarnings("unchecked")
	protected Q getThis() {
		return (Q)this;
	}

	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 * Depending on the return value of {@link #isPagingSupported()}, this method will either directly invoke
	 * the given web target (paging not supported), or retrieve all data page by page (paging is supported).
	 * 
	 */
	protected void processAll(WebTarget target, PagingData pagingData, IJSONMapProcessor processor) {
		if ( !isPagingSupported() ) {
			processAll(target, processor);
		} else {
			do {
				processor.nextPage();
				target = target.queryParam("start", ""+pagingData.getStart()).queryParam("limit", ""+pagingData.getPageSize());
				JSONMap data = processAll(target, processor);
				pagingData.setTotal( data.get("count", Integer.class) );
				pagingData.setLastPageSize( data.get("data", JSONList.class).size() );
				pagingData.start(pagingData.getStart() + pagingData.getLastPageSize());
				
			} while ( pagingData.getStart() < pagingData.getTotal() && pagingData.getLastPageSize() >= pagingData.getPageSize() );
		}
	}
	
	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 */
	protected JSONMap processAll(WebTarget target, IJSONMapProcessor processor) {
		JSONMap data = conn().executeRequest(HttpMethod.GET, target, JSONMap.class);
		JSONList list = data.get("data", JSONList.class);
		if ( processor != null ) {
			for ( JSONMap obj : list.asValueType(JSONMap.class) ) {
				processor.process(obj);
			}
		}
		return data;
	}

	protected class PagingData {
		private int start = 0;
		private int pageSize = 50;
		private int max = -1;
		private int total = -1;
		private int lastPageSize = -1;
		
		public int getStart() {
			return start;
		}
		public PagingData start(int start) {
			this.start = start;
			return this;
		}
		public int getPageSize() {
			if ( this.max==-1 ) {
				return pageSize; 
			} else {
				return Math.min(pageSize, max-start);
			}
		}
		public PagingData pageSize(int pageSize) {
			this.pageSize = pageSize;
			return this;
		}
		public int getMax() {
			return max;
		}
		public PagingData max(int max) {
			this.max = max;
			return this;
		}
		public int getTotal() {
			return total;
		}
		protected void setTotal(int total) {
			this.total = total;
		}
		public int getLastPageSize() {
			return lastPageSize;
		}
		public void setLastPageSize(int lastPageSize) {
			this.lastPageSize = lastPageSize;
		}
		
	}
}
