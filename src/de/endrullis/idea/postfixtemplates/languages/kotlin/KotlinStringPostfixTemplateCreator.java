package de.endrullis.idea.postfixtemplates.languages.kotlin;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class KotlinStringPostfixTemplateCreator {

	@NotNull
	static CustomKotlinStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomKotlinStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template, provider, mapping);
	}

}
