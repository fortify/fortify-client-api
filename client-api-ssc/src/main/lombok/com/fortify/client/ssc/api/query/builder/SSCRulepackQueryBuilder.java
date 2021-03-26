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
import com.fortify.util.applier.ifblank.IfBlankAction;

/**
 * This class allows for building an {@link SSCEntityQuery} instance that allows for
 * querying SSC applications.
 * 
 * @author Ruud Senden
 * 
 */
public final class SSCRulepackQueryBuilder extends AbstractSSCEntityQueryBuilder<SSCRulepackQueryBuilder> 
	implements ISSCEntityQueryBuilderParamFields<SSCRulepackQueryBuilder>,
		ISSCEntityQueryBuilderParamOrderBy<SSCRulepackQueryBuilder>,
		ISSCEntityQueryBuilderParamQ<SSCRulepackQueryBuilder>
{
	@SSCRequiredActionsPermitted({"GET=/api/v\\d+/coreRulepacks"})
	public SSCRulepackQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v1/coreRulepacks");
	}
	
	public final SSCRulepackQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}

	public final SSCRulepackQueryBuilder paramOrderBy(IfBlankAction ifBlankAction, SSCOrderBy orderBy) {
		return super.paramOrderBy(ifBlankAction, orderBy);
	}
	
	public final SSCRulepackQueryBuilder paramQ(IfBlankAction ifBlankAction, String q) {
		return super.paramQ(ifBlankAction, q);
	}

	public final SSCRulepackQueryBuilder paramQAnd(IfBlankAction ifBlankAction, String field, Object value) {
		return super.paramQAnd(ifBlankAction, field, value);
	}

	public SSCRulepackQueryBuilder id(IfBlankAction ifBlankAction, String id) {
		return paramQAnd(ifBlankAction, "id", id);
	}
}