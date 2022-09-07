package de.endrullis.idea.postfixtemplates.languages.php;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.postfixCompletion.PhpPostfixUtils;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static de.endrullis.idea.postfixtemplates.languages.php.PhpPostfixTemplatesUtils.isInstanceOf;
import static de.endrullis.idea.postfixtemplates.languages.php.PhpPostfixTemplatesUtils.isProjectClass;

/**
 * Custom postfix template for PHP.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("WeakerAccess")
public class CustomPhpStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	/**
	 * Contains predefined type-to-psiCondition mappings as well as cached mappings for individual types.
	 */
	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(SpecialType.ANY.name(), e -> true);
		for (PhpType phpType : PhpPostfixTemplatesUtils.PHP_TYPES) {
			if (phpType.isNotExtendablePrimitiveType()) {
				put(phpType.toString(), e -> e instanceof PhpTypedElement && ((PhpTypedElement) e).getType().getTypes().stream().anyMatch(s -> phpType.getTypes().contains(s)));
			} else {
				put(phpType.toString(), e -> e instanceof PhpTypedElement && isInstanceOf(((PhpTypedElement) e).getType(), phpType, e));
			}
		}
	}};

	public CustomPhpStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, PhpPostfixUtils.selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition == null) {
			val phpType = new PhpType().add(matchingClass);
			psiElementCondition = e -> e instanceof PhpTypedElement && isInstanceOf(((PhpTypedElement) e).getType(), phpType, e);
			// type2psiCondition.put(matchingClass, psiElementCondition);
		}

		if (conditionClass != null) {
			val oldPsiElementCondition = psiElementCondition;
			psiElementCondition = e -> {
				if (isProjectClass(conditionClass, e)) {
					return oldPsiElementCondition.value(e);
				} else {
					return false;
				}
			};
		}

		return psiElementCondition;
	}

}
