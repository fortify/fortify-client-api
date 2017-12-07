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
package com.fortify.api.wie.connection.api.query;

import java.util.List;

import com.fortify.api.util.rest.json.IJSONMapPreProcessor;
import com.fortify.api.util.rest.json.JSONMapFilterRegEx;
import com.fortify.api.wie.connection.WIEAuthenticatingRestConnection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Accessors;

@Getter(AccessLevel.PROTECTED) @Accessors(fluent=true)
@Builder
public class WIEMacrosQuery extends AbstractWIEEntityQuery {
	// Fields supported by AbstractRestConnectionWithCacheQuery
	private final WIEAuthenticatingRestConnection conn;
	private final @Singular List<IJSONMapPreProcessor> preProcessors;
	private final boolean useCache;
	
	public static class WIEMacrosQueryBuilder {
		public WIEMacrosQueryBuilder names(String... names) {
			return preProcessor(new JSONMapFilterRegEx("name", "\\Q"+String.join("\\E|\\Q", names)+"\\E", true));
		}
	}
	
	@Override
	protected boolean isPagingSupported() {
		return true;
	}
	
	@Override
	protected String getTargetPath() {
		return "/api/v1/macros";
	}

}
