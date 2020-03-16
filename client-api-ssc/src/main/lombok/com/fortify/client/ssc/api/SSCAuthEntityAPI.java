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

import com.fortify.client.ssc.api.query.builder.SSCAuthEntitiesQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access SSC application related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCAuthEntityAPI extends AbstractSSCAPI {
	public SSCAuthEntityAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCAuthEntitiesQueryBuilder queryAuthEntities() {
		return new SSCAuthEntitiesQueryBuilder(conn());
	}
	
	public JSONMap getAuthEntityByEntityName(String entityName, String... fields) {
		return queryAuthEntities()
				.paramEntityName(entityName).paramFields(fields)
				.build().getUnique();
	}
	
	public String getAuthEntityIdByEntityName(String entityName) {
		return getAuthEntityByEntityName(entityName, "id").get("id", String.class);
	}
}
