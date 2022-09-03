package de.endrullis.idea.postfixtemplates.languages.dart;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static de.endrullis.idea.postfixtemplates.languages.dart.DartPostfixTemplatesUtils.IS_ANY;

@SuppressWarnings("WeakerAccess")
public class CustomDartStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(SpecialType.ANY.name(), IS_ANY);
	}};

	public CustomDartStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		return type2psiCondition.get(matchingClass);
	}

}
