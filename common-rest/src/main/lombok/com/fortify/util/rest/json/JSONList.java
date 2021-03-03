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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.convert.support.DefaultConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fortify.util.spring.expression.helper.InternalExpressionHelper;

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
	 * @param c {@link Collection} used to initialize this {@link JSONList}
	 */
	public JSONList(Collection<? extends Object> c) {
		super(c);
	}

	/**
	 * @see ArrayList#ArrayList(int)
	 * @param initialCapacity for this {@link JSONList}
	 */
	public JSONList(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Cast the current {@link JSONList} instance
	 * to a {@link List} with the given value
	 * type.
	 * @param <T>  List value type to cast the current {@link JSONList} instance to
	 * @param type List value type to cast the current {@link JSONList} instance to
	 * @return This {@link JSONList} instance cast to a {@link List} with the given value type
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
	 * @param <R>                 Value type for the list to be returned
	 * @param listValueExpression Expression to be evaluated on each entry in this {@link JSONList}
	 * @param listValueType       Value type for the list to be returned
	 * @return {@link List} containing the results of evaluating the given expression on each of the entries in this {@link JSONList}
	 */
	public final <R> List<R> getValues(String listValueExpression, Class<R> listValueType) {
		List<R> result = new ArrayList<R>();
		for( Object value : this ){
			CollectionUtils.addIgnoreNull(result, InternalExpressionHelper.get().evaluateSimpleExpression(value, listValueExpression, listValueType) );
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
	 * @param matchExpression to be evaluated on each entry in this {@link JSONList}
	 * @param matchValue against which to match the outcome of the expression for each entry
	 * @return Copy of this {@link JSONList}, containing only values for which the given expression result matches the given value
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
	 * @param <M> Type of the value to match against
	 * @param <R> Type of the return value
	 * @param matchExpression to be evaluated on each entry in this {@link JSONList}
	 * @param matchValue against which to match the outcome of the expression for each entry
	 * @param returnExpression to be evaluated as the return value of this method
	 * @param returnType Type of the return value
	 * @return Result of evaluating the given returnExpression on the first entry in this {@link JSONList} 
	 *         for which the given matchValue matches with the outcome of the given matchExpression
	 */
	public final <M,R> R mapValue(String matchExpression, M matchValue, String returnExpression, Class<R> returnType) {
		Object value = find(matchExpression, matchValue, Object.class);
		return InternalExpressionHelper.get().evaluateSimpleExpression(value, returnExpression, returnType);
	}
	
	/**
	 * Find the first entry in this {@link JSONList} instance
	 * for which the given SpEL match expression matches the 
	 * given match value, and convert that entry to the given
	 * value type.
	 * 
	 * @param <R> Type of the return value
	 * @param matchExpression to be evaluated on each entry in this {@link JSONList}
	 * @param matchValue against which to match the outcome of the expression for each entry
	 * @param type Type of the return value
	 * @return First entry in this {@link JSONList} for which the given matchValue matches with 
	 *         the outcome of the given matchExpression, converted to the given type
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
	 * 
	 * @param <K> Key type for the {@link LinkedHashMap} to be returned
	 * @param <V> Value type for the {@link LinkedHashMap} to be returned
	 * @param keyExpression Expression used to calculate the map keys
	 * @param keyType Key type for the {@link LinkedHashMap} to be returned
	 * @param valueExpression Expression used to calculate the map values
	 * @param valueType Value type for the {@link LinkedHashMap} to be returned
	 * @return {@link LinkedHashMap} with keys and values calculated by evaluating the key and value expressions on the entries in this {@link JSONList}
	 */
	public final <K, V> LinkedHashMap<K, V> toMap(String keyExpression, Class<K> keyType, String valueExpression, Class<V> valueType) {
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		for ( Object obj : this ) {
			K key = InternalExpressionHelper.get().evaluateSimpleExpression(obj, keyExpression, keyType);
			V value = InternalExpressionHelper.get().evaluateSimpleExpression(obj, valueExpression, valueType);
			result.put(key, value);
		}
		return result;
	}
	
	/**
	 * Same as {@link #toMap(String, Class, String, Class)}, but returning a
	 * {@link JSONMap} instead.
	 * 
	 * @param <V> Value type for the {@link JSONMap} to be returned
	 * @param keyExpression Expression used to calculate the map keys
	 * @param keyType must be String.class
	 * @param valueExpression Expression used to calculate the map values
	 * @param valueType Value type for the {@link JSONMap} to be returned
	 * @return {@link JSONMap} with keys and values calculated by evaluating the key and value expressions on the entries in this {@link JSONList}
	 */
	public final <V> JSONMap toJSONMap(String keyExpression, Class<String> keyType, String valueExpression, Class<V> valueType) {
		return new JSONMap(toMap(keyExpression, String.class, valueExpression, valueType));
	}

	/**
	 * Same as {@link #toMap(String, Class, String, Class)}, but the resulting
	 * {@link Map} will contain the values from this {@link JSONList} instance
	 * as-is, without evaluating some SpEL value expression. 
	 * 
	 * @param <K> Key type for the {@link LinkedHashMap} to be returned
	 * @param <V> Value type for the {@link LinkedHashMap} to be returned
	 * @param keyExpression Expression used to calculate the map keys
	 * @param keyType Key type for the {@link LinkedHashMap} to be returned
	 * @param valueType Value type for the {@link LinkedHashMap} to be returned
	 * @return {@link LinkedHashMap} with keys calculated by evaluating the keyExpression on the entries in this {@link JSONList},
	 *         and the corresponding {@link JSONList} entries as values
	 */
	public final <K, V> LinkedHashMap<K, V> toMap(String keyExpression, Class<K> keyType, Class<V> valueType) {
		return toMap(keyExpression, keyType, "#this", valueType);
	}
	
	/**
	 * This method will evaluate the given matchExpression on the given JSONObject, then
	 * checks whether the evaluation result matches the given matchValue.
	 * @param obj {@link Object} on which to evaluate the given matchExpression
	 * @param matchExpression to be evaluated on the given object
	 * @param matchValue against which to match the expression result
	 * @return Boolean indicating whether the result of evaluating the given matchExpression
	 * 		against the given {@link Object} matches the given matchValue.
	 */
	private boolean isMatching(Object obj, String matchExpression, Object matchValue) {
		if ( matchValue == null ) { return false; }
		Object expressionResult = InternalExpressionHelper.get().evaluateSimpleExpression(obj, matchExpression, matchValue.getClass());
		return expressionResult==matchValue || (matchValue!=null && matchValue.equals(expressionResult));
	}
	
	public void forEachBlock(int blockSize, Consumer<JSONList> action) {
		int size = size();
		for ( int i = 0 ; i < size ; i+=blockSize ) {
			action.accept(new JSONList(subList(i, Math.min(size, i+blockSize))));
		}
	}
	
	public JSONMap addNewJSONMap() {
		JSONMap jsonMap = new JSONMap();
		add(jsonMap);
		return jsonMap;
	}
	
	/**
	 * Return a JSON string representation of this {@link JSONList} instance. Note that
	 * this is on a best-effort basis; the return value may not always be valid JSON.
	 * 
	 * @return String representation of this {@link JSONList} instance
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
	 * 
	 * @return Indented string representation of this {@link JSONList} instance
	 */
	public String toIndentedString() {
		try {
			return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}

	/**
	 * @see #get(int)
	 * This overloaded method adds support for converting the value to the given type.
	 * 
	 * @param <T> Method return type
	 * @param index from which to retrieve a value from this {@link JSONList} 
	 * @param type Type to which to convert the value at the given index
	 * @return Value located at the given index in this {@link JSONList}, possibly converted to the give type
	 */
	public <T> T get(int index, Class<T> type) {
		return JSONConversionServiceFactory.getConversionService().convert(get(index), type);
	}
	
	public <T> T getOrDefault(int index, T defaultValue, Class<T> type) {
		return index<size() ? get(index, type) : defaultValue;
	}
	
	public Object getOrDefault(int index, Object defaultValue) {
		return getOrDefault(index, defaultValue, Object.class);
	}
	
	public Object getOrNull(int index) {
		return getOrDefault(index, null);
	}
	
	public <T> T getOrNull(int index, Class<T> type) {
		return getOrDefault(index, null, type);
	}
	
	public JSONMap getOrCreateJSONMap(int index) {
		growTo(index+1, JSONMap::new); // If necessary, initialize this list with empty JSONMap instances up to the given index
		return get(index, JSONMap.class);
	}
	
	public void growTo(int newSize) {
		growTo(newSize, ()->null);
	}
	
	public void growTo(int newSize, Supplier<?> eltSupplier) {
		for ( int i=size(); i<newSize; i++) {
			add(eltSupplier.get());
		}
	}
}
