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
package com.fortify.api.ssc.connection.api.query.builder;

import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamFields;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamOrderBy;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamQ;
import com.fortify.api.util.rest.json.AbstractJSONMapEnrich;
import com.fortify.api.util.rest.json.JSONMap;

import lombok.RequiredArgsConstructor;

public final class SSCApplicationVersionsQueryBuilder extends AbstractSSCEntityQueryBuilder<SSCApplicationVersionsQueryBuilder> {
	private final SSCParamFields paramFields = add(new SSCParamFields());
	private final SSCParamOrderBy paramOrderBy = add(new SSCParamOrderBy());
	private final SSCParamQ paramQ = add(new SSCParamQ());
	
	public SSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
		preProcessor(new SSCJSONMapEnrichWithApplicationVersionDeepLink(conn));
	}

	public final SSCApplicationVersionsQueryBuilder paramFields(String... fields) {
		paramFields.paramFields(fields); return _this();
	}

	public final SSCApplicationVersionsQueryBuilder orderBy(String orderBy) {
		paramOrderBy.orderBy(orderBy); return _this();
	}

	public final SSCApplicationVersionsQueryBuilder paramQAnd(String field, String value) {
		paramQ.paramQAnd(field, value); return _this();
	}

	public SSCApplicationVersionsQueryBuilder id(String id) {
		return paramQAnd("id", id);
	}

	public SSCApplicationVersionsQueryBuilder applicationName(String applicationName) {
		return paramQAnd("project.name", applicationName);
	}

	public SSCApplicationVersionsQueryBuilder versionName(String versionName) {
		return paramQAnd("name", versionName);
	}
	
	public SSCApplicationVersionsQueryBuilder nameOrId(String applicationVersionNameOrId, String separator) {
		String[] appVersionElements = applicationVersionNameOrId.split(separator);
		if ( appVersionElements.length == 1 ) {
			return id(appVersionElements[0]);
		} else if ( appVersionElements.length == 2 ) {
			return applicationName(appVersionElements[0]).versionName(appVersionElements[1]);
		} else {
			throw new IllegalArgumentException("Applications or versions containing a '+separator+' can only be specified by id");
		}
	}
	
	public SSCApplicationVersionsQueryBuilder nameOrId(String applicationVersionNameOrId) {
		return nameOrId(applicationVersionNameOrId, ":");
	}
	
	@Override
	protected String getTargetPath() {
		return "/api/v1/projectVersions";
	}
	
	@RequiredArgsConstructor
	private static final class SSCJSONMapEnrichWithApplicationVersionDeepLink extends AbstractJSONMapEnrich {
		private final SSCAuthenticatingRestConnection conn;
		@Override
		public void enrich(JSONMap json) {
			String applicationVersionId = json.get("id", String.class);
			if ( StringUtils.isNotBlank(applicationVersionId) ) {
				json.put("deepLink", conn.api().applicationVersion().getApplicationVersionDeepLink(applicationVersionId));
			}
		}
	}
}