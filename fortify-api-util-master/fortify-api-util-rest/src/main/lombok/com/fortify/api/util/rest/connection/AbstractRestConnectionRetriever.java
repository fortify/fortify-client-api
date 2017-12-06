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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.glassfish.jersey.client.ClientProperties;

import com.google.common.base.Splitter;

import lombok.Getter;
import lombok.Setter;

/**
 * This abstract {@link IRestConnectionRetriever} implementation can be used as a base
 * class for {@link IRestConnectionRetriever} implementations.
 * @author Ruud Senden
 *
 * @param <C>
 */
public abstract class AbstractRestConnectionRetriever<C extends IRestConnection> implements IRestConnectionRetriever<C> {
	private C connection;
	@Getter @Setter private String baseUrl = getDefaultBaseUrl();
	@Getter @Setter private ProxyConfiguration proxy = getDefaultProxy();
	@Getter @Setter private Map<String, Object> connectionProperties = getDefaultConnectionProperties();
	@Getter @Setter private Credentials credentials = getDefaultCredentials();
	
	public final C getConnection() {
		if ( connection == null ) {
			connection = createConnection();
		}
		return connection;
	}

	protected abstract C createConnection();
	
	protected String getDefaultBaseUrl() { return null; }
	protected ProxyConfiguration getDefaultProxy() { return null; }
	protected Map<String, Object> getDefaultConnectionProperties() { return null; }
	protected Credentials getDefaultCredentials() { return null; }
	
	
	public void setUri(String uriWithProperties) {
		String[] parts = uriWithProperties.split(";");
		if ( parts.length > 0 ) {
			URI uri = parseUri(parts[0]);
			setBaseUrl(getBaseUrlFromUri(uri));
			if ( parts.length > 1 ) {
				setConnectionProperties(getConnectionProperties(parts[1]));
			}
			setCredentials(getCredentialsFromUri(uri));
		}
		
	}

	private Map<String, Object> getConnectionProperties(String propertiesString) {
		Map<String, Object> properties = new HashMap<String, Object>();
		if ( StringUtils.isNotBlank(propertiesString) ) {
			properties = Collections.<String,Object>unmodifiableMap(
					Splitter.on(',').withKeyValueSeparator("=").split(propertiesString));
			Map<String, Object> connectionProperties = new HashMap<String, Object>();
			Map<String,String> propertyKeyReplacementMap = getPropertyKeyReplacementMap();
			for ( Map.Entry<String, Object> entry : connectionProperties.entrySet() ) {
				connectionProperties.put(propertyKeyReplacementMap.getOrDefault(entry.getKey(),  entry.getKey()), entry.getValue());
			}
		}
		return properties;
	}

	private URI parseUri(String uriString) {
		try {
			URI uri = new URI(uriString);
			return uri;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Input cannot be parsed as URI: "+uriString);
		}
	}

	protected String getBaseUrlFromUri(URI uri) {
		if ( uri == null ) {
			throw new RuntimeException("URI must be configured");
		}
		try {
			return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null).toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error constructing URI");
		}
	}
	
	// TODO This method probably doesn't work correctly if decoded username or password contains a ':'
	protected Credentials getCredentialsFromUri(URI uri) {
		String userInfo = uri.getUserInfo();
		return StringUtils.isBlank(userInfo) ? null : new UsernamePasswordCredentials(userInfo);
	}

	// TODO add additional property mappings, or provide a more automated way of mapping simple property names to Jersey config properties
	protected Map<String, String> getPropertyKeyReplacementMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("connectTimeout", ClientProperties.CONNECT_TIMEOUT);
		result.put("readTimeout", ClientProperties.READ_TIMEOUT);
		return result;
	}
}
