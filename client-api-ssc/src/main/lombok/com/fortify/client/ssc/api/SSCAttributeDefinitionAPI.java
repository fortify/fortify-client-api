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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.query.builder.SSCAttributeDefinitionsQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * This class is used to access SSC attribute-related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCAttributeDefinitionAPI extends AbstractSSCAPI {
	public static enum SSCAttributeDefinitionCategory {
		TECHNICAL, ORGANIZATION, BUSINESS, DYNAMIC_SCAN_REQUEST
	}
	
	// TODO Differentiate between types supported by dynamic scan request vs other categories
	// Technical/Organization/Business support all types except FILE and SENSITIVE_TEXT
	// Dynamic scan request supports all types except DATE and INTEGER
	public static enum SSCAttributeDefinitionType {
		TEXT, LONG_TEXT, SENSITIVE_TEXT, SINGLE, MULTIPLE, BOOLEAN, INTEGER, DATE, FILE
	}
	
	public static enum SSCAttributeDefinitionAppEntityType {
		PROJECT_VERSION, DYNAMIC_SCAN_REQUEST, ALL
	}
	
	public SSCAttributeDefinitionAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCAttributeDefinitionsQueryBuilder queryAttributeDefinitions() {
		return new SSCAttributeDefinitionsQueryBuilder(conn());
	}
	
	public JSONList getAttributeDefinitions(String... fields) {
		return queryAttributeDefinitions().paramFields(fields).build().getAll();
	}
	
	/**
	 * Get an {@link SSCAttributeDefinitionHelper} instance for efficiently
	 * working with attribute definition data.
	 * @return New {@link SSCAttributeDefinitionHelper} instance
	 */
	public SSCAttributeDefinitionHelper getAttributeDefinitionHelper() {
		return new SSCAttributeDefinitionHelper();
	}
	
	/**
	 * This class provides various utility methods for working with SSC
	 * attribute definitions. Data is loaded from SSC only once per 
	 * {@link SSCAttributeDefinitionHelper} instance. As such it is 
	 * recommended to store and re-use a single instance of this class
	 * where possible. Keep in mind though that you need to get a fresh
	 * instance in order to see any attribute definition changes on SSC. 
	 * 
	 * @author Ruud Senden
	 *
	 */
	public final class SSCAttributeDefinitionHelper {
		private JSONList attributeDefinitions;
		
		/**
		 * Instances can only be created through the {@link SSCAttributeDefinitionAPI#getAttributeDefinitionHelper()}
		 * method.
		 */
		private SSCAttributeDefinitionHelper() {}
		
		/**
		 * Get the list of attribute definitions, lazy loading the list
		 * if it hasn't been loaded before by this instance.
		 * @return {@link JSONList} containing attribute definitions
		 */
		public JSONList getAttributeDefinitions() {
			if ( attributeDefinitions==null ) {
				attributeDefinitions = SSCAttributeDefinitionAPI.this.getAttributeDefinitions();
			}
			return attributeDefinitions;
		}
		
		/**
		 * Get the attribute definition id for the given attribute name
		 * @param attributeName for which to get the id
		 * @return attribute id for the given attribute name 
		 */
		public String getAttributeIdForName(String attributeName) {
			JSONList attributeDefinitions = getAttributeDefinitions();
			return attributeDefinitions.mapValue("name", attributeName, "id", String.class);
		}
		
		/**
		 * Get the attribute name for the given attribute definition id
		 * @param attributeId for which to get the name
		 * @return attribute name for the given attribute id
		 */
		public String getAttributeNameForId(String attributeId) {
			JSONList attributeDefinitions = getAttributeDefinitions();
			return attributeDefinitions.mapValue("id", attributeId, "name", String.class);
		}
		
		/**
		 * Get a {@link Map} containing all attribute definitions indexed by both name and id,
		 * useful for looking up attribute definitions by either name or id.
		 * @return {@link JSONMap} containing both attribute names and id's as keys, and the full attribute definition as values
		 */
		public JSONMap getAttributeDefinitionsByNameAndId() {
			JSONList attributeDefinitions = getAttributeDefinitions();
			JSONMap attributeDefinitionsByNameOrId = attributeDefinitions.toJSONMap("name", String.class, "#this", JSONMap.class);
			attributeDefinitionsByNameOrId.putAll(attributeDefinitions.toMap("id", String.class, JSONMap.class));
			return attributeDefinitionsByNameOrId;
		}
		
		/**
		 * Get a {@link MultiValueMap} containing all required attribute definition id's
		 * as keys, and one or more default values for that attribute as map value. Mainly
		 * used to automatically set default attribute values when creating new SSC application
		 * versions.
		 * @return {@link MultiValueMap}
		 */
		public MultiValueMap<String, Object> getRequiredAttributesWithDefaultValues() {
			MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
			JSONList attributeDefinitions = getAttributeDefinitions();
			for ( JSONMap attributeDefinition : attributeDefinitions.asValueType(JSONMap.class) ) {
				if ( !"DYNAMIC_SCAN_REQUEST".equals(attributeDefinition.get("category"))
						&& attributeDefinition.get("required", Boolean.class)
						&& Arrays.asList("PROJECT_VERSION", "ALL").contains(attributeDefinition.get("appEntityType"))
						&& !attributeDefinition.get("hasDefault", Boolean.class) ) {
					String id = attributeDefinition.get("id", String.class);
					JSONList options = attributeDefinition.get("options", JSONList.class);
					if ( options != null && !options.isEmpty() ) {
						result.add(id, options.mapValue("true", "true", "guid", String.class));
					} else {
						Object value;
						switch ( attributeDefinition.get("type", String.class) ) {
							case "INTEGER": value = 0; break;
							case "BOOLEAN": value = true; break;
							case "DATE": value = new Date(); break;
							default: value = "Auto-filled"; break;
						}
						result.add(id, value);
					}
				}
			}
			return result;
		}
	}
	
	/**
	 * Return an {@link SSCCreateAttributeDefinitionBuilder} instance to assist
	 * with adding a new attribute definition to SSC. Don't forget to call
	 * {@link SSCCreateAttributeDefinitionBuilder#execute()} to actually
	 * send the update request to SSC. 
	 * @return New {@link SSCCreateAttributeDefinitionBuilder} instance
	 */
	public SSCCreateAttributeDefinitionBuilder createAttributeDefinition() {
		return new SSCCreateAttributeDefinitionBuilder(conn());
	}
	
	@Setter @Accessors(fluent=true)
	public static final class SSCCreateAttributeDefinitionBuilder {
		private final SSCAuthenticatingRestConnection conn;
		@JsonProperty private String name;
		@JsonProperty private String description = "";
		@JsonProperty private boolean required = false;
		@JsonProperty private boolean hidden = false;
		@JsonProperty private SSCAttributeDefinitionCategory category = SSCAttributeDefinitionCategory.TECHNICAL;
		@JsonProperty private SSCAttributeDefinitionType type = SSCAttributeDefinitionType.TEXT;
		@JsonProperty private SSCAttributeDefinitionAppEntityType appEntityType = SSCAttributeDefinitionAppEntityType.PROJECT_VERSION;
		@JsonProperty private List<SSCAttributeDefinitionOption> options;
		
		/**
		 * Private constructor; instances can only be created through
		 * {@link SSCAttributeDefinitionAPI#createAttributeDefinition()}
		 * @param conn The {@link SSCAuthenticatingRestConnection} instance used to make requests to SSC
		 */
		private SSCCreateAttributeDefinitionBuilder(SSCAuthenticatingRestConnection conn) {
			this.conn = conn;
		}
		
		public SSCCreateAttributeDefinitionBuilder option(SSCAttributeDefinitionOption option) {
			if ( options==null ) { options = new ArrayList<>(); }
			options.add(option);
			return this;
		}
		
		@SSCRequiredActionsPermitted({"POST=/api/v\\d+/attributeDefinitions"})
		public JSONMap execute() {
			Assert.notNull(name, "Attribute definition name must be specified");
			if ( !SSCAttributeDefinitionType.SINGLE.equals(type) && !SSCAttributeDefinitionType.MULTIPLE.equals(type) ) {
				Assert.isNull(options, "Options are only supported for type SINGLE or MULTIPLE");
			}
			return conn.executeRequest(HttpMethod.POST, 
					conn.getBaseResource().path("/api/v1/attributeDefinitions"), 
					Entity.entity(this, "application/json"), JSONMap.class);	
		}
		
		@Setter @Accessors(fluent=true)
		public static final class SSCAttributeDefinitionOption {
			@JsonProperty private final String name;
			@JsonProperty private String description;
			@JsonProperty private boolean hidden;
			
			public SSCAttributeDefinitionOption(String name) {
				this.name = name;
			}
		}
	}

}
