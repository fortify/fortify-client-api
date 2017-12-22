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
package test;

import java.io.File;
import java.util.Date;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.SSCApplicationVersionAPI;
import com.fortify.api.ssc.connection.api.SSCMetricsAPI.MetricType;
import com.fortify.api.ssc.json.preprocessor.SSCJSONMapFilterApplicationVersionHasAllCustomTags;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.preprocessor.JSONMapFilterCompareDate;
import com.fortify.api.util.rest.json.preprocessor.AbstractJSONMapFilter.MatchMode;
import com.fortify.api.util.rest.json.preprocessor.JSONMapFilterCompareDate.DateComparisonOperator;
import com.fortify.api.util.rest.query.IRestConnectionQuery;

public class SSCSamples extends AbstractSamples {
	private final SSCAuthenticatingRestConnection conn;
	private final JSONMap applicationVersion;
	private final String applicationVersionId;
	
	
	public SSCSamples(String baseUrlWithCredentials) {
		this.conn = SSCAuthenticatingRestConnection.builder().baseUrl(baseUrlWithCredentials).build();
		this.applicationVersion = conn.api().applicationVersion().getApplicationVersionByNameOrId("WebGoat:5.0", ":");
		if ( this.applicationVersion == null ) {
			throw new IllegalStateException("Your SSC instance must have an application 'WebGoat' with version '5.0'");
		}
		this.applicationVersionId = this.applicationVersion.get("id", String.class);
		
	}

	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			throw new IllegalArgumentException("SSC URL in format http(s)://<user>:<password>@host:port/ssc must be provided as first parameter");
		}
		if ( args.length < 2 ) {
			throw new IllegalArgumentException("Path to FPR file must be provided as second parameter");
		}
		SSCSamples samples = new SSCSamples(args[0]);
		samples.sample1QueryApplicationVersions();
		samples.sample2UploadAndQueryArtifacts(args[1]);
		samples.sample3InvokeAuditAssistant();
		samples.sample4QueryApplicationVersionIssues();
		samples.sample5QueryJobs();
		samples.sample6WaitForJobCreation();
		samples.sample7QueryMetrics();
	}
	
	public final void sample1QueryApplicationVersions() throws Exception {
		print("\n\n---- Query application versions ----");
		SSCApplicationVersionAPI api = conn.api().applicationVersion();
		print(api.queryApplicationVersions().id(applicationVersionId).onDemandCustomTags("customTagNames", "name").build().getUnique().get("customTagNames"));
		for ( int i = 0 ; i < 10 ; i++ ) {
			print(api.queryApplicationVersions().applicationName("WebGoat").paramFields("id", "name").useCache(true).build().getAll());
			print(api.queryApplicationVersions().id(applicationVersionId).useCache(true).build().getAll());
			print(api.queryApplicationVersions().applicationName("WebGoat").versionName("5.0").useCache(true).build().getUnique());
			print(api.getApplicationVersionByNameOrId("WebGoat:5.0", ":"));
			print(api.queryApplicationVersions().useCache(true).onDemandAttributeValuesByName("test")
					.preProcessor(new SSCJSONMapFilterApplicationVersionHasAllCustomTags(MatchMode.INCLUDE, "test")).build().getAll());
			print(api.queryApplicationVersions().useCache(true).build().getAll());
		}
	}
	
	public final void sample2UploadAndQueryArtifacts(String artifactPath) throws Exception {
		print("\n\n---- Upload artifact and wait at most 1 minute for processing to complete ----");
		String artifactId = conn.api().artifact().uploadArtifactAndWaitProcessingCompletion(applicationVersionId, new File(artifactPath), 60);
		print(artifactId);
		print(conn.api().artifact().getArtifactById(artifactId, true));
		print(conn.api().artifact().getArtifactById(artifactId, true).get("uploadDate", Date.class).getClass().getName());
	}
	
	public final void sample3InvokeAuditAssistant() throws Exception {
		print("\n\n---- Invoke audit assistant and waiting for completion (wait at most 5 minutes)----");
		print(conn.api().auditAssistant().invokeAuditAssistant(applicationVersionId, 300));
	}
	
	public final void sample4QueryApplicationVersionIssues() throws Exception {
		print("\n\n---- Query application version issues including on-demand data ----");
		JSONList issues = conn.api().issue().queryIssues(applicationVersionId).onDemandDetails().onDemandAuditHistory().onDemandComments().maxResults(1).build().getAll();
		print(issues);
		print(issues.asValueType(JSONMap.class).get(0).get("issueDetails"));
		print(issues);
		print(issues.asValueType(JSONMap.class).get(0).get("issueComments"));
		print(issues);
		print(issues.asValueType(JSONMap.class).get(0).get("issueAuditHistory"));
		print(issues);
	}
	
	public final void sample5QueryJobs() throws Exception {
		print("\n\n---- Query jobs ----");
		JSONMap job = conn.api().job().queryJobs().maxResults(1).build().getUnique();
		print(job);
	}
	
	public final void sample6WaitForJobCreation() throws Exception {
		print("\n\n---- Wait 60 seconds for artifact upload job creation ----");
		print("(please upload artifact to application version WebGoat 5.0) ----");
		IRestConnectionQuery query = conn.api().job().queryJobs()
				.jobClassName("com.fortify.manager.BLL.jobs.ArtifactUploadJob")
				.preProcessor(new JSONMapFilterCompareDate(MatchMode.INCLUDE, "finishTime", DateComparisonOperator.gt, new Date())).build();
		print(query.toString());
		print(conn.api().job().waitForJobCreation(query, 60));
	}
	
	public final void sample7QueryMetrics() throws Exception {
		print("\n\n---- Query metrics ----");
		print(conn.api().metrics().queryApplicationVersionMetricHistories(applicationVersionId, MetricType.variable).useCache(true).build().getAll());
		print(conn.api().metrics().queryApplicationVersionMetricHistories(applicationVersionId, MetricType.performanceIndicator).useCache(true).build().getAll());
	}
	

}
