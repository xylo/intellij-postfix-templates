package de.endrullis.idea.postfixtemplates.languages.javascript;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class JavaScriptPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "javascript";
	}

	@Override
	public String getPluginClassName() {
		return "com.intellij.lang.javascript.template.postfix.JSPostfixTemplateUtils";
	}

	@NotNull
	@Override
	protected CustomJavaScriptStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomJavaScriptStringPostfixTemplate(matchingClass, templateName, description, template, provider, mapping);
	}

}
