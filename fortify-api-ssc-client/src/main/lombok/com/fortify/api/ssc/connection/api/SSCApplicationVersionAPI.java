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
package com.fortify.api.ssc.connection.api;

import java.util.Arrays;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.ssc.connection.api.query.SSCApplicationVersionQuery;
import com.fortify.api.ssc.connection.api.query.SSCApplicationVersionQuery.SSCApplicationVersionQueryBuilder;

public class SSCApplicationVersionAPI extends AbstractSSCAPI {
	public SSCApplicationVersionAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public SSCApplicationVersionQueryBuilder query() {
		return SSCApplicationVersionQuery.builder().conn(conn());
	}
	
	public static void main(String[] args) {
		SSCAuthenticatingRestConnection conn = new SSCAuthenticatingRestConnection("http://localhost:1710/ssc", "ssc",  "Admin123!", null);
		System.out.println(1);
		System.out.println(conn.api().applicationVersion().query().applicationName("WebGoat").paramFields(Arrays.asList("id", "name")).build().getAll());
		System.out.println(2);
		System.out.println(conn.api().applicationVersion().query().id("6").build().getAll());
		System.out.println(3);
		System.out.println(conn.api().applicationVersion().query().applicationName("WebGoat").versionName("5.0").build().getUnique());
	}

}
