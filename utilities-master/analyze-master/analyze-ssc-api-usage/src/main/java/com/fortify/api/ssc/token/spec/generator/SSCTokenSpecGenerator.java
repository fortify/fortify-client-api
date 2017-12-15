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
package com.fortify.api.ssc.token.spec.generator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

// TODO Add documentation
// TODO clean up this class
public class SSCTokenSpecGenerator {
	public static void main(String[] jars) throws IOException {
		if ( jars==null || jars.length==0 ) {
			System.err.println("Usage:\n");
			System.err.println("\tjava -jar <fortify-api-ssc-token-spec-generator-[version].jar> <Jar file(s) to analyze>\n");
			System.err.println("If your SSC client is available as a shaded JAR, you will only need to provide this single");
			System.err.println("shaded JAR file. Otherwise, you will need to provide your main JAR file, as well as all");
			System.err.println("dependencies, including the fortify-api-ssc-client-[version].jar file.");
		} else {
			Map<String, Set<String>> methodsToAnnotationValuesMap = new HashMap<>();
			if (findAnnotatedMethods(jars, methodsToAnnotationValuesMap)) {
				while (findIndirectMethodInvocations(jars, methodsToAnnotationValuesMap)) {
				}
			}
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
		Set<String> requiredActionsPermitted = new HashSet<>();
		System.out.println("\nMethods in custom code that require SSC permitted actions: ");
		for (Map.Entry<String, Set<String>> entry : methodsToAnnotationValuesMap.entrySet()) {
			if (!entry.getKey().startsWith("com/fortify/api/ssc")) {
				System.out.println(entry.getKey()+": "+entry.getValue());
				requiredActionsPermitted.addAll(entry.getValue());
			}
		}
		return requiredActionsPermitted;
	}

	protected static boolean findAnnotatedMethods(String[] jars, Map<String, Set<String>> methodsToAnnotationValuesMap) {
		System.out.println("Scanning for methods annotated with @SSCRequiredActionsPermitted");
		FindMethodsAnnotatedWithSSCRequiredActionsPermitted classVisitor = new FindMethodsAnnotatedWithSSCRequiredActionsPermitted(methodsToAnnotationValuesMap);
		visitClasses(jars, classVisitor);
		System.out.println("Methods and required actions permitted found until now:");
		System.out.println(methodsToAnnotationValuesMap);
		return classVisitor.hasFoundNew();
	}

	protected static boolean findIndirectMethodInvocations(String[] jars, Map<String, Set<String>> methodsToAnnotationValuesMap) {
		System.out.println("Next round of scanning for methods that indirectly call methods annotated with @SSCRequiredActionsPermitted");
		FindInvocationsToMethodsInAnnotationValuesMap classVisitor = new FindInvocationsToMethodsInAnnotationValuesMap(methodsToAnnotationValuesMap);
		visitClasses(jars, classVisitor);
		System.out.println("Methods and required actions permitted found until now:");
		System.out.println(methodsToAnnotationValuesMap);
		return classVisitor.hasFoundNew();
	}

	protected static void visitClasses(String[] jars, ClassVisitor classVisitor) {
		for (String jar : jars) {
			JarFile jarFile = null;
			try {
				try {
					jarFile = new JarFile(jar);
			        Enumeration<JarEntry> entries = jarFile.entries();
		
			        while (entries.hasMoreElements()) {
			            JarEntry entry = entries.nextElement();
		
			            if (entry.getName().endsWith(".class")) {
			            	try {
				                InputStream stream = null;
				                try {
				                	stream = new BufferedInputStream(jarFile.getInputStream(entry), 1024);
				                	new ClassReader(stream).accept(classVisitor, 0);
								} finally {
				                	stream.close();
				                }
			            	} catch ( IOException e ) { e.printStackTrace(); }
			            }
			        }
				} finally {
					jarFile.close();
				}
			} catch ( IOException e ) { e.printStackTrace(); }
		}
	}
	
	/**
	 * Abstract {@link ClassVisitor} implementation that keeps track of current class name
	 * being visited.
	 * 
	 * @author Ruud Senden
	 *
	 */
	static abstract class AbstractClassVisitor extends ClassVisitor {
		private String className;
		
		public AbstractClassVisitor() {
			super(Opcodes.ASM5);
		}
		
		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			this.className = name;
			super.visit(version, access, name, signature, superName, interfaces);
		}
		
		public String getClassName() {
			return className;
		}
	}

