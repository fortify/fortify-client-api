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
package com.fortify.client.ssc.json.preprocessor;

import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.AbstractJSONMapFilter;
import com.fortify.util.rest.query.IRestConnectionQueryConfigAware;

/**
 * Filter SSC application versions based on the SSC bug tracker plugin short display name configured
 * for each application version.
 * 
 * @author Ruud Senden
 *
 */
public class SSCJSONMapFilterApplicationVersionHasBugTrackerShortDisplayName extends AbstractJSONMapFilter implements IRestConnectionQueryConfigAware<SSCApplicationVersionsQueryBuilder>{
	private final String bugTrackerPluginShortDisplayName;
	
	public SSCJSONMapFilterApplicationVersionHasBugTrackerShortDisplayName(MatchMode matchMode, String bugTrackerPluginShortDisplayName) {
		super(matchMode);
		this.bugTrackerPluginShortDisplayName = bugTrackerPluginShortDisplayName;
	}
	
	@Override
	protected boolean isMatching(JSONMap json) {
		return bugTrackerPluginShortDisplayName.equals(json.getPath("bugTracker.bugTracker.shortDisplayName"));
	}
	
	@Override
	public void setRestConnectionQueryConfig(SSCApplicationVersionsQueryBuilder builder) {
		builder.onDemandBugTracker("bugTracker");
	}
}
