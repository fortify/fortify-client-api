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
package com.fortify.util.rest.webtarget;

import java.util.List;

import javax.ws.rs.client.WebTarget;

/**
 * This {@link IWebTargetUpdater} implementation allows for 
 * appending paths (as configured through the constructor) 
 * to a given {@link WebTarget} instance.
 * 
 * @author Ruud Senden
 *
 */
public class WebTargetPathUpdater implements IWebTargetUpdater {
	private final String[] paths;
	
	/**
	 * Create a new instance for adding the given paths to
	 * {@link WebTarget} instances.
	 * @param paths
	 */
	public WebTargetPathUpdater(String... paths) {
		this.paths = paths;
	}
	
	/**
	 * Create a new instance for adding the given paths to
	 * {@link WebTarget} instances.
	 * @param paths
	 */
	public WebTargetPathUpdater(List<String> paths) {
		this.paths = paths.toArray(new String[]{});
	}

	/**
	 * Update the given {@link WebTarget} instance by adding the
	 * configured path.
	 */
	@Override
	public WebTarget update(WebTarget target) {
		for ( String path : paths ) {
			target = target.path(path);
		}
		return target;
	}

}
