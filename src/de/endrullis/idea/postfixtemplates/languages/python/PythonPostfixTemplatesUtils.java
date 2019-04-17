package de.endrullis.idea.postfixtemplates.languages.python;

import java.util.Set;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Utilities for Python postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class PythonPostfixTemplatesUtils {

	static final Set<String> PYTHON_TYPES = _Set(
		"object",
		"list",
		"dict",
		"set",
		"tuple",
		"int",
		"float",
		"complex",
		"str",
		"unicode",
		"bytes",
		"bool",
		"classmethod",
		"staticmethod",
		"type"
	);

	/*
	static final Set<PyType> PYTHON_TYPES = _Set(
		PyBuiltinCache.getInstance(null).getBoolType()
	);
	*/

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
