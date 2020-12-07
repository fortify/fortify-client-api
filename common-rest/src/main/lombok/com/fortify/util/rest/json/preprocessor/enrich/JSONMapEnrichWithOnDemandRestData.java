/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
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
package com.fortify.util.rest.json.preprocessor.enrich;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.WebTarget;

import com.fortify.util.rest.connection.IRestConnection;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.rest.json.embed.IEmbedDefinition;
import com.fortify.util.rest.json.ondemand.AbstractJSONMapOnDemandLoaderWithIRestConnection;

public class JSONMapEnrichWithOnDemandRestData extends JSONMapEnrichWithOnDemandProperty {
	public JSONMapEnrichWithOnDemandRestData(IRestConnection conn, IEmbedDefinition embedDefinition) {
		super(embedDefinition.getPropertyName(), new JSONMapOnDemandLoaderRestData(conn, true, embedDefinition));
	}

	private static final class JSONMapOnDemandLoaderRestData extends AbstractJSONMapOnDemandLoaderWithIRestConnection {
		private static final long serialVersionUID = 1L;
		private final IEmbedDefinition embedDefinition;
		
		public JSONMapOnDemandLoaderRestData(IRestConnection conn, boolean storeValue, IEmbedDefinition embedDefinition) {
			super(conn, storeValue);
			this.embedDefinition = embedDefinition;
		}

		@Override
		public Object getOnDemand(IRestConnection conn, String propertyName, JSONMap parent) {
			return !embedDefinition.isEnabled(parent)
					? null
					: getResult(conn, parent);
		}
		
		private Object getResult(IRestConnection conn, JSONMap parent) {
			WebTarget webTarget = getWebTarget(conn, parent);
			return embedDefinition.getResult(conn.executeRequest(HttpMethod.GET, webTarget, JSONMap.class));
		}

		protected WebTarget getWebTarget(IRestConnection conn, JSONMap parent) {
			String uri = embedDefinition.buildUri(parent);
			return conn.getResource(uri);
		}
	}
}
