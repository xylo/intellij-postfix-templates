package de.endrullis.idea.postfixtemplates.languages.groovy;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.*;

/**
 * Some static additions to {@link com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils}.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
abstract class GroovyPostfixTemplatesUtils {

	private GroovyPostfixTemplatesUtils() {
	}

	@NotNull
	static Condition<PsiElement> isCustomClass(String clazz) {
		return element -> {
			if (element instanceof GrExpression) {
				return isCustomClass(((GrExpression) element).getType(), clazz);
			} else {
				return false;
			}
		};
	}

	static final Condition<PsiElement> IS_ARRAY = element -> {
		if (!(element instanceof GrExpression)) {
			return false;
		} else {
			PsiType type = ((GrExpression) element).getType();
			return isArray(type);
		}
	};

	static final Condition<PsiElement> IS_ITERABLE_OR_ARRAY = element -> {
		if (!(element instanceof GrExpression)) {
			return false;
		} else {
			PsiType type = ((GrExpression) element).getType();
			return isArray(type) || isIterable(type);
		}
	};

	static final Condition<PsiElement> IS_DECIMAL_NUMBER =
		element -> element instanceof GrExpression && isDecimalNumber(((GrExpression) element).getType());

	static final Condition<PsiElement> IS_DECIMAL_NUMBER_LITERAL =
		element -> element instanceof PsiLiteralExpression && isDecimalNumber(((PsiLiteralExpression) element).getType());

	public static final Condition<PsiElement> IS_BOOLEAN = (element) -> {
   return element instanceof PsiExpression && isBoolean(((PsiExpression)element).getType());
 };

	static final Condition<PsiElement> IS_VOID =
		element -> element instanceof GrExpression && isVoid(((GrExpression) element).getType());

	static final Condition<PsiElement> IS_NON_VOID =
		element -> element instanceof GrExpression && isNonVoid(((GrExpression) element).getType());

	static final Condition<PsiElement> IS_ANY =
		element -> true;

	static final Condition<PsiElement> IS_FIELD =
		element -> element instanceof PsiField;

	static final Condition<PsiElement> IS_LOCAL_VARIABLE =
		element -> element instanceof PsiLocalVariable;

	static final Condition<PsiElement> IS_VARIABLE =
		element -> element instanceof PsiField || element instanceof PsiLocalVariable;

	static final Condition<PsiElement> IS_ASSIGNMENT =
		element -> element instanceof PsiField || element instanceof PsiAssignmentExpression;

	static Condition<PsiElement> isCertainNumberType(@NotNull PsiType expectedType) {
		return element -> element instanceof GrExpression && isCertainDecimalNumberType(((GrExpression) element).getType(), expectedType);
	}

	static Condition<PsiElement> isCertainNumberLiteral(@NotNull PsiType expectedType) {
		return element -> element instanceof PsiLiteralExpression && isCertainDecimalNumberType(((GrExpression) element).getType(), expectedType);
	}

	static Condition<PsiElement> STRING_LITERAL = element -> {
		return element instanceof PsiLiteralExpression && InheritanceUtil.isInheritor(((PsiLiteralExpression) element).getType(), "java.lang.String");
	};

	/**
	 * Contains byte, short, char, int, long, float, and double.
	 */
	static final Set<PsiType> NUMERIC_TYPES = new HashSet<>(Arrays.asList(
		PsiTypes.byteType(), PsiTypes.shortType(), PsiTypes.charType(), PsiTypes.intType(), PsiTypes.longType(), PsiTypes.floatType(), PsiTypes.doubleType())
	);

	@Contract("null,_ -> false")
	static boolean isCustomClass(@Nullable PsiType type, @NotNull String clazz) {
		return InheritanceUtil.isInheritor(type, clazz);
	}

	@Contract("null -> false")
	static boolean isDecimalNumber(@Nullable PsiType type) {
		if (type == null) {
			return false;
		}

		return NUMERIC_TYPES.contains(type)
			|| NUMERIC_TYPES.contains(PsiPrimitiveType.getUnboxedType(type))
			|| isCustomClass(type, "java.math.BigDecimal");
	}

	@Contract("null,_ -> false")
	static boolean isCertainDecimalNumberType(@Nullable PsiType type, @NotNull PsiType expectedType) {
		if (type == null) {
			return false;
		}

		return expectedType.equals(type) || expectedType.equals(PsiPrimitiveType.getUnboxedType(type));
	}

	@Contract("null -> false")
	static boolean isVoid(@Nullable PsiType type) {
		return PsiTypes.voidType().equals(type);
	}

}