	/**
	 * Abstract {@link ClassVisitor} implementation that allows for updating a
	 * given methodsToAnnotationValuesMap.
	 * 
	 * @author Ruud Senden
	 *
	 */
	static abstract class AbstractAnnotationValuesClassVisitor extends AbstractClassVisitor {
		private final Map<String, Set<String>> methodsToAnnotationValuesMap;
		private boolean foundNew = false;
		
		public AbstractAnnotationValuesClassVisitor(Map<String, Set<String>> methodsToAnnotationValuesMap) {
			this.methodsToAnnotationValuesMap = methodsToAnnotationValuesMap;
		}
		
		protected void addValues(String method, Set<String> values) {
			Set<String> existingValues = methodsToAnnotationValuesMap.get(method);
			if (existingValues == null) {
				existingValues = new HashSet<String>();
				methodsToAnnotationValuesMap.put(method, existingValues);
			}
			foundNew |= existingValues.addAll(values);
		}

		public boolean hasFoundNew() {
			return foundNew;
		}
		
		public Map<String, Set<String>> getMethodsToAnnotationValuesMap() {
			return methodsToAnnotationValuesMap;
		}
	}

	/**
	 * Abstract {@link MethodVisitor} implementation that keeps track of 
	 * class name and method currently being visited.
	 * 
	 * @author Ruud Senden
	 *
	 */
	static abstract class AbstractMethodVisitor extends MethodVisitor {
		private final String className;
		private final String methodName;
		
		public AbstractMethodVisitor(String className, String methodName) {
			super(Opcodes.ASM5);
			this.className = className;
			this.methodName = methodName;
		}

		// TODO Add method parameter types
		public String getMethodDescription() {
			return className+"."+methodName;
		}
	}

	/**
	 * {@link ClassVisitor} implementation that finds all methods annotated with SSCRequiredActionsPermitted.
	 * 
	 * @author Ruud Senden
	 *
	 */
	static class FindMethodsAnnotatedWithSSCRequiredActionsPermitted extends AbstractAnnotationValuesClassVisitor {
		public FindMethodsAnnotatedWithSSCRequiredActionsPermitted(Map<String, Set<String>> methodsToAnnotationValuesMap) {
			super(methodsToAnnotationValuesMap);
		}

		@Override
		public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
			return new AbstractMethodVisitor(getClassName(), name) {
				private Set<String> values = new HashSet<>();
				@Override
				public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
					if ("Lcom/fortify/api/ssc/annotation/SSCRequiredActionsPermitted;".equals(desc)) {
						return new AnnotationVisitor(Opcodes.ASM5) {
							@Override
							public AnnotationVisitor visitArray(String name) {
								if ( !"value".equals(name) ) {
									return null;
								} else {
									return new AnnotationVisitor(Opcodes.ASM5) {
										@Override
										public void visit(String name, Object value) {
											values.add((String)value);
										}
									};
								}
							}
						};
					}
					return null;
				}

				@Override
				public void visitEnd() {
					if ( !values.isEmpty() ) {
						addValues(getMethodDescription(), values);
					}
				}
			};
		}
	}
	
	/**
	 * {@link ClassVisitor} that finds all invocations to any method in the given methodsToAnnotationValuesMap
	 * 
	 * @author Ruud Senden
	 *
	 */
	static class FindInvocationsToMethodsInAnnotationValuesMap extends AbstractAnnotationValuesClassVisitor {
		public FindInvocationsToMethodsInAnnotationValuesMap(Map<String, Set<String>> methodsToAnnotationValuesMap) {
			super(methodsToAnnotationValuesMap);
		}

		@Override
		public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
			return new AbstractMethodVisitor(getClassName(), name) {
				private Set<String> values = new HashSet<>();
				@Override
				public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
					String method = owner+"."+name;
					if ( getMethodsToAnnotationValuesMap().containsKey(method) ) {
						values.addAll(getMethodsToAnnotationValuesMap().get(method));
					}
				}

				@Override
				public void visitEnd() {
					if ( !values.isEmpty() ) {
						addValues(getMethodDescription(), values);
					}
				}
			};
		}
	}

}