/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

public class JSONConversionServiceFactory {
	private static final ConversionService INSTANCE = getDefaultConversionService();
	private static DateTimeFormatter fmtDateTime = getDefaultDateTimeFormatter();
	
	public static final ConversionService getConversionService() {
		return INSTANCE;
	}

	private static final DateTimeFormatter getDefaultDateTimeFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd[['T'][' ']HH:mm:ss[.SSS][.SS]][Z]");
	}

	public static final ConversionService getDefaultConversionService() {
		DefaultConversionService result = new DefaultConversionService();
		result.addConverter(new DateConverter());
		return result;
	}
	
	public static final void setDateTimePattern(String pattern) {
		fmtDateTime = DateTimeFormatter.ofPattern(pattern);
	}
	
	private static final class DateConverter implements Converter<String,Date> {
		@Override
		public Date convert(String source) {
			return parseDate(source);
		}
		
		private Date parseDate(String source) {
			return Date.from(parseZonedDateTime(source).toInstant());
		}
		
		private ZonedDateTime parseZonedDateTime(String source) {
			TemporalAccessor temporalAccessor = fmtDateTime.parseBest(source, ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
			if (temporalAccessor instanceof ZonedDateTime) {
			    return ((ZonedDateTime) temporalAccessor);
			}
			if (temporalAccessor instanceof LocalDateTime) {
			    return ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault());
			}
			return ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault());
		}
	}
}
