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
package com.fortify.util.rest.json.preprocessor.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fortify.util.rest.json.JSONMap;

import lombok.Getter;

/**
 * This {@link AbstractJSONMapFilter} implementation allows for filtering
 * {@link JSONMap} instances by evaluating the configured {@link Pattern}
 * instances against the value for the corresponding JSON property path.
 * If any of the {@link Pattern} doesn't match the corresponding property
 * value, the given {@link JSONMap} instance is considered as 'not matching',
 * and as a result (depending on the configured {@link MatchMode}) will
 * be either included or excluded from further processing.
 *  
 * @author Ruud Senden
 *
 */
@Getter
public class JSONMapFilterRegEx extends AbstractJSONMapFilter {
	private final Map<String, Pattern> fieldPathPatternsMap;
	
	public JSONMapFilterRegEx(MatchMode matchMode, Map<String, Pattern> fieldPathPatternsMap) {
		super(matchMode);
		this.fieldPathPatternsMap = Collections.unmodifiableMap(new HashMap<>(fieldPathPatternsMap));
	}
	
	public JSONMapFilterRegEx(MatchMode matchMode, String fieldPath, Pattern pattern) {
		this(matchMode, Collections.singletonMap(fieldPath, pattern));
	}
	
	public JSONMapFilterRegEx(MatchMode matchMode, String fieldPath, String regex) {
		this(matchMode, fieldPath, Pattern.compile(regex));
	}
	
	// We cannot create a constructor for this, as generic type erasure would result in duplicate constructor
	public static final JSONMapFilterRegEx fromFieldPathToPatternStringMap(MatchMode matchMode, Map<String, String> fieldPathPatternsMap) {
		return new JSONMapFilterRegEx(matchMode, fieldPathPatternsMap.entrySet().stream().collect(Collectors.toMap(e->e.getKey(), e->Pattern.compile(e.getValue()))));
	}

	@Override
	protected boolean isMatching(JSONMap json) {
		for ( Map.Entry<String, Pattern> entry : fieldPathPatternsMap.entrySet() ) {
			String value = json.getPath(entry.getKey(), String.class);
			if ( value==null || !entry.getValue().matcher(value).matches() ) { return false; }
		}
		return true;
	}
}
