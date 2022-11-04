package de.endrullis.idea.postfixtemplates.languages.latex;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;

/**
 * Custom postfix template for Latex.
 */
@SuppressWarnings("WeakerAccess")
public class CustomLatexStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

    public CustomLatexStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement, PostfixTemplateExpressionSelector expressionSelector) {
        super(name, example, template, provider, psiElement, expressionSelector);
    }

}
