package de.endrullis.idea.postfixtemplates.languages.javascript;

import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class JavaScriptPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "javascript";
	}

	@NotNull
	@Override
	protected CustomJavaScriptStringPostfixTemplate createTemplate(String matchingClass, String conditionClass, String templateName, String description, String template) {
		return new CustomJavaScriptStringPostfixTemplate(matchingClass, templateName, description, template);
	}

}
