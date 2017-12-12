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
package com.fortify.api.ssc.annotation;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;

@SupportedAnnotationTypes("com.fortify.api.ssc.annotation.SSCRequiredActionsPermitted")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SSCRequiredActionsPermittedProcessor extends AbstractProcessor implements TaskListener {
	Trees trees;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		System.out.println("!!!! TEST1 !!!!");
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
		JavacTask.instance(processingEnv).setTaskListener(this);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("!!!! TEST2 !!!!");
		// Process @Unsafe annotated methods if needed
		return true;
	}

	@Override
	public void finished(final TaskEvent taskEvt) {
		//System.out.println("!!!! TEST3 !!!!");
		if (taskEvt.getKind() == TaskEvent.Kind.ANALYZE) {
			//System.out.println("!!!! TEST4 !!!!");
			taskEvt.getCompilationUnit().accept(new TreeScanner<Void, Void>() {
				@Override
				public Void visitMethodInvocation(MethodInvocationTree methodInv, Void v) {
					//System.out.println("!!!! TEST5 !!!!: "+methodInv);
					Element method = TreeInfo.symbol((JCTree) methodInv.getMethodSelect());
					SSCRequiredActionsPermitted sscRequiredActionsPermitted = method.getAnnotation(SSCRequiredActionsPermitted.class);
					if (sscRequiredActionsPermitted != null) {
						System.out.println("!!!! TEST6 !!!!: "+String.join(",", sscRequiredActionsPermitted.value()));
						JCTree jcTree = (JCTree) methodInv.getMethodSelect();
						//System.out.println("!!!! TEST7 !!!!: "+jcTree);
						trees.printMessage(Diagnostic.Kind.WARNING, "Call to unsafe method.", jcTree,
								taskEvt.getCompilationUnit());
					}
					return super.visitMethodInvocation(methodInv, v);
				}
			}, null);
		}
	}

	@Override
	public void started(TaskEvent taskEvt) {
	}
}
