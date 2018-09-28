package de.endrullis.idea.postfixtemplates.languages.python;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class PythonStringPostfixTemplateCreator {

	@NotNull
	static CustomPythonStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomPythonStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template, provider, mapping);
	}

}
