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

import com.fortify.client.fod.api.FoDReleaseAPI;
import com.fortify.client.fod.api.FoDVulnerabilityAPI;
import com.fortify.client.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.processor.AbstractJSONMapProcessor;

/**
 * This class demonstrates the use of (parts of) the FoD client API.
 * 
 * @author Ruud Senden
 *
 */
public class FoDSamples extends AbstractSamples {
	private final FoDAuthenticatingRestConnection conn;
	private final JSONMap release;
	private final String releaseId;
	
	
	public FoDSamples(String baseUrlWithCredentials) {
		this.conn = FoDAuthenticatingRestConnection.builder().baseUrl(baseUrlWithCredentials).build();
		this.release = conn.api(FoDReleaseAPI.class).getReleaseByNameOrId("WebGoat:5.0", true);
		if ( this.release == null ) {
			throw new IllegalStateException("Your FoD instance must have an application 'WebGoat' with release '5.0'");
		}
		this.releaseId = this.release.get("releaseId", String.class);
		
	}

	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			throw new IllegalArgumentException("FoD URL in format http(s)://<user>:<password>@host:port/ must be provided as first parameter");
		}
		FoDSamples samples = new FoDSamples(args[0]);
		samples.sample1QueryReleases();
		samples.sample2QueryVulnerabilities();
	}
	
	public final void sample1QueryReleases() throws Exception {
		printHeader("Query releases");
		FoDReleaseAPI api = conn.api(FoDReleaseAPI.class);
		
		printHeader("Query all releases, max 3 results");
		JSONList results = api.queryReleases().maxResults(3).paramFields("id").build().getAll();
		print(results);
		print("count: "+results.size());
		
		printHeader("Various application version queries to demonstrate caching");
		for ( int i = 0 ; i < 10 ; i++ ) {
			print(api.queryReleases().applicationName("WebGoat").paramFields("id", "name").useCache(true).build().getAll());
			print(api.queryReleases().releaseId(releaseId).useCache(true).build().getAll());
			print(api.queryReleases().applicationName("WebGoat").releaseName("5.0").useCache(true).build().getUnique());
			print(api.getReleaseByNameOrId("WebGoat:5.0", true));
			print(api.queryReleases().useCache(true).build().getAll());
		}
	}
	
	public final void sample2QueryVulnerabilities() throws Exception {
		printHeader("Query vulnerabilities");
		FoDVulnerabilityAPI api = conn.api(FoDVulnerabilityAPI.class);
		api.queryVulnerabilities(releaseId).maxResults(5).onDemandAll().build().processAll(new AbstractJSONMapProcessor() {
			
			@Override
			public void process(JSONMap json) {
				print("Vulnerability data");
				print(json);
				print("Vulnerability summary");
				print(json.get("summary", JSONMap.class));
				print("Vulnerability traces");
				print(json.get("traces", JSONList.class));
			}
		});
	}

}
