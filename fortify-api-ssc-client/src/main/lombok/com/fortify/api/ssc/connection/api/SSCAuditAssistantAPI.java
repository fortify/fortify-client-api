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
package com.fortify.api.ssc.connection.api;

import java.util.Date;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import org.apache.commons.collections.CollectionUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.SSCJobQuery;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.JSONMapFilterDateCompare;
import com.fortify.api.util.rest.json.JSONMapFilterDateCompare.DateComparisonOperator;
import com.fortify.api.util.rest.json.JSONMapFilterRegEx;
import com.fortify.api.util.spring.SpringExpressionUtil;

public class SSCAuditAssistantAPI extends AbstractSSCAPI {
	public SSCAuditAssistantAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	/**
	 * This method will invoke Audit Assistant for the given application version
	 * and return immediately.
	 * 
	 * @param applicationVersionId
	 * @return
	 */
	public boolean invokeAuditAssistant(String applicationVersionId) {
		JSONMap entity = new JSONMap();
		entity.put("type", "SEND_TO_AUDITASSISTANT");
		entity.put("actionResponse", true);
		JSONMap data = conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource().path("/api/v1/projectVersions/").path(applicationVersionId).path("/action"),
				Entity.entity(entity, "application/json"), JSONMap.class);
		return SpringExpressionUtil.evaluateExpression(data, "data.status=='success'", Boolean.class);
	}
	
	/**
	 * This method will invoke Audit Assistant for the given application version,
	 * and wait (up to the given time-out) for the audit assistant results to be processed.
	 * 
	 * @param applicationVersionId
	 * @param timeOutSeconds
	 * @return Artifact Id for the audit assistant artifact
	 */
	public String invokeAuditAssistant(String applicationVersionId, long timeOutSeconds) {
		String result = null;
		SSCJobAPI jobApi = conn().api().job();
		Date now = new Date();
		SSCJobQuery jobQuery = jobApi.query()
				// Query for artifact upload job
			.jobClassName("com.fortify.manager.BLL.jobs.ArtifactUploadJob")
				// Only for selected application version 
			.filter(new JSONMapFilterRegEx("projectVersionId", applicationVersionId, true))
				// Only for artifact names starting with 'AA_' (Audit Assistant)
			.filter(new JSONMapFilterRegEx("artifactName", "AA_.*\\.fpr", true))
				// Only include jobs finished after now (to wait for processing completion)
			.filter(new JSONMapFilterDateCompare("finishTime", DateComparisonOperator.gt, now, true))
				// Only include jobs started after now
			.filter(new JSONMapFilterDateCompare("startTime", DateComparisonOperator.gt, now, true))
			.build();
		if ( invokeAuditAssistant(applicationVersionId) ) {
			JSONList jobs = jobApi.waitForJobCreation(jobQuery, timeOutSeconds);
			System.out.println(jobs);
			if ( CollectionUtils.isNotEmpty(jobs) ) {
				result = ""+jobs.asValueType(JSONMap.class).get(0).getPath("jobData.PARAM_ARTIFACT_ID", Integer.class);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		SSCAuthenticatingRestConnection conn = new SSCAuthenticatingRestConnection("http://localhost:1710/ssc", "ssc",  "Admin123!", null);
		System.out.println(conn.api().auditAssistant().invokeAuditAssistant("8", 300));
	}

}
