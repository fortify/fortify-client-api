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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import com.fortify.api.util.rest.connection.IRestConnection;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.preprocessor.IJSONMapFilter;
import com.fortify.api.util.rest.json.preprocessor.IJSONMapPreProcessor;
import com.fortify.api.util.rest.json.preprocessor.JSONMapFilterMaxResults;
import com.fortify.api.util.rest.json.processor.IJSONMapProcessor;
import com.fortify.api.util.rest.json.processor.JSONMapProcessorWithPreProcessors;
import com.fortify.api.util.rest.json.processor.JSONMapsToJSONListProcessor;
import com.fortify.api.util.rest.webtarget.IWebTargetUpdater;

import lombok.Getter;

/**
 * <p>This abstract class provides an implementation for {@link IRestConnectionQuery} to allow for
 * querying REST API's. Usually for each target system, you would have one (or a limited number of) 
 * concrete implementations of this class. These concrete implementations will need to implement and override
 * various methods to handle things like paging ({@link #updatePagingDataFromResponse(PagingData, Object)}
 * and {@link #updateWebTargetWithPagingData(WebTarget, PagingData)}, and getting the necessary
 * {@link JSONList} instance from the REST response ({@link #getResponseTypeClass()} and 
 * {@link #getJSONListFromResponse(Object)}.</p>
 * 
 * <p>This class is configured through a {@link AbstractRestConnectionQueryConfig} instance, which allows for
 * configuring the various details for building the actual REST requests and processing responses. This
 * includes {@link IWebTargetUpdater} instances for generating the actual REST request, and {@link IJSONMapPreProcessor}
 * instances for pre-processing each individual {@link JSONMap} instance returned by the REST request.</p>
 * 
 * <p>Query implementations usually should not extends this class directly, but rather extend
 * {@link AbstractRestConnectionWithCacheQuery} to add optional support for caching. The common 
 * class structure looks as follows:
 * <ul><li>{@link AbstractRestConnectionQuery}
 *     <ul><li>{@link AbstractRestConnectionWithCacheQuery}
 *         <ul><li>MySystemQuery</li></ul>
 *     </li></ul>
 * </li></ul>
 * Instances of MySystemQuery are not created directly by API consumers, but rather through build()
 * methods on concrete {@link AbstractRestConnectionQueryConfig} implementations.</p> 
 * 
 * @author Ruud Senden
 */
@Getter
public abstract class AbstractRestConnectionQuery<ConnType extends IRestConnection, ResponseType> implements IRestConnectionQuery {
	private final ConnType conn;
	private final List<IWebTargetUpdater> webTargetUpdaters;
	private final List<IJSONMapPreProcessor> preProcessors;
	private final int maxResults;
	private final boolean useCache;
	private final boolean pagingSupported;
	private final Entity<?> entity;
	private final String httpMethod;
	private final IRequestInitializer requestInitializer;
	private final boolean hasFilters;
	
	protected AbstractRestConnectionQuery(AbstractRestConnectionQueryConfig<ConnType, ?> config) {
		this.conn = config.getConn();
		this.webTargetUpdaters = Collections.unmodifiableList(config.getWebTargetUpdaters());
		this.preProcessors =  Collections.unmodifiableList(config.getPreProcessors());
		this.maxResults = config.getMaxResults();
		this.useCache = config.isUseCache();
		this.pagingSupported = config.isPagingSupported();
		this.entity = config.getEntity();
		this.httpMethod = config.getHttpMethod();
		this.requestInitializer = config.getRequestInitializer();
		boolean hasFilters = false;
		for ( IJSONMapPreProcessor preProcessor : this.preProcessors ) {
			if ( preProcessor instanceof IJSONMapFilter ) {
				hasFilters = true; break;
			}
		}
		this.hasFilters = hasFilters;
	}
	
	/* (non-Javadoc)
	 * @see com.fortify.api.util.rest.query.IRestConnectionQuery#processAll(com.fortify.api.util.rest.json.IJSONMapProcessor)
	 */
	@Override
	public void processAll(IJSONMapProcessor processor) {
		processAll(getWebTarget(), new PagingData(hasFilters).max(maxResults), processor);
	}

