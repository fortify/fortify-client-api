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
package com.fortify.api.wie.connection.api;

import com.fortify.api.wie.connection.WIEAuthenticatingRestConnection;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * <p>This is the main class for accessing higher-level WIE client API functionality.
 * Actual API functions are provided by the various WIE*API classes; this main API
 * class merely provides access to these WIE*API instances.</p>
 * 
 * <p>Instances of this class are usually not created directly, but rather
 * accessed via the {@link WIEAuthenticatingRestConnection#api()} method.</p>
 * 
 * @author Ruud Senden
 *
 */
@Accessors(fluent = true)
@Getter
public class WIEAPI extends AbstractWIEAPI {
	private final WIEMacroAPI macro;
	private final WIEScanAPI scan;
	
	public WIEAPI(WIEAuthenticatingRestConnection conn) {
		super(conn);
		this.macro = new WIEMacroAPI(conn);
		this.scan = new WIEScanAPI(conn);
	}

}
