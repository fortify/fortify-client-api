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
package com.fortify.api.util.rest.webtarget;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * {@link IWebTargetUpdaterBuilder} implementation for building
 * {@link WebTargetTemplateResolver} instances. This builder 
 * allows template values to be configured through the 
 * {@link #templateValue(String, Object)} method.
 * 
 * @author Ruud Senden
 *
 */
@RequiredArgsConstructor
public class WebTargetTemplateResolverBuilder implements IWebTargetUpdaterBuilder {
	private final Map<String,Object> templateValues = new HashMap<>();
	private boolean encodeSlashInPath = false;
	
	public final WebTargetTemplateResolverBuilder templateValue(String name, Object value) {
		this.templateValues.put(name, value); return this;
	}
	
	public final WebTargetTemplateResolverBuilder encodeSlashInPath(boolean encodeSlashInPath) {
		this.encodeSlashInPath = encodeSlashInPath; return this;
	}
	
	@Override
	public IWebTargetUpdater build() {
		return new WebTargetTemplateResolver(templateValues, encodeSlashInPath);
	}

}
