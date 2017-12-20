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

import org.apache.commons.lang.StringUtils;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.builder.SSCApplicationVersionAttributesQueryBuilder;
import com.fortify.api.ssc.connection.api.query.builder.SSCAttributeDefinitionsQueryBuilder;
import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.util.rest.json.JSONMap;

public class SSCAttributeAPI extends AbstractSSCAPI {
	public SSCAttributeAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionAttributesQueryBuilder queryApplicationVersionAttributes(String applicationVersionId) {
		return new SSCApplicationVersionAttributesQueryBuilder(conn(), applicationVersionId);
	}
	
	public SSCAttributeDefinitionsQueryBuilder queryAttributeDefinitions() {
		return new SSCAttributeDefinitionsQueryBuilder(conn());
	}
	
	public JSONList getAttributeDefinitions(boolean useCache, String... fields) {
		return queryAttributeDefinitions().useCache(useCache).paramFields(fields==null?null:fields).build().getAll();
	}
	
	public JSONList getApplicationVersionAttributes(String applicationVersionId, boolean useCache, String... fields) {
		return queryApplicationVersionAttributes(applicationVersionId).useCache(useCache).paramFields(fields).build().getAll();
	}
	
	/**
	 * Get all application version attribute values for the given application version,
	 * indexed by attribute name. Attributes without any value will not be included
	 * in the result.
	 * @param applicationVersionId
	 * @return
	 */
	public JSONMap getApplicationVersionAttributeValuesByName(String applicationVersionId) {
		JSONMap result = new JSONMap();
		JSONList attrs = getApplicationVersionAttributes(applicationVersionId, true, "guid","value","values");
		JSONList attrDefs = getAttributeDefinitions(true, "guid","name");
		for ( JSONMap attr : attrs.asValueType(JSONMap.class) ) {
			String attrName = attrDefs.mapValue("guid", attr.get("guid", String.class), "name", String.class);
			JSONList attrValues = attr.get("values", JSONList.class);
			String attrValue = attr.get("value", String.class);
			if ( StringUtils.isNotBlank(attrValue) ) {
				result.put(attrName, new JSONList(Arrays.asList(attrValue))); 
			} else if ( attrValues!=null && attrValues.size()>0 ) {
				result.put(attrName, new JSONList(attrValues.getValues("name", String.class)));
			}
		}
		return result;
	}

}
