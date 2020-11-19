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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionBugFilingRequirementsQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionBugTrackerQueryBuilder;
import com.fortify.client.ssc.api.query.builder.SSCBugTrackersQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.expression.helper.InternalExpressionHelper;

/**
 * This class is used to access SSC bug tracker related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCBugTrackerAPI extends AbstractSSCAPI {
	public SSCBugTrackerAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionBugTrackerQueryBuilder queryApplicationVersionBugTracker(String applicationVersionId) {
		return new SSCApplicationVersionBugTrackerQueryBuilder(conn(), applicationVersionId);
	}
	
	public SSCApplicationVersionBugFilingRequirementsQueryBuilder queryApplicationVersionBugFilingRequirements(String applicationVersionId) {
		return new SSCApplicationVersionBugFilingRequirementsQueryBuilder(conn(), applicationVersionId);
	}

	public SSCBugTrackersQueryBuilder queryBugTrackers() {
		return new SSCBugTrackersQueryBuilder(conn());
	}
	
	public JSONList getBugTrackers(String... fields) {
		return queryBugTrackers().paramFields(fields).build().getAll();
	}
	
	/**
	 * Get all short display names for all available native SSC bug tracker integrations,
	 * indexed by bug tracker plugin id.
	 * @return
	 */
	public Map<String, String> getBugTrackerShortDisplayNamesByIds() {
		return getBugTrackers().toMap("pluginId", String.class, "shortDisplayName", String.class);
	}
	
	/**
	 * Get the bug tracker plugin id's for the given bug tracker names in unspecified order
	 * @param bugTrackerPluginNames
	 * @return
	 */
	public Set<String> getBugTrackerPluginIdsForNames(final Set<String> bugTrackerPluginNames) {
		Map<String, String> namesById = getBugTrackerShortDisplayNamesByIds();
		namesById.values().removeIf(new Predicate<String>() {
			public boolean test(String name) {
				return !bugTrackerPluginNames.contains(name);
			}
		});
		return namesById.keySet();
	}
	
	/**
	 * Get a JSON object describing the configured native bug tracker integration for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	public JSONMap getApplicationVersionBugTracker(String applicationVersionId) {
		return queryApplicationVersionBugTracker(applicationVersionId).build().getUnique().get("bugTracker", JSONMap.class);
	}
	
	/**
	 * Get the short name for the configured native bug tracker integration for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	public String getApplicationVersionBugTrackerShortName(String applicationVersionId) {
		return InternalExpressionHelper.get().evaluateSimpleExpression(getApplicationVersionBugTracker(applicationVersionId), "shortDisplayName", String.class);
	}
	
	/**
	 * Authenticate with SSC native bug tracker integration
	 * @param applicationVersionId
	 * @param bugTrackerUserName
	 * @param bugTrackerPassword
	 */
	@SSCRequiredActionsPermitted({"POST=/api/v\\d+/projectVersions/\\d+/bugfilingrequirements/action"})
	public void authenticateForBugFiling(String applicationVersionId, String bugTrackerUserName, String bugTrackerPassword) {
		JSONMap request = new JSONMap();
		request.put("type", "login");
		request.put("ids", new JSONList()); // Is this necessary to add?
		request.putPath("values.username", bugTrackerUserName);
		request.putPath("values.password", bugTrackerPassword);
		/* JSONMap result = */ conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource().path("/api/v1/projectVersions")
				.path(applicationVersionId).path("bugfilingrequirements/action"),
				Entity.entity(request, "application/json"), JSONMap.class);
		// TODO Check result
	}
	
	/**
	 * File a bug via an SSC native bug tracker integration. Before calling this method, the 
	 * {@link #authenticateForBugFiling(String, String, String)} must have been called
	 * (if {@link #isBugTrackerAuthenticationRequired(String)} returns true).
	 * @param applicationVersionId
	 * @param issueDetails
	 * @param issueInstanceIds
	 * @return
	 */
	@SSCRequiredActionsPermitted({"POST=/api/v\\d+/projectVersions/\\d+/issues/action"})
	public JSONMap fileBug(String applicationVersionId, Map<String,Object> issueDetails, List<String> issueInstanceIds) {
		// TODO Clean up this code
		JSONMap bugFilingRequirements = getInitialBugFilingRequirements(applicationVersionId);
		Set<String> processedDependentParams = new HashSet<String>();
		boolean allDependentParamsProcessed = false;
		while ( !allDependentParamsProcessed ) {
			JSONList bugParams = bugFilingRequirements.get("bugParams", JSONList.class);
			JSONList bugParamsWithDependenciesAndChoiceList = bugParams.filter("hasDependentParams && choiceList.size()>0", true);
			LinkedHashMap<String,JSONMap> bugParamsMap = bugParamsWithDependenciesAndChoiceList.toMap("identifier", String.class, JSONMap.class);
			bugParamsMap.keySet().removeAll(processedDependentParams);
			if ( bugParamsMap.isEmpty() ) {
				allDependentParamsProcessed = true;
			} else {
				Iterator<Entry<String, JSONMap>> iterator = bugParamsMap.entrySet().iterator();
				while ( iterator.hasNext() ) {
					Map.Entry<String, JSONMap> entry = iterator.next();
					String key = entry.getKey();
					processedDependentParams.add(key);
					String value = (String)issueDetails.get(key);
					if ( value != null && !value.equals(entry.getValue().get("value")) ) {
						entry.getValue().put("value", value);
						bugFilingRequirements = getBugFilingRequirements(applicationVersionId, bugFilingRequirements, key);
						break;
					}
				}
			}
		}
		
		JSONList bugParams = bugFilingRequirements.get("bugParams", JSONList.class);
		JSONList bugParamsWithoutDependencies = bugParams.filter("hasDependentParams", false);
		for ( JSONMap bugParam : bugParamsWithoutDependencies.asValueType(JSONMap.class) ) {
			String key = bugParam.get("identifier", String.class);
			String value = (String)issueDetails.get(key);
			if ( value != null ) {
				bugParam.put("value", value);
			}
		}
		
		JSONMap request = new JSONMap();
		request.put("type", "FILE_BUG");
		request.put("actionResponse", "false");
		request.putPath("values.bugParams", bugFilingRequirements.get("bugParams", JSONList.class));
		request.putPath("values.issueInstanceIds", issueInstanceIds);
		return conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource().path("/api/v1/projectVersions")
				.path(applicationVersionId).path("issues/action"),
				Entity.entity(request, "application/json"), JSONMap.class);
	}
	
	/**
	 * Check whether SSC bug tracker authentication is required
	 * @param applicationVersionId
	 * @return
	 */
	public boolean isBugTrackerAuthenticationRequired(String applicationVersionId) {
		JSONMap bugFilingRequirements = getInitialBugFilingRequirements(applicationVersionId);
		return InternalExpressionHelper.get().evaluateSimpleExpression(bugFilingRequirements, "requiresAuthentication", Boolean.class);
	}
	
	/**
	 * Get a JSONMap describing the initial bug filing requirements for the given application version
	 * @param applicationVersionId
	 * @return
	 */
	private JSONMap getInitialBugFilingRequirements(String applicationVersionId) {
		return queryApplicationVersionBugFilingRequirements(applicationVersionId).build().getUnique();
	}
	
	/**
	 * Get a JSONMap describing the bug filing requirements for the given application version and
	 * given bug parameter data
	 * @param applicationVersionId
	 * @param data
	 * @param changedParamIdentifier
	 * @return
	 */
	private JSONMap getBugFilingRequirements(String applicationVersionId, JSONMap data, String changedParamIdentifier) {
		JSONList request = new JSONList();
		request.add(data);
		return queryApplicationVersionBugFilingRequirements(applicationVersionId)
				.paramChangedParamIdentifier(changedParamIdentifier).paramBugParams(request)
				.build().getUnique();
	}
}
