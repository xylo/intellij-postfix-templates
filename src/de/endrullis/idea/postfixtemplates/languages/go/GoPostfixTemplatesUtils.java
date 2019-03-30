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

import java.util.function.Function;

/**
 * Utilities for PHP postfix templates.
 *
 * @author Philip Griggs (philipgriggs@gmail.com)
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
		return GoPsiImplUtil.builtin(type != null ? type.getUnderlyingType((PsiElement) null) : null);
	}

	static final Condition<PsiElement> IS_BOOL = goExp(element -> {
		return GoTypeUtil.isBoolean(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_INT = goExp(element -> {
		return GoTypeUtil.isIntegerType(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_INT64 = goExp(element -> {
		GoType       type = element.getGoType(null);
		return type != null && type.getText().equals("int64");
	});

	static final Condition<PsiElement> IS_UINT = goExp(element -> {
		return GoTypeUtil.isUintType(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_FLOAT32 = goExp(element -> {
		return GoTypeUtil.isFloat32(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_FLOAT64 = goExp(element -> {
		return GoTypeUtil.isFloat64(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_FLOAT = goExp(element -> {
		return nullTypeToFalse(element.getGoType(null), goType -> GoTypeUtil.isFloatType(goType, element));
	});

	static final Condition<PsiElement> IS_BYTESLICE = goExp(element -> {
		return GoTypeUtil.isByteSlice(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_ERROR = goExp(element -> {
		return GoTypeUtil.isError(element.getGoType(null), element);
	});

	static final Condition<PsiElement> IS_ARRAY = goExp(element -> {
//        return GoTypeUtil.isArray(element.getGoType(null));
		GoType       type = element.getGoType(null);
		type = type != null ? type.getUnderlyingType(element) : null;
		return type instanceof GoArrayOrSliceType;
	});

	static final Condition<PsiElement> IS_COMPLEX = goExp(element -> {
		return nullTypeToFalse(element.getGoType(null), goType -> GoTypeUtil.isComplexType(goType, element));
	});

	static final Condition<PsiElement> IS_NIL = goExp(element -> {
		return nullTypeToFalse(element.getGoType(null), goType -> GoTypeUtil.isAllowedComparingToNil(goType, element));
	});

	static final Condition<PsiElement> IS_STRING = goExp(element -> {
		return GoTypeUtil.isString(element.getGoType(null), element);
	});

	private static Condition<PsiElement> goExp(Condition<GoExpression> f) {
		return expression -> {
			if (expression instanceof GoExpression) {
				return f.value((GoExpression) expression);
			} else {
				return false;
			}
		};
	}

	private static boolean nullTypeToFalse(GoType goType, Function<GoType, Boolean> f) {
		if (goType == null) {
			return false;
		} else {
			return f.apply(goType);
		}
	}

}
