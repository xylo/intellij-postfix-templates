package de.endrullis.idea.postfixtemplates.languages.java;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class JavaPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "java";
	}

	@NotNull
	@Override
	protected CustomJavaStringPostfixTemplate createTemplate(String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomJavaStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template, provider);
	}

}
