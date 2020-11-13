/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates, a Micro Focus company
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
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamFields;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamOrderBy;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamQ;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithDeepLink;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC application versions.
 * 
 * @author Ruud Senden
 * 
 */
public final class SSCApplicationVersionsQueryBuilder 
	extends AbstractSSCApplicationVersionsQueryBuilder<SSCApplicationVersionsQueryBuilder> 
	implements ISSCEntityQueryBuilderParamFields<SSCApplicationVersionsQueryBuilder>,
			ISSCEntityQueryBuilderParamOrderBy<SSCApplicationVersionsQueryBuilder>,
			ISSCEntityQueryBuilderParamQ<SSCApplicationVersionsQueryBuilder>
{
	private static final String[] DEEPLINK_FIELDS = {"id"};
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/projectVersions"})
	public SSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn);
		appendPath("/api/v1/projectVersions");
		preProcessor(new JSONMapEnrichWithDeepLink(conn.getBaseUrlStringWithoutTrailingSlash()+"/html/ssc/index.jsp#!/version/${id}/fix", DEEPLINK_FIELDS));
	}

	public final SSCApplicationVersionsQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}

	public final SSCApplicationVersionsQueryBuilder paramOrderBy(boolean ignoreIfBlank, SSCOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	public final SSCApplicationVersionsQueryBuilder paramQ(boolean ignoreIfBlank, String q) {
		return super.paramQ(ignoreIfBlank, q);
	}

	public final SSCApplicationVersionsQueryBuilder paramQAnd(boolean ignoreIfBlank, String field, Object value) {
		return super.paramQAnd(ignoreIfBlank, field, value);
	}

	public SSCApplicationVersionsQueryBuilder id(boolean ignoreIfBlank, String id) {
		return super.paramQAnd(ignoreIfBlank, "id", id);
	}

	public SSCApplicationVersionsQueryBuilder applicationId(boolean ignoreIfBlank, String applicationId) {
		return super.paramQAnd(ignoreIfBlank, "project.id", applicationId);
	}
	
	public SSCApplicationVersionsQueryBuilder applicationName(boolean ignoreIfBlank, String applicationName) {
		return super.paramQAnd(ignoreIfBlank, "project.name", applicationName);
	}
	
	public SSCApplicationVersionsQueryBuilder versionName(boolean ignoreIfBlank, String versionName) {
		return super.paramQAnd(ignoreIfBlank, "name", versionName);
	}
	
	
	public SSCApplicationVersionsQueryBuilder applicationAndOrVersionName(boolean ignoreIfBlank, String applicationAndOrVersionName) {
		return applicationAndOrVersionName(ignoreIfBlank, applicationAndOrVersionName, ":");
	}
	
	public SSCApplicationVersionsQueryBuilder applicationAndOrVersionName(boolean ignoreIfBlank, String applicationAndOrVersionName, String separator) {
		if ( isBlank(!ignoreIfBlank, "Application and/or version name", applicationAndOrVersionName) ) {
			return _this();
		} else {
			String[] elts = applicationAndOrVersionName.split(separator);
			if ( elts.length == 1 && StringUtils.isNotBlank(elts[0]) || elts.length == 2 && StringUtils.isBlank(elts[1]) ) {
				return applicationName(false, elts[0]);
			} else if ( elts.length == 2 && StringUtils.isBlank(elts[0]) ) {
				return versionName(false, elts[1]);
			} else if ( elts.length == 2 ) {
				return applicationName(false, elts[0]).versionName(false, elts[1]);
			} else {
				throw new IllegalArgumentException("Applications or versions containing a '"+separator+"' are unsupported");
			}
		}
	}
	
	public SSCApplicationVersionsQueryBuilder nameOrId(boolean ignoreIfBlank, String applicationVersionNameOrId, String separator) {
		if ( isBlank(!ignoreIfBlank, "Version name or id", applicationVersionNameOrId) ) {
			return _this();
		} else {
			String[] appVersionElements = applicationVersionNameOrId.split(separator);
			if ( appVersionElements.length == 1 ) {
				return id(false, appVersionElements[0]);
			} else if ( appVersionElements.length == 2 ) {
				return applicationName(false, appVersionElements[0]).versionName(false, appVersionElements[1]);
			} else {
				throw new IllegalArgumentException("Applications or versions containing a '+separator+' can only be specified by id");
			}
		}
	}
	
	public SSCApplicationVersionsQueryBuilder nameOrId(boolean ignoreIfBlank, String applicationVersionNameOrId) {
		return nameOrId(ignoreIfBlank, applicationVersionNameOrId, ":");
	}
}