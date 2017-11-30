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
import com.fortify.api.ssc.connection.api.IssueSearchOptions;

import lombok.Builder;
import lombok.Singular;

public class SSCIssueQuery extends AbstractSSCApplicationVersionChildEntityQuery {
	public enum QueryMode {
		adv, issues
	}
	private IssueSearchOptions issueSearchOptions;
	private String paramGroupId;
	private String paramGroupingType;
	private String paramFilterSetId;
	private String paramFilter;
	private QueryMode paramQm;

	@Builder
	protected SSCIssueQuery(
			SSCAuthenticatingRestConnection conn, 
			String parentId,
			@Singular Map<String, String> paramQAnds,
			List<String> paramFields, 
			String paramOrderBy,
			String paramGroupId,
			String paramGroupingType,
			String paramFilterSetId,
			String paramFilter,
			QueryMode paramQm,
			IssueSearchOptions issueSearchOptions,
			Integer maxResults) {
		super(conn, parentId);
		setParamQAnds(paramQAnds);
		setParamOrderBy(paramOrderBy);
		setMaxResults(maxResults);
		this.paramGroupId = paramGroupId;
		this.paramGroupingType = paramGroupingType;
		this.paramFilterSetId = paramFilterSetId;
		this.paramFilter = paramFilter;
		this.paramQm = paramQm;
		this.issueSearchOptions = issueSearchOptions;
	}
	
	public static class SSCIssueQueryBuilder {
		private IssueSearchOptions issueSearchOptions = new IssueSearchOptions();
		
		public SSCIssueQueryBuilder includeHidden() {
			issueSearchOptions.setIncludeHidden(true);
			return this;
		}
		
		public SSCIssueQueryBuilder includeRemoved() {
			issueSearchOptions.setIncludeRemoved(true);
			return this;
		}
		
		public SSCIssueQueryBuilder includeSuppressed() {
			issueSearchOptions.setIncludeSuppressed(true);
			return this;
		}
	}

	@Override
	protected WebTarget addChildEntityPath(WebTarget target) {
		return target.path("issues");
	}

	@Override
	protected boolean isPagingSupported() {
		return true;
	}
	
	@Override
	protected WebTarget addExtraParameters(WebTarget target) {
		target = addParameterIfNotBlank(target, "groupid", paramGroupId);
		target = addParameterIfNotBlank(target, "groupingtype", paramGroupingType);
		target = addParameterIfNotBlank(target, "filterset", paramFilterSetId);
		target = addParameterIfNotBlank(target, "filter", paramFilter);
		target = addParameterIfNotBlank(target, "qm", paramQm==null?null:paramQm.name());
		return target;
	}
	
	@Override
	protected void initRequest() {
		conn().api().issue().updateApplicationVersionIssueSearchOptions(getParentId(), this.issueSearchOptions);
	}
}
