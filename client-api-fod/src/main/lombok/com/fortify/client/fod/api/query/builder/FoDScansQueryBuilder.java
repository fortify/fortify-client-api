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
package com.fortify.client.fod.api.query.builder;

import java.util.Date;

import com.fortify.client.fod.api.query.FoDEntityQuery;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamFields;
import com.fortify.client.fod.api.query.builder.AbstractFoDEntityQueryBuilder.IFoDEntityQueryBuilderParamOrderByWithDirection;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;

/**
 * This class allows for building an {@link FoDEntityQuery} instance that allows for
 * querying FoD applications.
 * 
 * @author Ruud Senden
 *
 */
public class FoDScansQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDScansQueryBuilder> 
	implements IFoDEntityQueryBuilderParamFields<FoDScansQueryBuilder>, 
	           IFoDEntityQueryBuilderParamOrderByWithDirection<FoDScansQueryBuilder> 
{
	public FoDScansQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/scans");
	}
	
	@Override
	public FoDScansQueryBuilder paramFields(boolean ignoreIfBlank, String... fields) {
		return super.paramFields(ignoreIfBlank, fields);
	}
	
	public FoDScansQueryBuilder paramOrderBy(boolean ignoreIfBlank, FoDOrderBy orderBy) {
		return super.paramOrderBy(ignoreIfBlank, orderBy);
	}
	
	public FoDScansQueryBuilder paramStartedOnStartDate(Date dateTime) {
		return queryParam("startedOnStartDate", FOD_DATE_TIME_FORMAT.format(dateTime));
	}
	
	public FoDScansQueryBuilder paramStartedOnEndDate(Date dateTime) {
		return queryParam("startedOnEndDate", FOD_DATE_TIME_FORMAT.format(dateTime));
	}
	
	public FoDScansQueryBuilder paramCompletedOnStartDate(Date dateTime) {
		return queryParam("completedOnStartDate", FOD_DATE_TIME_FORMAT.format(dateTime));
	}
	
	public FoDScansQueryBuilder paramCompletedOnEndDate(Date dateTime) {
		return queryParam("completedOnEndDate", FOD_DATE_TIME_FORMAT.format(dateTime));
	}
	
}
