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
 * <p>This class provides a default {@link IExpressionHelper} instance for use
 * throughout an application. By default, {@link InternalExpressionHelper} is
 * registered as the default {@link IExpressionHelper}. Applications can use
 * a different default {@link IExpressionHelper} instance by calling the
 * {@link #set(IExpressionHelper)} method on application start-up.</p>
 * 
 * <p>Note: To avoid classes storing the default expression helper instance at class
 * load time (when the application possibly hasn't had the chance yet to update the 
 * default instance), the {@link #get()} method should not used to initialize static 
 * fields.</p>
 * 
 * <ul>
 *  <li>Incorrect: <code>private <b>static</b> final IExpressionHelper EXPRESSIONHELPER = DefaultExpressionHelperProvider.get()</code></li>
 *  <li>Correct:   <code>private final IExpressionHelper EXPRESSIONHELPER = DefaultExpressionHelperProvider.get()</code>
 * </ul>
 * 
 * @author Ruud Senden
 *
 */
public class DefaultExpressionHelper {
	private static IExpressionHelper defaultExpressionHelper = InternalExpressionHelper.get();

	public static final IExpressionHelper get() {
		return defaultExpressionHelper;
	}

	public static final void set(IExpressionHelper defaultExpressionHelper) {
		DefaultExpressionHelper.defaultExpressionHelper = defaultExpressionHelper;
	}
}
