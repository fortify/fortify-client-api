/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
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

import java.util.function.Consumer;

import com.fortify.client.ssc.annotation.SSCCopyToConstructors;
import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCApplicationVersionAttributeAPI;
import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoaderWithConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;

public abstract class AbstractSSCApplicationVersionsQueryBuilder<T extends AbstractSSCApplicationVersionsQueryBuilder<T>>
		extends AbstractSSCEntityQueryBuilder<T> {
	
	public AbstractSSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
	}
	
	protected T embedSubEntity(String propertyName, String entityName, EmbedType embedType, String... fields) {
		return embed(propertyName, "/api/v1/projectVersions/${id}/"+entityName, embedType, fields);
	}
	
	public T embedAttributeValuesByName(EmbedType embedType) {
		return embedAttributeValuesByName("attributeValuesByName", embedType);
	}
	
	public T embedAttributeValuesByName(String propertyName, EmbedType embedType) {
		switch (embedType) {
		case ONDEMAND: return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderAttributeValuesByName(getConn())));
		case PRELOAD: embedAttributes("attributes", EmbedType.PRELOAD, "guid", "value", "values");
			return pagePreProcessor(new SSCJSONListAddAttributeValuesByName(getConn(), propertyName));
		default: throw new RuntimeException("Unknown embed type: "+embedType.name());
		}
	}
	
	public T embedAttributes(EmbedType embedType, String... fields) {
		return embedAttributes("attributes", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/attributes" })
	public T embedAttributes(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "attributes", embedType);
	}
	
	public T embedAuditAssistantTrainingStatus(EmbedType embedType, String... fields) {
		return embedAuditAssistantTrainingStatus("auditAssistantTrainingStatus", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/auditAssistantTrainingStatus" })
	public T embedAuditAssistantTrainingStatus(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "auditAssistantTrainingStatus", embedType, fields);
	}
	
	public T embedAuditAssistantStatus(EmbedType embedType, String... fields) {
		return embedAuditAssistantStatus("auditAssistantStatus", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/auditAssistantStatus" })
	public T embedAuditAssistantStatus(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "auditAssistantStatus", embedType, fields);
	}
	
	public T embedAuthEntities(EmbedType embedType, String... fields) {
		return embedAuthEntities("authEntities", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/authEntities" })
	public T embedAuthEntities(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "authEntities", embedType, fields);
	}
	
	public T embedBugfilingrequirements(EmbedType embedType, String... fields) {
		return embedBugfilingrequirements("bugfilingrequirements", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/bugfilingrequirements" })
	public T embedBugfilingrequirements(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "bugfilingrequirements", embedType, fields);
	}
	
	public T embedBugtracker(EmbedType embedType, String... fields) {
		return embedBugtracker("bugtracker", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/bugtracker" })
	public T embedBugtracker(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "bugtracker", embedType, fields);
	}
	
	public T embedCustomTags(EmbedType embedType, String... fields) {
		return embedCustomTags("customTags", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/customTags" })
	public T embedCustomTags(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "customTags?limit=-1", embedType, fields);
	}
	
	public T embedDynamicScanRequests(EmbedType embedType, String... fields) {
		return embedDynamicScanRequests("dynamicScanRequests", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/dynamicScanRequests" })
	public T embedDynamicScanRequests(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "dynamicScanRequests", embedType, fields);
	}
	
	public T embedDynamicScanRequestTemplate(EmbedType embedType) {
		return embedDynamicScanRequestTemplate("dynamicScanRequestTemplate", embedType);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/dynamicScanRequestTemplate" })
	public T embedDynamicScanRequestTemplate(String propertyName, EmbedType embedType) {
		return embedSubEntity(propertyName, "dynamicScanRequestTemplate", embedType);
	}
	
	public T embedDynamicScanRequestsSummary(EmbedType embedType) {
		return embedDynamicScanRequestsSummary("dynamicScanRequestsSummary", embedType);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/dynamicScanRequestsSummary" })
	public T embedDynamicScanRequestsSummary(String propertyName, EmbedType embedType) {
		return embedSubEntity(propertyName, "dynamicScanRequestsSummary", embedType);
	}
	
	public T embedFilterSets(EmbedType embedType, String... fields) {
		return embedFilterSets("filterSets", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/filterSets" })
	public T embedFilterSets(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "filterSets?limit=-1", embedType, fields);
	}
	
	public T embedFolders(EmbedType embedType) {
		return embedFolders("folders", embedType);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/folders" })
	public T embedFolders(String propertyName, EmbedType embedType) {
		return embedSubEntity(propertyName, "folders", embedType);
	}
	
	public T embedIidMigrations(EmbedType embedType, String... fields) {
		return embedIidMigrations("iidMigrations", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/iidMigrations" })
	public T embedIidMigrations(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "iidMigrations", embedType, fields);
	}
	
	public T embedIssueSearchOptions(EmbedType embedType) {
		return embedIssueSearchOptions("issueSearchOptions", embedType);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/issueSearchOptions" })
	public T embedIssueSearchOptions(String propertyName, EmbedType embedType) {
		return embedSubEntity(propertyName, "issueSearchOptions", embedType);
	}
	
	public T embedIssueSelectorSet(EmbedType embedType, String... fields) {
		return embedIssueSelectorSet("issueSelectorSet", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/issueSelectorSet" })
	public T embedIssueSelectorSet(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "issueSelectorSet", embedType, fields);
	}
	
	public T embedPerformanceIndicatorHistories(EmbedType embedType, String... fields) {
		return embedPerformanceIndicatorHistories("performanceIndicatorHistories", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/performanceIndicatorHistories" })
	public T embedPerformanceIndicatorHistories(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "performanceIndicatorHistories?limit=-1", embedType, fields);
	}
	
	public T embedResponsibilities(EmbedType embedType, String... fields) {
		return embedResponsibilities("responsibilities", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/responsibilities" })
	public T embedResponsibilities(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "responsibilities?limit=-1", embedType);
	}
	
	public T embedResultProcessingRules(EmbedType embedType, String... fields) {
		return embedResultProcessingRules("resultProcessingRules", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/resultProcessingRules" })
	public T embedResultProcessingRules(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "resultProcessingRules", embedType, fields);
	}
	
	public T embedVariableHistories(EmbedType embedType, String... fields) {
		return embedVariableHistories("variableHistories", embedType, fields);
	}
	
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/variableHistories" })
	public T embedVariableHistories(String propertyName, EmbedType embedType, String... fields) {
		return embedSubEntity(propertyName, "variableHistories?limit=-1", embedType, fields);
	}

	/**
	 * Use {@link #embedAttributeValuesByName(EmbedType)}
	 * @return
	 */
	@Deprecated
	public T onDemandAttributeValuesByName() {
		return onDemandAttributeValuesByName("attributeValuesByName");
	}
	
	/**
	 * Add on-demand attribute for all application version attribute values by name.
	 * Attributes without any value will be excluded from the result.
	 * 
	 * Use {@link #embedAttributeValuesByName(String, EmbedType)} instead
	 * @param propertyName
	 * @return
	 */
	// TODO Add support for pre-loading instead of on-demand
	@Deprecated
	public T onDemandAttributeValuesByName(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderAttributeValuesByName(getConn())));
	}

	/**
	 * Use {@link #embedAttributes(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandAttributes() {
		return onDemandAttributes("attributes");
	}

	/**
	 * Use {@link #embedBugtracker(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandBugTracker() {
		return onDemandBugTracker("bugTracker");
	}

	/**
	 * Use {@link #embedCustomTags(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandCustomTags() {
		return onDemandCustomTags("customTags");
	}

	/**
	 * Use {@link #embedFilterSets(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandFilterSets() {
		return onDemandFilterSets("filterSets");
	}

	/**
	 * Use {@link #embedIssueSearchOptions(EmbedType)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandIssueSearchOptions() {
		return onDemandIssueSearchOptions("issueSearchOptions");
	}

	/**
	 * Use {@link #embedPerformanceIndicatorHistories(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandPerformanceIndicatorHistories() {
		return onDemandPerformanceIndicatorHistories("performanceIndicatorHistories");
	}

	/**
	 * Use {@link #embedVariableHistories(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandVariableHistories() {
		return onDemandVariableHistories("variableHistories");
	}

	/**
	 * Use {@link #embedResponsibilities(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandResponsibilities() {
		return onDemandResponsibilities("responsibilities");
	}

	/**
	 * Use {@link #embedResultProcessingRules(EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandResultProcessingRules() {
		return onDemandResultProcessingRules("processingRules");
	}

	/**
	 * Use {@link #embedAttributes(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/attributes" })
	public T onDemandAttributes(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/attributes");
	}

	/**
	 * Use {@link #embedBugtracker(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/bugtracker" })
	public T onDemandBugTracker(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/bugtracker", fields));
	}

	/**
	 * Use {@link #embedCustomTags(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/customTags" })
	public T onDemandCustomTags(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/customTags", fields));
	}

	/**
	 * Use {@link #embedFilterSets(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/filterSets" })
	public T onDemandFilterSets(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/filterSets");
	}

	/**
	 * Use {@link #embedIssueSearchOptions(String, EmbedType)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/issueSearchOptions" })
	public T onDemandIssueSearchOptions(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/issueSearchOptions", fields));
	}

	/**
	 * Use {@link #embedPerformanceIndicatorHistories(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/performanceIndicatorHistories" })
	public T onDemandPerformanceIndicatorHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/performanceIndicatorHistories", fields));
	}

	/**
	 * Use {@link #embedVariableHistories(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/variableHistories" })
	public T onDemandVariableHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/variableHistories", fields));
	}

	/**
	 * Use {@link #embedResponsibilities(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/responsibilities" })
	public T onDemandResponsibilities(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/responsibilities");
	}

	/**
	 * Use {@link #embedResultProcessingRules(String, EmbedType, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/resultProcessingRules" })
	public T onDemandResultProcessingRules(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/resultProcessingRules", fields));
	}
	
	protected static final class SSCJSONMapOnDemandLoaderAttributeValuesByName extends AbstractJSONMapOnDemandLoaderWithConnection<SSCAuthenticatingRestConnection> {
		private static final long serialVersionUID = 1L;
		private volatile JSONList attrDefs;

		public SSCJSONMapOnDemandLoaderAttributeValuesByName(SSCAuthenticatingRestConnection conn) {
			super(conn, true);
		}
		
		@Override @SSCCopyToConstructors
		public Object getOnDemand(SSCAuthenticatingRestConnection conn, String propertyName, JSONMap parent) {
			if ( attrDefs==null ) {
				attrDefs = conn.api(SSCAttributeDefinitionAPI.class).getAttributeDefinitions(true, "guid","name");
			}
			return conn.api(SSCApplicationVersionAttributeAPI.class).getApplicationVersionAttributeValuesByName(parent.get("id",String.class), attrDefs);
		}
		
		@Override
		protected Class<SSCAuthenticatingRestConnection> getConnectionClazz() {
			return SSCAuthenticatingRestConnection.class;
		}
	}
	
	private static final class SSCJSONListAddAttributeValuesByName implements Consumer<JSONList> {
		private final SSCAuthenticatingRestConnection conn;
		private final String propertyName;
		private volatile JSONList attrDefs;
		
		public SSCJSONListAddAttributeValuesByName(SSCAuthenticatingRestConnection conn, String propertyName) {
			this.conn = conn;
			this.propertyName = propertyName;
		}
		@Override
		public void accept(JSONList list) {
			list.asValueType(JSONMap.class).forEach(this::addAttributeValuesByName);
		}
		private JSONList getAttributeDefinitions() {
			if ( attrDefs==null ) {
				attrDefs = conn.api(SSCAttributeDefinitionAPI.class).getAttributeDefinitions(true, "guid","name");
			}
			return attrDefs;
		}
		
		private void addAttributeValuesByName(JSONMap json) {
			JSONList attrs = json.getPath("attributes", JSONList.class);
			if ( attrs==null ) {
				throw new IllegalArgumentException("Application version does not contain attributes list");
			}
			JSONMap attrValuesByName = conn.api(SSCApplicationVersionAttributeAPI.class).convertApplicationVersionAttributeValuesListToMap(attrs, getAttributeDefinitions());
			json.put(propertyName, attrValuesByName);
		}
	}
}