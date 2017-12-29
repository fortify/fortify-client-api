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
package com.fortify.client.fod.api.query.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.util.rest.query.AbstractRestConnectionQueryConfig;
import com.fortify.util.rest.query.IRestConnectionQuery;
import com.fortify.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.util.rest.webtarget.WebTargetQueryParamUpdater;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * <p>This abstract base class is used to build {@link FoDEntityQuery} instances. Concrete implementations
 * will need to provide the actual FoD REST API endpoint by calling the {@link #appendPath(String)} method
 * (usually in their constructor), and indicate whether this endpoint supports paging (by providing the
 * pagingSupported parameter to the constructor of this superclass).</p>
 * 
 * <p>This class provides various protected methods for configuring common FoD request parameters,
 * like 'filter' and 'orderBy'. Depending on whether the target FoD endpoint supports these parameters,
 * concrete implementations can override these methods as 'public' to make the generic method available, 
 * and/or provide more specialized builder methods that call these generic methods, for example to support
 * specific fields to be added to the 'filter' parameter.</p>  
 *  
 * @author Ruud Senden
 *
 * @param <T> Concrete builder type
 */
public abstract class AbstractFoDEntityQueryBuilder<T> extends AbstractRestConnectionQueryConfig<FoDAuthenticatingRestConnection, T> {
	private FoDParamFilter paramFilter = add(new FoDParamFilter());
	
	/**
	 * Create new instance for given {@link FoDAuthenticatingRestConnection} and indicator whether paging is supported.
	 * @param conn
	 * @param pagingSupported
	 */
	protected AbstractFoDEntityQueryBuilder(FoDAuthenticatingRestConnection conn, boolean pagingSupported) {
		super(conn, pagingSupported);
	}
	
	/**
	 * Build an {@link IRestConnectionQuery} instance from the current
	 * configuration.
	 * 
	 * @return
	 */
	public IRestConnectionQuery build() {
		return new FoDEntityQuery(this);
	}
	
	/**
	 * Add the 'orderBy' query parameter to the request configuration
	 * 
	 * @param orderBy
	 * @return
	 */
	protected T paramOrderBy(String orderBy) {
		return queryParam("orderBy", orderBy);
	}
	
	/**
	 * Add the 'orderByDirection' query parameter to the request configuration
	 * 
	 * @param orderByDirection
	 * @return
	 */
	protected T paramOrderByDirection(FoDOrderByDirection orderByDirection) {
		return queryParam("orderByDirection", orderByDirection.name());
	}
	
	protected T paramOrderBy(String orderBy, FoDOrderByDirection orderByDirection) {
		paramOrderBy(orderBy);
		return paramOrderByDirection(orderByDirection);
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
	 * Add the given field and values to the 'filter' query
	 * parameter. Multiple values for the same field (either
	 * through a single call or multiple calls to this method)
	 * will be OR-ed together.
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	protected T paramFilterAnd(String field, String... values) {
		paramFilter.paramFilterAnd(field, values); return _this();
	}
	
	/**
	 * Add the given filter string (formatted according to FoD
	 * REST API documentation) to the 'filter' query parameter.
	 * 
	 * @param filter
	 * @return
	 */
	protected T paramFilterAnd(String filter) {
		paramFilter.paramFilterAnd(filter); return _this();
	}
	
	/**
	 * {@link IWebTargetUpdaterBuilder} implementation for adding the
	 * FoD 'filter' request parameter.
	 *  
	 * @author Ruud Senden
	 *
	 */
	private static class FoDParamFilter implements IWebTargetUpdaterBuilder {
		private final ListMultimap<String, String> paramFilterAndsMap = ArrayListMultimap.create();
		private final List<String> paramFilterAndsList = new ArrayList<>();
		
		public final FoDParamFilter paramFilterAnd(String field, String... values) {
			paramFilterAndsMap.putAll(field, Arrays.asList(values));
			return this;
		}
		
		public final FoDParamFilter paramFilterAnd(String paramFilterAnd) {
			paramFilterAndsList.add(paramFilterAnd);
			return this;
		}

		@Override
		public IWebTargetUpdater build() {
			StringBuffer sb = new StringBuffer();
			appendParamFilterAndsMap(sb);
			appendParamFilterAndsList(sb);
			return new WebTargetQueryParamUpdater("filter", sb.toString());
		}

		private void appendParamFilterAndsList(StringBuffer sb) {
			if ( !paramFilterAndsList.isEmpty() ) {
				for ( String filterAppend : paramFilterAndsList ) {
					appendFilter(sb, filterAppend);
				}
			}
		}

		private void appendParamFilterAndsMap(StringBuffer sb) {
			if ( !paramFilterAndsMap.isEmpty() ) {
				for ( Entry<String, Collection<String>> entry : paramFilterAndsMap.asMap().entrySet() ) {
					String filterAppend = entry.getKey()+":"+StringUtils.join(entry.getValue(),'|');
					appendFilter(sb, filterAppend);
				}
			}
		}

		private void appendFilter(StringBuffer sb, String filterAppend) {
			if ( sb.length() == 0 ) {
				sb.append(filterAppend);
			} else {
				sb.append("+"+filterAppend);
			}
		}

	} 
}
