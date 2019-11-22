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
package com.fortify.util.rest.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.apache.connector.ApacheHttpClientBuilderConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fortify.util.log4j.LogMaskingHelper;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;

/**
 * <p>Base class for low-level functionality for accessing REST API's. Concrete implementations
 * can override various methods to fine-tune behavior related to calling REST API's. Instances
 * of this class are configured using a corresponding {@link AbstractRestConnectionConfig} instance.
 * Concrete implementations usually provide a static builder() method that returns a subclass
 * of {@link AbstractRestConnectionConfig} which also implements the {@link IRestConnectionBuilder}
 * interface, allowing clients to easily build a concrete {@link AbstractRestConnection} implementation.</p>
 * 
 * <p>This base class allows for caching REST results by calling the {@link #executeRequest(String, WebTarget, Class, String)}
 * method, specifying the cache name to use as the last method parameter. Caches can be configured 
 * through a properties file named [ConnectionClassName]Cache.properties. This properties file can 
 * contain the following entries:</p>
 *  <ul>
 *   <li>cacheManager:[cacheSpec]<br>
 *       Cache specification for the connection-specific cache manager. You can use this
 *       for example to limit the total number of caches, or to clean up complete caches
 *       based on a time-out. By default, caches are kept indefinitely.</li>
 *   <li>default:[cacheSpec]<br>
 *       Default cache specification for individual caches. If not specified, the default
 *       specification will be 'maximumSize=1000,expireAfterWrite=60s'.</li>
 *   <li>[cacheName]:[cacheSpec]<br>
 *       Cache specification for individual caches.</li>
 *  </ul>
 *  <p>Caching can be globally disabled on a connection using the 
 *  {@link AbstractRestConnectionConfig#useCache(boolean)} method.
 *  The format for the cache specification is described here:
 *  <a href="https://google.github.io/guava/releases/19.0/api/docs/com/google/common/cache/CacheBuilderSpec.html">https://google.github.io/guava/releases/19.0/api/docs/com/google/common/cache/CacheBuilderSpec.html</a>
 *  </p>
 * 
 * <p>This base class allows for serialization of instances using a customized serialization mechanism.
 * This customized serialization mechanism must be enabled using the 
 * {@link AbstractRestConnectionConfig#enableSerializationSingleJVM()} or 
 * {@link AbstractRestConnectionConfig#enableSerializationMultiJVM(String)} methods. Upon serialization,
 * only the connection id is serialized. Upon deserialization, this connection id is looked up in
 * the static connection instances map.</p>
 * 
 * <p>Implementations may choose to also provide more high-level functionality. Usually such implementations
 * provide an api() method that provides access to these more high-level functionalities, keeping the
 * actual connection implementation lean and clean.</p>
 */
@CommonsLog
@ToString
public abstract class AbstractRestConnection implements IRestConnection {
	private static final Pattern EXPR_AUTH_HEADER = Pattern.compile("Authorization: (.*)", Pattern.CASE_INSENSITIVE);
	private static final Set<String> DEFAULT_HTTP_METHODS_TO_PRE_AUTHENTICATE = new HashSet<String>(Arrays.asList("POST","PUT","PATCH"));
	
	private Properties cacheProperties; 
	private LoadingCache<String, Cache<CacheKey, Object>> cacheManager;
	private final Map<Class<?>, Object> apis = new HashMap<>();
	
	@Getter private final URI baseUrl;
	@Getter private final boolean useCache;
	private final ProxyConfig proxy;
	private final Map<String, Object> connectionProperties;
	@Getter private final String connectionId;
	private final CredentialsProvider credentialsProvider;
	private Client client;
	
