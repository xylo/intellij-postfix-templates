package de.endrullis.idea.postfixtemplates.languages.swift;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class SwiftPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "swift";
	}

	@Override
	public String getPluginClassName() {
		return "com.intellij.lang.swift.template.postfix.JSPostfixTemplateUtils";
	}

	@NotNull
	@Override
	protected CustomSwiftStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomSwiftStringPostfixTemplate(matchingClass, templateName, description, template, provider, mapping);
	}

}
