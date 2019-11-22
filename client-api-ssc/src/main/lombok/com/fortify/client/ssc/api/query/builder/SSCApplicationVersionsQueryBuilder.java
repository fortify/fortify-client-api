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
package com.fortify.client.ssc.api.query.builder;

import org.apache.commons.lang.StringUtils;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC application versions.
 * 
 * @author Ruud Senden
 * 
 */
public final class SSCApplicationVersionsQueryBuilder extends AbstractSSCApplicationVersionsQueryBuilder<SSCApplicationVersionsQueryBuilder> {
	private static final String[] DEEPLINK_FIELDS = {"id"};
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions"})
	public SSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn);
		appendPath("/api/v1/projectVersions");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBaseUrlStringWithoutTrailingSlash()+"/html/ssc/index.jsp#!/version/${id}/fix", DEEPLINK_FIELDS));
	}

	public final SSCApplicationVersionsQueryBuilder paramFields(String... fields) {
		return super.paramFields(replaceField(JSONMapEnrichWithDeepLink.DEEPLINK_FIELD, DEEPLINK_FIELDS, fields));
	}

	public final SSCApplicationVersionsQueryBuilder paramOrderBy(String orderBy, SSCOrderByDirection direction) {
		return super.paramOrderBy(orderBy, direction);
	}

	public final SSCApplicationVersionsQueryBuilder paramQAnd(String field, String value) {
		return super.paramQAnd(field, value);
	}

	public SSCApplicationVersionsQueryBuilder id(String id) {
		return super.paramQAnd("id", id);
	}
	
	public SSCApplicationVersionsQueryBuilder applicationId(String applicationId) {
		return super.paramQAnd("project.id", applicationId);
	}

	public SSCApplicationVersionsQueryBuilder applicationName(String applicationName) {
		return super.paramQAnd("project.name", applicationName);
	}

	public SSCApplicationVersionsQueryBuilder versionName(String versionName) {
		return super.paramQAnd("name", versionName);
	}
	
	public SSCApplicationVersionsQueryBuilder applicationAndOrVersionName(String applicationAndOrVersionName) {
		return applicationAndOrVersionName(applicationAndOrVersionName, ":");
	}
	
	public SSCApplicationVersionsQueryBuilder applicationAndOrVersionName(String applicationAndOrVersionName, String separator) {
		if ( StringUtils.isBlank(applicationAndOrVersionName) ) {
			throw new IllegalArgumentException("Application and/or version name must be specified");
		}
		String[] elts = applicationAndOrVersionName.split(separator);
		if ( elts.length == 1 && StringUtils.isNotBlank(elts[0]) || elts.length == 2 && StringUtils.isBlank(elts[1]) ) {
			return applicationName(elts[0]);
		} else if ( elts.length == 2 && StringUtils.isBlank(elts[0]) ) {
			return versionName(elts[1]);
		} else if ( elts.length == 2 ) {
			return applicationName(elts[0]).versionName(elts[1]);
		} else {
			throw new IllegalArgumentException("Applications or versions containing a '"+separator+"' are unsupported");
		}
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
}