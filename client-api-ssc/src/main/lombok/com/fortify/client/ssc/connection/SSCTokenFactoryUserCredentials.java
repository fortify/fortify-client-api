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
package com.fortify.client.ssc.connection;

import java.util.Date;
import java.util.regex.Pattern;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import org.apache.commons.codec.binary.Base64;

import com.fortify.util.log4j.LogMaskingHelper;
import com.fortify.util.rest.connection.AbstractRestConnectionConfig;
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
	private final Pattern EXPR_TOKEN = Pattern.compile("\"token\":\"([^\"]+)\"");
	private final SSCBasicRestConnection conn;
	private final String userName;
	private final String password;
	private final String tokenDescription;
	private SSCTokenFactoryUserCredentials.TokenData tokenData = null;
	public SSCTokenFactoryUserCredentials(AbstractRestConnectionConfig<?> config, String userName, String password, String tokenDescription) {
		this.conn = new SSCBasicRestConnection(config);
		this.userName = userName;
		this.password = password;
		this.tokenDescription = tokenDescription;
	}
	
	@Override
	public synchronized String getTokenSynchronized() {
		return getToken();
	}
	
	@Override
	public void close() {
		revokeToken();
		this.conn.close();
	}
	
	private void revokeToken() {
		if ( tokenData != null ) {
			JSONMap postData = new JSONMap();
			postData.putPath("tokens", new String[]{tokenData.getToken()});
			tokenData = null;
			try {
				performTokenRequest("/api/v1/tokens/action/revoke", postData);
				log.debug("[SSC] Revoked access token");
			} catch ( RuntimeException e ) {
				log.warn("[SSC] Error revoking access token");
				log.debug("Token revocation exception details", e);
			}
		}
	}
	
	public String getToken() {
		if ( tokenData == null || tokenData.isExpired() ) {
			JSONMap postData = new JSONMap();
			postData.putPath("type", "UnifiedLoginToken");
			postData.putPath("description", tokenDescription);
			tokenData = getTokenData(performTokenRequest("/api/v1/tokens", postData));
			log.info("[SSC] Obtained access token, expiring at "+tokenData.getTerminalDate().toString());
		}
		return tokenData.getToken();
	}
	
	private TokenData getTokenData(JSONMap json) {
		JSONMap data = json.get("data", JSONMap.class);
		return new TokenData((String)data.get("token"), data.get("terminalDate", Date.class));
	}
	
	private JSONMap performTokenRequest(String endpoint, JSONMap postData) {
		String authHeaderValue = "Basic "+Base64.encodeBase64String((userName+":"+password).getBytes());
		return LogMaskingHelper.maskByPatternGroups().patterns(EXPR_TOKEN).on(() ->
			conn.executeRequest(HttpMethod.POST, conn.getBaseResource().path(endpoint)
					.request().header("Authorization", authHeaderValue), 
					Entity.entity(postData, "application/json"), JSONMap.class));
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
