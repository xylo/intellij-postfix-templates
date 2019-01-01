package de.endrullis.idea.postfixtemplates.languages.go;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static de.endrullis.idea.postfixtemplates.languages.go.GoPostfixTemplatesUtils.*;

/**
 * Custom postfix template for Go.
 */
@SuppressWarnings("WeakerAccess")
public class CustomGoStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(GoSpecialType.ANY.name(), e -> true);
		put(GoSpecialType.BOOLEAN.name(), IS_BOOL);
		put(GoSpecialType.INT.name(), IS_INT);
		put(GoSpecialType.INT64.name(), IS_INT64);
		put(GoSpecialType.UINT.name(), IS_UINT);
		put(GoSpecialType.FLOAT32.name(), IS_FLOAT32);
		put(GoSpecialType.FLOAT64.name(), IS_FLOAT64);
		put(GoSpecialType.FLOAT.name(), IS_FLOAT);
		put(GoSpecialType.BYTESLICE.name(), IS_BYTESLICE);
		put(GoSpecialType.ERROR.name(), IS_ERROR);
		put(GoSpecialType.ARRAY.name(), IS_ARRAY);
		put(GoSpecialType.COMPLEX.name(), IS_COMPLEX);
		put(GoSpecialType.NIL.name(), IS_NIL);
		put(GoSpecialType.STRING.name(), IS_STRING);
	}};

	public CustomGoStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(String clazz) {
		return type2psiCondition.get(clazz);
	}

}
