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
package com.fortify.client.ssc.api.query.builder;

import java.util.function.Consumer;

import com.fortify.client.ssc.annotation.SSCCopyToConstructors;
import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCAttributeAPI;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoaderWithConnection;
import com.fortify.util.rest.json.preprocessor.enrich.JSONMapEnrichWithOnDemandProperty;

public abstract class AbstractSSCApplicationVersionsQueryBuilder<T extends AbstractSSCApplicationVersionsQueryBuilder<T>>
		extends AbstractSSCEntityQueryBuilder<T> {
	
	public AbstractSSCApplicationVersionsQueryBuilder(SSCAuthenticatingRestConnection conn) {
		super(conn, true);
	}
	
	public T embedSubEntity(String entityName, boolean onDemand, String... fields) {
		return embed(entityName, "/api/v1/projectVersions/${id}/"+entityName, onDemand, fields);
	}
	
	public T embedAttributeValuesByName(boolean onDemand) {
		if ( onDemand ) {
			return preProcessor(new JSONMapEnrichWithOnDemandProperty("attributeValuesByName", 
				new SSCJSONMapOnDemandLoaderAttributeValuesByName(getConn())));
		} else {
			embedSubEntity("attributes", false, "guid", "value", "values");
			return pagePreProcessor(new SSCJSONListAddAttributeValuesByName(getConn(), "attributeValuesByName"));
		}
	}
	
	/**
	 * Use {@link #embedAttributeValuesByName()}
	 * @return
	 */
	@Deprecated 
	public T onDemandAttributeValuesByName() {
		return onDemandAttributeValuesByName("attributeValuesByName");
	}
	
	/**
	 * Add on-demand attribute for all application version attribute values by name.
	 * Attributes without any value will be excluded from the result.
	 * 
	 * Use {@link #embedAttributeValuesByName(boolean)} instead
	 * @param propertyName
	 * @return
	 */
	// TODO Add support for pre-loading instead of on-demand
	@Deprecated
	public T onDemandAttributeValuesByName(String propertyName) {
		return preProcessor(new JSONMapEnrichWithOnDemandProperty(propertyName, 
				new SSCJSONMapOnDemandLoaderAttributeValuesByName(getConn())));
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandAttributes() {
		return onDemandAttributes("attributes");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandBugTracker() {
		return onDemandBugTracker("bugTracker");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandCustomTags() {
		return onDemandCustomTags("customTags");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandFilterSets() {
		return onDemandFilterSets("filterSets");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandIssueSearchOptions() {
		return onDemandIssueSearchOptions("issueSearchOptions");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandPerformanceIndicatorHistories() {
		return onDemandPerformanceIndicatorHistories("performanceIndicatorHistories");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandVariableHistories() {
		return onDemandVariableHistories("variableHistories");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandResponsibilities() {
		return onDemandResponsibilities("responsibilities");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	public T onDemandResultProcessingRules() {
		return onDemandResultProcessingRules("processingRules");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/attributes" })
	public T onDemandAttributes(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/attributes");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/bugtracker" })
	public T onDemandBugTracker(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/bugtracker", fields));
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/customTags" })
	public T onDemandCustomTags(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/customTags", fields));
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/filterSets" })
	public T onDemandFilterSets(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/filterSets");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/issueSearchOptions" })
	public T onDemandIssueSearchOptions(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/issueSearchOptions", fields));
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/performanceIndicatorHistories" })
	public T onDemandPerformanceIndicatorHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/performanceIndicatorHistories", fields));
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/variableHistories" })
	public T onDemandVariableHistories(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/variableHistories", fields));
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/responsibilities" })
	public T onDemandResponsibilities(String propertyName) {
		return onDemand(propertyName, "/api/v1/projectVersions/${id}/responsibilities");
	}

	/**
	 * Use {@link #embedSubEntity(String, boolean, String...)} instead
	 * @return
	 */
	@Deprecated
	@SSCRequiredActionsPermitted({ "GET=/api/v\\d+/projectVersions/\\d+/resultProcessingRules" })
	public T onDemandResultProcessingRules(String propertyName, String... fields) {
		return onDemand(propertyName, appendOnDemandFields("/api/v1/projectVersions/${id}/resultProcessingRules", fields));
	}
	
	protected static final class SSCJSONMapOnDemandLoaderAttributeValuesByName extends AbstractJSONMapOnDemandLoaderWithConnection<SSCAuthenticatingRestConnection> {
		private static final long serialVersionUID = 1L;
		private volatile JSONList attrDefs;

		public SSCJSONMapOnDemandLoaderAttributeValuesByName(SSCAuthenticatingRestConnection conn) {
			super(conn, true);
		}
		
		@Override @SSCCopyToConstructors
		public Object getOnDemand(SSCAuthenticatingRestConnection conn, String propertyName, JSONMap parent) {
			if ( attrDefs==null ) {
				attrDefs = conn.api(SSCAttributeAPI.class).getAttributeDefinitions(true, "guid","name");
			}
			return conn.api(SSCAttributeAPI.class).getApplicationVersionAttributeValuesByName(parent.get("id",String.class), attrDefs);
		}
		
		@Override
		protected Class<SSCAuthenticatingRestConnection> getConnectionClazz() {
			return SSCAuthenticatingRestConnection.class;
		}
	}
	
	private static final class SSCJSONListAddAttributeValuesByName implements Consumer<JSONList> {
		private final SSCAuthenticatingRestConnection conn;
		private final String propertyName;
		private volatile JSONList attrDefs;
		
		public SSCJSONListAddAttributeValuesByName(SSCAuthenticatingRestConnection conn, String propertyName) {
			this.conn = conn;
			this.propertyName = propertyName;
		}
		@Override
		public void accept(JSONList list) {
			list.asValueType(JSONMap.class).forEach(this::addAttributeValuesByName);
		}
		private JSONList getAttributeDefinitions() {
			if ( attrDefs==null ) {
				attrDefs = conn.api(SSCAttributeAPI.class).getAttributeDefinitions(true, "guid","name");
			}
			return attrDefs;
		}
		
		private void addAttributeValuesByName(JSONMap json) {
			JSONList attrs = json.getPath("attributes", JSONList.class);
			if ( attrs==null ) {
				throw new IllegalArgumentException("Application version does not contain attributes list");
			}
			JSONMap attrValuesByName = conn.api(SSCAttributeAPI.class).convertApplicationVersionAttributeValuesListToMap(attrs, getAttributeDefinitions());
			json.put(propertyName, attrValuesByName);
		}
	}
}