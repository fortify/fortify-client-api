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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fortify.client.ssc.annotation.SSCCopyToConstructors;
import com.fortify.client.ssc.api.query.builder.EmbedType;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionsQueryBuilder;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.preprocessor.filter.AbstractJSONMapFilter;
import com.fortify.util.rest.query.IRestConnectionQueryConfigAware;

/**
 * Filter SSC application versions based on whether the SSC application version
 * contains values for all configured attribute name(s).
 * 
 * @author Ruud Senden
 *
 */
public class SSCJSONMapFilterApplicationVersionHasValuesForAllAttributeIds extends AbstractJSONMapFilter implements IRestConnectionQueryConfigAware<SSCApplicationVersionsQueryBuilder> {
	private final Collection<String> attributeIds;
	private final EmbedType embedType;
	
	public SSCJSONMapFilterApplicationVersionHasValuesForAllAttributeIds(MatchMode matchMode,	EmbedType embedType, Collection<String> attributeIds) {
		super(matchMode);
		this.embedType = embedType;
		this.attributeIds = attributeIds;
	}

	public SSCJSONMapFilterApplicationVersionHasValuesForAllAttributeIds(MatchMode matchMode, EmbedType embedType, String... attributeGuids) {
		this(matchMode, embedType, Arrays.asList(attributeGuids));
	}

	@Override
	protected boolean isMatching(JSONMap json) {
		// SSC only returns attributes for which a value has been set, so we just need to check
		// whether all id's are available in the attributes list.
		List<String> availableAttributeIds = json.get("attributes", JSONList.class).getValues("id", String.class);
		return availableAttributeIds.containsAll(attributeIds);
	}

	@Override @SSCCopyToConstructors
	public void setRestConnectionQueryConfig(SSCApplicationVersionsQueryBuilder currentBuilder) {
		currentBuilder.embedAttributes(embedType);
	}
}
