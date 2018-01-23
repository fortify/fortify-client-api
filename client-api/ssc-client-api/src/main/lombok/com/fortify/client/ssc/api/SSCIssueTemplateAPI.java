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
package com.fortify.client.ssc.api;

import java.text.MessageFormat;

import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionFilterSetsQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCIssueTemplatesQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access SSC issue template related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCIssueTemplateAPI extends AbstractSSCAPI {
	public SSCIssueTemplateAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCIssueTemplatesQueryBuilder queryIssueTemplates() {
		return new SSCIssueTemplatesQueryBuilder(conn());
	}
	
	public JSONList getIssueTemplates(boolean useCache) {
		return queryIssueTemplates().useCache(useCache).build().getAll();
	}
	
	public SSCApplicationVersionFilterSetsQueryBuilder queryApplicationVersionFilterSets(String applicationVersionId) {
		return new SSCApplicationVersionFilterSetsQueryBuilder(conn(), applicationVersionId);
	}
	
	public JSONList getApplicationVersionFilterSets(String applicationVersionId) {
		return queryApplicationVersionFilterSets(applicationVersionId).build().getAll();
	}
	
	public String getIssueTemplateIdForName(String issueTemplateName) {
		JSONList issueTemplates = getIssueTemplates(true);
		if ( issueTemplates != null ) {
			return issueTemplates.mapValue("name", issueTemplateName, "id", String.class);
		}
		return null;
	}
	
	/**
	 * @param guidOrTitle filter set GUID or title
	 * @return filter set matching the given GUID or title, or null if not found
	 */
	public final JSONMap findFilterSetByGuidOrTitle(String applicationVersionId, String filterSetGuidOrTitle) {
		String matchExpr = MessageFormat.format("guid==''{0}'' || title==''{0}''", new Object[]{filterSetGuidOrTitle});
		return getApplicationVersionFilterSets(applicationVersionId).find(matchExpr, true, JSONMap.class);
	}
	
	/**
	 * @return default filter set
	 */
	public final JSONMap findDefaultFilterSet(String applicationVersionId) {
		return getApplicationVersionFilterSets(applicationVersionId).find("defaultFilterSet", true, JSONMap.class);
	}
}
