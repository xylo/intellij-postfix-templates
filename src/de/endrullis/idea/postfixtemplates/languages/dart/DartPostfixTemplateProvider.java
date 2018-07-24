package de.endrullis.idea.postfixtemplates.languages.dart;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class DartPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "dart";
	}

	@Override
	public String getPluginClassName() {
		return "com.jetbrains.lang.dart.psi.DartExpression";
	}

	@NotNull
	@Override
	protected CustomDartStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return DartStringPostfixTemplateCreator.createTemplate(mapping, matchingClass, conditionClass, templateName, description, template, provider);
	}

}

