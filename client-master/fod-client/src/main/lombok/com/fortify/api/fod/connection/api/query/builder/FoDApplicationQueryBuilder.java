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
package com.fortify.api.fod.connection.api.query.builder;

import com.fortify.api.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.api.fod.json.ondemand.FoDJSONMapOnDemandLoaderRest;
import com.fortify.api.util.rest.json.JSONMap;
import com.fortify.api.util.rest.json.ondemand.AbstractJSONMapOnDemandLoader;
import com.fortify.api.util.rest.json.preprocessor.JSONMapEnrichWithOnDemandProperty;

public class FoDApplicationQueryBuilder extends AbstractFoDEntityQueryBuilder<FoDApplicationQueryBuilder> {
	public FoDApplicationQueryBuilder(FoDAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v3/applications");
		// TODO Add PreProcessor to add deep link?
	}
	
	@Override
	public FoDApplicationQueryBuilder paramFields(String... fields) {
		return super.paramFields(fields);
	}
	
	@Override
	public FoDApplicationQueryBuilder paramFilterAnd(String field, String... values) {
		return super.paramFilterAnd(field, values);
	}
	
	@Override
	public FoDApplicationQueryBuilder paramFilterAnd(String filter) {
		return super.paramFilterAnd(filter);
	}
	
	@Override
	public FoDApplicationQueryBuilder paramOrderBy(String orderBy, OrderByDirection orderByDirection) {
		return super.paramOrderBy(orderBy, orderByDirection);
	}
	
	public FoDApplicationQueryBuilder applicationId(String applicationId) {
		return super.paramFilterAnd("applicationId", applicationId);
	}
	
	public FoDApplicationQueryBuilder applicationName(String applicationName) {
		return super.paramFilterAnd("applicationName", applicationName);
	}
	
	public FoDApplicationQueryBuilder applicationType(String applicationType) {
		return super.paramFilterAnd("applicationType", applicationType);
	}
	
	public FoDApplicationQueryBuilder includeAttributesMap() {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty("attributesMap", new AttributesMapOnDemandLoader()));
	}
	
	public FoDApplicationQueryBuilder onDemandReleases() {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty("releases", 
				new FoDJSONMapOnDemandLoaderRest(getConn(), "/api/v3/applications/${applicationId}/releases")));
	}
	
	private static class AttributesMapOnDemandLoader extends AbstractJSONMapOnDemandLoader {
		private static final long serialVersionUID = 1L;
		public AttributesMapOnDemandLoader() {
			super(true);
		}
		
		@Override
		public Object getOnDemand(String propertyName, JSONMap parent) {
			return parent.getOrCreateJSONList("attributes")
					.filter("value!='(Not Set)'", true).toMap("name", String.class, "value", String.class);
		}
	}
}
