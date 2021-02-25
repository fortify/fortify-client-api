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
	
	public JSONList getIssueTemplates() {
		return queryIssueTemplates().build().getAll();
	}
	
	public SSCApplicationVersionFilterSetsQueryBuilder queryApplicationVersionFilterSets(String applicationVersionId) {
		return new SSCApplicationVersionFilterSetsQueryBuilder(conn(), applicationVersionId);
	}
	
	public JSONList getApplicationVersionFilterSets(String applicationVersionId) {
		return queryApplicationVersionFilterSets(applicationVersionId).build().getAll();
	}
	
	/**
	 * Get an {@link SSCIssueTemplateHelper} instance for efficiently
	 * working with issue template data.
	 * @return New {@link SSCIssueTemplateHelper} instance
	 */
	public SSCIssueTemplateHelper getIssueTemplateHelper() {
		return new SSCIssueTemplateHelper();
	}
	
	/**
	 * This class provides various utility methods for working with SSC
	 * issue templates. Data is loaded from SSC only once per 
	 * {@link SSCIssueTemplateHelper} instance. As such it is 
	 * recommended to store and re-use a single instance of this class
	 * where possible. Keep in mind though that you need to get a fresh
	 * instance in order to see any issue template changes on SSC. 
	 * 
	 * @author Ruud Senden
	 *
	 */
	public final class SSCIssueTemplateHelper {
		private JSONList issueTemplates;
		
		/**
		 * Instances can only be created through the {@link SSCIssueTemplateAPI#getIssueTemplateHelper()}
		 * method.
		 */
		private SSCIssueTemplateHelper() {}
		
		/**
		 * Lazy-load the list of issue templates from SSC
		 * @return {@link JSONList} containing SSC issue template data
		 */
		public JSONList getIssueTemplates() {
			if ( issueTemplates==null ) {
				issueTemplates = SSCIssueTemplateAPI.this.getIssueTemplates();
			}
			return issueTemplates;
		}
		
		/**
		 * Get the {@link JSONMap} object representing the default issue template
		 * (if any). This method returns null if no default template is defined on SSC.
		 * @return {@link JSONMap} containing issue template data for the default SSC issue template, or null if no default issue template is defined
		 */
		public JSONMap getDefaultIssueTemplate() {
			return getIssueTemplates().find("defaultTemplate", true, JSONMap.class);
		}
		
		/**
		 * Get the id of the default issue template (if any). This method returns
		 * null if no default template is defined on SSC.
		 * @return Id of the default issue template, or null if no default issue template is defined
		 */
		public String getDefaultIssueTemplateId() {
			JSONMap defaultIssueTemplate = getDefaultIssueTemplate();
			return defaultIssueTemplate==null ? null : defaultIssueTemplate.get("id", String.class);
		}
		
		/**
		 * Get the issue template id for the given issue template name.
		 * @param issueTemplateName for which to get the issue template id
		 * @return issue template id for the given issue template name
		 */
		public String getIssueTemplateIdForName(String issueTemplateName) {
			JSONList issueTemplates = getIssueTemplates();
			if ( issueTemplates != null ) {
				return issueTemplates.mapValue("name", issueTemplateName, "id", String.class);
			}
			return null;
		}
	}
	
	
	
	/**
	 * Find a filter set by GUID or title
	 * @param applicationVersionId Application version id
	 * @param filterSetGuidOrTitle filter set GUID or title
	 * @return filter set matching the given GUID or title, or null if not found
	 */
	public final JSONMap findFilterSetByGuidOrTitle(String applicationVersionId, String filterSetGuidOrTitle) {
		String matchExpr = MessageFormat.format("guid==''{0}'' || title==''{0}''", new Object[]{filterSetGuidOrTitle});
		return getApplicationVersionFilterSets(applicationVersionId).find(matchExpr, true, JSONMap.class);
	}
	
	/**
	 * Get the default filter set for the given application version id
	 * @param applicationVersionId for which to retrieve the default filter set
	 * @return default filter set for the given application version id
	 */
	public final JSONMap findDefaultFilterSet(String applicationVersionId) {
		return getApplicationVersionFilterSets(applicationVersionId).find("defaultFilterSet", true, JSONMap.class);
	}
}
