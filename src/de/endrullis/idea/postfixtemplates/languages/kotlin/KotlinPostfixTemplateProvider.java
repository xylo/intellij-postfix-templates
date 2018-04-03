package de.endrullis.idea.postfixtemplates.languages.kotlin;

import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class KotlinPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "kotlin";
	}

	@Override
	public String getPluginClassName() {
		return "org.jetbrains.kotlin.psi.KtElement";
	}

	@NotNull
	@Override
	protected CustomKotlinStringPostfixTemplate createTemplate(String matchingClass, String conditionClass, String templateName, String description, String template) {
		return KotlinStringPostfixTemplateCreator.createTemplate(matchingClass, conditionClass, templateName, description, template);
	}

}
