package de.endrullis.idea.postfixtemplates.languages.python;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyPsiFacade;
import com.jetbrains.python.psi.PyTypedElement;
import com.jetbrains.python.psi.PyUtil;
import com.jetbrains.python.psi.impl.PyTypeProvider;
import com.jetbrains.python.psi.types.PyClassType;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.TypeEvalContext;
import com.jetbrains.python.pyi.PyiTypeProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for Python postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class PythonPostfixTemplatesUtils {

	/*
	@NotNull
	static Condition<PsiElement> isCustomClass(String clazz) {
		PyTypeProvider tp;
		PyType t;
		t.isBuiltin()
		PyPsiFacade.getInstance(null).createClassByQName()
		return e -> e instanceof PyTypedElement && TypeEvalContext.codeAnalysis(e.getProject(), e.getContainingFile()).getType((PyTypedElement) e);
	}

	private static boolean isCustomClass(SqlType sqlType, String type) {
		return StringUtils.substringBefore(sqlType.getDisplayName(), "(").equals(type);
	}

	static Condition<PsiElement> isCategory(SqlType.Category category) {
		return element -> element instanceof SqlExpression && ((SqlExpression) element).getSqlType().getCategory().equals(category);
	}
	*/

}
