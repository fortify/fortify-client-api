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

import javax.ws.rs.HttpMethod;

import org.apache.commons.codec.binary.Base64;

import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.wie.connection.WIEAuthenticatingRestConnection;
import com.fortify.api.wie.connection.api.query.builder.WIEMacrosQueryBuilder;

/**
 * This class is used to access WIE macro-related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class WIEMacroAPI extends AbstractWIEAPI {

	public WIEMacroAPI(WIEAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public WIEMacrosQueryBuilder queryMacros() {
		return new WIEMacrosQueryBuilder(conn()).useCache(true);
	}
	
	public byte[] getMacroData(String macroId) {
		JSONMap response = conn().executeRequest(HttpMethod.GET, 
				conn().getBaseResource().path("/api/v1/macros/{id}/macroData").resolveTemplate("id", macroId), JSONMap.class);
		String data = response.get("data", String.class);
		return Base64.decodeBase64(data);
	}

}
