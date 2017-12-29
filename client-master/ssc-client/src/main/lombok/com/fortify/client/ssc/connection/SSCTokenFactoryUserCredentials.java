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
package com.fortify.client.ssc.connection;

import java.util.Date;

import javax.ws.rs.HttpMethod;

import org.apache.commons.codec.binary.Base64;

import com.fortify.util.rest.json.JSONMap;

import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;

/**
 * This class is used to generate SSC tokens for accessing the
 * SSC REST API. Given an {@link SSCBasicRestConnection} instance
 * and SSC userName and password, it will call the SSC /oauth/token
 * API to request a REST token. The token will be automatically
 * refreshed as required.
 * 
 * @author Ruud Senden
 *
 */
@CommonsLog
public final class SSCTokenFactoryUserCredentials implements ISSCTokenFactory {
	private final SSCBasicRestConnection conn;
	private final String userName;
	private final String password;
	private SSCTokenFactoryUserCredentials.TokenData tokenData = null;
	public SSCTokenFactoryUserCredentials(SSCBasicRestConnection conn, String userName, String password) {
		this.conn = conn;
		this.userName = userName;
		this.password = password;
	}
	
	public String getToken() {
		if ( tokenData == null || tokenData.isExpired() ) {
			String authHeaderValue = "Basic "+Base64.encodeBase64String((userName+":"+password).getBytes());
			tokenData = getTokenData(conn.executeRequest(HttpMethod.POST, conn.getBaseResource().path("/api/v1/auth/obtain_token").request().header("Authorization", authHeaderValue), null, JSONMap.class));
			log.info("[SSC] Obtained access token, expiring at "+tokenData.getTerminalDate().toString());
		}
		return tokenData.getToken();
	}
	
	private TokenData getTokenData(JSONMap json) {
		JSONMap data = (JSONMap)json.get("data");
		return new TokenData((String)data.get("token"), data.get("terminalDate", Date.class));
	}

	@Data
	private static final class TokenData {
		private final String token;
        private final Date terminalDate;
        
		public boolean isExpired() {
			return new Date().getTime() > getTerminalDate().getTime();
		}
	}
}
