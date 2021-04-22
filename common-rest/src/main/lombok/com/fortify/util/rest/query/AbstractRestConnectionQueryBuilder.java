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
package com.fortify.util.rest.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import org.apache.commons.lang.StringUtils;

import com.fortify.util.applier.ifblank.IfBlankAction;
import com.fortify.util.rest.connection.IRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.embed.StandardEmbedConfig;
import com.fortify.util.rest.json.embed.StandardEmbedDefinition;
import com.fortify.util.rest.json.preprocessor.IJSONMapPreProcessor;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandRestData;
import com.fortify.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.util.rest.webtarget.WebTargetPathUpdaterBuilder;
import com.fortify.util.rest.webtarget.WebTargetQueryParamUpdaterBuilder;
import com.fortify.util.rest.webtarget.WebTargetTemplateResolverBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>This abstract class allows for configuring an {@link AbstractRestConnectionQuery}
 * instance for a specific query. Usually for each target system query endpoint,
 * you would have a corresponding concrete implementation of this class, that allows
 * for configuring the endpoint details like target path and any query parameters.</p>
 * 
 * <p>The common class structure looks as follows:</p>
 * <ul><li>{@link AbstractRestConnectionQueryBuilder}
 *     <ul><li>AbstractMySystemQueryBuilder<br>
 *             Specifies connection type and adds method 
 *             <code>public MySystemRestConnectionQuery build() {return new MySystemRestConnectionQuery(this);}</code>
 *         <ul><li>MySystemEndpoint1QueryBuilder</li>
 *             <li>MySystemEndpoint2QueryBuilder</li>
 *         </ul>
 *     </li></ul>
 * </li></ul>
 * 
 * <p>This allows clients of your API to execute queries like this:
 * <code>new MySystemEndpoint1QueryBuilder(conn, requiredProperty1, ...).criteria1(value).criteria2(value).build().getAll()</code>
 * Usually constructing the QueryBuilder instances is not left to API consumers, but the system-specific
 * API provides methods like <code>querySomething(requiredProperty1, ...)</code> that construct and return
 * the corresponding QueryBuilder instance. As such, API consumers would do something like this:
 * <code>api.querySomething(requiredProperty1, ...).criteria1(value).criteria2(value).build().getAll()</code></p>
 * 
 * <p>Concrete implementations of this class can utilize various functionality provided by this base class,
 * and the {@link com.fortify.util.rest.webtarget} package to generate requests based on configurable
 * search criteria. See for example the SSC REST client code for examples.</p>
 * 
 * @author Ruud Senden
 *
 * @param <ConnType> Concrete {@link IRestConnection} type
 * @param <T> Concrete type of this class
 */
@Getter
public abstract class AbstractRestConnectionQueryBuilder<ConnType extends IRestConnection, T extends AbstractRestConnectionQueryBuilder<ConnType, T>> 
{
	private final ConnType conn;
	private final List<IWebTargetUpdaterBuilder> webTargetUpdaterBuilders = new ArrayList<>();
	private final WebTargetPathUpdaterBuilder webTargetPathUpdaterBuilder = new WebTargetPathUpdaterBuilder();
	private final WebTargetQueryParamUpdaterBuilder webTargetQueryParamUpdaterBuilder = new WebTargetQueryParamUpdaterBuilder();
	private final WebTargetTemplateResolverBuilder webTargetTemplateResolverBuilder = new WebTargetTemplateResolverBuilder();
	
	private final List<BiConsumer<PagingData, JSONList>> pagePreProcessors = new ArrayList<>();
	private final List<BiConsumer<PagingData, JSONList>> pagePostProcessors = new ArrayList<>();
	private final List<IJSONMapPreProcessor> preProcessors = new ArrayList<>();
	private int maxResults = -1;
	private final boolean pagingSupported;
	@Setter(AccessLevel.PROTECTED) private String httpMethod = HttpMethod.GET;
	@Setter(AccessLevel.PROTECTED) private Entity<?> entity = null;
	@Setter(AccessLevel.PROTECTED) private IRequestInitializer requestInitializer = null;
	@Setter(AccessLevel.PROTECTED) private boolean encodeSlashInPath = false;
	
	protected AbstractRestConnectionQueryBuilder(ConnType conn, boolean pagingSupported) {
		this.conn = conn;
		this.pagingSupported = pagingSupported;
	}
	