	/* (non-Javadoc)
	 * @see com.fortify.api.util.rest.query.IRestConnectionQuery#getAll()
	 */
	@Override
	public JSONList getAll() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(processor);
		return processor.getJsonList();
	}
	
	/* (non-Javadoc)
	 * @see com.fortify.api.util.rest.query.IRestConnectionQuery#getUnique()
	 */
	@Override
	public JSONMap getUnique() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(getWebTarget(), new PagingData(hasFilters).max(Math.min(2, maxResults)), processor);
		JSONList list = processor.getJsonList();
		if ( list == null || list.size() == 0 ) {
			return null;
		}
		if ( list.size() > 1 ) {
			throw new RuntimeException("More than one object found: "+list); // TODO Use less generic exception type
		}
		return list.asValueType(JSONMap.class).get(0);
	}
	
	/*
	public int getCount() {
		return -1; // TODO
	}
	*/
	
	protected final WebTarget getWebTarget() {
		WebTarget webTarget = conn.getBaseResource();
		for ( IWebTargetUpdater updater : webTargetUpdaters ) {
			webTarget = updater.update(webTarget);
		}
		return webTarget;
	}
	
	protected ResponseType executeRequest(WebTarget target) {
		if ( entity == null ) {
			if ( useCache ) {
				return conn.executeRequest(getHttpMethod(), target, getResponseTypeClass(), getCacheName());
			} else {
				return conn.executeRequest(httpMethod, target, getResponseTypeClass());
			}
		} else {
			return conn.executeRequest(httpMethod, target, entity, getResponseTypeClass());
		}
	}
	
	protected String getCacheName() {
		return this.getClass().getName();
	}
	
	protected void updatePagingDataFromResponse(PagingData pagingData, ResponseType data) {
		throw new UnsupportedOperationException("Paging is not supported by "+this.getClass().getName());
	}

	protected WebTarget updateWebTargetWithPagingData(WebTarget target, PagingData pagingData) {
		throw new UnsupportedOperationException("Paging is not supported by "+this.getClass().getName());
	}
	
	protected abstract Class<ResponseType> getResponseTypeClass();

	protected abstract JSONList getJSONListFromResponse(ResponseType data);
	
	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 * Depending on the return value of {@link #isPagingSupported()}, this method will either directly invoke
	 * the given web target (paging not supported), or retrieve all data page by page (paging is supported).
	 * 
	 */
	private void processAll(WebTarget target, PagingData pagingData, IJSONMapProcessor processor) {
		if ( requestInitializer != null ) { requestInitializer.initRequest(); }
		List<IJSONMapPreProcessor> preProcessorsWithMaxResults = new ArrayList<>(preProcessors);
		preProcessorsWithMaxResults.add(new JSONMapFilterMaxResults(maxResults));
		processor = new JSONMapProcessorWithPreProcessors(preProcessorsWithMaxResults, processor);
		if ( !pagingSupported ) {
			processSingleRequest(target, processor, null);
		} else {
			do {
				processor.nextPage(pagingData);
				WebTarget pagingTarget = updateWebTargetWithPagingData(target, pagingData);
				ResponseType response = processSingleRequest(pagingTarget, processor, pagingData);
				updatePagingDataFromResponse(pagingData, response);
			} while ( pagingData.getStart() < pagingData.getTotal() && pagingData.getPageSize()>0 );
		}
	}
	
	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 */
	private ResponseType processSingleRequest(WebTarget target, IJSONMapProcessor processor, PagingData pagingData) {
		ResponseType data = executeRequest(target);
		JSONList list = getJSONListFromResponse(data);
		if ( processor != null ) {
			for ( JSONMap obj : list.asValueType(JSONMap.class) ) {
				processor.process(obj);
				if ( pagingData != null ) {
					pagingData.addToCurrentCount(1);
				}
			}
		}
		return data;
	}
	
	public static interface IRequestInitializer {
		public void initRequest();
	}
}
