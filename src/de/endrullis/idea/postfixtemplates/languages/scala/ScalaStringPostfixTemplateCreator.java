package de.endrullis.idea.postfixtemplates.languages.scala;

import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class ScalaStringPostfixTemplateCreator {

	@NotNull
	static StringBasedPostfixTemplate createTemplate(String matchingClass, String conditionClass, String templateName, String description, String template) {
		return new CustomScalaStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template);
	}

}
