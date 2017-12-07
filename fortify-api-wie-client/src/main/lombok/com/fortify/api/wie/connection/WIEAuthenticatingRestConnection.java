/*******************************************************************************
 * (c) Copyright 2017 Hewlett Packard Enterprise Development LP
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
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

import org.apache.http.auth.Credentials;
import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fortify.api.util.rest.connection.IRestConnection;
import com.fortify.api.util.rest.connection.RestConnection;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.wie.connection.api.WIEAPI;

/**
 * This {@link RestConnection} implementation provides various
 * methods for working with the WIE Enterprise API.
 * 
 * @author Ruud Senden
 *
 */
public class WIEAuthenticatingRestConnection extends WIEBasicRestConnection {
	private String apiKey = null;
	private final WIEAPI api = new WIEAPI(this);
	
	public WIEAuthenticatingRestConnection(WIERestConnectionConfig config) {
		super(config);
	}
	
	public final WIEAPI api() {
		return api;
	}
	
	@Override
	protected WebTarget updateWebTarget(WebTarget webTarget) {
		webTarget = super.updateWebTarget(webTarget);
		if ( apiKey == null ) {
			Credentials credentials = getConfig().getCredentials();
			if ( credentials==null ) {
				throw new IllegalStateException("No WIE credentials have been configured");
			}
			JSONMap auth = new JSONMap();
			auth.put("username", credentials.getUserPrincipal().getName());
			auth.put("password", credentials.getPassword());
			apiKey = new WIEBasicRestConnection(getConfig())
					.executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/auth"),
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
	
	public static final IWIERestConnectionBuilder builder() {
		return (IWIERestConnectionBuilder)builder(new WIERestConnectionBuilderInvocationHandler());
	}
	
	private static final class WIERestConnectionBuilderInvocationHandler extends RestConnectionBuilderInvocationHandler<WIERestConnectionConfig> {
		public WIERestConnectionBuilderInvocationHandler() {
			super(new WIERestConnectionConfig());
		}
		
		@Override
		public IRestConnection build(WIERestConnectionConfig config) {
			return new WIEAuthenticatingRestConnection(config);
		}
		
		@Override
		protected Class<?> getInterfaceType() {
			return IWIERestConnectionBuilder.class;
		}
	}
	
	
	
	public static void main(String[] args) {
		WIEAuthenticatingRestConnection conn = WIEAuthenticatingRestConnection.builder().uri("https://ssc:Admin123!@rs-fortifywie.westeurope.cloudapp.azure.com/WIE/REST;readTimeout=80000").build();
		System.out.println(conn.api().macro().queryMacros().names("test", "abc").build().getAll());
	}
}
