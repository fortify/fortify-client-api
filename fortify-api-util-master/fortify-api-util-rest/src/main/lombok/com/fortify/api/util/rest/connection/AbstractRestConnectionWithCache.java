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
package com.fortify.api.util.rest.connection;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Data;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;

/**
 * <p>This {@link AbstractRestConnection} extension adds functionality for caching REST call results.
 * In this default implementation, caching is only available for {@link WebTarget}-based 
 * invocations that do not include any {@link Entity}'s. Caching must be explicitly requested
 * by invoking the overloaded {@link #executeRequest(String, WebTarget, Class, String)} to
 * specify the cache name.</p>
 * 
 *  <p>Caches can be configured through a properties file named 
 *  [ConnectionClassName]Cache.properties. This properties file can contain the following entries:
 *  <ul>
 *   <li>cacheManager:[cacheSpec]<br/>
 *       Cache specification for the connection-specific cache manager. You can use this
 *       for example to limit the total number of caches, or to clean up complete caches
 *       based on a time-out. By default, caches are kept indefinitely.</li>
 *   <li>default:[cacheSpec]<br/>
 *       Default cache specification for individual caches. If not specified, the default
 *       specification will be 'maximumSize=1000,expireAfterWrite=60s'.</li>
 *   <li>[cacheName]:[cacheSpec]<br/>
 *       Cache specification for individual caches.</li>
 *  </ul>
 *  The format for the cache specification is described here:
 *  <a href="https://google.github.io/guava/releases/19.0/api/docs/com/google/common/cache/CacheBuilderSpec.html">https://google.github.io/guava/releases/19.0/api/docs/com/google/common/cache/CacheBuilderSpec.html</a>
 *  </p>
 * 
 * @author Ruud Senden
 *
 */
@CommonsLog
@ToString(callSuper=true)
public abstract class AbstractRestConnectionWithCache extends AbstractRestConnection {
	private Properties cacheProperties; 
	private LoadingCache<String, Cache<CacheKey, Object>> cacheManager;

	protected AbstractRestConnectionWithCache(RestConnectionConfig<?> config) {
		super(config);
		initCache();
	}
	
	protected void initCache() {
		try {
			cacheProperties = PropertiesLoaderUtils.loadAllProperties(getCachePropertiesResourceName());
		} catch (IOException e) {
			throw new RuntimeException("Error loading cache properties", e);
		} 
		cacheManager = CacheBuilder.from(cacheProperties.getProperty("cacheManager", getDefaultCacheManagerSpec()))
				.build(new CacheLoader<String, Cache<CacheKey, Object>>() {
					@Override
					public Cache<CacheKey, Object> load(String key) throws Exception {
						String cacheSpec = cacheProperties.getProperty(key, cacheProperties.getProperty("default", getDefaultCacheSpec()));
						log.debug("Creating cache "+key+" with spec "+cacheSpec);
						return CacheBuilder.from(cacheSpec).build();
					}
				});
	}
	
	protected String getCachePropertiesResourceName() {
		return this.getClass().getSimpleName()+"Cache.properties";
	}
	
	protected String getDefaultCacheManagerSpec() {
		return "";
	}
	
	protected String getDefaultCacheSpec() {
		return "maximumSize=1000,expireAfterWrite=60s";
	}
	
	@SuppressWarnings("unchecked")
	public <T> T executeRequest(String httpMethod, WebTarget webResource, Class<T> returnType, String cacheName) {
		Cache<CacheKey, Object> cache = cacheManager.getUnchecked(cacheName);
		CacheKey cacheKey = getCacheKey(httpMethod, webResource, returnType);
		T result = (T)cache.getIfPresent(cacheKey);
		if ( result == null ) {
			log.trace("Cache miss: "+webResource.getUri());
			result = super.executeRequest(httpMethod, webResource, returnType);
			cache.put(cacheKey, result);
		} else {
			log.trace("Cache hit: "+webResource.getUri());
		}
		return result;
	}

	private CacheKey getCacheKey(String httpMethod, WebTarget webResource, Class<?> returnType) {
		return new CacheKey(httpMethod, webResource.getUri(), returnType);
	}
	
	@Data
	protected static class CacheKey {
		private final String httpMethod;
		private final URI uri;
		private final Class<?> returnType;
	}

}
