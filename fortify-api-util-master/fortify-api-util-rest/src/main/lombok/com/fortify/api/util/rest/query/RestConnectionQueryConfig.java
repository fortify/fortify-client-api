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
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import com.fortify.api.util.rest.connection.IRestConnection;
import com.fortify.api.util.rest.json.IJSONMapPreProcessor;
import com.fortify.api.util.rest.query.AbstractRestConnectionQuery.IRequestInitializer;
import com.fortify.api.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.api.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.api.util.rest.webtarget.WebTargetPathUpdater;
import com.fortify.api.util.rest.webtarget.WebTargetTemplateResolver;
import com.google.common.collect.ImmutableMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class RestConnectionQueryConfig<ConnType extends IRestConnection, T> 
{
	private final ConnType conn;
	private final List<IWebTargetUpdaterBuilder> webTargetUpdaterBuilders = new ArrayList<>();
	private final List<IJSONMapPreProcessor> preProcessors = new ArrayList<>();
	private int maxResults = -1;
	private final boolean pagingSupported;
	private final ImmutableMap.Builder<String, Object> templateValues = ImmutableMap.<String,Object>builder();
	@Setter(AccessLevel.PROTECTED) private String httpMethod = HttpMethod.GET;
	@Setter(AccessLevel.PROTECTED) private Entity<?> entity = null;
	@Setter(AccessLevel.PROTECTED) private IRequestInitializer requestInitializer = null;
	@Setter(AccessLevel.PROTECTED) private boolean encodeSlashInPath = false;
	
	protected RestConnectionQueryConfig(ConnType conn, boolean pagingSupported) {
		this.conn = conn;
		this.pagingSupported = pagingSupported;
	}
	
	public T preProcessor(IJSONMapPreProcessor preProcessor) {
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
	
	protected <B extends IWebTargetUpdaterBuilder> B add(B builder) {
		webTargetUpdaterBuilders.add(builder);
		return builder;
	}
	
	protected ImmutableMap.Builder<String, Object> templateValues() {
		return templateValues;
	}
	
	protected List<IWebTargetUpdater> getWebTargetUpdaters() {
		List<IWebTargetUpdater> result = new ArrayList<>(webTargetUpdaterBuilders.size());
		result.add(new WebTargetPathUpdater(getTargetPath()));
		for ( IWebTargetUpdaterBuilder builder : webTargetUpdaterBuilders ) {
			result.add(builder.build());
		}
		result.add(new WebTargetTemplateResolver(getTemplateValues().build(), isEncodeSlashInPath()));
		return result;
	}
	
	protected abstract String getTargetPath();
}