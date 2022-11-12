package de.endrullis.idea.postfixtemplates.languages.latex;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class LatexPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "latex";
	}

	@Override
	public String getPluginClassName() {
		return "nl.hannahsten.texifyidea.editor.postfix.LatexPostfixExpressionSelector";
	}

	@NotNull
	@Override
	protected CustomLatexStringPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider) {
		try {
			Class<?> expressionSelectorClazz = Class.forName(getPluginClassName());
			boolean mathOnly = SpecialType.MATH.name().equals(matchingClass);
			boolean textOnly = SpecialType.TEXT.name().equals(matchingClass);
			PostfixTemplateExpressionSelector expressionSelector = ((PostfixTemplateExpressionSelector) expressionSelectorClazz.getDeclaredConstructor(boolean.class, boolean.class).newInstance(mathOnly, textOnly));
			return new CustomLatexStringPostfixTemplate(matchingClass, templateName, description, template, provider, mapping, expressionSelector);
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException |
				 InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
