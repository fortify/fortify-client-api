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
package com.fortify.client.ssc.json.preprocessor.filter;

import com.fortify.client.ssc.api.json.embed.SSCEmbedConfig.EmbedType;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.filter.AbstractJSONMapFilter;
import com.fortify.util.rest.query.IRestConnectionQueryConfigAware;

/**
 * Filter SSC application versions based on the SSC bug tracker plugin id configured
 * for each application version.
 * 
 * @author Ruud Senden
 *
 */
public class SSCJSONMapFilterApplicationVersionHasBugTrackerId extends AbstractJSONMapFilter implements IRestConnectionQueryConfigAware<SSCApplicationVersionsQueryBuilder>{
	private final String bugTrackerPluginId;
	private final EmbedType embedType;
	
	public SSCJSONMapFilterApplicationVersionHasBugTrackerId(MatchMode matchMode, String bugTrackerPluginId) {
		// For backward compatibility we use EmbedType.ONDEMAND by default
		this(matchMode, EmbedType.ONDEMAND, bugTrackerPluginId);
	}
	
	public SSCJSONMapFilterApplicationVersionHasBugTrackerId(MatchMode matchMode, EmbedType embedType, String bugTrackerPluginId) {
		super(matchMode);
		this.embedType = embedType;
		this.bugTrackerPluginId = bugTrackerPluginId;
	}
	
	@Override
	protected boolean isMatching(JSONMap json) {
		return bugTrackerPluginId.equals(json.getPath("bugTracker.bugTracker.id"));
	}
	
	@Override
	public void setRestConnectionQueryConfig(SSCApplicationVersionsQueryBuilder builder) {
		builder.embedBugtracker(embedType);
	}
}
