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
package com.fortify.api.wie.connection;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fortify.api.util.rest.connection.AbstractRestConnection;
import com.fortify.api.util.rest.connection.AbstractRestConnectionWithUsernamePasswordConfig;
import com.fortify.api.util.rest.connection.IRestConnectionBuilder;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.wie.connection.api.WIEAPI;

/**
 * This class provides an authenticated REST connection for WIE. Low-level API's are
 * available through the various executeRequest() methods provided by {@link AbstractRestConnection}.
 * Higher-level API's are available through the {@link #api()} method. Instances of this class
 * can be created using the {@link #builder()} method.
 * 
 * @author Ruud Senden
 *
 */
public class WIEAuthenticatingRestConnection extends WIEBasicRestConnection {
	private String apiKey = null;
	private final WIEAPI api = new WIEAPI(this);
	private final WIEBasicRestConnection basicConn;
	private final JSONMap auth;
	
	protected WIEAuthenticatingRestConnection(AbstractRestConnectionWithUsernamePasswordConfig<?> config) {
		super(config);
		this.basicConn = new WIEBasicRestConnection(config);
		this.auth = getAuth(config);
	}
	
	private JSONMap getAuth(AbstractRestConnectionWithUsernamePasswordConfig<?> config) {
		JSONMap result = new JSONMap();
		result.put("username", config.getUserName());
		result.put("password", config.getPassword());
		return result;
	}

	public final WIEAPI api() {
		return api;
	}
	
	@Override
	protected WebTarget updateWebTarget(WebTarget webTarget) {
		webTarget = super.updateWebTarget(webTarget);
		if ( apiKey == null ) {
			apiKey = basicConn.executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/auth"),
							Entity.entity(auth, MediaType.APPLICATION_JSON), JSONMap.class)
					.get("data", String.class);
		}
		return webTarget.queryParam("api_key", apiKey);
	}
	
	public void uploadTempFile(String sessionId, String fileId, int fileType, File file) {
		JSONMap request = new JSONMap();
		request.put("sessionID", sessionId);
		request.put("fileId", fileId);
		request.put("fileName", file.getName());
		request.put("fileType", fileType);
		executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/tempFile"),
				Entity.entity(request, MediaType.APPLICATION_JSON), null);
		
        MultiPart multiPart = new FormDataMultiPart();
        try {
			multiPart.type(new MediaType("multipart", "form-data",
		    		Collections.singletonMap(Boundary.BOUNDARY_PARAMETER, Boundary.createBoundary())));
			//multiPart.bodyPart(new FormDataBodyPart("Filename", file.getName()));
			multiPart.bodyPart(new FileDataBodyPart("data", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
			executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/tempFile/{id}/fileData")
					.resolveTemplate("id", fileId), Entity.entity(multiPart, multiPart.getMediaType()), null);
        } finally {
        	try {
				multiPart.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public static final WIEAuthenticatingRestConnectionBuilder builder() {
		return new WIEAuthenticatingRestConnectionBuilder();
	}
	
	public static final class WIEAuthenticatingRestConnectionBuilder extends AbstractRestConnectionWithUsernamePasswordConfig<WIEAuthenticatingRestConnectionBuilder> implements IRestConnectionBuilder<WIEAuthenticatingRestConnection> {
		@Override
		public WIEAuthenticatingRestConnection build() {
			return new WIEAuthenticatingRestConnection(this);
		}
	}
}
