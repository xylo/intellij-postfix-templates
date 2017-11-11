package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Some static additions to {@link com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils}.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public abstract class MyJavaPostfixTemplatesUtils {

	private MyJavaPostfixTemplatesUtils() {
	}

	public static Condition<PsiElement> isCustomClass(String clazz) {
		return element -> element instanceof PsiExpression && isCustomClass(((PsiExpression) element).getType(), clazz);
	}

	public static final Condition<PsiElement> IS_DECIMAL_NUMBER =
		element -> element instanceof PsiExpression && isDecimalNumber(((PsiExpression) element).getType());

	public static final Condition<PsiElement> IS_VOID =
		element -> element instanceof PsiExpression && isVoid(((PsiExpression) element).getType());

	public static final Condition<PsiElement> IS_ANY =
		element -> element instanceof PsiExpression;

	public static final Condition<PsiElement> IS_FIELD =
		element -> element instanceof PsiField;

	public static final Condition<PsiElement> IS_CLASS =
		element -> element instanceof PsiReferenceExpression &&
			((PsiReferenceExpression) element).advancedResolve(true).getElement() instanceof PsiClass;

	public static final Condition<PsiElement> IS_LOCAL_VARIABLE =
		element -> element instanceof PsiLocalVariable;

	public static final Condition<PsiElement> IS_VARIABLE =
		element -> element instanceof PsiField || element instanceof PsiLocalVariable;

	public static final Condition<PsiElement> IS_ASSIGNMENT =
		element -> element instanceof PsiField || element instanceof PsiAssignmentExpression;

	public static Condition<PsiElement> isCertainNumberType(@NotNull PsiType expectedType) {
		return element -> element instanceof PsiExpression && isCertainDecimalNumberType(((PsiExpression) element).getType(), expectedType);
	}

	/**
	 * Contains byte, short, char, int, long, float, and double.
	 */
	public static final Set<PsiType> NUMERIC_TYPES = new HashSet<>(Arrays.asList(
		PsiType.BYTE, PsiType.SHORT, PsiType.CHAR, PsiType.INT, PsiType.LONG, PsiType.FLOAT, PsiType.DOUBLE)
	);

	@Contract("null,_ -> false")
	public static boolean isCustomClass(@Nullable PsiType type, @NotNull String clazz) {
		return type != null && InheritanceUtil.isInheritor(type, clazz);
	}

	@Contract("null -> false")
	public static boolean isDecimalNumber(@Nullable PsiType type) {
		if (type == null) {
			return false;
		}

		return NUMERIC_TYPES.contains(type) || NUMERIC_TYPES.contains(PsiPrimitiveType.getUnboxedType(type));
	}

	@Contract("null,_ -> false")
	public static boolean isCertainDecimalNumberType(@Nullable PsiType type, @NotNull PsiType expectedType) {
		if (type == null) {
			return false;
		}

		return expectedType.equals(type) || expectedType.equals(PsiPrimitiveType.getUnboxedType(type));
	}

	@Contract("null -> false")
	public static boolean isVoid(@Nullable PsiType type) {
		return type != null && PsiType.VOID.equals(type);
	}

}
