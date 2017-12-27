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
package com.fortify.api.webinspect.connection.api;

import com.fortify.api.webinspect.connection.WebInspectAuthenticatingRestConnection;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * <p>This is the main class for accessing higher-level WebInspect client API functionality.
 * Actual API functions are provided by the various WebInspect*API classes; this main API
 * class merely provides access to these WebInspect*API instances.</p>
 * 
 * <p>Instances of this class are usually not created directly, but rather
 * accessed via the {@link WebInspectAuthenticatingRestConnection#api()} method.</p>
 * 
 * @author Ruud Senden
 *
 */
@Accessors(fluent = true)
@Getter
public class WebInspectAPI extends AbstractWebInspectAPI {
	private final WebInspectProxyAPI proxy;
	private final WebInspectScanAPI scan;
	private final WebInspectMacroAPI macro;
	private final WebInspectSecureBaseAPI secureBase;
	
	public WebInspectAPI(WebInspectAuthenticatingRestConnection conn) {
		super(conn);
		this.proxy = new WebInspectProxyAPI(conn);
		this.scan = new WebInspectScanAPI(conn);
		this.macro = new WebInspectMacroAPI(conn);
		this.secureBase = new WebInspectSecureBaseAPI(conn);
	}

}
