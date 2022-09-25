package de.endrullis.idea.postfixtemplates.languages.swift;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.template.postfix.JSPostfixTemplateUtils;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
/*  */
/**
 * Custom postfix template for Swift.
 */
@SuppressWarnings("WeakerAccess")
public class CustomSwiftStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	public CustomSwiftStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, JSPostfixTemplateUtils.selectorTopmost());
	}

	protected PsiElement getElementToRemove(PsiElement expr) {
   return expr.getParent() instanceof JSExpressionStatement ? expr : super.getElementToRemove(expr);
 }

}
