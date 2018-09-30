package de.endrullis.idea.postfixtemplates.languages.php;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.sql.psi.SqlExpression;
import com.intellij.sql.psi.SqlType;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Utilities for PHP postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class PhpPostfixTemplatesUtils {

	public static final Set<PhpType> PHP_TYPES = _Set(
		PhpType.EMPTY,
		PhpType.MIXED,
		PhpType.NULL,
		PhpType.STRING,
		PhpType.BOOLEAN,
		PhpType.INT,
		PhpType.FLOAT,
		PhpType.OBJECT,
		PhpType.CLOSURE,
		PhpType.CALLABLE,
		PhpType.RESOURCE,
		PhpType.ARRAY,
		PhpType.ITERABLE,
		PhpType.NUMBER,
		PhpType.VOID,
		//PhpType.NUMERIC,
		//PhpType.SCALAR,
		//PhpType.FLOAT_INT,
		PhpType.UNSET,
		PhpType.STATIC,
		PhpType.EXCEPTION,
		PhpType.THROWABLE,
		PhpType.$THIS
	);

	@NotNull
	static Condition<PsiElement> isCustomClass(String clazz) {
		return element -> element instanceof SqlExpression && isCustomClass(((SqlExpression) element).getSqlType(), clazz.toLowerCase());
	}

	private static boolean isCustomClass(SqlType sqlType, String type) {
		return StringUtils.substringBefore(sqlType.getDisplayName(), "(").equals(type);
	}

	static Condition<PsiElement> isCategory(SqlType.Category category) {
		return element -> element instanceof SqlExpression && ((SqlExpression) element).getSqlType().getCategory().equals(category);
	}

}
