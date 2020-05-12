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

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.SSCEntityQuery;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamFields;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamOrderBy;
import com.fortify.client.ssc.api.query.builder.AbstractSSCEntityQueryBuilder.ISSCEntityQueryBuilderParamQ;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC jobs.
 * 
 * @author Ruud Senden
 * 
 */
public final class SSCJobsQueryBuilder extends AbstractSSCEntityQueryBuilder<SSCJobsQueryBuilder> 
	implements ISSCEntityQueryBuilderParamFields<SSCJobsQueryBuilder>,
		ISSCEntityQueryBuilderParamOrderBy<SSCJobsQueryBuilder>,
		ISSCEntityQueryBuilderParamQ<SSCJobsQueryBuilder>
{
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/jobs"})
	public SSCJobsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v1/jobs");
	}

	public final SSCJobsQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}

	@Deprecated
	public final SSCJobsQueryBuilder paramOrderBy(String orderBy, SSCOrderByDirection direction) {
		return paramOrderBy(false, new SSCOrderBy(orderBy, direction));
	}
	
	public final SSCJobsQueryBuilder paramOrderBy(boolean ignoreIfBlank, SSCOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	@Deprecated
	public final SSCJobsQueryBuilder paramQ(String q) {
		return paramQ(false, q);
	}
	
	public final SSCJobsQueryBuilder paramQ(boolean ignoreIfBlank, String q) {
		return super.paramQ(ignoreIfBlank, q);
	}

	@Deprecated
	public final SSCJobsQueryBuilder paramQAnd(String field, Object value) {
		return paramQAnd(false, field, value);
	}
	
	public final SSCJobsQueryBuilder paramQAnd(boolean ignoreIfBlank, String field, Object value) {
		return super.paramQAnd(ignoreIfBlank, field, value);
	}
	
	@Deprecated
	public final SSCJobsQueryBuilder id(String id) {
		return super.paramQAnd(false, "id", id);
	}
	
	public final SSCJobsQueryBuilder id(boolean ignoreIfBlank, String id) {
		return super.paramQAnd(ignoreIfBlank, "id", id);
	}

	@Deprecated
	public final SSCJobsQueryBuilder jobClassName(String jobClassName) {
		return super.paramQAnd(false, "jobClassName", jobClassName);
	}
	
	public final SSCJobsQueryBuilder jobClassName(boolean ignoreIfBlank, String jobClassName) {
		return super.paramQAnd(ignoreIfBlank, "jobClassName", jobClassName);
	}

	@Deprecated
	public final SSCJobsQueryBuilder priority(int priority) {
		return super.paramQAnd(false, "priority", "" + priority);
	}
	
	public final SSCJobsQueryBuilder priority(boolean ignoreIfBlank, int priority) {
		return super.paramQAnd(ignoreIfBlank, "priority", "" + priority);
	}

	@Deprecated
	public final SSCJobsQueryBuilder state(String state) {
		return super.paramQAnd(false, "state", state);
	}
	
	public final SSCJobsQueryBuilder state(boolean ignoreIfBlank, String state) {
		return super.paramQAnd(ignoreIfBlank, "state", state);
	}
}