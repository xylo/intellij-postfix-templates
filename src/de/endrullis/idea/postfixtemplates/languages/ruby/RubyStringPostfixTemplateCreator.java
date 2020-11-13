package de.endrullis.idea.postfixtemplates.languages.ruby;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;

/**
 * @author YenTing Chen &lt;solofat@gmail.com&gt;
 */
class RubyStringPostfixTemplateCreator {

	@NotNull
	static CustomRubyStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return new CustomRubyStringPostfixTemplate(matchingClass, conditionClass, templateName, description, template, provider, mapping);
	}

}
