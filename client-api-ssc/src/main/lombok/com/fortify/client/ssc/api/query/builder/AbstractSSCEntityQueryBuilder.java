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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.fortify.client.ssc.api.SSCBulkAPI;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.client.ssc.json.ondemand.SSCJSONMapOnDemandLoaderRest;
import com.fortify.util.rest.json.ondemand.IJSONMapOnDemandLoader;
import com.fortify.util.rest.query.AbstractRestConnectionQueryBuilder;
import com.fortify.util.rest.query.IRestConnectionQuery;
import com.fortify.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.util.rest.webtarget.WebTargetQueryParamUpdater;

/**
 * <p>This abstract base class is used to build {@link SSCEntityQuery} instances. Concrete implementations
 * will need to provide the actual SSC REST API endpoint by calling the {@link #appendPath(String)} method
 * (usually in their constructor), and indicate whether this endpoint supports paging (by providing the
 * pagingSupported parameter to the constructor of this superclass).</p>
 * 
 * <p>This class provides various protected methods for configuring common SSC request parameters,
 * like 'fields' and 'orderBy'. Depending on whether the target SSC endpoint supports these parameters,
 * concrete implementations can override these methods as 'public' to make the generic method available, 
 * and/or provide more specialized builder methods that call these generic methods, for example to support
 * specific fields to be added to the 'q' parameter.</p>  
 *  
 * @author Ruud Senden
 *
 * @param <T> Concrete builder type
 */
public abstract class AbstractSSCEntityQueryBuilder<T extends AbstractSSCEntityQueryBuilder<T>> extends AbstractRestConnectionQueryBuilder<SSCAuthenticatingRestConnection, T> {
	private SSCParamQ paramQ = add(new SSCParamQ());
	
	/**
	 * Create new instance for given {@link SSCAuthenticatingRestConnection} and indicator whether paging is supported.
	 * @param conn
	 * @param pagingSupported
	 */
	protected AbstractSSCEntityQueryBuilder(SSCAuthenticatingRestConnection conn, boolean pagingSupported) {
		super(conn, pagingSupported);
	}
	
	/**
	 * Build an {@link IRestConnectionQuery} instance from the current
	 * configuration.
	 * 
	 * @return
	 */
	@Override
	public IRestConnectionQuery build() {
		return new SSCEntityQuery(this);
	}
	
