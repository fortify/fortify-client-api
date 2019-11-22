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
package com.fortify.client.ssc.api.query.builder;

import com.fortify.client.ssc.annotation.SSCCopyToConstructors;
import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCAttributeAPI;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoaderWithConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;

public abstract class AbstractSSCApplicationVersionsQueryBuilder<T extends AbstractSSCApplicationVersionsQueryBuilder<T>>
		extends AbstractSSCEntityQueryBuilder<T> {
	
	public AbstractSSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
	}

	protected static final class SSCJSONMapOnDemandLoaderAttributeValuesByName extends AbstractJSONMapOnDemandLoaderWithConnection<SSCAuthenticatingRestConnection> {
			private static final long serialVersionUID = 1L;
	
			public SSCJSONMapOnDemandLoaderAttributeValuesByName(SSCAuthenticatingRestConnection conn) {
				super(conn, true);
			}
			
			@Override @SSCCopyToConstructors
			public Object getOnDemand(SSCAuthenticatingRestConnection conn, String propertyName, JSONMap parent) {
				return conn.api(SSCAttributeAPI.class).getApplicationVersionAttributeValuesByName(parent.get("id",String.class));
			}
			
			@Override
			protected Class<SSCAuthenticatingRestConnection> getConnectionClazz() {
				return SSCAuthenticatingRestConnection.class;
			}
			
		}

	public T onDemandAttributes() {
		return onDemandAttributes("attributes");
	}

	public T onDemandAttributeValuesByName() {
		return onDemandAttributeValuesByName("attributeValuesByName");
	}

	public T onDemandBugTracker() {
		return onDemandBugTracker("bugTracker");
	}

	public T onDemandCustomTags() {
		return onDemandCustomTags("customTags");
	}

	public T onDemandFilterSets() {
		return onDemandFilterSets("filterSets");
	}

	public T onDemandIssueSearchOptions() {
		return onDemandIssueSearchOptions("issueSearchOptions");
	}

	public T onDemandPerformanceIndicatorHistories() {
		return onDemandPerformanceIndicatorHistories("performanceIndicatorHistories");
	}

	public T onDemandVariableHistories() {
		return onDemandVariableHistories("variableHistories");
	}

	public T onDemandResponsibilities() {
		return onDemandResponsibilities("responsibilities");
	}

	public T onDemandResultProcessingRules() {
		return onDemandResultProcessingRules("processingRules");
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/attributes" })
	public T onDemandAttributes(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/attributes");
	}

	/**
	 * Add on-demand attribute for all application version attribute values by name.
	 * Attributes without any value will be excluded from the result.
	 * @param propertyName
	 * @return
	 */
	public T onDemandAttributeValuesByName(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderAttributeValuesByName(getConn())));
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/bugtracker" })
	public T onDemandBugTracker(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/bugtracker", fields));
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/customTags" })
	public T onDemandCustomTags(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/customTags", fields));
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/filterSets" })
	public T onDemandFilterSets(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/filterSets");
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/issueSearchOptions" })
	public T onDemandIssueSearchOptions(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/issueSearchOptions", fields));
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/performanceIndicatorHistories" })
	public T onDemandPerformanceIndicatorHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/performanceIndicatorHistories", fields));
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/variableHistories" })
	public T onDemandVariableHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/variableHistories", fields));
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/responsibilities" })
	public T onDemandResponsibilities(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/responsibilities");
	}

	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/resultProcessingRules" })
	public T onDemandResultProcessingRules(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/resultProcessingRules", fields));
	}
}