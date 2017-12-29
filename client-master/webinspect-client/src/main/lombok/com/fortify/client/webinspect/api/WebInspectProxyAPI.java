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
package com.fortify.client.webinspect.api;

import java.nio.file.CopyOption;
import java.nio.file.Path;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.fortify.client.webinspect.connection.WebInspectAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access functionality related to WebInspect Proxies.
 * 
 * @author Ruud Senden
 *
 */
public class WebInspectProxyAPI extends AbstractWebInspectAPI {
	public WebInspectProxyAPI(WebInspectAuthenticatingRestConnection conn) {
		super(conn);
	}

	public JSONMap createProxy(String instanceId, String address, int port) {
		JSONMap entity = new JSONMap();
		entity.put("port", port);
		entity.put("address", address);
		entity.put("instanceId", instanceId);
		return conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource().path("/proxy"), 
				Entity.entity(entity, MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	public JSONMap deleteProxy(String instanceId) {
		return conn().executeRequest(HttpMethod.DELETE, 
				conn().getBaseResource().path("/proxy/{instanceId}").resolveTemplate("instanceId", instanceId), 
				JSONMap.class);
	}
	
	public JSONMap saveProxyTrafficOnServer(String instanceId, String extension, String action) {
		return conn().executeRequest(HttpMethod.PUT, 
				conn().getBaseResource().path("/proxy/{instanceId}.{extension}")
				.resolveTemplate("instanceId", instanceId)
				.resolveTemplate("extension", extension)
				.queryParam("action", action), 
				Entity.entity("", MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	public void saveProxyTraffic(String instanceId, String extension, Path outputPath, CopyOption... copyOptions) {
		conn().executeRequestAndSaveResponse(HttpMethod.GET, 
				conn().getBaseResource().path("/proxy/{instanceId}.{extension}")
				.resolveTemplate("instanceId", instanceId)
				.resolveTemplate("extension", extension), outputPath, copyOptions);
	}
	
}
