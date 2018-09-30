package de.endrullis.idea.postfixtemplates.languages.sql;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.sql.psi.SqlType;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom postfix template for SQL.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("WeakerAccess")
public class CustomSqlStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	/** Contains predefined type-to-psiCondition mappings as well as cached mappings for individual types. */
	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), e -> true);
		for (SqlType.Category category : SqlType.Category.values()) {
			put(category.name(), SqlPostfixTemplatesUtils.isCategory(category));
		}
	}};

	public CustomSqlStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition == null) {
			psiElementCondition = SqlPostfixTemplatesUtils.isCustomClass(matchingClass);
		}

		return psiElementCondition;
	}

}
