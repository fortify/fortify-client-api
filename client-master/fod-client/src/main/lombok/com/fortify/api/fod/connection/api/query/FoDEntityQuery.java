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
package com.fortify.api.fod.connection.api.query;

import java.util.Arrays;

import javax.ws.rs.client.WebTarget;

import com.fortify.api.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.query.AbstractRestConnectionQuery;
import com.fortify.api.util.rest.query.AbstractRestConnectionQueryConfig;
import com.fortify.api.util.rest.query.PagingData;

import lombok.Getter;

/**
 * This class provides FoD-specific functionality for handling paging and processing REST responses.
 * Usually this class is instantiated through the various build() methods provided by the 
 * query builder implementations in the {@link  com.fortify.api.fod.connection.api.query.builder}
 * package.
 * 
 * @author Ruud Senden
 */
@Getter
public class FoDEntityQuery extends AbstractRestConnectionQuery<JSONMap> {
	public FoDEntityQuery(AbstractRestConnectionQueryConfig<FoDAuthenticatingRestConnection,?> config) {
		super(config);
	}

	@Override
	protected WebTarget updateWebTargetWithPagingData(WebTarget target, PagingData pagingData) {
		return target.queryParam("offset", ""+pagingData.getNextPageStart()).queryParam("limit", ""+pagingData.getNextPageSize());
	}
	
	@Override
	protected void updatePagingDataFromResponse(PagingData pagingData, JSONMap data) {
		pagingData.setTotalAvailable( data.get("totalCount", Integer.class) );
	}
	
	@Override
	protected JSONList getJSONListFromResponse(JSONMap json) {
		Object data = json.get("items", Object.class);
		return (data instanceof JSONList) ? (JSONList)data : new JSONList(Arrays.asList(data));
	}
	
	@Override
	protected Class<JSONMap> getResponseTypeClass() {
		return JSONMap.class;
	}
}
