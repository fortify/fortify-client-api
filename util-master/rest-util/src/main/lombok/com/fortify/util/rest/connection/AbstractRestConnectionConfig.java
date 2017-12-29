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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.ClientProperties;

import com.google.common.base.Splitter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This abstract base class allows for configuring {@link AbstractRestConnection} instances
 * by setting properties like base URL, proxy configuration and additional connection properties.
 * 
 * @author Ruud Senden
 *
 * @param <T>
 */
@Data @EqualsAndHashCode(callSuper=false)
public abstract class AbstractRestConnectionConfig<T extends AbstractRestConnectionConfig<T>> {
	private URI baseUrl;
	private ProxyConfig proxy = new ProxyConfig();
	private Map<String, Object> connectionProperties;
	private String connectionId = null;
	
	public T baseUrl(String baseUrl) {
		setBaseUrl(baseUrl);
		return getThis();
	}
	
	public T proxy(ProxyConfig proxy) {
		setProxy(proxy);
		return getThis();
	}
	
	public T connectionProperties(String connectionProperties) {
		setConnectionProperties(connectionProperties);
		return getThis();
	}
	
	public T connectionProperties(Map<String, Object> connectionProperties) {
		setConnectionProperties(connectionProperties);
		return getThis();
	}
	
	/**
	 * @see #setMultiJVMSerializationId(String)
	 * @param connectionId
	 * @return
	 */
	public T enableSerializationMultiJVM(String connectionId) {
		setMultiJVMSerializationId(connectionId);
		return getThis();
	}
	
	/**
	 * @see #setSingleJVMSerializationSupported(boolean)
	 * @return
	 */
	public T enableSerializationSingleJVM() {
		setSingleJVMSerializationSupported(true);
		return getThis();
	}
	
	/**
	 * Enable support for serializing and de-serializing the connection instance
	 * across multiple JVM's. For each JVM, the same connection id must be used,
	 * and the connection instance must be initialized before de-serializing any
	 * objects that reference this connection instance.
	 *  
	 * @param connectionId
	 */
	public void setMultiJVMSerializationId(String connectionId) {
		setConnectionId(connectionId);
	}
	
	/**
	 * Enable support for serializing and de-serializing the connection instance
	 * within a single JVM. This simply generates a random and unique connection id.
	 *  
	 * @param supported
	 */
	public void setSingleJVMSerializationSupported(boolean supported) {
		if ( supported == true ) {
			if ( StringUtils.isBlank(getConnectionId()) ) {
				setConnectionId(UUID.randomUUID().toString());
			}
		} else {
			this.connectionId = null;
		}
	}
	
	protected void setConnectionId(String connectionId) {
		if ( StringUtils.isBlank(connectionId) ) {
			throw new IllegalArgumentException("Connection id cannot be blank");
		}
		this.connectionId = connectionId;
	}
	
	public void setConnectionProperties(Map<String, Object> connectionProperties) {
		this.connectionProperties = connectionProperties;
	}
	
	public void setConnectionProperties(String propertiesString) {
		if ( StringUtils.isNotBlank(propertiesString) ) {
			Map<String, Object> orgProperties = Collections.<String,Object>unmodifiableMap(
					Splitter.on(',').withKeyValueSeparator("=").split(propertiesString));
			Map<String, Object> connectionProperties = new HashMap<String, Object>();
			Map<String,String> propertyKeyReplacementMap = getPropertyKeyReplacementMap();
			for ( Map.Entry<String, Object> entry : orgProperties.entrySet() ) {
				connectionProperties.put(propertyKeyReplacementMap.getOrDefault(entry.getKey(),  entry.getKey()), entry.getValue());
			}
		}
		setConnectionProperties(connectionProperties);
	}
	
	@SuppressWarnings("unchecked")
	protected T getThis() {
		return (T)this;
	}
	
	public void setBaseUrl(String baseUrl) {
		parseBaseUrl(baseUrl);
	}
	
	protected void setBaseUrl(URI baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	protected void parseBaseUrl(String uriWithProperties) {
		String[] parts = uriWithProperties.split(";");
		if ( parts.length > 0 ) {
			parseUriString(parts[0]);
			if ( parts.length > 1 ) {
				parseUriProperties(parts[1]);
			}
		}
	}

	protected void parseUriProperties(String propertiesFromUri) {
		setConnectionProperties(propertiesFromUri);
	}

	protected void parseUriString(String uriString) {
		try {
			parseUri(new URI(uriString));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Input cannot be parsed as URI: "+uriString);
		}
	}
	
	protected void parseUri(URI uri) {
		if ( StringUtils.isNotBlank(uri.getUserInfo()) ) {
			parseUriUserInfo(uri.getUserInfo());
		}
		setBaseUrl(removeUserInfo(uri));
	}

	protected void parseUriUserInfo(String userInfo) {};

	protected URI removeUserInfo(URI uri) {
		if ( uri == null ) {
			throw new RuntimeException("URI must be configured");
		}
		try {
			return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), null);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error constructing URI");
		}
	}

	// TODO add additional property mappings, or provide a more automated way of mapping simple property names to Jersey config properties
	protected Map<String, String> getPropertyKeyReplacementMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("connectTimeout", ClientProperties.CONNECT_TIMEOUT);
		result.put("readTimeout", ClientProperties.READ_TIMEOUT);
		return result;
	}
}