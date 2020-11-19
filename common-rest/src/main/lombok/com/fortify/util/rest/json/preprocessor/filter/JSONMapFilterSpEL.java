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

import org.springframework.expression.Expression;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.expression.helper.InternalExpressionHelper;

import lombok.Getter;

/**
 * This {@link AbstractJSONMapFilter} implementation evaluates the
 * configured SpEL expression against a given {@link JSONMap} instance,
 * and either includes or excludes (based on the configured {@link MatchMode})
 * this {@link JSONMap} instance from further processing based on the
 * expression evaluation result.
 * 
 * @author Ruud Senden
 *
 */
@Getter
public class JSONMapFilterSpEL extends AbstractJSONMapFilter {
	private final Expression expression;
	
	public JSONMapFilterSpEL(MatchMode matchMode, Expression expression) {
		super(matchMode);
		this.expression = expression;
	}
	
	public JSONMapFilterSpEL(MatchMode matchMode, String expression) {
		this(matchMode, InternalExpressionHelper.get().parseSimpleExpression(expression));
	}

	@Override
	protected boolean isMatching(JSONMap json) {
		return InternalExpressionHelper.get().evaluateExpression(json, expression, Boolean.class);
	}
}
