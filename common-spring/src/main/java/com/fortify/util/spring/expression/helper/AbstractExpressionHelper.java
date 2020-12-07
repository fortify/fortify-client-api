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
package com.fortify.util.spring.expression.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.fortify.util.spring.MapAccessorIgnoreNonExistingProperties;
import com.fortify.util.spring.expression.SimpleExpression;
import com.fortify.util.spring.expression.TemplateExpression;

/**
 * This class provides several utility methods related to 
 * Spring Expression Language, for example for evaluating
 * (template) expressions on input objects.
 */
public abstract class AbstractExpressionHelper implements IExpressionHelper {
	private final ExpressionParser expressionParser = createExpressionParser();
	private final ParserContext templateParserContext = createTemplateParserContext();
	private final EvaluationContext evaluationContext = createEvaluationContext();
	
	protected AbstractExpressionHelper() {}

	/**
	 * Create the {@link ExpressionParser} to be used. This default
	 * implementation returns a new {@link SpelExpressionParser} instance
	 * configured with the configuration returned by the 
	 * {@link #createSpelParserConfiguration()} method. Subclasses can
	 * override this method to return a different {@link ExpressionParser}
	 * implementation.
	 * @return
	 */
	protected ExpressionParser createExpressionParser() {
		return new SpelExpressionParser(createSpelParserConfiguration());
	}
	
	/**
	 * Create the {@link SpelParserConfiguration} to be used for creating
	 * the default {@link SpelExpressionParser} returned by the 
	 * {@link #createExpressionParser()} method. This default implementation
	 * returns a default {@link SpelParserConfiguration} instance. Subclasses 
	 * can override this method to modify the configuration. 
	 * @return
	 */
	protected SpelParserConfiguration createSpelParserConfiguration() {
		return new SpelParserConfiguration();
	}
	
	/**
	 * Create the {@link TemplateParserContext} to be used for parsing
	 * template expressions. This default implementation returns a
	 * {@link TemplateParserContext} configured with <code>${</code>
	 * as the expression prefix, and <code>}</code>> as the expression
	 * suffix. Subclasses can override this method to provide a 
	 * customized {@link TemplateParserContext} instance.
	 * @return
	 */
	protected TemplateParserContext createTemplateParserContext() {
		return new TemplateParserContext("${","}");
	}

	/**
	 * Create the {@link EvaluationContext} to be used for evaluating
	 * expressions. This default implementation simply calls 
	 * {@link #createStandardEvaluationContext()} to create the {@link EvaluationContext}
	 * instances. Subclasses can override this method to provide a customized 
	 * {@link EvaluationContext} instance.
	 * @return
	 */
	protected EvaluationContext createEvaluationContext() {
		return createStandardEvaluationContext();
	}

	/**
	 * Create a {@link StandardEvaluationContext} configured with the property
	 * accessors returned by the {@link #createPropertyAccessors()} method.
	 * @return
	 */
	protected final StandardEvaluationContext createStandardEvaluationContext() {
		StandardEvaluationContext result = new StandardEvaluationContext();
		result.setPropertyAccessors(createPropertyAccessors());
		return result;
	}
	
	/**
	 * Create the list of property accessors to be used. This default
	 * implementation adds {@link ReflectivePropertyAccessor} and
	 * {@link MapAccessorIgnoreNonExistingProperties}. Subclasses can
	 * override this method to modify this list of default accessors.
	 * @return
	 */
	protected List<PropertyAccessor> createPropertyAccessors() {
		List<PropertyAccessor> result = new ArrayList<PropertyAccessor>();
		result.add(new ReflectivePropertyAccessor());
		result.add(new MapAccessorIgnoreNonExistingProperties());
		return result;
	}

	/**
	 * Parse the given string as a SpEL expression.
	 * @param exprStr
	 * @return The SpEL {@link Expression} object for the given expression string, or null if input is null
	 */
	@Override
	public final SimpleExpression parseSimpleExpression(String exprStr) {
		return exprStr==null ? null : new SimpleExpression(expressionParser.parseExpression(exprStr));
	}
	
	/**
	 * Parse the given string as a SpEL template expression.
	 * @param exprStr
	 * @return The SpEL {@link Expression} object for the given expression string, or null if input is null 
	 */
	@Override
	public final TemplateExpression parseTemplateExpression(String exprStr) {
		return exprStr==null ? null : new TemplateExpression(expressionParser.parseExpression(exprStr.replace("\\n", "\n"), templateParserContext));
	}
	
	@Override
	public final <T> T evaluateExpression(Object input, Expression expression, Class<T> returnType) {
		if ( input==null || expression==null ) { return null; }
		return expression.getValue(evaluationContext, input, returnType);
	}
	
	@Override
	public final <T> T evaluateSimpleExpression(Object input, String expression, Class<T> returnType) {
		return evaluateExpression(input, parseSimpleExpression(expression), returnType);
	}
	
	@Override
	public final <T> T evaluateTemplateExpression(Object input, String expression, Class<T> returnType) {
		return evaluateExpression(input, parseTemplateExpression(expression), returnType);
	}

	@Override
	public final <I,O> Function<I,O> expressionAsFunction(Expression expr, Class<O> returnType) {
		return input -> expr==null ? null : expr.getValue(evaluationContext, input, returnType);
	}
	
	@Override
	public final <I,O> Function<I,O> simpleExpressionAsFunction(String exprStr, Class<O> returnType) {
		return expressionAsFunction(parseSimpleExpression(exprStr), returnType);
	}
	
	@Override
	public final <I,O> Function<I,O> templateExpressionAsFunction(String exprStr, Class<O> returnType) {
		return expressionAsFunction(parseTemplateExpression(exprStr), returnType);
	}
}
