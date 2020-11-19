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

/**
 * {@link IExpressionHelper} implementation for internally generated expressions that
 * require standard behavior. As an example, internally generated template expressions
 * may use <code>${expression}</code> syntax; such expressions would not evaluate
 * correctly when a custom {@link IExpressionHelper} instance with different expression
 * prefix and suffix is registered with {@link DefaultExpressionHelper}. 
 *    
 * @author Ruud Senden
 *
 */
public class InternalExpressionHelper extends AbstractExpressionHelper {
	public static final InternalExpressionHelper INSTANCE = new InternalExpressionHelper();
	private InternalExpressionHelper() {}
	public static final IExpressionHelper get() {
		return INSTANCE;
	}
}
