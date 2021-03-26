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
package com.fortify.client.ssc.api.query.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.fortify.client.ssc.api.SSCBulkAPI;
import com.fortify.client.ssc.api.json.embed.SSCEmbedConfig;
import com.fortify.client.ssc.api.json.embed.SSCEmbedConfig.EmbedType;
import com.fortify.client.ssc.api.json.embed.SSCEmbedConfig.SSCEmbedConfigBuilder;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.applier.ifblank.IfBlank;
import com.fortify.util.applier.ifblank.IfBlankAction;
import com.fortify.util.rest.json.embed.StandardEmbedConfig;
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
	 * @param conn {@link SSCAuthenticatingRestConnection} used to connect to SSC
	 * @param pagingSupported indicates whether paging is supported by the configured SSC endpoint
	 */
	protected AbstractSSCEntityQueryBuilder(SSCAuthenticatingRestConnection conn, boolean pagingSupported) {
		super(conn, pagingSupported);
	}
	
	/**
	 * Build an {@link IRestConnectionQuery} instance from the current
	 * configuration.
	 * 
	 * @return {@link IRestConnectionQuery} instance
	 */
	@Override
	public IRestConnectionQuery build() {
		return new SSCEntityQuery(this);
	}
	
	/**
	 * Add the 'embed' query parameter to the request configuration
	 * 
	 * @param ifBlankAction specifies how to handle blank values
	 * @param entity The entity to use as a value for the embed request parameter
	 * @return Self for chaining
	 */
	protected T paramEmbed(IfBlankAction ifBlankAction, String entity) {
		return queryParam(ifBlankAction, "embed", entity);
	}
	
	protected T paramEmbed(String entity) {
		return queryParam(IfBlank.ERROR(), "embed", entity);
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramEmbed(String)} method.
	 *
	 * @param <T> Concrete {@link AbstractSSCEntityQueryBuilder} instance
	 */
	public static interface ISSCEntityQueryBuilderParamEmbed<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramEmbed(IfBlankAction ifBlankAction, String entity);
	}
	
	/**
	 * Add the 'fields' query parameter to the request configuration
	 * 
	 * @param fields to be added to the fields request parameter
	 * @return Self for chaining
	 */
	protected T paramFields(String... fields) {
		return queryParam("fields", StringUtils.join(fields, ","));
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramFields(String...)} method.
	 *
	 * @param <T> Concrete {@link AbstractSSCEntityQueryBuilder} instance
	 */
	public static interface ISSCEntityQueryBuilderParamFields<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramFields(String... fields);
	}
	
	/**
	 * Add the 'orderBy' query parameter to the request configuration.
	 * 
	 * @param ifBlankAction specifies how to handle blank values 
	 * @param orderBy defines both the field and direction to order by
	 * @return Self for chaining
	 */
	protected T paramOrderBy(IfBlankAction ifBlankAction, SSCOrderBy orderBy) {
		ifBlankAction.apply("orderBy", orderBy, this::isBlankOrderBy, v-> {
			String orderByParam = v.getField();
			if ( SSCOrderByDirection.DESC.equals(v.getDirection()) ) {
				orderByParam = "-"+orderByParam;
			}
			queryParam("orderby", orderByParam);
		});
		return _this();
	}
	
	private boolean isBlankOrderBy(SSCOrderBy orderBy) {
		return orderBy==null || StringUtils.isBlank(orderBy.getField());
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramOrderBy(IfBlankAction, SSCOrderBy)} method.
	 *
	 * @param <T> Concrete {@link AbstractSSCEntityQueryBuilder} instance
	 */
	public static interface ISSCEntityQueryBuilderParamOrderBy<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramOrderBy(IfBlankAction ifBlankAction, SSCOrderBy orderBy);
	}
	
	/**
	 * Add the 'groupby' query parameter to the request configuration
	 * 
	 * @param ifBlankAction specifies how to handle blank values
	 * @param groupBy specifies the value for the groupby request parameter
	 * @return Self for chaining
	 */
	protected T paramGroupBy(IfBlankAction ifBlankAction, String groupBy) {
		return queryParam(ifBlankAction, "groupby", groupBy);
	}
	
	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the {@link #paramGroupBy(IfBlankAction, String)} method.
	 *
	 * @param <T> Concrete {@link AbstractSSCEntityQueryBuilder} instance
	 */
	public static interface ISSCEntityQueryBuilderParamGroupBy<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramGroupBy(IfBlankAction ifBlankAction, String groupBy);
	}
	
	/**
	 * Add the 'q' query parameter to the request configuration.
	 * This will take the given value literally, as opposed to
	 * {@link #paramQAnd(IfBlankAction, String, Object)} that allows for building
	 * a multi-part 'q' parameter.
	 * 
	 * @param ifBlankAction specifies how to handle blank values
	 * @param q specifies the value for the 1 request parameter
	 * @return Self for chaining
	 */
	protected T paramQ(IfBlankAction ifBlankAction, String q) {
		return queryParam(ifBlankAction, "q", q);
	}
	
	/**
	 * Add the 'q' query parameter to the request configuration. If
	 * already set, the new field and value will be 'and-ed' to the
	 * current query parameter value.
	 * 
	 * @param ifBlankAction specifies how to handle blank values
	 * @param field specifies a field to be added to the q request parameter
	 * @param value specifies the corresponding value to be added to the q request parameter
	 * @return Self for chaining
	 */
	protected T paramQAnd(IfBlankAction ifBlankAction, String field, Object value) {
		ifBlankAction.apply(field, value, this::isBlankObject, v->paramQ.paramQAnd(field, v));
		return _this();
	}
	
	private boolean isBlankObject(Object value) {
		return value==null || (value instanceof String && StringUtils.isBlank((String)value));
	}

	/**
	 * This interface is to be implemented by all {@link AbstractSSCEntityQueryBuilder}
	 * implementations that expose the various paramQ*() methods method.
	 *
	 * @param <T> Concrete {@link AbstractSSCEntityQueryBuilder} instance
	 */
	public static interface ISSCEntityQueryBuilderParamQ<T extends AbstractSSCEntityQueryBuilder<T>> {
		public T paramQ(IfBlankAction ifBlankAction, String q);
		public T paramQAnd(IfBlankAction ifBlankAction, String field, Object value);
	}
	
	
	/**
	 * Allows for embedding additional SSC entities into the resulting JSON objects. 
	 * Depending on the embedType in given {@link SSCEmbedConfig}, the additional 
	 * entities are either loaded on demand whenever they are accessed, or pre-loaded 
	 * using SSC bulk requests.
	 * 
	 * @param embedConfig describes the entity to be embedded in the results
	 * @return Self for chaining
	 */
	public T embed(SSCEmbedConfig embedConfig) {
		EmbedType embedType = embedConfig.getEmbedType();
		switch (embedType) {
		case ONDEMAND: return super.embed(embedConfig);
		case PRELOAD: return embedPreload(embedConfig);
		default: throw new RuntimeException("Unknown embed type: "+embedType.name());
		}
	}
	
	public T embedSubEntity(String propertyName, String subEntity, EmbedType embedType, String... fields) {
		return embed(createEmbedConfigBuilder()
				.propertyName(propertyName)
				.subEntity(subEntity)
				.embedType(embedType)
				.param("fields", fields==null?null : String.join(",", fields))
				.build());
	}

	/**
	 * Subclasses can override this method to create a builder for an
	 * {@link SSCEmbedConfig} subclass that supports loading sub-entities
	 * by providing an implementation for the {@link StandardEmbedConfig#getSubEntityUri(String)} method. 
	 * @return {@link SSCEmbedConfigBuilder} instance
	 */
	protected SSCEmbedConfigBuilder<?,?> createEmbedConfigBuilder() {
		return SSCEmbedConfig.builder();
	}

	protected T embedPreload(SSCEmbedConfig embedConfig) {
		return pagePreProcessor(getConn().api(SSCBulkAPI.class).bulkEmbedder(embedConfig).asPagePreProcessor());
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
