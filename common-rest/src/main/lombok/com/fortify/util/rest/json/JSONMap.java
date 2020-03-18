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
package com.fortify.util.rest.json;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fortify.util.rest.json.ondemand.IJSONMapOnDemandLoader;
import com.fortify.util.spring.SpringExpressionUtil;

/**
 * This class represents JSON objects as a standard Java
 * {@link Map}, adding some JSON-related utility methods.
 * 
 * @author Ruud Senden
 *
 */
public class JSONMap extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/**
	 * @see LinkedHashMap#LinkedHashMap()
	 */
	public JSONMap() {
		super();
	}

	/**
	 * @see LinkedHashMap#LinkedHashMap(int, float, boolean)
	 */
	public JSONMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	/**
	 * @see LinkedHashMap#LinkedHashMap(int, float)
	 */
	public JSONMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @see LinkedHashMap#LinkedHashMap(int)
	 */
	public JSONMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @see LinkedHashMap#LinkedHashMap(Map)
	 */
	public JSONMap(Map<? extends String, ? extends Object> m) {
		super(m);
	}
	
	/**
	 * Copy only the given fields from the source map into this new instance
	 */
	public JSONMap(Map<? extends String, ? extends Object> source, String field, String... extraFields) {
		super((extraFields==null?0:extraFields.length)+1);
		put(field, source.get(field));
		if ( extraFields != null ) {
			for ( String extraField : extraFields ) {
				put(extraField, source.get(extraField));
			}
		}
	}
	
	/**
	 * @see LinkedHashMap#getOrDefault(Object, Object)
	 * This override adds support for loading on-demand values.
	 */
	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		return getOnDemandValue(key, super.getOrDefault(key, defaultValue));
	}

	/**
	 * @see LinkedHashMap#get(Object)
	 * This override adds support for loading on-demand values.
	 */
	@Override
	public Object get(Object key) {
		return getOnDemandValue(key, super.get(key));
	}
	
	/**
	 * @see #getOrDefault(Object, Object)
	 * This overloaded method adds support for converting the value to the given type.
	 */
	public <T> T getOrDefault(Object key, T defaultValue, Class<T> type) {
		return JSONConversionServiceFactory.getConversionService().convert(getOrDefault(key, defaultValue), type);
	}

	/**
	 * @see #get(Object)
	 * This overloaded method adds support for converting the value to the given type.
	 */
	public <T> T get(Object key, Class<T> type) {
		return JSONConversionServiceFactory.getConversionService().convert(get(key), type);
	}
	
	/**
	 * This method allows for getting the value for the given property
	 * path, and converts this value to the given type.
	 */
	public <T> T getPath(String path, Class<T> type) {
		return JSONConversionServiceFactory.getConversionService().convert(getPath(path), type);
	}
	
	/**
	 * This method allows for getting the value for the given property
	 * path.
	 */
	public Object getPath(String path) {
		return SpringExpressionUtil.evaluateExpression(this, path, Object.class);
	}
	
	/**
	 * Get the {@link JSONMap} instance with the given key, or create and
	 * return a new {@link JSONMap} instance if the given key does not exist.
	 * @param key
	 * @return
	 */
	public JSONMap getOrCreateJSONMap(String key) {
		JSONMap result = get(key, JSONMap.class);
		if ( result == null ) {
			result = new JSONMap();
			put(key, result);
		}
		return result;
	}
	
	/**
	 * Get the {@link JSONList} instance with the given key, or create and
	 * return a new {@link JSONList} instance if the given key does not exist.
	 * @param key
	 * @return
	 */
	public JSONList getOrCreateJSONList(String key) {
		JSONList result = get(key, JSONList.class);
		if ( result == null ) {
			result = new JSONList();
			put(key, result);
		}
		return result;
	}

	/**
	 * Add multiple values to this {@link JSONMap}, each value under the
	 * corresponding property path. See {@link #putPath(String, Object, boolean)}
	 * for more information.
	 * @param map
	 * @param ignoreNullOrEmptyValues
	 */
	public void putPaths(Map<String, Object> map, boolean ignoreNullOrEmptyValues) {
		for ( Map.Entry<String, Object> entry : map.entrySet() ) {
			putPath(entry.getKey(), entry.getValue(), ignoreNullOrEmptyValues);
		}
	}
	
	/**
	 * Add multiple values to this {@link JSONMap}, each value under the
	 * corresponding property path. See {@link #putPath(String, Object)}
	 * for more information.
	 * @param map
	 */
	public void putPaths(Map<String, Object> map) {
		for ( Map.Entry<String, Object> entry : map.entrySet() ) {
			putPath(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Add a value to this {@link JSONMap} under the given property path.
	 * This will create the property path if it does not yet exist.
	 * @param path
	 * @param value
	 */
	public void putPath(String path, Object value) {
		putPath(path, value, false);
	}
	
	/**
	 * Add a value to this {@link JSONMap} under the given property path.
	 * This will create the property path if it does not yet exist. Depending
	 * on the ignoreNullOrEmptyValues parameter, values that are null, blank 
	 * strings, or empty collections or arrays, will be ignored.
	 * @param path
	 * @param value
	 */
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
		if ( object instanceof IJSONMapOnDemandLoader ) {
			object = ((IJSONMapOnDemandLoader)object).getAndStoreOnDemand((String)key, this);
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
	
	/**
	 * Return a JSON string representation of this {@link JSONMap} instance. Note that
	 * this is on a best-effort basis; the return value may not always be valid JSON.
	 */
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}
	
	/**
	 * Return an indented JSON string representation of this {@link JSONMap} instance.
	 * Note that this is on a best-effort basis; the return value may not always be valid 
	 * JSON.
	 */
	public String toIndentedString() {
		try {
			return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}

	public boolean containsAllKeys(String... requiredProperties) {
		for ( String requiredProperty : requiredProperties ) {
			if ( !containsKey(requiredProperty) ) { return false; }
		}
		return true;
	}
}
