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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.reflect.ClassPath;

// TODO Add documentation
// TODO clean up this class
public class SSCTokenSpecGenerator {
	public static void main(String[] args) throws IOException {
		Set<ClassPath.ClassInfo> classes = ClassPath.from(SSCTokenSpecGenerator.class.getClassLoader()).getAllClasses();
		System.out.println("Set of classes to be analyzed: ");
		System.out.println(classes);
		
		Map<String, Set<String>> methodsToAnnotationValuesMap = new HashMap<>();
		if ( findAnnotatedMethods(classes, methodsToAnnotationValuesMap) ) {
			while ( findIndirectMethodInvocations(classes, methodsToAnnotationValuesMap) ) {}
		}
		// TODO Should we also report any API method invocations outside of methods?
		//      (i.e. field initializers, static blocks, ...)
		for ( Map.Entry<String, Set<String>> entry : methodsToAnnotationValuesMap.entrySet() ) {
			if ( !entry.getKey().startsWith("com/fortify/api/ssc") ) {
				System.out.println(entry.getKey()+": "+entry.getValue());
			}
		}
	}

	protected static boolean findAnnotatedMethods(Set<ClassPath.ClassInfo> classes, Map<String, Set<String>> methodsToAnnotationValuesMap) {
		System.out.println("Scanning for methods annotated with @SSCRequiredActionsPermitted");
		FindAnnotatedMethods classVisitor = new FindAnnotatedMethods(methodsToAnnotationValuesMap);
		visitClasses(classes, classVisitor);
		System.out.println("Methods and required actions permitted found until now:");
		System.out.println(methodsToAnnotationValuesMap);
		return classVisitor.hasFoundNew();
	}
	
	protected static boolean findIndirectMethodInvocations(Set<ClassPath.ClassInfo> classes, Map<String, Set<String>> methodsToAnnotationValuesMap) {
		System.out.println("Next round of scanning for methods that indirectly call methods annotated with @SSCRequiredActionsPermitted");
		FindMethodInvocations classVisitor = new FindMethodInvocations(methodsToAnnotationValuesMap);
		visitClasses(classes, classVisitor);
		System.out.println("Methods and required actions permitted found until now:");
		System.out.println(methodsToAnnotationValuesMap);
		return classVisitor.hasFoundNew();
	}

	protected static void visitClasses(Set<ClassPath.ClassInfo> classes, ClassVisitor classVisitor) {
		for (ClassPath.ClassInfo info : classes) {
			try {
				new ClassReader(info.getName()).accept(classVisitor, 0);
			} catch (Exception e) {
			}
		}
	}

	static abstract class AbstractClassVisitor extends ClassVisitor {
		private final Map<String, Set<String>> methodsToAnnotationValuesMap;
		private boolean foundNew = false;
		private String className;

		public AbstractClassVisitor(Map<String, Set<String>> methodsToAnnotationValuesMap) {
			super(Opcodes.ASM5);
			this.methodsToAnnotationValuesMap = methodsToAnnotationValuesMap;
		}
		
		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			this.className = name;
			super.visit(version, access, name, signature, superName, interfaces);
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
		
		public String getClassName() {
			return className;
		}
		
		public Map<String, Set<String>> getMethodsToAnnotationValuesMap() {
			return methodsToAnnotationValuesMap;
		}
	}

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

	static class FindAnnotatedMethods extends AbstractClassVisitor {
		public FindAnnotatedMethods(Map<String, Set<String>> methodsToAnnotationValuesMap) {
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
	
	static class FindMethodInvocations extends AbstractClassVisitor {
		public FindMethodInvocations(Map<String, Set<String>> methodsToAnnotationValuesMap) {
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
