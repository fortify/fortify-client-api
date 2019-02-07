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
package com.fortify.util.log4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

@Plugin(name = LogMaskingConverter.NAME, category = PatternConverter.CATEGORY)
@ConverterKeys(LogMaskingConverter.NAME)
public final class LogMaskingConverter extends LogEventPatternConverter {
	public static final String NAME = "mm";
	private static final Map<Pattern, String> MASKS = new HashMap<>();

	public LogMaskingConverter(String[] options) {
		super(NAME, NAME);
	}

	public static final LogMaskingConverter newInstance(final String[] options) {
		return new LogMaskingConverter(options);
	}
	
	public static final void mask(String pattern, String replacement) {
		mask(Pattern.compile(pattern), replacement);
	}
	
	public static final void mask(Pattern pattern, String replacement) {
		MASKS.put(pattern, replacement);
	}
	
	public static final void mask(Map<Pattern, String> masks) {
		MASKS.putAll(masks);
	}

	@Override
	public final void format(LogEvent event, StringBuilder outputMessage) {
		String message = event.getMessage().getFormattedMessage();
		for ( Map.Entry<Pattern, String> entry : MASKS.entrySet() ) {
			message = entry.getKey().matcher(message).replaceAll(entry.getValue());
		}
		outputMessage.append(message);
	}

	public static void removeMask(Pattern pattern) {
		MASKS.remove(pattern);
		
	}
}
