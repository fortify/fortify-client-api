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
package com.fortify.api.fod.connection.api;

import com.fortify.api.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.api.fod.connection.api.query.builder.FoDReleasesQueryBuilder;
import com.fortify.api.util.rest.json.JSONMap;

/**
 * This class is used to access FoD release-related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class FoDReleaseAPI extends AbstractFoDAPI {
	public FoDReleaseAPI(FoDAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public FoDReleasesQueryBuilder queryReleases() {
		return new FoDReleasesQueryBuilder(conn());
	}
	
	public JSONMap getReleaseById(String releaseId) {
		return queryReleases().releaseId(releaseId).useCache(true).build().getUnique();
	}
	
	public JSONMap getReleaseByName(String applicationName, String releaseName) {
		return queryReleases().applicationName(applicationName).releaseName(releaseName).useCache(true).build().getUnique();
	}
	
	public JSONMap getReleaseByNameOrId(String nameOrId, String separator) {
		return queryReleases().nameOrId(nameOrId, separator).useCache(true).build().getUnique();
	}
}
