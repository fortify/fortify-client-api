/*******************************************************************************
 * (c) Copyright 2017 Hewlett Packard Enterprise Development LP
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
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
package com.fortify.api.ssc.json.preprocessor;

import java.util.Arrays;
import java.util.Collection;

import com.fortify.api.ssc.annotation.SSCCopyToConstructors;
import com.fortify.api.ssc.connection.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.preprocessor.AbstractJSONMapFilter;
import com.fortify.api.util.rest.query.IRestConnectionQueryConfigAware;

/**
 * Filter SSC application versions based on whether the SSC application version
 * contains values for all configured attribute name(s).
 * 
 * @author Ruud Senden
 *
 */
public class SSCJSONMapFilterApplicationVersionHasValuesForAllAttributes extends AbstractJSONMapFilter implements IRestConnectionQueryConfigAware<SSCApplicationVersionsQueryBuilder> {
	private final Collection<String> attributeNames;

	public SSCJSONMapFilterApplicationVersionHasValuesForAllAttributes(MatchMode matchMode,	Collection<String> attributeNames) {
		super(matchMode);
		this.attributeNames = attributeNames;
	}

	public SSCJSONMapFilterApplicationVersionHasValuesForAllAttributes(MatchMode matchMode, String... attributeNames) {
		this(matchMode, Arrays.asList(attributeNames));
	}

	@Override
	protected boolean isMatching(JSONMap json) {
		JSONMap attributeValuesByName = json.get("attributeValuesByName", JSONMap.class);
		return attributeValuesByName.keySet().containsAll(attributeNames);
	}

	@Override @SSCCopyToConstructors
	public void setRestConnectionQueryConfig(SSCApplicationVersionsQueryBuilder currentBuilder) {
		currentBuilder.onDemandAttributeValuesByName("attributeValuesByName");
	}
}
