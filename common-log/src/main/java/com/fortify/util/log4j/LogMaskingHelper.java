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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.LogEvent;

// TODO Check/update for thread safety (use concurrent map?)
/**
 * Applications that want to enable log masking must provide an actual 
 * Log4J plugin class that extends from this class, as follows:
 * 
 * <pre><code>

@Plugin(name = LogMaskingHelper.NAME, category = LogMaskingHelper.CATEGORY)
@ConverterKeys(LogMaskingHelper.NAME)
public final class LogMaskingConverterPlugin extends LogEventPatternConverter {
	public LogMaskingConverterPlugin(String[] options) {
		super(LogMaskingHelper.NAME, LogMaskingHelper.NAME);
	}

	public static final LogMaskingConverterPlugin newInstance(final String[] options) {
		return new LogMaskingConverterPlugin(options);
	}
	
	@Override
	public void format(LogEvent event, StringBuilder outputMessage) {
		LogMaskingHelper.format(event, outputMessage);
	}
}


 * </pre></code>
 * 
 * @author Ruud Senden
 *
 */
public class LogMaskingHelper {
	public static final String NAME = "mm";
	private static final Map<UUID, IMasker> MASKS = new HashMap<>();
	
	public static final PatternGroupMasker maskByPatternGroups() {
		return new PatternGroupMasker();
	}
	

	public static final UUID add(IMasker masker) {
		UUID uuid = UUID.randomUUID();
		MASKS.put(uuid, masker);
		return uuid;
	}
	
	public static final void remove(UUID uuid) {
		MASKS.remove(uuid);
	}

	public static final void format(LogEvent event, StringBuilder outputMessage) {
		String message = event.getMessage().getFormattedMessage();
		for (IMasker masker : MASKS.values()) {
			message = masker.mask(message);
		}
		outputMessage.append(message);
	}
	
	public static interface IMasker {
		public abstract String mask(String input);
	}
	
	public static abstract class AbstractMasker implements IMasker {
		public UUID add() {
			return LogMaskingHelper.add(this);
		}
		
		public void on(Runnable r) {
			UUID uuid = add();
			try {
				r.run();
			} finally {
				LogMaskingHelper.remove(uuid);
			}
		}
		
		/*
		public <R> R on(Callable<R> c) throws Exception {
			UUID uuid = add();
			try {
				return c.call();
			} finally {
				LogMaskingConverter.remove(uuid);
			}
		}
		*/
	}
	
	// TODO Replace duplicate expressions only once, across multiple instances of this class (but only for single invocation);
	//      we need some place to maintain state per invocation of LogMaskingConverter.format()
	public static final class PatternGroupMasker extends AbstractMasker {
		private Pattern[] patterns = null;
		private String replacement = "[hidden]";
		
		public PatternGroupMasker patterns(String... regexes) {
			patterns(Arrays.stream(regexes).map(regex -> Pattern.compile(regex)).toArray(Pattern[]::new));
			return this;
		}
		public PatternGroupMasker patterns(Pattern... patterns) {
			this.patterns = patterns;
			return this;
		}
		public PatternGroupMasker replacement(String replacement) {
			this.replacement = replacement;
			return this;
		}
		
		@Override
		public String mask(String input) {
			for ( Pattern pattern : patterns ) {
				input = replace(input, pattern, replacement);
			}
			return input;
		}
		
		// Based on https://stackoverflow.com/a/53428097
		private static final String replace(String input, Pattern pattern, String replacement) {
			Matcher m = pattern.matcher(input);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				Matcher m2 = pattern.matcher(m.group(0));
				if (m2.find()) {
					StringBuilder stringBuilder = new StringBuilder(m2.group(0));
					String result = stringBuilder.replace(m2.start(1), m2.end(1), replacement).toString();
					m.appendReplacement(sb, result);
				}
			}
			m.appendTail(sb);
			return sb.toString();
		}
	}
}
