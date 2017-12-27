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

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.builder.SSCApplicationVersionMetricHistoriesQueryBuilder;
import com.fortify.api.util.rest.json.JSONList;

/**
 * This class is used to access SSC metrics-related functionality (variables and performance indicators).
 * 
 * @author Ruud Senden
 *
 */
public class SSCMetricsAPI extends AbstractSSCAPI {
	public static enum MetricType {
		performanceIndicator, variable
	}
	
	public SSCMetricsAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionMetricHistoriesQueryBuilder queryApplicationVersionMetricHistories(String applicationVersionId, MetricType metricType) {
		return new SSCApplicationVersionMetricHistoriesQueryBuilder(conn(), applicationVersionId, metricType);
	}
	
	public JSONList getApplicationVersionMetrics(String applicationVersionId, MetricType metricType, boolean useCache) {
		return queryApplicationVersionMetricHistories(applicationVersionId, metricType).useCache(useCache).build().getAll();
	}
}
