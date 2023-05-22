package de.endrullis.idea.postfixtemplates.languages.sql;

import com.intellij.database.types.DasTypeCategory;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.sql.psi.SqlExpression;
import com.intellij.sql.psi.SqlType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for SQL postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class SqlPostfixTemplatesUtils {

	@NotNull
	static Condition<PsiElement> isCustomClass(String clazz) {
		return element -> element instanceof SqlExpression && isCustomClass(((SqlExpression) element).getSqlType(), clazz.toLowerCase());
	}

	private static boolean isCustomClass(SqlType sqlType, String type) {
		return StringUtils.substringBefore(sqlType.getDisplayName(), "(").equals(type);
	}

	static Condition<PsiElement> isCategory(DasTypeCategory category) {
		return element -> element instanceof SqlExpression && ((SqlExpression) element).getSqlType().getCategory().equals(category);
	}

}
