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

import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.IJSONMapFilter;
import com.fortify.api.util.rest.json.IJSONMapProcessor;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapsToJSONListProcessor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * <p>This abstract class can be used as a base class for querying entity data from SSC. It supports the following
 * standard SSC query parameters:
 * <ul>
 *  <li>q: {@link #setParamQAnds(Map)} sets a map of field names and corresponding values to filter the results by</li>
 *  <li>fields: {@link #setParamFields(List)} sets the list of fields to be returned by SSC</li>
 *  <li>orderby: {@link #setParamOrderBy(String)} sets the field to order the results by</li>
 *  <li>groupby: {@link #setParamGroupBy(String)} sets the field used to group the results</li>
 *  <li>embed: {@link #setParamEmbed(String)} allows for embedding additional entities in the results</li>
 * </ul></p>
 * 
 * <p>In addition, the following methods are available for client-side filtering:
 * <ul>
 *  <li>TODO Implement regex filtering, SpEL, before/after, ...</li>
 * </ul></p>
 * 
 * <p>All of the methods mentioned above are defined as 'protected' in this class, and should only be called when initializing
 * a new instance of this class, before calling any of the methods that actually execute the requests. Subclasses would 
 * usually define a constructor that calls the various setters based on constructor arguments. This constructor would then
 * be annotated with the {@link Builder} annotation to generate a corresponding Builder class that can be used by API clients
 * to build queries. Please see the various example implementations in this package.</p>
 * 
 * <pre>
 * TODO Describe Paging
 * TODO Describe methods for retrieving/processing results
 * TODO Add support for caching?
 * </pre>
 * 
 * @author Ruud Senden
 */
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractSSCEntityQuery {
	private final SSCAuthenticatingRestConnection conn;
	private Map<String, String> paramQAnds;
	private List<String> paramFields;
	private String paramOrderBy;
	private String paramGroupBy;
	private String paramEmbed;
	private List<IJSONMapFilter> filters;
	private Integer maxResults;
	
	protected AbstractSSCEntityQuery(SSCAuthenticatingRestConnection conn) {
		this.conn = conn;
	}
	
	protected final SSCAuthenticatingRestConnection conn() {
		return conn;
	}
	
	protected WebTarget getWebTarget() {
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
		if ( MapUtils.isNotEmpty(paramQAnds) ) {
			StringBuffer q = new StringBuffer();
			for ( Map.Entry<String, String> entry : paramQAnds.entrySet() ) {
				String qAppend = entry.getKey()+":\""+entry.getValue()+"\"";
				if ( q.length() == 0 ) {
					q.append(qAppend);
				} else {
					q.append("+and+"+qAppend);
				}
			}
			webTarget = webTarget.queryParam("q", q.toString());
		}
		return webTarget;
	}

	protected WebTarget addParameterFields(WebTarget webTarget) {
		if ( CollectionUtils.isNotEmpty(paramFields) ) {
			webTarget = webTarget.queryParam("fields", StringUtils.join(paramFields, ","));
		}
		return webTarget;
	}
	
	protected WebTarget addParameterOrderBy(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(paramOrderBy) ) {
			webTarget = webTarget.queryParam("orderby", paramOrderBy);
		}
		return webTarget;
	}
	
	protected WebTarget addParameterGroupBy(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(paramGroupBy) ) {
			webTarget = webTarget.queryParam("groupby", paramGroupBy);
		}
		return webTarget;
	}
	
	protected WebTarget addParameterEmbed(WebTarget webTarget) {
		if ( StringUtils.isNotBlank(paramEmbed) ) {
			webTarget = webTarget.queryParam("embed", paramEmbed);
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
		processAll(getWebTarget(), new PagingData().max(this.maxResults==null?-1:this.maxResults), processor);
	}

	public JSONList getAll() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(processor);
		return processor.getJsonList();
	}
	
	public JSONMap getUnique() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(getWebTarget(), new PagingData().max(1), processor);
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
			} while ( pagingData.getStart() < pagingData.getTotal() && pagingData.getPageSize()>0 );
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

	@Data
	protected class PagingData {
		private int start = 0;
		private int pageSize = 50;
		private int max = -1;
		private int total = -1;
		private int lastPageSize = -1;
		
		public int getPageSize() {
			if ( this.max==-1 ) {
				return pageSize; 
			} else {
				return Math.min(pageSize, max-start);
			}
		}
		
		public void setLastPageSize(int lastPageSize) {
			this.lastPageSize = lastPageSize;
			this.start = this.start + lastPageSize;
		}
		
		public PagingData max(int max) {
			setMax(max);
			return this;
		}
	}
}
