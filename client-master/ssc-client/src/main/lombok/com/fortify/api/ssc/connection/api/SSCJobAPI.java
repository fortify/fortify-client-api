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

import org.apache.commons.collections.CollectionUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.builder.SSCJobsQueryBuilder;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.preprocessor.AbstractJSONMapFilter.MatchMode;
import com.fortify.api.util.rest.json.preprocessor.JSONMapFilterCompareDate;
import com.fortify.api.util.rest.json.preprocessor.JSONMapFilterCompareDate.DateComparisonOperator;
import com.fortify.api.util.rest.query.IRestConnectionQuery;

public class SSCJobAPI extends AbstractSSCAPI {
	public SSCJobAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCJobsQueryBuilder queryJobs() {
		return new SSCJobsQueryBuilder(conn());
	}
	
	public JSONMap waitForJobCompletion(String jobId, int timeOutSeconds) {
		long startTime = new Date().getTime();
		IRestConnectionQuery query = queryJobs().id(jobId).build();
		JSONMap job = query.getUnique();
		while ( new Date().getTime() < startTime+timeOutSeconds*1000 && "RUNNING".equals(job.get("state", String.class)) ) {
			try {
				Thread.sleep(1000L);
			} catch ( InterruptedException ignore ) {}
			job = query.getUnique();
		}
		return job;
	}
	
	public JSONList waitForJobCreation(IRestConnectionQuery query, long timeOutSeconds) {
		long startTime = new Date().getTime();
		JSONList jobs = query.getAll();
		while ( new Date().getTime() < startTime+timeOutSeconds*1000 && CollectionUtils.isEmpty(jobs) ) {
			try {
				Thread.sleep(1000L);
			} catch ( InterruptedException ignore ) {}
			jobs = query.getAll();
		}
		return jobs;
	}
	
	public static void main(String[] args) {
		SSCAuthenticatingRestConnection conn = SSCAuthenticatingRestConnection.builder().baseUrl("http://ssc:Admin123!@localhost:1710/ssc").build();
		JSONMap job = conn.api().job().queryJobs().maxResults(1).build().getUnique();
		System.out.println(job);
		
		IRestConnectionQuery query = conn.api().job().queryJobs()
				.jobClassName("com.fortify.manager.BLL.jobs.ArtifactUploadJob")
				.preProcessor(new JSONMapFilterCompareDate("finishTime", DateComparisonOperator.gt, new Date(), MatchMode.INCLUDE)).build();
		System.out.println(query.toString());
		System.out.println(conn.api().job().waitForJobCreation(query, 60));
	}

}
