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
package com.fortify.client.fod.json.preprocessor.filter;

import java.util.Arrays;
import java.util.Collection;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.filter.AbstractJSONMapFilter;

/**
 * Filter SSC application versions based configured names or id's
 * 
 * @author Ruud Senden
 *
 */
public class FoDJSONMapFilterApplicationReleaseNamesOrIds extends AbstractJSONMapFilter {
	private final Collection<String> namesOrIds;
	
	public FoDJSONMapFilterApplicationReleaseNamesOrIds(MatchMode matchMode, Collection<String> namesOrIds) {
		super(matchMode);
		this.namesOrIds = namesOrIds;
	}
	
	public FoDJSONMapFilterApplicationReleaseNamesOrIds(MatchMode matchMode, String[] namesOrIds) {
		this(matchMode, Arrays.asList(namesOrIds));
	}
	
	public FoDJSONMapFilterApplicationReleaseNamesOrIds(MatchMode matchMode, String namesOrIds) {
		this(matchMode, namesOrIds.split(","));
	}

	@Override
	protected boolean isMatching(JSONMap json) {
		boolean result = false;
		for ( String nameOrId : namesOrIds ) {
			String[] appVersionElements = nameOrId.split(":"); // TODO Make separator configurable?
			if ( appVersionElements.length == 1 ) {
				result |= appVersionElements[0].equals(json.get("releaseId", String.class));
			} else if ( appVersionElements.length == 2 ) {
				result |= appVersionElements[0].equals(json.getPath("releaseName", String.class)) 
						&& appVersionElements[1].equals(json.get("applicationName", String.class));
			}
			if ( result == true ) break;
		}
		return result;
	}
}
