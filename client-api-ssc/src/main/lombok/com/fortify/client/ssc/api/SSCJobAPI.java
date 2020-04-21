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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.fortify.client.ssc.api.query.builder.SSCJobsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.query.IRestConnectionQuery;

/**
 * This class is used to access SSC job-related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCJobAPI extends AbstractSSCAPI {
	public SSCJobAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCJobsQueryBuilder queryJobs() {
		return new SSCJobsQueryBuilder(conn());
	}
	
	public final JSONMap getJobById(String jobId, String... fields) {
		return queryJobs().id(jobId).paramFields(fields).build().getUnique();
	}
	
	public JSONMap waitForJobCompletion(String jobId, int timeOutSeconds) {
		Set<String> incompleteStates = new HashSet<>(Arrays.asList("RUNNING", "PREPARED", "WAITING_FOR_WORKER")); 
		long startTime = new Date().getTime();
		JSONMap job = getJobById(jobId, "state");
		while ( new Date().getTime() < startTime+timeOutSeconds*1000 && incompleteStates.contains(job.get("state", String.class)) ) {
			try {
				Thread.sleep(1000L);
			} catch ( InterruptedException ignore ) {}
			job = getJobById(jobId, "state");
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
}
