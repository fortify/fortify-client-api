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
package com.fortify.util.rest.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.convert.support.DefaultConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fortify.util.spring.SpringExpressionUtil;

/**
 * This class represents JSON arrays/lists as a standard Java
 * {@link ArrayList}, adding some JSON-related utility methods.
 * 
 * @author Ruud Senden
 *
 */
public class JSONList extends ArrayList<Object> {
	private static final long serialVersionUID = 1L;

	/**
	 * @see ArrayList#ArrayList()
	 */
	public JSONList() {
		super();
	}

	/**
	 * @see ArrayList#ArrayList(Collection)
	 */
	public JSONList(Collection<? extends Object> c) {
		super(c);
	}

	/**
	 * @see ArrayList#ArrayList(int)
	 */
	public JSONList(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Cast the current {@link JSONList} instance
	 * to a {@link List} with the given value
	 * type.
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> asValueType(Class<T> type) {
		return (List<T>)this;
	}
	
	/**
	 * Get a new {@link List} with the given value type
	 * by evaluating the given SpEL expression on each
	 * entry in this {@link JSONList} instance. If the
	 * given expression evaluates to null for any entry,
	 * the entry will be ignored.
	 * @param listValueExpression
	 * @param listValueType
	 * @return
	 */
	public final <R> List<R> getValues(String listValueExpression, Class<R> listValueType) {
		List<R> result = new ArrayList<R>();
		for( Object value : this ){
			CollectionUtils.addIgnoreNull(result, SpringExpressionUtil.evaluateExpression(value, listValueExpression, listValueType) );
		}
		return result;
	}
	
	/**
	 * Filter the current {@link JSONList} instance by evaluating
	 * the given SpEL expression on each entry, and comparing the
	 * evaluation result against the given match value. Each
	 * matched entry will be included in the new {@link JSONList}
	 * instance returned by this method.
	 * 
	 * @param matchExpression
	 * @param matchValue
	 * @return
	 */
	public final JSONList filter(String matchExpression, Object matchValue) {
		JSONList result = new JSONList();
		for ( Object value : this ) {
			if ( isMatching(value, matchExpression, matchValue) ) {
				result.add( value );
			}
		}
		return result;
	}
	
	/**
	 * Find the first entry in this {@link JSONList} instance
	 * for which the given SpEL match expression matches the 
	 * given match value, then return the result of evaluating the
	 * given SpEL return expression on this entry.
	 * 
	 * @param matchExpression
	 * @param matchValue
	 * @param returnExpression
	 * @param returnType
	 * @return
	 */
	public final <M,R> R mapValue(String matchExpression, M matchValue, String returnExpression, Class<R> returnType) {
		Object value = find(matchExpression, matchValue, Object.class);
		return SpringExpressionUtil.evaluateExpression(value, returnExpression, returnType);
	}
	
	/**
	 * Find the first entry in this {@link JSONList} instance
	 * for which the given SpEL match expression matches the 
	 * given match value, and convert that entry to the given
	 * value type.
	 */
	public final <R> R find(String matchExpression, Object matchValue, Class<R> type) {
		if ( matchExpression == null ) { return null; }
		for ( Object value : this ) {
			if ( isMatching(value, matchExpression, matchValue) ) {
				return new DefaultConversionService().convert(value, type);
			}
		}
		return null;
	}
	
	/**
	 * For each entry in this {@link JSONList} instance, evaluate
	 * the given SpEL key and value expressions, and return a new
	 * {@link LinkedHashMap} containing the evaluated keys and values.
	 * @param keyExpression
	 * @param keyType
	 * @param valueExpression
	 * @param valueType
	 * @return
	 */
	public final <K, V> LinkedHashMap<K, V> toMap(String keyExpression, Class<K> keyType, String valueExpression, Class<V> valueType) {
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		for ( Object obj : this ) {
			K key = SpringExpressionUtil.evaluateExpression(obj, keyExpression, keyType);
			V value = SpringExpressionUtil.evaluateExpression(obj, valueExpression, valueType);
			result.put(key, value);
		}
		return result;
	}
	
	/**
	 * Same as {@link #toMap(String, Class, String, Class)}, but returning a
	 * {@link JSONMap} instead.
	 * @param keyExpression
	 * @param keyType
	 * @param valueExpression
	 * @param valueType
	 * @return
	 */
	public final <V> JSONMap toJSONMap(String keyExpression, Class<String> keyType, String valueExpression, Class<V> valueType) {
		return new JSONMap(toMap(keyExpression, keyType, valueExpression, valueType));
	}

	/**
	 * Same as {@link #toMap(String, Class, String, Class)}, but the resulting
	 * {@link Map} will contain the values from this {@link JSONList} instance
	 * as-is, without evaluating some SpEL value expression. 
	 * @param keyExpression
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	public final <K, V> LinkedHashMap<K, V> toMap(String keyExpression, Class<K> keyType, Class<V> valueType) {
		return toMap(keyExpression, keyType, "#this", valueType);
	}
	
	/**
	 * This method will evaluate the given matchExpression on the given JSONObject, then
	 * checks whether the evaluation result matches the given matchValue.
	 * @param obj
	 * @param matchExpression
	 * @param matchValue
	 * @return Boolean indicating whether the result of evaluating the given matchExpression
	 * 		against the given {@link JSONObject} matches the given matchValue.
	 */
	private static boolean isMatching(Object obj, String matchExpression, Object matchValue) {
		if ( matchValue == null ) { return false; }
		Object expressionResult = SpringExpressionUtil.evaluateExpression(obj, matchExpression, matchValue.getClass());
		return expressionResult==matchValue || (matchValue!=null && matchValue.equals(expressionResult));
	}
	
	/**
	 * Return a JSON string representation of this {@link JSONList} instance. Note that
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
	 * Return an indented JSON string representation of this {@link JSONList} instance.
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
}
