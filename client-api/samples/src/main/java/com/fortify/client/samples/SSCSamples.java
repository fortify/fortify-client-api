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
package com.fortify.client.samples;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import com.fortify.client.ssc.api.SSCApplicationVersionAPI;
import com.fortify.client.ssc.api.SSCArtifactAPI;
import com.fortify.client.ssc.api.SSCAuditAssistantAPI;
import com.fortify.client.ssc.api.SSCIssueAPI;
import com.fortify.client.ssc.api.SSCJobAPI;
import com.fortify.client.ssc.api.SSCMetricsAPI;
import com.fortify.client.ssc.api.SSCMetricsAPI.MetricType;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.client.ssc.json.preprocessor.filter.SSCJSONMapFilterApplicationVersionHasAllCustomTags;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.filter.AbstractJSONMapFilter.MatchMode;
import com.fortify.util.rest.json.preprocessor.filter.JSONMapFilterCompareDate;
import com.fortify.util.rest.json.preprocessor.filter.JSONMapFilterCompareDate.DateComparisonOperator;
import com.fortify.util.rest.query.IRestConnectionQuery;

/**
 * This class demonstrates the use of (parts of) the SSC client API.
 * 
 * @author Ruud Senden
 *
 */
public class SSCSamples extends AbstractSamples {
	private final SSCAuthenticatingRestConnection conn;
	private JSONMap applicationVersion;
	private String applicationVersionId;
	
	
	public SSCSamples(String baseUrlWithCredentials) {
		this.conn = SSCAuthenticatingRestConnection.builder().baseUrl(baseUrlWithCredentials).build();
	}

	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			throw new IllegalArgumentException("SSC URL in format http(s)://<user>:<password>@host:port/ssc must be provided as first parameter");
		}
		if ( args.length < 2 ) {
			throw new IllegalArgumentException("Path to FPR file must be provided as second parameter");
		}
		SSCSamples samples = new SSCSamples(args[0]);
		samples.sample0CreateApplicationVersion();
		samples.sample1QueryApplicationVersions();
		JSONMap artifact = samples.sample2UploadAndQueryArtifacts(args[1]);
		samples.sample3ApproveArtifact(artifact);
		samples.sample4InvokeAuditAssistant();
		samples.sample5QueryApplicationVersionIssues();
		samples.sample6QueryJobs();
		samples.sample7WaitForJobCreation();
		samples.sample8QueryMetrics();
	}
	
	public final void sample0CreateApplicationVersion() throws Exception {
		printHeader("Create application version");
		SSCApplicationVersionAPI api = conn.api(SSCApplicationVersionAPI.class);
		this.applicationVersionId = api.createApplicationVersion()
			.applicationName("SSCSamples").versionName(UUID.randomUUID().toString())
			.autoAddRequiredAttributes(true).issueTemplateName("Prioritized High Risk Issue Template").execute();
		this.applicationVersion = api.getApplicationVersionById(applicationVersionId, false);
	}
	
	public final void sample1QueryApplicationVersions() throws Exception {
		printHeader("Query application versions");
		SSCApplicationVersionAPI api = conn.api(SSCApplicationVersionAPI.class);
		
		printHeader("Query all versions, max 3 results");
		JSONList results = api.queryApplicationVersions().maxResults(3).paramFields("id").build().getAll();
		print(results);
		print("count: "+results.size());
		
		printHeader("Get custom tag names for current application version");
		print(api.queryApplicationVersions().id(applicationVersionId).onDemandCustomTags("customTagNames", "name").build().getUnique().get("customTagNames"));
		
		printHeader("Various application version queries to demonstrate caching");
		for ( int i = 0 ; i < 10 ; i++ ) {
			print(api.queryApplicationVersions().applicationName("WebGoat").paramFields("id", "name").useCache(true).build().getAll());
			print(api.queryApplicationVersions().id(applicationVersionId).useCache(true).build().getAll());
			print(api.queryApplicationVersions().applicationName("WebGoat").versionName("5.0").useCache(true).build().getUnique());
			print(api.getApplicationVersionByNameOrId("WebGoat:5.0", true));
			print(api.queryApplicationVersions().useCache(true).onDemandAttributeValuesByName("test")
					.preProcessor(new SSCJSONMapFilterApplicationVersionHasAllCustomTags(MatchMode.INCLUDE, "test")).build().getAll());
			print(api.queryApplicationVersions().useCache(true).build().getAll());
		}
	}
	
	public final JSONMap sample2UploadAndQueryArtifacts(String artifactPath) throws Exception {
		printHeader("Upload artifact and wait at most 1 minute for processing to complete");
		String artifactId = conn.api(SSCArtifactAPI.class).uploadArtifactAndWaitProcessingCompletion(applicationVersionId, new File(artifactPath), 60);
		print(artifactId);
		if ( artifactId != null ) {
			JSONMap artifact = conn.api(SSCArtifactAPI.class).getArtifactById(artifactId, true);
			print(artifact);
			print(artifact.get("uploadDate", Date.class).getClass().getName());
			return artifact;
		} else {
			return null;
		}
	}
	
	public final void sample3ApproveArtifact(JSONMap artifact) {
		printHeader("Approve artifact if necessary");
		if ( artifact != null && artifact.get("status", String.class).equals("REQUIRE_AUTH") ) {
			conn.api(SSCArtifactAPI.class).approveArtifact(artifact.get("id",String.class), "Auto-approved");
		}
	}
	
	public final void sample4InvokeAuditAssistant() throws Exception {
		printHeader("Invoke audit assistant and waiting for completion (wait at most 5 minutes)");
		print(conn.api(SSCAuditAssistantAPI.class).invokeAuditAssistant(applicationVersionId, 300));
	}
	
	public final void sample5QueryApplicationVersionIssues() throws Exception {
		printHeader("Query application version issues including on-demand data");
		JSONList issues = conn.api(SSCIssueAPI.class).queryIssues(applicationVersionId).onDemandDetails().onDemandAuditHistory().onDemandComments().maxResults(1).build().getAll();
		print(issues);
		print(issues.asValueType(JSONMap.class).get(0).get("issueDetails"));
		print(issues);
		print(issues.asValueType(JSONMap.class).get(0).get("issueComments"));
		print(issues);
		print(issues.asValueType(JSONMap.class).get(0).get("issueAuditHistory"));
		print(issues);
	}
	
	public final void sample6QueryJobs() throws Exception {
		printHeader("Query jobs ----");
		JSONMap job = conn.api(SSCJobAPI.class).queryJobs().maxResults(1).build().getUnique();
		print(job);
	}
	
	public final void sample7WaitForJobCreation() throws Exception {
		printHeader("Wait 60 seconds for artifact upload job creation");
		printHeader("(please upload artifact to any application version)");
		IRestConnectionQuery query = conn.api(SSCJobAPI.class).queryJobs()
				.jobClassName("com.fortify.manager.BLL.jobs.ArtifactUploadJob")
				.preProcessor(new JSONMapFilterCompareDate(MatchMode.INCLUDE, "finishTime", DateComparisonOperator.gt, new Date())).build();
		print(query.toString());
		print(conn.api(SSCJobAPI.class).waitForJobCreation(query, 60));
	}
	
	public final void sample8QueryMetrics() throws Exception {
		printHeader("Query metrics");
		print(conn.api(SSCMetricsAPI.class).queryApplicationVersionMetricHistories(applicationVersionId, MetricType.variable).useCache(true).build().getAll());
		print(conn.api(SSCMetricsAPI.class).queryApplicationVersionMetricHistories(applicationVersionId, MetricType.performanceIndicator).useCache(true).build().getAll());
	}
	

}
