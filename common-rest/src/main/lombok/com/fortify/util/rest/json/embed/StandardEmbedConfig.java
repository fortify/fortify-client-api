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

import com.fortify.util.rest.json.embed.StandardEmbedDefinition.OnErrorAction;

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
@Data @SuperBuilder @AllArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class StandardEmbedConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private String propertyName;
	private String subEntity;
	private String uri;
	private String resultExpression;
	private String embedIf;
	private OnErrorAction onError;
	@Singular private Map<String, Object> params;
	
	public StandardEmbedConfig() {}
	
	public String getPropertyName() {
		return propertyName!=null ? propertyName : getPropertyNameFromSubEntity();
	}
	
	protected String getPropertyNameFromSubEntity() {
		return subEntity==null ? null : subEntity.replaceAll("-", "_").replaceAll("\\W", "");
	}

	public String getUri() {
		return uri!=null ? uri : getSubEntityUri();
	}
	
	protected String getSubEntityUri() {
		return subEntity==null ? null : getSubEntityUri(subEntity);
	}

	public String getResultExpression() {
		return resultExpression!=null ? resultExpression : getDefaultResultExpression();
	}
	
	public OnErrorAction getOnError() {
		return onError==null ? OnErrorAction.FAIL : onError;
	}
	
	protected String getDefaultResultExpression() {
		return null;
	}
	
	protected String getSubEntityUri(String subEntity) {
		throw new RuntimeException("Embedding sub-entities is not supported by this embed configuration class");
	}
}
