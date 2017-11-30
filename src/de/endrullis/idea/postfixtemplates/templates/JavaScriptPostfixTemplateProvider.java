package de.endrullis.idea.postfixtemplates.templates;

import org.jetbrains.annotations.NotNull;

public class JavaScriptPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "javascript";
	}

	@NotNull
	@Override
	protected CustomJavaScriptStringPostfixTemplate createTemplate(String className, String templateName, String description, String template) {
		return new CustomJavaScriptStringPostfixTemplate(className, templateName, description, template);
	}

}
