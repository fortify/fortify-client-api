/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC, a Micro Focus company
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
package com.fortify.util.rest.json.preprocessor;

import java.util.Date;

import com.fortify.util.rest.json.JSONMap;

/**
 * This {@link JSONMapFilterSpEL} implementation allows for filtering {@link JSONMap}
 * instances by comparing the value for the configured JSON property path against a 
 * given {@link Date}, using the configured {@link DateComparisonOperator}.
 * 
 * @author Ruud Senden
 *
 */
public class JSONMapFilterCompareDate extends JSONMapFilterSpEL {
	public static enum DateComparisonOperator {
		lt, gt, le, ge, eq, ne
	}
	
	public JSONMapFilterCompareDate(MatchMode matchMode, String fieldPath, DateComparisonOperator operator, Date compareDate) {
		super(matchMode, getDateExpression(fieldPath, operator, compareDate));
	}

	private static String getDateExpression(String fieldPath, DateComparisonOperator operator, Date compareDate) {
		String expression = "getPath('"+fieldPath+"', T(java.util.Date))?.getTime() "+operator.name()+" "+compareDate.getTime()+"L";
		return expression;
	}
	
	
}
