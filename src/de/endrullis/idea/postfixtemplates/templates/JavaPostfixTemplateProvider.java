package de.endrullis.idea.postfixtemplates.templates;

import org.jetbrains.annotations.NotNull;

public class JavaPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "java";
	}

	@NotNull
	@Override
	protected CustomJavaStringPostfixTemplate createTemplate(String className, String templateName, String description, String template) {
		return new CustomJavaStringPostfixTemplate(className, templateName, description, template);
	}

}
