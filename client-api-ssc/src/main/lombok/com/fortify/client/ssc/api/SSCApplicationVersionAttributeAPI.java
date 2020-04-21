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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;

import org.springframework.util.MultiValueMap;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.api.SSCAttributeDefinitionAPI.SSCAttributeDefinitionHelper;
import com.fortify.client.ssc.api.query.builder.SSCApplicationVersionAttributesQueryBuilder;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONList;
import com.fortify.util.rest.json.JSONMap;

/**
 * This class is used to access SSC application version attribute related functionality.
 * 
 * @author Ruud Senden
 *
 */
public class SSCApplicationVersionAttributeAPI extends AbstractSSCAPI {
	public SSCApplicationVersionAttributeAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionAttributesQueryBuilder queryApplicationVersionAttributes(String applicationVersionId) {
		return new SSCApplicationVersionAttributesQueryBuilder(conn(), applicationVersionId);
	}
	
	public JSONList getApplicationVersionAttributes(String applicationVersionId, String... fields) {
		return queryApplicationVersionAttributes(applicationVersionId).paramFields(fields).build().getAll();
	}
	
	public SSCApplicationVersionAttributesUpdater updateApplicationVersionAttributes(String applicationVersionId) {
		return new SSCApplicationVersionAttributesUpdater(applicationVersionId);
	}
	
	public final class SSCApplicationVersionAttributesUpdater {
		private final String applicationVersionId;
		private final JSONList requestData = new JSONList();
		private SSCAttributeDefinitionHelper attributeDefinitionHelper;
		private JSONMap attributeDefinitionsByNameOrId;
		
		private SSCApplicationVersionAttributesUpdater(String applicationVersionId) {
			this.applicationVersionId = applicationVersionId;
		}
		
		public SSCApplicationVersionAttributesUpdater withHelper(SSCAttributeDefinitionHelper attributeDefinitionHelper) {
			this.attributeDefinitionHelper = attributeDefinitionHelper;
			return this;
		}
		
		public SSCApplicationVersionAttributesUpdater byNameOrId(String attributeNameOrId, List<Object> attributeValues) {
			JSONMap attributeDefinition = getAttributeDefinitionsByNameOrId().get(attributeNameOrId, JSONMap.class);
			if ( attributeDefinition == null ) {
				throw new IllegalArgumentException("Attribute name or id "+attributeNameOrId+" does not exist");
			} else {
				JSONMap attributeData = new JSONMap();
				attributeData.put("attributeDefinitionId", attributeDefinition.get("id"));
				
				String type = attributeDefinition.get("type", String.class);
				
				// Check whether only single (or no) value is given if attribute type is not MULTIPLE 
				if ( !"MULTIPLE".equals(type) && attributeValues!=null && attributeValues.size()>1 ) {
					throw new IllegalArgumentException("Attribute "+attributeNameOrId+" can only contain a single value");
				}
				
				if ( "MULTIPLE".equals(type) || "SINGLE".equals(type) ) {
					attributeData.put("values", getGuidValuesList(attributeDefinition, attributeNameOrId, attributeValues));
					attributeData.put("value", null);
				} else {
					attributeData.put("values", null);
					attributeData.put("value", getSimpleValue(attributeValues));
				}
				requestData.add(attributeData);
			}
			return this;
		}
		
		public SSCApplicationVersionAttributesUpdater byNameOrId(MultiValueMap<String, Object> attributeNameOrIdToValuesMap) {
			for (Entry<String, List<Object>> entry : attributeNameOrIdToValuesMap.entrySet()) {
				byNameOrId(entry.getKey(), entry.getValue());
			}
			return this;
		}
		
		@SSCRequiredActionsPermitted({"PUT=/api/v\\d+/projectVersions/\\d+/attributes"})
		public JSONList execute() {
			JSONMap result = conn().executeRequest(HttpMethod.PUT, 
					conn().getBaseResource().path("/api/v1/projectVersions").path(applicationVersionId).path("attributes"), 
					Entity.entity(requestData, "application/json"), JSONMap.class);
			return result.get("data", JSONList.class);
		}
		
		private SSCAttributeDefinitionHelper getAttributeDefinitionHelper() {
			if ( attributeDefinitionHelper==null ) {
				attributeDefinitionHelper = conn().api(SSCAttributeDefinitionAPI.class).getAttributeDefinitionHelper();
			}
			return attributeDefinitionHelper;
		}
		
		private JSONMap getAttributeDefinitionsByNameOrId() {
			if ( attributeDefinitionsByNameOrId==null ) {
				attributeDefinitionsByNameOrId = getAttributeDefinitionHelper().getAttributeDefinitionsByNameAndId();
			}
			return attributeDefinitionsByNameOrId;
		}
		
		private Object getSimpleValue(List<Object> attributeValues) {
			Object value = attributeValues == null ? null : attributeValues.get(0);
			if ( value != null ) {
				if ( value instanceof Date ) {
					Date valueDate = (Date)value;
					value = new SimpleDateFormat("yyyy-MM-dd").format(valueDate);
				}
			}
			return value;
		}

		private JSONList getGuidValuesList(JSONMap attributeDefinition, String attributeNameOrId, List<Object> attributeValues) {
			JSONMap optionsByNameAndGuid = attributeDefinition.get("optionsByNameAndGuid", JSONMap.class);
			JSONList values = new JSONList();
			for ( Object optionValueOrGuid : attributeValues ) {
				JSONMap option = optionsByNameAndGuid.get(optionValueOrGuid, JSONMap.class);
				if ( option == null ) {
					throw new IllegalArgumentException("Invalid option "+optionValueOrGuid+" for attribute "+attributeNameOrId);
				} else {
					JSONMap value = new JSONMap();
					value.put("guid", option.get("guid", String.class));
					values.add(value);
				}
			}
			return values;
		}
	}

}
