package de.endrullis.idea.postfixtemplates.languages.ruby;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom postfix template for Ruby.
 */
@SuppressWarnings("WeakerAccess")
public class CustomRubyStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	/** Contains predefined type-to-psiCondition mappings as well as cached mappings for individual types. */
	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(SpecialType.ANY.name(), e -> true);
	}};

	public CustomRubyStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		return type2psiCondition.get(matchingClass);
	}
}
