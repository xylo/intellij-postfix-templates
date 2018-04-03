package de.endrullis.idea.postfixtemplates.languages.kotlin;

import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class KotlinStringPostfixTemplateCreator {

	@NotNull
	static CustomKotlinStringPostfixTemplate createTemplate(String matchingClass, String conditionClass, String templateName, String description, String template) {
		return new CustomKotlinStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template);
	}

}
