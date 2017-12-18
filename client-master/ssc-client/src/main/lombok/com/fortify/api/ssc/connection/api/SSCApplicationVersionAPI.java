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
package com.fortify.api.ssc.connection.api;

import java.util.HashMap;
import java.util.Map;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.api.util.rest.json.JSONMap;

public class SSCApplicationVersionAPI extends AbstractSSCAPI {
	public SSCApplicationVersionAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionsQueryBuilder queryApplicationVersions() {
		return new SSCApplicationVersionsQueryBuilder(conn());
	}
	
	public JSONMap getApplicationVersionById(String applicationVersionId) {
		return queryApplicationVersions().id(applicationVersionId).useCache(true).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByName(String applicationName, String versionName) {
		return queryApplicationVersions().applicationName(applicationName).versionName(versionName).useCache(true).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId, String separator) {
		return queryApplicationVersions().nameOrId(nameOrId, separator).useCache(true).build().getUnique();
	}
	
	public static void main(String[] args) {
		Map<String,Object> properties = new HashMap<>();
		properties.put("SSCBaseUrl", "http://localhost:1710/ssc");
		properties.put("SSCUserName", "ssc");
		properties.put("SSCPassword", "Admin123!");
		properties.put("SSCProxy.url", "http://xxx.yyy/");
		Map<String, Object> newMap = new HashMap<>();
		SSCAuthenticatingRestConnection conn = SSCAuthenticatingRestConnection.builder().fromMap(properties, "SSC", true).toMap(newMap, "Test", true).build();
		System.out.println(newMap);
		SSCApplicationVersionAPI api = conn.api().applicationVersion();
		for ( int i = 0 ; i < 10 ; i++ ) {
			System.out.println(api.queryApplicationVersions().applicationName("WebGoat").paramFields("id", "name").useCache(true).build().getAll());
			System.out.println(api.queryApplicationVersions().id("6").useCache(true).build().getAll());
			System.out.println(api.queryApplicationVersions().applicationName("WebGoat").versionName("5.0").useCache(true).build().getUnique());
			System.out.println(api.getApplicationVersionByNameOrId("WebGoat:5.0", ":"));
			System.out.println(api.queryApplicationVersions().useCache(true).build().getAll());
		}
	}

}