	protected AbstractRestConnection(AbstractRestConnectionConfig<?> config) {
		initCache();
		this.baseUrl = config.getBaseUrl();
		this.useCache = config.isUseCache();
		this.proxy = config.getProxy();
		this.connectionProperties = config.getConnectionProperties();
		this.connectionId = this.getClass().getName()+config.getConnectionId();
		this.credentialsProvider = createCredentialsProvider(config);
		Connections.register(this);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T api(Class<T> type) {
		Object result = apis.get(type);
		if ( result == null ) {
			try {
				result = ConstructorUtils.invokeConstructor(type, new Object[]{this});
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				throw new IllegalArgumentException("Cannot load API class "+type.getName(), e);
			}
			apis.put(type, result);
		}
		return (T) result;
	}

	/**
	 * Execute a request for the given method using the given web resource.
	 * @param httpMethod The HTTP method to be used, as specified by one of the constants
	 *                   in {@link HttpMethod}
	 * @param webResource The web resource used to execute the request. Usually this web resource 
	 * 					  is created using {@link #getBaseResource()}.path(...)...
	 * @param returnType The return type for the data returned by the request.
	 * @return The result of executing the HTTP request.
	 */
	public <T> T executeRequest(String httpMethod, WebTarget webResource, Class<T> returnType) {
		return executeRequest(httpMethod, webResource, null, returnType);
	}
	
	/**
	 * Execute a request for the given method using the given web resource and entity.
	 * @param httpMethod The HTTP method to be used, as specified by one of the constants
	 *                   in {@link HttpMethod}
	 * @param webResource The web resource used to execute the request. Usually this web resource 
	 * 					  is created using {@link #getBaseResource()}.path(...)...
	 * @param returnType The return type for the data returned by the request.
	 * @return The result of executing the HTTP request.
	 */
	public <T> T executeRequest(String httpMethod, WebTarget webResource, Entity<?> entity, Class<T> returnType) {
		return executeRequest(httpMethod, updateWebTarget(webResource).request(), entity, returnType);
	}
	
	/**
	 * Execute a request for the given method using the given builder.
	 * @param httpMethod The HTTP method to be used, as specified by one of the constants
	 *                   in {@link HttpMethod}
	 * @param builder	 The builder used to execute the request. Usually this builder is created
	 *                   using {@link #getBaseResource()}.path(...).entity(...).accept(...)...
	 * @param returnType The return type for the data returned by the request.
	 * @return The result of executing the HTTP request.
	 */
	public <T> T executeRequest(String httpMethod, Builder builder, Class<T> returnType) {
		return executeRequest(httpMethod, builder, null, returnType);
	}
	
	/**
	 * Execute a request for the given method using the given builder.
	 * @param httpMethod The HTTP method to be used, as specified by one of the constants
	 *                   in {@link HttpMethod}
	 * @param builder	 The builder used to execute the request. Usually this builder is created
	 *                   using {@link #getBaseResource()}.path(...).builder(...)...
	 * @param entity     The entity to be submitted, may be null
	 * @param returnType The return type for the data returned by the request.
	 * @return The result of executing the HTTP request.
	 */
	public <T> T executeRequest(String httpMethod, Builder builder, Entity<?> entity, Class<T> returnType) {
		Response response = null;
		UUID uuidAuthHeader = null;
		try {
			uuidAuthHeader = LogMaskingHelper.maskByPatternGroups().patterns(EXPR_AUTH_HEADER).add();
			initializeConnection(httpMethod);
			builder = updateBuilder(builder);
			response = builder.build(httpMethod, entity).invoke();
			return checkResponseAndGetOutput(httpMethod, builder, response, returnType);
		} catch ( ClientErrorException e ) {
			throw new RuntimeException("Error accessing remote system:\n"+e.getMessage(), e);
		} finally {
			LogMaskingHelper.remove(uuidAuthHeader);
			if ( response != null && (returnType==null || !Response.class.isAssignableFrom(returnType)) ) { response.close(); }
		}
	}
	
	public void executeRequestAndSaveResponse(String httpMethod, WebTarget webTarget, Path outputPath, CopyOption... copyOptions) {
		Response response = executeRequest(httpMethod, webTarget, Response.class);
		try {
			InputStream inputStream = (InputStream)response.getEntity();
			Files.copy(inputStream, outputPath, copyOptions);
		} catch (IOException e) {
			throw new RuntimeException("Error writing response to file", e);
		} finally {
			if ( response != null ) {
				response.close();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T executeRequest(String httpMethod, WebTarget webResource, Class<T> returnType, String cacheName) {
		T result;
		if ( cacheName == null || !useCache ) {
			log.trace("No cache name specified or caching disabled, not using cache: "+webResource.getUri());
			result = executeRequest(httpMethod, webResource, returnType);
		} else {
			Cache<CacheKey, Object> cache = cacheManager.getUnchecked(cacheName);
			CacheKey cacheKey = getCacheKey(httpMethod, webResource, returnType);
			result = (T)cache.getIfPresent(cacheKey);
			if ( result == null ) {
				log.trace("Cache miss: "+webResource.getUri());
				result = executeRequest(httpMethod, webResource, returnType);
				cache.put(cacheKey, result);
			} else {
				log.trace("Cache hit: "+webResource.getUri());
			}
		}
		return result;
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
		return "maximumSize=1000,expireAfterWrite=15m";
	}
	
	protected CacheKey getCacheKey(String httpMethod, WebTarget webResource, Class<?> returnType) {
		return new CacheKey(httpMethod, webResource.getUri(), returnType);
	}	

	/**
	 * Authenticating with the server may require several round trips,
	 * especially when using NTLM authentication. For HTTP methods that
	 * may contain a (possibly large) payload, we do not want this 
	 * payload to be included in the authentication requests for the
	 * following reasons:
	 * <ul>
	 *  <li>Re-sending a large payload multiple times can have a negative 
	 *      impact on performance
	 *  <li>If the client sends a non-repeatable entity, any round trip 
	 *      after the initial attempt (which may fail due to authentication
	 *      being required) will trigger an exception
	 * </ul>
	 * 
	 * As such, if the {@link #doInitializeAuthenticatedConnection()} method
	 * returns true, we call {@link #initializeAuthenticatedConnection()} to 
	 * pre-authenticate the connection for the HTTP methods returned by 
	 * {@link #getHttpMethodsToPreAuthenticate()}.
	 * 
	 * @param httpMethod for which to initialize the connection
	 */
	protected void initializeConnection(String httpMethod) {
		if ( doInitializeAuthenticatedConnection() && getHttpMethodsToPreAuthenticate().contains(httpMethod.toUpperCase()) ) {
			initializeAuthenticatedConnection();
		}
	}
	
	/**
	 * Indicate whether the {@link #initializeAuthenticatedConnection()} method
	 * should be called for the HTTP methods returned by {@link #getHttpMethodsToPreAuthenticate()}.
	 * By default, this method returns false if preemptive basic authentication
	 * is enabled (assuming the connection will be automatically authenticated
	 * upon the actual request). If preemptive basic authentication is disabled,
	 * this method will return true.
	 * @return Flag indicating whether {@link #initializeAuthenticatedConnection()}
	 *         should be called if necessary
	 */
	protected boolean doInitializeAuthenticatedConnection() {
		return !doPreemptiveBasicAuthentication(); // TODO Add check whether credentials are available
	}

	/**
	 * <p>Subclasses should override this method to set up an authenticated connection.
	 * Implementations would usually execute some cheap request (for example a GET or
	 * HEAD request on a resource that only returns a small payload), resulting
	 * in authentication to be performed if needed.</p>
	 * 
	 * <p>Note that the request to authenticate cannot use any of the HTTP methods
	 * returned by {@link #getHttpMethodsToPreAuthenticate()} as this would result 
	 * in an endless loop.</p>
	 * 
	 * <p>Also note that if a subclass does not override the {@link #doInitializeAuthenticatedConnection()}
	 * and/or {@link #doPreemptiveBasicAuthentication()} methods, this method will
	 * never be called (since preemptive basic authentication is enabled by 
	 * default, {@link #doInitializeAuthenticatedConnection()} will always return
	 * false). As such, we provide a default empty implementation instead of
	 * making this an abstract method.</p>
	 */
	protected void initializeAuthenticatedConnection() {}
	
	/**
	 * Check the response code. If successful, return the entity with the given return type,
	 * otherwise throw an exception.
	 * @param response
	 * @param returnType
	 * @return The entity from the given {@link ClientResponse} if available
	 */
	protected <T> T checkResponseAndGetOutput(String httpMethod, Builder builder, Response response, Class<T> returnType) {
		StatusType status = response.getStatusInfo();
		if ( status != null && status.getFamily() == Family.SUCCESSFUL ) {
			return getSuccessfulResponse(response, returnType, status);
		} else {
			throw getUnsuccesfulResponseException(response);
		}
	}

	/**
	 * Get the return value for a successful response.
	 * @param response
	 * @param returnType
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getSuccessfulResponse(Response response, Class<T> returnType, StatusType status) {
		if ( returnType!=null && returnType.isAssignableFrom(response.getClass()) ) {
			return (T)response;
		} else {
			try {
				if ( status.getStatusCode() == Status.NO_CONTENT.getStatusCode() ) {
					return null;
				} else if (returnType == null || returnType.isAssignableFrom(Void.class)) {
					return null; // TODO do we need to read the entity if there is any?
				} else {
					return response.readEntity(returnType);
				}
			} finally {
				response.close();
			}
		}
	}
	
	protected RuntimeException getUnsuccesfulResponseException(Response response) {
		String reasonPhrase = getReasonPhrase(response);
		String msg = "Error accessing remote system "+baseUrl+": "+reasonPhrase;
		String longMsg = msg+", response contents: \n"+response.readEntity(String.class);
		// By adding a new exception as the cause, we make sure that the response
		// contents will be logged whenever this RuntimeException is logged.
		RuntimeException re = new RuntimeException(msg, new Exception(longMsg));
		return re;
	}

	/** 
	 * Get the reason phrase from the response or status info.
	 * Jersey uses hard-coded reason phrases, so we try to read the 
	 * reason phrase(s) directly from the headers first.
	 * @param response to get the reason phrase from
	 * @return Reason phrase from the response
	 */
	private String getReasonPhrase(Response response) {
		List<String> reasonPhrases = response.getStringHeaders().get("Reason-Phrase");
		StatusType status = response.getStatusInfo();
		String reasonPhrase;
		if ( reasonPhrases!=null&&reasonPhrases.size()>0 ) {
			reasonPhrase = reasonPhrases.toString();
		} else if ( status != null ) {
			reasonPhrase = status.getReasonPhrase();
		} else {
			reasonPhrase = response.toString();
		}
		return reasonPhrase;
	}
	
	/**
	 * Get a {@link WebTarget} object for the configured REST base URL.
	 * @return A {@link WebTarget} instance for the configured REST base URL.
	 */
	public final WebTarget getBaseResource() {
		return getClient().target(baseUrl);
	}
	
	/**
	 * Get a {@link WebTarget} object for the given uriString. If the given
	 * uriString is an absolute URI, it will be used as-is. If the given 
	 * uriString is relative, it will be appended to the configured REST
	 * base URL. See {@link URI#resolve(String)} for more details. 
	 * 
	 * @param uriString Absolute or relative URI for which to get a {@link WebTarget} instance.
	 * @return A {@link WebTarget} instance for the given URI.
	 */
	public final WebTarget getResource(String uriString) {
		// TODO clean this up
		return getClient().target(URI.create(baseUrl.toString()+"/").resolve(StringUtils.removeStart(uriString,"/")));
	}

	/**
	 * Get the cached client for executing requests. If the client
	 * has not been previously cached, this method will call 
	 * {@link #createClient()} to create a new client and then
	 * cache it.
	 * @return Cache {@link Client} instance if available, new {@link Client} instance otherwise
	 */
	public final Client getClient() {
		if ( client == null ) {
			client = createClient();
		}
		return client;
	}
	
	/**
	 * This method should be called once the connection is no longer needed,
	 * to perform various clean-up activities.
	 */
	public void close() {
		Connections.unRegister(this);
		getClient().close();
		cacheManager.invalidateAll();
		apis.clear();
	}
	
	public String getBaseUrlStringWithoutTrailingSlash() {
		return StringUtils.removeEnd(getBaseUrl().toASCIIString(), "/");
	}
	
	public String getBaseUrlStringWithTrailingSlash() {
		return getBaseUrlStringWithoutTrailingSlash()+"/";
	}

	/**
	 * <p>Create a new client for executing requests. The default
	 * implementation creates a client based on Apache HttpClient
	 * 4.x by calling {@link #createClientConfig()} to get the
	 * client configuration, and then calls 
	 * {@link ClientBuilder#newClient(javax.ws.rs.core.Configuration)}
	 * to generate a new Client instance.
	 * Some of the Jersey-specific settings like the CookieStore
	 * and whether to use preemptive basic authentication can
	 * be specified by overriding the corresponding methods.</p> 
	 * 
	 * <p>Subclasses can override this method to create a different 
	 * type of client. Note however that other classes may
	 * depend on the use of Apache HttpClient.</p>
	 * 
	 * <p>Subclasses that would would like to customize Apache
	 * HttpClient behavior should override {@link #createClientConfig()}.</p>
	 * 
	 * @return New {@link Client} instance
	 */
	protected Client createClient() {
		ClientConfig config = createClientConfig();
		Client client = ClientBuilder.newClient(config);
		return client;
	}
	
	protected ClientConfig createClientConfig() {
		ClientConfig clientConfig = new ClientConfig();
		if ( proxy != null && StringUtils.isNotBlank(proxy.getUrl()) ) {
			clientConfig.property(ClientProperties.PROXY_URI, proxy.getUrl());
			clientConfig.property(ClientProperties.PROXY_USERNAME, proxy.getUserName());
			clientConfig.property(ClientProperties.PROXY_PASSWORD, proxy.getPassword());
		}
		clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
		clientConfig.property(ApacheClientProperties.CREDENTIALS_PROVIDER, credentialsProvider);
		clientConfig.property(ApacheClientProperties.PREEMPTIVE_BASIC_AUTHENTICATION, doPreemptiveBasicAuthentication());
		if ( connectionProperties != null ) {
			for ( Map.Entry<String,Object> property : connectionProperties.entrySet() ) {
				clientConfig.property(property.getKey(), property.getValue());
			}
		}
		clientConfig.connectorProvider(new ApacheConnectorProvider());
		clientConfig.register(new HttpClientBuilderConfigurator());
		clientConfig.register(JacksonFeature.class);
		clientConfig.register(MultiPartFeature.class);
		clientConfig.register(new LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, 10000));
		return clientConfig;
	}
	
	/**
	 * This {@link ApacheHttpClientBuilderConfigurator} implementation simply calls
	 * {@link AbstractRestConnection#updateHttpClientBuilder(HttpClientBuilder) to
	 * update the given {@link HttpClientBuilder}.
	 */
	private final class HttpClientBuilderConfigurator implements ApacheHttpClientBuilderConfigurator {
		@Override
		public HttpClientBuilder configure(HttpClientBuilder httpClientBuilder) {
			updateHttpClientBuilder(httpClientBuilder);
			return httpClientBuilder;
		}
	}
	
	/**
	 * Create a {@link CredentialsProvider} for the given configuration.
	 * @param config
	 * @return
	 */
	protected CredentialsProvider createCredentialsProvider(AbstractRestConnectionConfig<?> config) {
		CredentialsProvider result = null;
		if ( config instanceof ICredentialsProvider ) {
			result = createCredentialsProvider();
			result.setCredentials(AuthScope.ANY, ((ICredentialsProvider)config).getCredentials());
		}
		return result;
	}
	
	/**
	 * Create the {@link CredentialsProvider} to use for requests.
	 * This default implementation returns a {@link BasicCredentialsProvider}
	 * instance.
	 * @return
	 */
	protected CredentialsProvider createCredentialsProvider() {
		return new BasicCredentialsProvider();
	}
	
	protected ServiceUnavailableRetryStrategy getServiceUnavailableRetryStrategy() {
		return null;
	}

	/**
	 * Create the {@link CookieStore} to use between requests.
	 * This default implementation returns a {@link BasicCookieStore}
	 * instance. Subclasses can override this method to return
	 * an alternative {@link CookieStore} implementation, or
	 * null if cookies should not be maintained between requests.
	 * @return
	 */
	protected CookieStore createCookieStore() {
		return new BasicCookieStore();
	}
	
	/**
	 * Indicate whether preemptive basic authentication should be used.
	 * If this method returns true, the Basic Authentication header will
	 * be sent on all requests, preventing multiple round trips for
	 * performing authentication. This default implementation returns 
	 * 
	 * @return Flag specifying whether preemptive Basic Authentication should be performed
	 */
	protected boolean doPreemptiveBasicAuthentication() {
		return false;
	}
	
	/**
	 * Subclasses can override this method to return the set of HTTP methods
	 * for which an authenticated connection should be initialized before
	 * executing the actual request. All returned HTTP methods should be
	 * in upper case. By default, this method returns POST, PUT and PATCH.
	 * @return {@link Set} of HTTP Methods that should be pre-authenticated
	 */
	protected Set<String> getHttpMethodsToPreAuthenticate() {
		return DEFAULT_HTTP_METHODS_TO_PRE_AUTHENTICATE;
	}
	
	/**
	 * Update the given {@link WebTarget} before executing the request.
	 * By default this method simply returns the given target. Subclasses
	 * can override this method to add information to the target that
	 * is required for every request, for example to add query parameters
	 * that need to be included in every request.
	 * @param webTarget to be updated
	 * @return updated webTarget
	 */
	protected WebTarget updateWebTarget(WebTarget webTarget) {
		return webTarget;
	}
	
	
	/**
	 * Update the given {@link Builder} before executing the request.
	 * By default this method simply returns the given builder. Subclasses
	 * can override this method to add information to the builder that
	 * is required for every request, for example to add headers that 
	 * need to be included in every request.
	 * @param builder to be updated
	 * @return updated builder
	 */
	protected Builder updateBuilder(Builder builder) {
		return builder;
	}
	
	/**
	 * URL-encode the given input String using UTF-8 encoding.
	 * @param input
	 * @return The URL-encoded input string
	 */
	public static final String urlEncode(String input) {
		try {
			return URLEncoder.encode(input, CharEncoding.UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to encode value "+input, e);
		}
	}
	
	/** 
	 * This method sets additional {@link HttpClientBuilder} properties.
	 * Subclasses may override this method, but should call 
	 * super.{@link #updateBuilder(Builder)} 
	 * @param httpClientBuilder
	 */
	protected void updateHttpClientBuilder(HttpClientBuilder httpClientBuilder) {
		httpClientBuilder.setServiceUnavailableRetryStrategy(getServiceUnavailableRetryStrategy());
	}

	@Data
	protected static class CacheKey {
		private final String httpMethod;
		private final URI uri;
		private final Class<?> returnType;
	}

	protected static class JacksonFeature implements Feature {

	    @SuppressWarnings("serial")
		private static final ObjectMapper mapper =
	        new ObjectMapper(){{
	        	final SimpleModule module = new SimpleModule("treemaps");
	            module.addAbstractTypeMapping(Map.class, JSONMap.class);
	            module.addAbstractTypeMapping(List.class, JSONList.class);
	            registerModule(module);
	        }};
	 
	    private static final JacksonJaxbJsonProvider provider =
	        new JacksonJaxbJsonProvider(){{
	            setMapper(mapper);
	        }};
	 
	    public boolean configure(FeatureContext context) {
	        context.register(provider);
	        return true;
	    }
	}
}
