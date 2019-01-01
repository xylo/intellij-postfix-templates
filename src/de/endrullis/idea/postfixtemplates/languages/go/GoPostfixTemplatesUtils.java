package de.endrullis.idea.postfixtemplates.languages.go;

import com.goide.psi.GoArrayOrSliceType;
import com.goide.psi.GoExpression;
import com.goide.psi.GoType;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.javascript.index.JavaScriptIndex;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.isArray;

/**
 * Utilities for PHP postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class GoPostfixTemplatesUtils {

	/*
	static boolean isInstanceOf(@NotNull JavaScriptTypeHelper subType, @NotNull PhpType superType, PsiElement psiElement) {
		JavaScriptTypeHelper.getInstance().getTypeForIndexing(psiElement);

		return superType.isConvertibleFrom(subType, PhpIndex.getInstance(psiElement.getProject()));
	}
	*/

	static boolean isProjectClass(@NotNull String conditionClass, PsiElement e) {
		return JavaScriptIndex.getInstance(e.getProject()).getClassByName(conditionClass, true) != null;
	}

	static void addCompletions(CompletionParameters parameters, CompletionResultSet resultSet) {
		// TODO
	}

	private static boolean isBuiltinType(@Nullable GoType type) {
		return GoPsiImplUtil.builtin(type != null ? type.getUnderlyingType() : null);
	}

	static final Condition<PsiElement> IS_BOOL = element -> {
		return GoTypeUtil.isBoolean(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_INT = element -> {
		return GoTypeUtil.isIntegerType(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_INT64 = element -> {
		GoExpression expr = (GoExpression) element;
		GoType       type = expr.getGoType(null);
		return type != null && type.getText().equals("int64");
	};

	static final Condition<PsiElement> IS_UINT = element -> {
		return GoTypeUtil.isUintType(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_FLOAT32 = element -> {
		return GoTypeUtil.isFloat32(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_FLOAT64 = element -> {
		return GoTypeUtil.isFloat64(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_FLOAT = element -> {
		return GoTypeUtil.isFloatType(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_BYTESLICE = element -> {
		return GoTypeUtil.isByteSlice(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_ERROR = element -> {
		return GoTypeUtil.isError(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_ARRAY = element -> {
//        return GoTypeUtil.isArray(((GoExpression) element).getGoType(null));
		GoExpression expr = (GoExpression) element;
		GoType       type = expr.getGoType(null);
		type = type != null ? type.getUnderlyingType() : null;
		return type instanceof GoArrayOrSliceType;

	};

	static final Condition<PsiElement> IS_COMPLEX = element -> {
		return GoTypeUtil.isComplexType(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_NIL = element -> {
		return GoTypeUtil.isAllowedComparingToNil(((GoExpression) element).getGoType(null));
	};

	static final Condition<PsiElement> IS_STRING = element -> {
		return GoTypeUtil.isString(((GoExpression) element).getGoType(null));
	};

}
