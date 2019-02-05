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
package com.fortify.util.rest.json.preprocessor.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.expression.TemplateExpression;

import lombok.Getter;

/**
 * This class allows for logging messages regarding {@link JSONMap} objects
 * that are either included or excluded by {@link AbstractJSONMapFilter}
 * instances.
 * 
 * @author Ruud Senden
 *
 */
public class JSONMapFilterListenerLogger implements IJSONMapFilterListener {
	public static enum LogLevel { TRACE, DEBUG, INFO, WARN, ERROR, FATAL }
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private final LogLevel logLevel;
	private final TemplateExpression logExpressionIncluded;
	private final TemplateExpression logExpressionExcluded;
	
	/**
	 * Constructor with single log expression for both included and excluded objects
	 * @param logLevel
	 * @param logExpression
	 */
	public JSONMapFilterListenerLogger(LogLevel logLevel, String logExpression) {
		this(logLevel, logExpression, logExpression);
	}
	
	/**
	 * Constructor with separate log expressions for included and excluded objects
	 * @param logLevel
	 * @param logExpressionIncluded
	 * @param logExpressionExcluded
	 */
	public JSONMapFilterListenerLogger(LogLevel logLevel, String logExpressionIncluded, String logExpressionExcluded) {
		this(logLevel, 
				SpringExpressionUtil.parseTemplateExpression(logExpressionIncluded), 
				SpringExpressionUtil.parseTemplateExpression(logExpressionExcluded));
	}
	
	/**
	 * Constructor with single log expression for both included and excluded objects
	 * @param logLevel
	 * @param logExpression
	 */
	public JSONMapFilterListenerLogger(LogLevel logLevel, TemplateExpression logExpression) {
		this(logLevel, logExpression, logExpression);
	}

	/**
	 * Constructor with separate log expressions for included and excluded objects
	 * @param logLevel
	 * @param logExpressionIncluded
	 * @param logExpressionExcluded
	 */
	public JSONMapFilterListenerLogger(LogLevel logLevel, TemplateExpression logExpressionIncluded, TemplateExpression logExpressionExcluded) {
		this.logLevel = logLevel;
		this.logExpressionIncluded = logExpressionIncluded;
		this.logExpressionExcluded = logExpressionExcluded;
	}

	@Override
	public void filtered(JSONMap json, boolean isIncluded, AbstractJSONMapFilter filter) {
		log(getLogMessage(json, isIncluded, filter));
	}
	
	protected String getLogMessage(JSONMap json, boolean isIncluded, AbstractJSONMapFilter filter) {
		return isIncluded ? getLogMessage(json, isIncluded, filter, this.logExpressionIncluded) 
				          : getLogMessage(json, isIncluded, filter, this.logExpressionExcluded);
	}
	
	protected String getLogMessage(JSONMap json, boolean isIncluded, AbstractJSONMapFilter filter, TemplateExpression logExpression) {
		return logExpression == null 
				? null 
				: SpringExpressionUtil.evaluateExpression(new LogExpressionInput(json, isIncluded, filter), logExpression, String.class);
	}

	protected void log(String message) {
		if ( message != null ) {
			switch (this.logLevel) {
			case DEBUG:
				if ( log.isDebugEnabled() ) { log.debug(message); }
				break;
			case ERROR:
				if ( log.isErrorEnabled() ) { log.error(message); }
				break;
			case FATAL:
				if ( log.isFatalEnabled() ) { log.fatal(message); }
				break;
			case INFO:
				if ( log.isInfoEnabled() ) { log.info(message); }
				break;
			case TRACE:
				if ( log.isTraceEnabled() ) { log.trace(message); }
				break;
			case WARN:
				if ( log.isWarnEnabled() ) { log.warn(message); }
				break;
			}
		}
	}
	
	@Getter
	public static final class LogExpressionInput {
		private final JSONMap json;
		private final boolean isIncluded;
		private final AbstractJSONMapFilter filter;
		
		LogExpressionInput(JSONMap json, boolean isIncluded, AbstractJSONMapFilter filter) {
			this.json = json;
			this.isIncluded = isIncluded;
			this.filter = filter;
		}
		
		public String getTextObjectIncludedOrExcluded() {
			return isIncluded() ? "included" : "excluded";
		}
		
		public String getTextObjectDoesOrDoesnt() {
			return isIncluded()==isIncludeMatching() ? "does" : "doesn't";
		}
		
		public String getTextObjectIsOrIsnt() {
			return isIncluded()==isIncludeMatching() ? "is" : "isn't";
		}
		
		public boolean isIncludeMatching() {
			return filter.isIncludeMatching();
		}
		
		public String getTextFilterIncludesOrExcludes() {
			return isIncludeMatching() ? "includes" : "excludes";
		}
	}

}
