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
package com.fortify.util.rest.json.embed;

import java.io.Serializable;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.expression.helper.InternalExpressionHelper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Data class for describing REST data to be embedded into REST query results
 * 
 * @author Ruud Senden
 *
 */
@Data @SuperBuilder @RequiredArgsConstructor
public class StandardEmbedDefinition implements Serializable, IEmbedDefinition {
	private static final long serialVersionUID = 1L;
	private final StandardEmbedConfig config;
	
	@Override
	public String getPropertyName() {
		return config.getPropertyName();
	}
	
	@Override
	public boolean isEnabled(JSONMap parent) {
		return config.getEmbedIf()==null ? true : InternalExpressionHelper.get().evaluateSimpleExpression(parent, config.getEmbedIf(), Boolean.class);
	}
	
	@Override
	public String buildUri(JSONMap parent) {
		String uriExpr = getUriExpr();
		return InternalExpressionHelper.get().evaluateTemplateExpression(parent, uriExpr, String.class);
	}
	
	private String getUriExpr() {
		String uriExpr = config.getUri();
		Map<String, Object> params = config.getParams();
		if ( params!=null && !params.isEmpty() ) {
			UriBuilder uriBuilder = UriBuilder.fromUri(uriExpr);
			params.entrySet().stream().filter(e->e.getValue()!=null).forEach(e->uriBuilder.queryParam(e.getKey(), e.getValue()));
			uriExpr = uriBuilder.toTemplate();
		}
		return uriExpr;
	}
	
	@Override
	public Object getResult(JSONMap rawResult) {
		return InternalExpressionHelper.get().evaluateSimpleExpression(rawResult, config.getResultExpression(), Object.class);
	}
}
