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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.SSCApplicationVersionAttributesQuery;
import com.fortify.api.ssc.connection.api.query.SSCApplicationVersionAttributesQuery.SSCApplicationVersionAttributesQueryBuilder;
import com.fortify.api.ssc.connection.api.query.SSCAttributeDefinitionQuery;
import com.fortify.api.ssc.connection.api.query.SSCAttributeDefinitionQuery.SSCAttributeDefinitionQueryBuilder;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;

public class SSCAttributeAPI extends AbstractSSCAPI {
	public SSCAttributeAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionAttributesQueryBuilder queryApplicationVersionAttributes(String applicationVersionId) {
		return SSCApplicationVersionAttributesQuery.builder().conn(conn()).applicationVersionId(applicationVersionId);
	}
	
	public SSCAttributeDefinitionQueryBuilder queryAttributeDefinitions() {
		return SSCAttributeDefinitionQuery.builder().conn(conn());
	}
	
	/**
	 * Get all application version attribute values for the given application version,
	 * indexed by attribute name.
	 * @param applicationVersionId
	 * @return
	 */
	public Map<String, List<String>> getApplicationVersionAttributeValuesByName(String applicationVersionId) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		JSONList attrs = queryApplicationVersionAttributes(applicationVersionId)
				.paramFields(Arrays.asList("guid","value","values")).useCache(true).build().getAll();
		JSONList attrDefs = queryAttributeDefinitions().paramFields(Arrays.asList("guid","name")).useCache(true).build().getAll();
		for ( JSONMap attr : attrs.asValueType(JSONMap.class) ) {
			String attrName = attrDefs.mapValue("guid", attr.get("guid", String.class), "name", String.class);
			JSONList attrValuesArray = attr.get("values", JSONList.class);
			String attrValue = attr.get("value", String.class);
			List<String> attrValues = StringUtils.isNotBlank(attrValue) 
					? Arrays.asList(attrValue) 
					: attrValuesArray==null ? null : attrValuesArray.getValues("name", String.class);
			result.put(attrName, attrValues);
		}
		return result;
	}

}
