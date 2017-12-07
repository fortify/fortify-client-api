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
package com.fortify.api.wie.connection.api.query;

import java.util.Arrays;

import javax.ws.rs.client.WebTarget;

import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery;
import com.fortify.api.util.rest.query.PagingData;
import com.fortify.api.wie.connection.WIEAuthenticatingRestConnection;

/**
 * <p>This abstract class can be used as a base class for querying entity data from SSC. </p>
 * 
 * TODO Add more JavaDoc
 * 
 * @author Ruud Senden
 */
public abstract class AbstractWIEEntityQuery extends AbstractRestConnectionWithCacheQuery<WIEAuthenticatingRestConnection, JSONMap> {
	
	@Override
	protected WebTarget getUpdatedBaseWebTarget(WebTarget webTarget) {
		webTarget = addTargetPath(webTarget);
		webTarget = addExtraParameters(webTarget);
		return webTarget;
	}
	
	protected WebTarget addTargetPath(WebTarget webTarget) {
		return webTarget.path(getTargetPath());
	}

	protected abstract String getTargetPath();
	
	/**
	 * Subclasses can override this method to add any additional
	 * request parameters that are not supported by this base class.
	 * 
	 * @param webTarget
	 * @return
	 */
	protected WebTarget addExtraParameters(WebTarget webTarget) {
		return webTarget;
	}
	
	@Override
	protected WebTarget updateWebTargetWithPagingData(WebTarget target, PagingData pagingData) {
		return target.queryParam("start", ""+pagingData.getStart()).queryParam("limit", ""+pagingData.getPageSize());
	}
	
	@Override
	protected void updatePagingDataFromResponse(PagingData pagingData, JSONMap data) {
		pagingData.setTotal( data.get("count", Integer.class) );
		pagingData.setLastPageSize( data.get("data", JSONList.class).size() );
	}
	
	@Override
	protected JSONList getJSONListFromResponse(JSONMap json) {
		Object data = json.get("data", Object.class);
		return (data instanceof JSONList) ? (JSONList)data : new JSONList(Arrays.asList(data));
	}
	
	@Override
	protected Class<JSONMap> getResponseTypeClass() {
		return JSONMap.class;
	}
}
