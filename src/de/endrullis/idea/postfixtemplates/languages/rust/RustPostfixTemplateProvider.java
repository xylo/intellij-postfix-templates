package de.endrullis.idea.postfixtemplates.languages.rust;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;

public class RustPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "rust";
	}

	@Override
	public String getPluginClassName() {
		return "org.rust.lang.core.RsPsiPattern";
	}

	@NotNull
	@Override
	protected CustomRustStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		return RustStringPostfixTemplateCreator.createTemplate(mapping, matchingClass, conditionClass, templateName, description, template, provider);
	}

}
