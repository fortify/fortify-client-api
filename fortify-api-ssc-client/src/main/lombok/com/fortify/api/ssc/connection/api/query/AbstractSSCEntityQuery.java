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
package com.fortify.api.ssc.connection.api.query;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery;
import com.fortify.api.util.rest.query.PagingData;

/**
 * <p>This abstract class can be used as a base class for querying entity data from SSC. </p>
 * 
 * TODO Add more JavaDoc
 * 
 * @author Ruud Senden
 */
public abstract class AbstractSSCEntityQuery extends AbstractRestConnectionWithCacheQuery<SSCAuthenticatingRestConnection, JSONMap> {
	protected Map<String, String> getParamQAnds() {
		return null;
	}

	protected List<String> getParamFields() {
		return null;
	}

	protected String getParamOrderBy() {
		return null;
	}

	protected String getParamGroupBy() {
		return null;
	}

	protected String getParamEmbed() {
		return null;
	}
	
	@Override
	protected WebTarget updateBaseWebTarget(WebTarget webTarget) {
		webTarget = addParameterFields(webTarget);
		webTarget = addParameterQuery(webTarget);
		webTarget = addParameterOrderBy(webTarget);
		webTarget = addParameterGroupBy(webTarget);
		webTarget = addParameterEmbed(webTarget);
		webTarget = addExtraParameters(webTarget);
		return webTarget;
	}

	protected WebTarget addParameterQuery(WebTarget webTarget) {
		Map<String,String> paramQAnds = getParamQAnds();
		if ( MapUtils.isNotEmpty(paramQAnds) ) {
			StringBuffer q = new StringBuffer();
			for ( Map.Entry<String, String> entry : paramQAnds.entrySet() ) {
				String qAppend = entry.getKey()+":\""+entry.getValue()+"\"";
				if ( q.length() == 0 ) {
					q.append(qAppend);
				} else {
					q.append("+and+"+qAppend);
				}
			}
			webTarget = webTarget.queryParam("q", q.toString());
		}
		return webTarget;
	}

	protected WebTarget addParameterFields(WebTarget webTarget) {
		List<String> paramFields = getParamFields();
		if ( CollectionUtils.isNotEmpty(paramFields) ) {
			webTarget = webTarget.queryParam("fields", StringUtils.join(paramFields, ","));
		}
		return webTarget;
	}
	
	protected WebTarget addParameterOrderBy(WebTarget webTarget) {
		return addParameterIfNotBlank(webTarget, "orderby", getParamOrderBy());
	}
	
	protected WebTarget addParameterGroupBy(WebTarget webTarget) {
		return addParameterIfNotBlank(webTarget, "groupby", getParamGroupBy());
	}
	
	protected WebTarget addParameterEmbed(WebTarget webTarget) {
		return addParameterIfNotBlank(webTarget, "embed", getParamEmbed());
	}
	
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
	protected JSONList getJSONListFromResponse(JSONMap data) {
		return data.get("data", JSONList.class);
	}
	
	@Override
	protected Class<JSONMap> getResponseTypeClass() {
		return JSONMap.class;
	}
}
