package de.endrullis.idea.postfixtemplates.languages.python;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.codeInsight.postfix.PyPostfixUtils;
import com.jetbrains.python.psi.PyTypedElement;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.TypeEvalContext;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom postfix template for Python.
 */
@SuppressWarnings("WeakerAccess")
public class CustomPythonStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	/** Contains predefined type-to-psiCondition mappings as well as cached mappings for individual types. */
	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(SpecialType.ANY.name(), e -> true);
		for (String pyType : PythonPostfixTemplatesUtils.PYTHON_TYPES) {
			put(pyType, e -> {
				if (e instanceof PyTypedElement) {
					PyType type = TypeEvalContext.codeAnalysis(e.getProject(), e.getContainingFile()).getType((PyTypedElement) e);
					return type != null && type.getName() != null && type.getName().equals(pyType);
				} else {
					return false;
				}
			});
		}
	}};

	public CustomPythonStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, PyPostfixUtils.selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		val psiElementCondition = type2psiCondition.get(matchingClass);

		// PyElementTypes.INTEGER_LITERAL_EXPRESSION
		//TypeEvalContext.codeAnalysis(e.getProject(), e.getContainingFile()).getType((PyTypedElement) e)

		if (psiElementCondition == null) {
			//psiElementCondition = PythonPostfixTemplatesUtils.isCustomClass(matchingClass);
		}

		return psiElementCondition;
	}

}
