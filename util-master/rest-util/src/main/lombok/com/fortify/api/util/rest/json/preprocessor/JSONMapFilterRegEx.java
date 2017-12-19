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
package com.fortify.api.util.rest.json.preprocessor;

import java.util.Map;
import java.util.regex.Pattern;

import com.fortify.api.util.rest.json.JSONMap;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class JSONMapFilterRegEx extends AbstractJSONMapFilter {
	private static final Function<String, Pattern> STRING_TO_PATTERN_TRANSFORMER = new Function<String, Pattern>() {
		@Override
		public Pattern apply(String input) {
			return Pattern.compile(input);
		}
		
	};
	private final ImmutableMap<String, Pattern> fieldPathPatternsMap;
	
	public JSONMapFilterRegEx(Map<String, Pattern> fieldPathPatternsMap, boolean includeMatching) {
		super(includeMatching);
		this.fieldPathPatternsMap = ImmutableMap.copyOf(fieldPathPatternsMap);
	}
	
	public JSONMapFilterRegEx(String fieldPath, Pattern pattern, boolean includeMatching) {
		this(ImmutableMap.of(fieldPath, pattern), includeMatching);
	}
	
	public JSONMapFilterRegEx(String fieldPath, String regex, boolean includeMatching) {
		this(fieldPath, Pattern.compile(regex), includeMatching);
	}
	
	// We cannot create a constructor for this, as generic type erasure would result in duplicate constructor
	public static final JSONMapFilterRegEx fromFieldPathToPatternStringMap(Map<String, String> fieldPathPatternsMap, boolean includeMatching) {
		return new JSONMapFilterRegEx(Maps.transformValues(fieldPathPatternsMap, STRING_TO_PATTERN_TRANSFORMER), includeMatching);
	}

	@Override
	protected boolean isMatching(JSONMap json) {
		for ( Map.Entry<String, Pattern> entry : fieldPathPatternsMap.entrySet() ) {
			String value = json.getPath(entry.getKey(), String.class);
			if ( !entry.getValue().matcher(value).matches() ) { return false; }
		}
		return true;
	}
}
