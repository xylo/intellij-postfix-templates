package de.endrullis.idea.postfixtemplates.languages.swift;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.javascript.index.JavaScriptIndex;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for PHP postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class SwiftPostfixTemplatesUtils {

	/*
	static boolean isInstanceOf(@NotNull JavaScriptTypeHelper subType, @NotNull PhpType superType, PsiElement psiElement) {
		JavaScriptTypeHelper.getInstance().getTypeForIndexing(psiElement);

		return superType.isConvertibleFrom(subType, PhpIndex.getInstance(psiElement.getProject()));
	}
	*/

	static boolean isProjectClass(@NotNull String conditionClass, PsiElement e) {
		return JavaScriptIndex.getInstance(e.getProject()).getClassByName(conditionClass, true) != null;
	}

	static void addCompletions(CompletionParameters parameters, CompletionResultSet resultSet) {
		// TODO
	}

}
