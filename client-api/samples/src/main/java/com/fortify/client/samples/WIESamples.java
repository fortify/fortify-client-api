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
package com.fortify.client.samples;

import com.fortify.client.wie.api.WIEMacroAPI;
import com.fortify.client.wie.connection.WIEAuthenticatingRestConnection;

/**
 * This class demonstrates the use of (parts of) the WIE client API.
 * 
 * @author Ruud Senden
 *
 */
public class WIESamples extends AbstractSamples {
	private final WIEAuthenticatingRestConnection conn;
	
	
	public WIESamples(String baseUrlWithCredentials) {
		this.conn = WIEAuthenticatingRestConnection.builder().baseUrl(baseUrlWithCredentials).build();
	}

	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			throw new IllegalArgumentException("WIE URL in format http[s]://<user>:<password>@<host>[:port]/WIE/REST must be provided as first parameter");
		}
		WIESamples samples = new WIESamples(args[0]);
		samples.sample1QueryAllMacros();
		samples.sample2QueryMacrosByName();
	}
	
	public final void sample1QueryAllMacros() throws Exception {
		printHeader("\n\n---- Query all macros ----");
		print(conn.api(WIEMacroAPI.class).queryMacros().build().getAll());
	}
	
	public final void sample2QueryMacrosByName() throws Exception {
		printHeader("\n\n---- Query macros 'test' and 'anotherTest' ----");
		print(conn.api(WIEMacroAPI.class).queryMacros().names("test", "anotherTest").build().getAll());
	}
}
