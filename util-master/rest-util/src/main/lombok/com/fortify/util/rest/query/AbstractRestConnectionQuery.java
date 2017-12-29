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
package com.fortify.util.rest.query;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import com.fortify.util.rest.connection.IRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.IJSONMapPreProcessor;
import com.fortify.util.rest.json.processor.IJSONMapProcessor;
import com.fortify.util.rest.json.processor.JSONMapsToJSONListProcessor;
import com.fortify.util.rest.webtarget.IWebTargetUpdater;

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
public abstract class AbstractRestConnectionQuery<ResponseType> implements IRestConnectionQuery {
	private final IRestConnection conn;
	private final List<IWebTargetUpdater> webTargetUpdaters;
	private final List<IJSONMapPreProcessor> preProcessors;
	private final int maxResults;
	private final boolean useCache;
	private final boolean pagingSupported;
	private final Entity<?> entity;
	private final String httpMethod;
	private final IRequestInitializer requestInitializer;
	
	protected AbstractRestConnectionQuery(AbstractRestConnectionQueryConfig<?, ?> config) {
		this.conn = config.getConn();
		this.webTargetUpdaters = Collections.unmodifiableList(config.getWebTargetUpdaters());
		this.preProcessors =  Collections.unmodifiableList(config.getPreProcessors());
		this.maxResults = config.getMaxResults();
		this.useCache = config.isUseCache();
		this.pagingSupported = config.isPagingSupported();
		this.entity = config.getEntity();
		this.httpMethod = config.getHttpMethod();
		this.requestInitializer = config.getRequestInitializer();
	}
	
	/* (non-Javadoc)
	 * @see com.fortify.util.rest.query.IRestConnectionQuery#processAll(com.fortify.util.rest.json.IJSONMapProcessor)
	 */
	@Override
	public void processAll(IJSONMapProcessor processor) {
		processAll(getWebTarget(), new PagingData().maxResults(maxResults), processor);
	}

	/* (non-Javadoc)
	 * @see com.fortify.util.rest.query.IRestConnectionQuery#getAll()
	 */
	@Override
	public JSONList getAll() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(processor);
		return processor.getJsonList();
	}
	
	/* (non-Javadoc)
	 * @see com.fortify.util.rest.query.IRestConnectionQuery#getUnique()
	 */
	@Override
	public JSONMap getUnique() {
		JSONMapsToJSONListProcessor processor = new JSONMapsToJSONListProcessor();
		processAll(getWebTarget(), new PagingData().maxResults(Math.min(2, maxResults)), processor);
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
				return conn.executeRequest(httpMethod, target, getResponseTypeClass(), getCacheName());
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
	
	/**
	 * This method must be overridden by all implementations that support paging, to update the
	 * {@link PagingData} object with information from the last request, for example the total
	 * number of available results. This method is only called if this class is configured
	 * with {@link #pagingSupported}==true.
	 * @param pagingData
	 * @param responseData
	 */
	protected void updatePagingDataFromResponse(PagingData pagingData, ResponseType responseData) {
		throw new UnsupportedOperationException("Paging is not supported by "+this.getClass().getName());
	}

	/**
	 * This method must be overridden by all implementations that support paging, to add information
	 * about page start and size to the request. This method is only called if this class is configured
	 * with {@link #pagingSupported}==true.
	 * @param target
	 * @param pagingData
	 * @return Updated {@link WebTarget}
	 */
	protected WebTarget updateWebTargetWithPagingData(WebTarget target, PagingData pagingData) {
		throw new UnsupportedOperationException("Paging is not supported by "+this.getClass().getName());
	}
	
	/**
	 * This method must be implemented by subclasses to return the response type class.
	 * @return
	 */
	protected abstract Class<ResponseType> getResponseTypeClass();

	/**
	 * This method must be implemented by subclasses to get a {@link JSONList} instance
	 * from the response data.
	 * @param responseData
	 * @return
	 */
	protected abstract JSONList getJSONListFromResponse(ResponseType responseData);
	
	/**
	 * Process all results returned by the given {@link WebTarget} by calling the given {@link IJSONMapProcessor}.
	 * Depending on the return value of {@link #isPagingSupported()}, this method will either directly invoke
	 * the given web target (paging not supported), or retrieve all data page by page (paging is supported).
	 * 
	 */
	private void processAll(WebTarget target, PagingData pagingData, IJSONMapProcessor processor) {
		if ( requestInitializer != null ) { requestInitializer.initRequest(); }
		processor = new JSONMapProcessorWithPreProcessorsAndPagingSupport(preProcessors, processor, pagingData);
		if ( !pagingSupported ) {
			processSingleRequest(target, processor, pagingData);
		} else {
			while (pagingData.calculateNextPageSize() > 0) {
				processor.notifyNextPage(pagingData);
				WebTarget pagingTarget = updateWebTargetWithPagingData(target, pagingData);
				ResponseType response = processSingleRequest(pagingTarget, processor, pagingData);
				updatePagingDataFromResponse(pagingData, response);
			}
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
				if ( pagingData.isMaxResultsReached() ) { break; }
				processor.process(obj);
			}
		}
		return data;
	}
}
