package de.endrullis.idea.postfixtemplates.languages.python;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class PythonPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "python";
	}

	@Override
	public String getPluginClassName() {
		return "com.jetbrains.python.psi.PyExpression";
	}

	@NotNull
	@Override
	protected CustomPythonStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return PythonStringPostfixTemplateCreator.createTemplate(mapping, matchingClass, conditionClass, templateName, description, template, provider);
	}

}