	public T pagePreProcessor(BiConsumer<PagingData, JSONList> pagePreProcessor) {
		this.pagePreProcessors.add(pagePreProcessor);
		return _this();
	}
	
	public T pagePreProcessor(Consumer<JSONList> pagePreProcessor) {
		return pagePreProcessor((pagingData,jsonList)->pagePreProcessor.accept(jsonList));
	}
	
	public T pagePreProcessor(final int blockSize, final Consumer<JSONList> pagePreProcessor) {
		return pagePreProcessor(jsonList->jsonList.forEachBlock(blockSize, pagePreProcessor));
	}
	
	public T pagePostProcessor(BiConsumer<PagingData, JSONList> pagePostProcessor) {
		this.pagePostProcessors.add(pagePostProcessor);
		return _this();
	}
	
	public T pagePostProcessor(Consumer<JSONList> pagePostProcessor) {
		return pagePreProcessor((pagingData,jsonList)->pagePostProcessor.accept(jsonList));
	}
	
	@SuppressWarnings("unchecked")
	public T preProcessor(IJSONMapPreProcessor preProcessor) {
		if ( preProcessor instanceof IRestConnectionQueryConfigAware ) {
			((IRestConnectionQueryConfigAware<T>)preProcessor).setRestConnectionQueryConfig(_this());
		}
		this.preProcessors.add(preProcessor);
		return _this();
	}
	
	public T maxResults(Integer maxResults) {
		this.maxResults = maxResults;
		return _this();
	}
	
	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T)this;
	}
	
	protected T queryParam(String paramName, String... paramValues) {
		webTargetQueryParamUpdaterBuilder.queryParam(paramName, paramValues);
		return _this();
	}
	
	protected T queryParam(IfBlankAction ifBlankAction, String paramName, String paramValue) {
		ifBlankAction.apply(paramName, paramValue, StringUtils::isBlank, v->queryParam(paramName, v));
		return _this();
	}
	
	protected T appendPath(String path) {
		webTargetPathUpdaterBuilder.appendPath(path);
		return _this();
	}
	
	protected String getPath() {
		return webTargetPathUpdaterBuilder.getPath();
	}
	
	protected T templateValue(String name, String value) {
		webTargetTemplateResolverBuilder.templateValue(name, value);
		return _this();
	}
	
	public <C extends StandardEmbedConfig> T embed(C embedConfig) {
		return preProcessor(new JSONMapEnrichWithOnDemandRestData(getConn(), new StandardEmbedDefinition(embedConfig)));
	}
	
	protected <B extends IWebTargetUpdaterBuilder> B add(B builder) {
		webTargetUpdaterBuilders.add(builder);
		return builder;
	}
	
	protected List<IWebTargetUpdater> getWebTargetUpdaters() {
		List<IWebTargetUpdater> result = new ArrayList<>(webTargetUpdaterBuilders.size());
		result.add(webTargetPathUpdaterBuilder.build());
		result.add(webTargetQueryParamUpdaterBuilder.build());
		for ( IWebTargetUpdaterBuilder builder : webTargetUpdaterBuilders ) {
			result.add(builder.build());
		}
		result.add(webTargetTemplateResolverBuilder.build());
		return result;
	}
	
	/** 
	 * Utility method for replacing a specific field in the given fields array
	 * @param fieldToReplace identified the field to be replaced in the given fields array
	 * @param replacements for the given field to be replaced
	 * @param fields in which to replace the given field
	 * @return Copy of the given fields array, with the given replacements replacing the field to be replaced
	 */
	protected String[] replaceField(String fieldToReplace, String[] replacements, String... fields) {
		if ( fields==null ) { return null; }
		List<String> resultList = new ArrayList<>(fields.length+1);
		for ( String field : fields ) {
			if ( field.equals(fieldToReplace) ) {
				Collections.addAll(resultList, replacements);
			} else {
				resultList.add(field);
			}
		}
		return resultList.toArray(new String[] {});
	}
	
	/**
	 * Utility method for checking whether the given {@link String} array
	 * is null, empty or contains only blank string values 
	 * @param values to be checked
	 * @return true if the given string array is null, empty, or contains only blank string values, false otherwise
	 */
	protected boolean isBlankStringArray(String[] values) {
		return values==null || Arrays.stream(values).filter(StringUtils::isNotBlank).count()==0;
	}

	public abstract IRestConnectionQuery build();
}