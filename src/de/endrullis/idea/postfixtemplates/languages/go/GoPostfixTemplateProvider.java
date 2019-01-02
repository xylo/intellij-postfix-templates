package de.endrullis.idea.postfixtemplates.languages.go;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class GoPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "go";
	}

	@Override
	public String getPluginClassName() {
		return "com.goide.psi.GoExpression";
	}

	@NotNull
	@Override
	protected CustomGoStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomGoStringPostfixTemplate(matchingClass, templateName, description, template, provider, mapping);
	}

}
