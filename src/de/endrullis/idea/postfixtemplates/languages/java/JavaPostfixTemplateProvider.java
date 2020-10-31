package de.endrullis.idea.postfixtemplates.languages.java;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.JavaCompletionContributor;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class JavaPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "java";
	}

	@Override
	public String getPluginClassName() {
		return "com.intellij.psi.PsiJavaReference";
	}

	@NotNull
	@Override
	protected CustomJavaStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomJavaStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template, provider, mapping);
	}

	protected boolean isSemicolonNeeded(@NotNull PsiFile file, @NotNull Editor editor) {
		return JavaCompletionContributor.semicolonNeeded(file, CompletionInitializationContext.calcStartOffset(editor.getCaretModel().getCurrentCaret()));
	}

}
