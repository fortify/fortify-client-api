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
package com.fortify.client.fod.api.query.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.client.fod.json.ondemand.FoDJSONMapOnDemandLoaderRest;
import com.fortify.util.rest.json.ondemand.IJSONMapOnDemandLoader;
import com.fortify.util.rest.query.AbstractRestConnectionQueryBuilder;
import com.fortify.util.rest.query.IRestConnectionQuery;
import com.fortify.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.util.rest.webtarget.WebTargetQueryParamUpdater;

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
public abstract class AbstractFoDEntityQueryBuilder<T extends AbstractFoDEntityQueryBuilder<T>> extends AbstractRestConnectionQueryBuilder<FoDAuthenticatingRestConnection, T> {
	protected static final FastDateFormat FOD_DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'hh:mm:ss");
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
	@Override
	public IRestConnectionQuery build() {
		return new FoDEntityQuery(this);
	}
	
	/**
	 * Add the 'orderBy' query parameter to the request configuration
	 * 
	 * @param ignoreIfBlank
	 * @param orderBy
	 * @return
	 */
	protected T paramOrderBy(boolean ignoreIfBlank, String field) {
		return queryParam(ignoreIfBlank, "orderBy", field);
	}
	
	/**
	 * Add the 'orderByDirection' query parameter to the request configuration
	 * 
	 * @param orderByDirection
	 * @return
	 */
	protected T paramOrderByDirection(boolean ignoreIfBlank, FoDOrderByDirection orderByDirection) {
		return queryParam(ignoreIfBlank, "orderByDirection", orderByDirection==null?null:orderByDirection.name());
	}
	
	protected T paramOrderBy(boolean ignoreIfBlank, FoDOrderBy orderBy) {
		if ( orderBy!=null ) {
			if ( StringUtils.isNotBlank(orderBy.getField()) ) {
				paramOrderBy(ignoreIfBlank, orderBy.getField());
			}
			if ( orderBy.getDirection()!=null ) {
				paramOrderByDirection(ignoreIfBlank, orderBy.getDirection());
			}
		}
		return _this();
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractFoDEntityQueryBuilder}
	 * implementations that expose the {@link #paramOrderBy(boolean, FoDOrderBy)} method.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface IFoDEntityQueryBuilderParamOrderByWithDirection<T extends AbstractFoDEntityQueryBuilder<T>> {
		public T paramOrderBy(boolean ignoreIfBlank, FoDOrderBy orderBy);
	}
	
	/**
	 * Add the 'fields' query parameter to the request configuration
	 * 
	 * @param fields
	 * @return
	 */
	protected T paramFields(boolean ignoreIfBlank, String... fields) {
		if ( !isBlank(!ignoreIfBlank, "fields", fields) ) {
			queryParam("fields", StringUtils.join(fields, ","));
		}
		return _this();
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractFoDEntityQueryBuilder}
	 * implementations that expose the various paramFields*() methods.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface IFoDEntityQueryBuilderParamFields<T extends AbstractFoDEntityQueryBuilder<T>> {
		public T paramFields(boolean ignoreIfBlank, String... fields);
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
	protected T paramFilterAnd(boolean ignoreIfBlank, String field, String... values) {
		if ( !isBlank(!ignoreIfBlank, "paramFilter."+field, values) ) {
			paramFilter.paramFilterAnd(field, values);
		}
		return _this();
	}
	
	/**
	 * Add the given filter string (formatted according to FoD
	 * REST API documentation) to the 'filter' query parameter.
	 * 
	 * @param filter
	 * @return
	 */
	protected T paramFilterAnd(boolean ignoreIfBlank, String filter) {
		if ( !isBlank(!ignoreIfBlank, "paramFilter", filter) ) {
			paramFilter.paramFilterAnd(filter); return _this();
		}
		return _this();
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractFoDEntityQueryBuilder}
	 * implementations that expose the various paramFilter*() methods.
	 * @author Ruud Senden
	 *
	 * @param <T>
	 */
	public static interface IFoDEntityQueryBuilderParamFilter<T extends AbstractFoDEntityQueryBuilder<T>> {
		public T paramFilterAnd(boolean ignoreIfBlank, String field, String... values);
		public T paramFilterAnd(boolean ignoreIfBlank, String filter);
	}
	
	/**
	 * Allows for embedding additional FoD entities into the resulting JSON objects.
	 * 
	 * @param descriptor
	 * @return
	 */
	public T embed(FoDEmbedDescriptor descriptor) {
		return onDemand(descriptor, this::getSubEntityUri);
	}
	
	public T embedSubEntity(String propertyName, String subEntity, String... fields) {
		return embed(FoDEmbedDescriptor.builder()
				.propertyName(propertyName)
				.subEntity(subEntity)
				.param("fields", fields==null?null : String.join(",", fields))
				.build());
	}
	
	protected String getSubEntityUri(String subEntity) {
		throw new RuntimeException("Embedding sub-entities is not supported by this entity query builder");
	}

	
	@Override
	protected IJSONMapOnDemandLoader createOnDemandLoader(String uri) {
		return new FoDJSONMapOnDemandLoaderRest(getConn(), uri);
	}
	
	/**
	 * {@link IWebTargetUpdaterBuilder} implementation for adding the
	 * FoD 'filter' request parameter.
	 *  
	 * @author Ruud Senden
	 *
	 */
	private static class FoDParamFilter implements IWebTargetUpdaterBuilder {
		private final Map<String, Collection<String>> paramFilterAndsMap = new LinkedHashMap<>();
		private final List<String> paramFilterAndsList = new ArrayList<>();
		
		public final FoDParamFilter paramFilterAnd(String field, String... values) {
			paramFilterAndsMap.put(field, Arrays.asList(values));
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
			return new WebTargetQueryParamUpdater("filters", sb.toString());
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
				for ( Entry<String, Collection<String>> entry : paramFilterAndsMap.entrySet() ) {
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
