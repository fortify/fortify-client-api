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
package com.fortify.client.wie.api;

import java.io.File;
import java.util.UUID;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.fortify.client.wie.connection.WIEAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access WIE scan-related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class WIEScanAPI extends AbstractWIEAPI {

	public WIEScanAPI(WIEAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public JSONMap createScan(ScanData scanData) {
		return conn().executeRequest(HttpMethod.POST, conn().getBaseResource().path("/api/v1/scans"), 
			Entity.entity(scanData.getJsonMap(), MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	public String uploadScanSettings(File file) {
		String uuid = UUID.randomUUID().toString();
		conn().uploadTempFile(uuid, uuid, 5, file);
		return uuid;
	}
	
	public static final class ScanData { 
		private JSONMap jsonMap = new JSONMap();
		
		protected JSONMap getJsonMap() {
			return jsonMap;
		}
		
		private ScanData putPath(String path, Object value) {
			jsonMap.putPath(path, value, true);
			return this;
		}
		
		public ScanData scanName(String value) {
			return putPath("name", value);
		}
		
		public ScanData projectName(String value) {
			return putPath("project.name", value);
		}
		
		public ScanData projectVersionName(String value) {
			return putPath("projectVersion.name", value);
		}
		
		public ScanData siteId(String value) {
			return putPath("projectVersion.siteId", value);
		}
		
		public ScanData priority(int value) {
			return putPath("priority", value);
		}
		
		public ScanData policyId(String value) {
			return putPath("policy.id", value);
		}
		
		public ScanData sensorId(String value) {
			return putPath("sensor.id", value);
		}
		
		public ScanData settingsFileId(String value) {
			return putPath("fileID", value);
		}
		
		public ScanData scanTemplateId(String value) {
			return putPath("scanTemplateId", value);
		}
		
		public ScanData startUri(String value) {
			return putPath("startURI", value);
		}

	}

}
