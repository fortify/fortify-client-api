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
package com.fortify.util.rest.webtarget;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections.MapUtils;

/**
 * This {@link IWebTargetUpdater} implementation allows for resolving template
 * values in {@link WebTarget} instances. For example, if the given {@link WebTarget}
 * contains a path '/api/entity/{id}', this {@link IWebTargetUpdater} implementation
 * allows for replacing the '{id}' variable with an actual id.
 * 
 * @author Ruud Senden
 *
 */
public class WebTargetTemplateResolver implements IWebTargetUpdater {
	private final Map<String,Object> templateValues;
	private final boolean encodeSlashInPath;
	
	/**
	 * Create a new instance for resolving the given template values
	 * in {@link WebTarget} instances. 
	 * @param templateValues
	 * @param encodeSlashInPath
	 */
	public WebTargetTemplateResolver(Map<String,Object> templateValues, boolean encodeSlashInPath) {
		this.templateValues = Collections.unmodifiableMap(templateValues);
		this.encodeSlashInPath = encodeSlashInPath;
	}

	/**
	 * Update the given {@link WebTarget} instance by resolving template
	 * variables.
	 */
	@Override
	public WebTarget update(WebTarget target) {
		if ( MapUtils.isNotEmpty(templateValues) ) {
			target = target.resolveTemplates(templateValues, encodeSlashInPath);
		}
		return target;
	}

}
