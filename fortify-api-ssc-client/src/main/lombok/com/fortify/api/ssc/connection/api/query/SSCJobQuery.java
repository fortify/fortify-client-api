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

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.IJSONMapFilter;

import lombok.Builder;
import lombok.Singular;

public final class SSCJobQuery extends AbstractSSCEntityQuery {
	
	@Builder
	private SSCJobQuery(
			SSCAuthenticatingRestConnection conn,
			@Singular List<IJSONMapFilter> filters,
			@Singular Map<String, String> paramQAnds,
			List<String> paramFields,
			String paramOrderBy,
			Integer maxResults) {
		super(conn);
		setFilters(filters);
		setParamQAnds(paramQAnds);
		setParamFields(paramFields);
		setParamOrderBy(paramOrderBy);
		setMaxResults(maxResults);
	}
	
	public static class SSCJobQueryBuilder {
		public SSCJobQueryBuilder id(String id) {
			return paramQAnd("id", id);
		}
		
		public SSCJobQueryBuilder jobClassName(String jobClassName) {
			return paramQAnd("jobClassName", jobClassName);
		}
		
		public SSCJobQueryBuilder priority(int priority) {
			return paramQAnd("priority", ""+priority);
		}
		
		public SSCJobQueryBuilder state(String state) {
			return paramQAnd("state", state);
		}
	}
	
	@Override
	protected boolean isPagingSupported() {
		return true;
	}
	
	@Override
	protected WebTarget getBaseWebTarget() {
		return conn().getBaseResource().path("api/v1/jobs");
	}
	
}