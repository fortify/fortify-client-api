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
package com.fortify.util.rest.query;

import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.core.UriBuilder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/**
 * Data class for describing sub-entities and/or arbitrary URI's
 * to be embedded into REST query results. 
 * 
 * @author Ruud Senden
 *
 */
@Data @SuperBuilder @AllArgsConstructor(access=AccessLevel.PACKAGE)
public class EmbedDescriptor {
	private String propertyName; 
	private String subEntity;
	private String uri;
	@Singular private Map<String, Object> params;
	
	public EmbedDescriptor() {}
	
	public String getPropertyName() {
		return propertyName!=null ? propertyName : subEntity; 
	}
	
	public String buildUriExpression(Function<String, String> subEntityToUriExpression) {
		String uriExpression = getUri()!=null ? getUri() : subEntityToUriExpression.apply(getSubEntity());
		Map<String, Object> params = getParams();
		if ( params!=null && !params.isEmpty() ) {
			UriBuilder uriBuilder = UriBuilder.fromUri(uriExpression);
			params.entrySet().stream().filter(e->e.getValue()!=null).forEach(e->uriBuilder.queryParam(e.getKey(), e.getValue()));
			uriExpression = uriBuilder.toTemplate();
		}
		return uriExpression;
	}
}
