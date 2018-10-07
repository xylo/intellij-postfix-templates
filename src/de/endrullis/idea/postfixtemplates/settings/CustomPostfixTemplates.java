package de.endrullis.idea.postfixtemplates.settings;

import java.util.Set;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CustomPostfixTemplates {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	/*
	@Nullable
	public static PsiExpression getTopmostExpression(PsiElement context) {
		PsiExpressionStatement statement = (PsiExpressionStatement) PsiTreeUtil.getNonStrictParentOfType(context, new Class[]{PsiExpressionStatement.class});
		return statement != null ? statement.getExpression() : null;
	}
	*/

}
