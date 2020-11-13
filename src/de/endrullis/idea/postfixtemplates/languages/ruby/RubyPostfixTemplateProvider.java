package de.endrullis.idea.postfixtemplates.languages.ruby;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class RubyPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "ruby";
	}

	@Override
	public String getPluginClassName() {
		return "org.jetbrains.plugins.ruby.ruby.lang.psi.RubyExpressionCodeFragment";
	}

	@NotNull
	@Override
	protected CustomRubyStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return RubyStringPostfixTemplateCreator.createTemplate(mapping, matchingClass, conditionClass, templateName, description, template, provider);
	}

}
