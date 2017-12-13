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

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import com.fortify.api.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.SSCEntityQuery;
import com.fortify.api.ssc.connection.api.query.builder.param.SSCParamFields;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.webtarget.WebTargetQueryParamUpdaterBuilder;

/**
 * This builder class can be used to build {@link SSCEntityQuery} instances
 * for querying bug filing requirements.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionBugFilingRequirementsQueryBuilder extends AbstractSSCApplicationVersionChildEntityQueryBuilder<SSCApplicationVersionBugFilingRequirementsQueryBuilder> {
	private final SSCParamFields paramFields = add(new SSCParamFields());
	private final WebTargetQueryParamUpdaterBuilder paramChangedParamIdentifier = add(new WebTargetQueryParamUpdaterBuilder("changeParamIdentifier"));
	
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions/\\d+/bugfilingrequirements"})
	public SSCApplicationVersionBugFilingRequirementsQueryBuilder(SSCAuthenticatingRestConnection conn, String applicationVersionId) {
		super(conn, applicationVersionId, false);
	}

	public final SSCApplicationVersionBugFilingRequirementsQueryBuilder paramFields(String... fields) {
		paramFields.paramFields(fields); return _this();
	}
	
	public final SSCApplicationVersionBugFilingRequirementsQueryBuilder paramChangedParamIdentifier(String paramChangedParamIdentifier) {
		this.paramChangedParamIdentifier.paramValues(paramChangedParamIdentifier); return _this();
	}
	
	@SSCRequiredActionsPermitted({"PUT=/api/v\\d+/projectVersions/\\d+/bugfilingrequirements"})
	public final SSCApplicationVersionBugFilingRequirementsQueryBuilder paramBugParams(JSONMap bugParams) {
		setEntity(Entity.entity(bugParams, "application/json"));
		setHttpMethod(HttpMethod.PUT);
		return _this();
	}
	
	@Override
	protected String getChildEntityPath() {
		return "bugfilingrequirements";
	}

}
