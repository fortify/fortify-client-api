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

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;

public abstract class AbstractSSCApplicationVersionsQueryBuilder<T extends AbstractSSCApplicationVersionsQueryBuilder<T>>
		extends AbstractSSCEntityQueryBuilder<T> {
	
	public AbstractSSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
	}
	
	protected T embedSubEntity(String propertyName, String entityName, EmbedType embedType, String... fields) {
		return embed(propertyName, "/api/v1/projectVersions/${id}/"+entityName, embedType, fields);
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
}