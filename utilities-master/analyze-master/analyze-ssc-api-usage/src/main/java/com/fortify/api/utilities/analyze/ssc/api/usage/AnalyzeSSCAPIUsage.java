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
package com.fortify.api.utilities.analyze.ssc.api.usage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fortify.api.utilities.analyze.CallsToAnnotatedMethodsAnalyzer;

// TODO Add documentation
// TODO clean up this class
public class AnalyzeSSCAPIUsage {
	public static void main(String[] jars) throws IOException {
		if ( jars==null || jars.length==0 ) {
			System.err.println("Usage:\n");
			System.err.println("\tjava -jar <fortify-api-ssc-token-spec-generator-[version].jar> <Jar file(s) to analyze>\n");
			System.err.println("If your SSC client is available as a shaded JAR, you will only need to provide this single");
			System.err.println("shaded JAR file. Otherwise, you will need to provide your main JAR file, as well as all");
			System.err.println("dependencies, including the fortify-api-ssc-client-[version].jar file.");
		} else {
			Map<String, Set<String>> methodsToAnnotationValuesMap = 
					new CallsToAnnotatedMethodsAnalyzer().findDirectAndIndirectInvocationsToAnnotatedMethods(jars, "Lcom/fortify/api/ssc/annotation/SSCRequiredActionsPermitted;", "Lcom/fortify/api/ssc/annotation/SSCCopyToConstructors;");
					// TODO new CallsToAnnotatedMethodsAnalyzer().findDirectAndIndirectInvocationsToAnnotatedMethods(jars, "Lcom/fortify/api/ssc/annotation/SSCCopyRequiredActionsPermittedFrom;");
			Set<String> requiredActionsPermitted = getRequiredActionsPermitted(methodsToAnnotationValuesMap);
			printTokenDefinition(requiredActionsPermitted);
		}
	}

	private static void printTokenDefinition(Set<String> requiredActionsPermitted) {
		System.out.println("\n\nThe SSC token specification to be added to SSC's WEB-INF/internal/serviceContext.xml");
		System.out.println("can be found below. Note that you will need to replace <tokenId> and <tokenName> with");
		System.out.println("appropriate values.\n");
		
		System.out.println("This token specification is generated based on regular Java method calls to the SSC");
		System.out.println("API. Custom REST API calls through the various executeRequest() methods are not taken");
		System.out.println("into account, unless the corresponding methods are annotated with the appropriate");
		System.out.println("@SSCRequiredActionsPermitted annotation. Any API calls made through Java reflection");
		System.out.println("are never taken into account for generating this token specification.\n");
		
		StringBuffer sb = new StringBuffer(
			"\t<bean id='<tokenId>' class='com.fortify.manager.security.ws.AuthenticationTokenSpec'>\n"+
			"\t\t<property name='key' value='<tokenName>'/>\n"+
			"\t\t<property name='maxDaysToLive' value='90' />\n"+
			"\t\t<property name='actionPermitted'>\n"+
			"\t\t\t<list value-type='java.lang.String'>\n");
		for ( String requiredActionPermitted : requiredActionsPermitted ) {
			sb.append("\t\t\t\t<value>"+requiredActionPermitted+"</value>\n");
		}
		sb.append(
			"\t\t\t</list>\n"+
			"\t\t</property>\n"+
			"\t\t<property name='terminalActions'>\n"+
			"\t\t\t<list value-type='java.lang.String'>\n"+
			"\t\t\t\t<value>InvalidateTokenRequest</value>\n"+
			"\t\t\t\t<value>DELETE=/api/v\\d+/auth/token</value>\n"+
			"\t\t\t</list>\n"+
			"\t\t</property>\n"+
			"\t</bean>\n");
		
		System.out.println(sb.toString().replace('\'', '"'));
		
	}

	protected static Set<String> getRequiredActionsPermitted(Map<String, Set<String>> methodsToAnnotationValuesMap) {
		// TODO Should we also report any API method invocations outside of
		// methods?
		// (i.e. field initializers, static blocks, ...)
		Set<String> requiredActionsPermitted = new TreeSet<>();
		System.out.println("\nMethods in custom code that require SSC permitted actions: ");
		for (Map.Entry<String, Set<String>> entry : methodsToAnnotationValuesMap.entrySet()) {
			if (!entry.getKey().startsWith("com/fortify/api/ssc")) {
				System.out.println(entry.getKey()+": "+entry.getValue());
				requiredActionsPermitted.addAll(entry.getValue());
			}
		}
		return requiredActionsPermitted;
	}
}
