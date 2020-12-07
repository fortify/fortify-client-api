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
package com.fortify.util.spring.expression.helper;

import java.util.function.Function;

import org.springframework.expression.Expression;

import com.fortify.util.spring.expression.SimpleExpression;
import com.fortify.util.spring.expression.TemplateExpression;

public interface IExpressionHelper {

	/**
	 * Parse the given string as a SpEL expression.
	 * @param exprStr
	 * @return The SpEL {@link Expression} object for the given expression string, or null if input is null
	 */
	SimpleExpression parseSimpleExpression(String exprStr);

	/**
	 * Parse the given string as a SpEL template expression.
	 * @param exprStr
	 * @return The SpEL {@link Expression} object for the given expression string, or null if input is null 
	 */
	TemplateExpression parseTemplateExpression(String exprStr);

	<T> T evaluateExpression(Object input, Expression expression, Class<T> returnType);

	<T> T evaluateSimpleExpression(Object input, String expression, Class<T> returnType);

	<T> T evaluateTemplateExpression(Object input, String expression, Class<T> returnType);

	<I, O> Function<I, O> expressionAsFunction(Expression expr, Class<O> returnType);
	
	<I, O> Function<I, O> simpleExpressionAsFunction(String simpleExpr, Class<O> returnType);
	
	<I, O> Function<I, O> templateExpressionAsFunction(String templateExpr, Class<O> returnType);

}