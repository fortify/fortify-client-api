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
package com.fortify.client.ssc.api;

import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access SSC application version related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionAPI extends AbstractSSCAPI {
	public SSCApplicationVersionAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionsQueryBuilder queryApplicationVersions() {
		return new SSCApplicationVersionsQueryBuilder(conn());
	}
	
	public JSONMap getApplicationVersionById(String applicationVersionId, boolean useCache) {
		return queryApplicationVersions().id(applicationVersionId).useCache(useCache).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByName(String applicationName, String versionName, boolean useCache) {
		return queryApplicationVersions().applicationName(applicationName).versionName(versionName).useCache(useCache).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId, String separator, boolean useCache) {
		return queryApplicationVersions().nameOrId(nameOrId, separator).useCache(useCache).build().getUnique();
	}
	
	public JSONMap getApplicationVersionByNameOrId(String nameOrId, boolean useCache) {
		return queryApplicationVersions().nameOrId(nameOrId).useCache(useCache).build().getUnique();
	}
}
