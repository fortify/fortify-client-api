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
package com.fortify.util.rest.json.preprocessor.enrich;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.expression.TemplateExpression;

/**
 * This {@link AbstractJSONMapEnrich} implementation allows for enriching a
 * given {@link JSONMap} instance with a 'deepLink' property by evaluating the
 * configured {@link TemplateExpression} on the {@link JSONMap} instance and
 * storing the evaluation result as the 'deepLink' property in the {@link JSONMap}
 * instance.
 * 
 * @author Ruud Senden
 *
 */
public class JSONMapEnrichWithDeepLink extends AbstractJSONMapEnrich {
	public static final String DEEPLINK_FIELD = "deepLink";
	private final TemplateExpression deepLinkExpression;
	private final String[] requiredProperties;
	
	public JSONMapEnrichWithDeepLink(TemplateExpression deepLinkExpression, String... requiredProperties) {
		this.deepLinkExpression = deepLinkExpression;
		this.requiredProperties = requiredProperties;
	}
	
	public JSONMapEnrichWithDeepLink(String deepLinkExpression, String... requiredProperties) {
		this(SpringExpressionUtil.parseTemplateExpression(deepLinkExpression), requiredProperties);
	}
	@Override
	protected final void enrich(JSONMap json) {
		if ( json.containsAllKeys(requiredProperties) ) {
			json.put(DEEPLINK_FIELD, SpringExpressionUtil.evaluateExpression(json, deepLinkExpression, String.class));
		}
	}
}