	/**
	 * Add the 'embed' query parameter to the request configuration
	 * 
	 * @param entity
	 * @return
	 */
	protected T paramEmbed(String entity) {
		return queryParam("embed", entity);
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramEmbed(String)} method.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface ISSCEntityQueryBuilderParamEmbed<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramEmbed(String entity);
	}
	
	/**
	 * Add the 'fields' query parameter to the request configuration
	 * 
	 * @param fields
	 * @return
	 */
	protected T paramFields(String... fields) {
		return queryParam("fields", StringUtils.join(fields, ","));
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramFields(String...)} method.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface ISSCEntityQueryBuilderParamFields<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramFields(String... fields);
	}
	
	/**
	 * Add the 'orderby' query parameter to the request configuration
	 * 
	 * @param orderBy
	 * @return
	 */
	protected T paramOrderBy(String orderBy, SSCOrderByDirection direction) {
		if ( SSCOrderByDirection.DESC.equals(direction) ) {
			orderBy = "-"+orderBy;
		}
		return queryParam("orderby", orderBy);
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramOrderBy(String, SSCOrderByDirection)} method.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface ISSCEntityQueryBuilderParamOrderBy<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramOrderBy(String orderBy, SSCOrderByDirection orderByDirection);
	}
	
	/**
	 * Add the 'groupby' query parameter to the request configuration
	 * 
	 * @param groupBy
	 * @return
	 */
	protected T paramGroupBy(String groupBy) {
		return queryParam("groupby", groupBy);
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramGroupBy(String)} method.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface ISSCEntityQueryBuilderParamGroupBy<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramGroupBy(String groupBy);
	}
	
	/**
	 * Add the 'q' query parameter to the request configuration.
	 * This will take the given value literally, as opposed to
	 * {@link #paramQAnd(String, Object)} that allows for building
	 * a multi-part 'q' parameter.
	 * 
	 * @param q
	 * @return
	 */
	protected T paramQ(String q) {
		return queryParam("q", q);
	}
	
	/**
	 * Add the 'q' query parameter to the request configuration. If
	 * already set, the new field and value will be 'and-ed' to the
	 * current query parameter value.
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	protected T paramQAnd(String field, Object value) {
		paramQ.paramQAnd(field, value); return _this();
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the various paramQ*() methods method.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface ISSCEntityQueryBuilderParamQ<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramQ(String q);
		public T paramQAnd(String field, Object value);
	}
	
	/**
	 * Allows for embedding additional SSC entities into the
	 * resulting JSON objects. Depending on the given embedType,
	 * the additional entities are either loaded on demand
	 * whenever they are accessed, or pre-loaded using SSC bulk 
	 * requests. 
	 * 
	 * @param propertyName
	 * @param uri
	 * @return
	 */
	public T embed(String propertyName, String uriExpression, EmbedType embedType, String... fields) {
		switch (embedType) {
		case ONDEMAND: return embedOnDemand(propertyName, uriExpression, fields);
		case PRELOAD: return embedPreload(propertyName, uriExpression, fields);
		default: throw new RuntimeException("Unknown embed type: "+embedType.name());
		}
	}

	/**
	 * Allows for embedding additional SSC entities into the resulting
	 * JSON objects;  
	 * @param propertyName
	 * @param uriExpression
	 * @param fields
	 * @return
	 */
	protected T embedOnDemand(String propertyName, String uriExpression, String... fields) {
		return onDemand(propertyName, appendOnDemandFields(uriExpression, fields));
	}

	protected T embedPreload(String propertyName, String uriExpression, String... fields) {
		return pagePreProcessor(
				getConn().api(SSCBulkAPI.class).bulkEmbedder()
					.targetProperty(propertyName)
					.uriExpression(appendOnDemandFields(uriExpression, fields))
					.asPagePreProcessor());
	}
	
	@Override
	protected IJSONMapOnDemandLoader createOnDemandLoader(String uriString) {
		return new SSCJSONMapOnDemandLoaderRest(getConn(), uriString);
	}
	
	protected String appendOnDemandFields(String uriString, String... fields) {
		String result = uriString;
		if ( fields!=null && fields.length > 0 ) {
			result = UriBuilder.fromUri(uriString).queryParam("fields", String.join(",", fields)).toTemplate();
		}
		return result;
	}
	
	/**
	 * {@link IWebTargetUpdaterBuilder} implementation for adding the
	 * SSC 'q' request parameter.
	 *  
	 * @author Ruud Senden
	 *
	 */
	private static class SSCParamQ implements IWebTargetUpdaterBuilder {
		private final Map<String, Object> paramQAnds = new HashMap<>();
		
		public final SSCParamQ paramQAnd(String field, Object value) {
			paramQAnds.put(field, value);
			return this;
		}

		@Override
		public IWebTargetUpdater build() {
			String q = null;
			if ( MapUtils.isNotEmpty(paramQAnds) ) {
				StringBuffer sb = new StringBuffer();
				for ( Map.Entry<String, Object> entry : paramQAnds.entrySet() ) {
					Object value = entry.getValue();
					String qAppend = value instanceof String 
							? entry.getKey()+":\""+entry.getValue()+"\""
							: entry.getKey()+":"+entry.getValue();
					if ( sb.length() == 0 ) {
						sb.append(qAppend);
					} else {
						sb.append("+and+"+qAppend);
					}
				}
				q = sb.toString();
			}
			return new WebTargetQueryParamUpdater("q", q);
		}

	} 
}
