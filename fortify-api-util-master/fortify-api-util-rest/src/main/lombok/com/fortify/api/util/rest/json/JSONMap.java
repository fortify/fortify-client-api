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
package com.fortify.api.util.rest.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortify.api.util.rest.ondemand.IOnDemandObject;
import com.fortify.api.util.spring.SpringExpressionUtil;

/**
 * This class represents JSON objects as a standard Java
 * {@link Map}, adding some JSON-related utility methods.
 * 
 * @author Ruud Senden
 *
 */
public class JSONMap extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public JSONMap() {
		super();
	}

	public JSONMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}



	public JSONMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}



	public JSONMap(int initialCapacity) {
		super(initialCapacity);
	}

	public JSONMap(Map<? extends String, ? extends Object> m) {
		super(m);
	}
	
	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		return getOnDemandValue(key, super.getOrDefault(key, defaultValue));
	}

	@Override
	public Object get(Object key) {
		return getOnDemandValue(key, super.get(key));
	}
	
	public <T> T getOrDefault(Object key, T defaultValue, Class<T> type) {
		return getConversionService().convert(getOrDefault(key, defaultValue), type);
	}

	public <T> T get(String key, Class<T> type) {
		return getConversionService().convert(get(key), type);
	}
	
	public <T> T getPath(String path, Class<T> type) {
		return getConversionService().convert(getPath(path), type);
	}
	
	public Object getPath(String path) {
		return SpringExpressionUtil.evaluateExpression(this, path, Object.class);
	}
	
	public JSONMap getOrCreateJSONMap(String key) {
		return getOrDefault(key, new JSONMap(), JSONMap.class);
	}
	
	public JSONList getOrCreateJSONList(String key) {
		return getOrDefault(key, new JSONList(), JSONList.class);
	}

	public void putPaths(Map<String, Object> map, boolean ignoreNullOrEmptyValues) {
		for ( Map.Entry<String, Object> entry : map.entrySet() ) {
			putPath(entry.getKey(), entry.getValue(), ignoreNullOrEmptyValues);
		}
	}
	
	public void putPath(String path, Object value) {
		putPath(path, value, false);
	}
	
	public void putPath(String path, Object value, boolean ignoreNullOrEmptyValues) {
		putPath(Arrays.asList(path.split("\\.")), value, ignoreNullOrEmptyValues);
	}
	
	private void putPath(List<String> path, Object value, boolean ignoreNullOrEmptyValues) {
		if ( !ignoreValue(value, ignoreNullOrEmptyValues) ) {
			if ( path.size()==1 ) {
				put(path.get(0), value);
			} else if ( path.size()>1 ){
				String currentSegment = path.get(0);
				JSONMap intermediate;
				if ( currentSegment.endsWith("[]") ) {
					JSONList list = getOrCreateJSONList(currentSegment.substring(0, currentSegment.length()-2));
					intermediate = new JSONMap();
					list.add(intermediate);
				} else {
					intermediate = getOrCreateJSONMap(currentSegment);
				}
				intermediate.putPath(path.subList(1, path.size()), value, ignoreNullOrEmptyValues);
			}
		}
	}
	
	private Object getOnDemandValue(Object key, Object object) {
		if ( object instanceof IOnDemandObject<?> ) {
			object = ((IOnDemandObject<?>)object).getObject();
			put((String) key, object);
		}
		return object;
	}

	private boolean ignoreValue(Object value, boolean ignoreNullOrEmptyValues) {
		return ignoreNullOrEmptyValues &&
				(value==null
					|| (value instanceof String && StringUtils.isBlank((String)value))
					|| (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>)value))
					|| (value instanceof Object[] && ArrayUtils.isEmpty((Object[])value)) );
	}
	
	public void putPaths(Map<String, Object> map) {
		for ( Map.Entry<String, Object> entry : map.entrySet() ) {
			putPath(entry.getKey(), entry.getValue());
		}
	}
	
	private ConversionService getConversionService() {
		DefaultConversionService result = new DefaultConversionService();
		result.addConverter(new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				try {
					return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(source);
				} catch ( ParseException e ) {
					throw new RuntimeException("Error parsing date format pattern", e);
				}
			}
		});
		return result;
	}
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}
}
