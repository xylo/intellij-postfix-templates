package de.endrullis.idea.postfixtemplates.languages.kotlin;

import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class KotlinPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "kotlin";
	}

	@NotNull
	@Override
	protected CustomKotlinStringPostfixTemplate createTemplate(String className, String templateName, String description, String template) {
		return new CustomKotlinStringPostfixTemplate(className, templateName, description, template);
	}

}
