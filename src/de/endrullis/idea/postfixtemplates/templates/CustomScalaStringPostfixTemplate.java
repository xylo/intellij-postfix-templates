package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.ScalaStringBasedPostfixTemplate;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.AncestorSelector;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.SelectorConditions$;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.SelectorType;

/**
 * Custom postfix template for Scala.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("WeakerAccess")
public class CustomScalaStringPostfixTemplate extends ScalaStringBasedPostfixTemplate {

	private final String template;

	public CustomScalaStringPostfixTemplate(String className, String templateName, String description, String template) {
		super(templateName, description, new AncestorSelector(getCondition(className), SelectorType.All()));

		this.template = template;
	}

	private static Condition<PsiElement> getCondition(String className) {
		// TODO: return real class name condition based on ScExpressions
		return expr -> true; // (Condition<PsiElement>) SelectorConditions$.MODULE$.ANY_EXPR();
	}


	@Nullable
	@Override
	public String getTemplateString(@NotNull PsiElement psiElement) {
		return template;
	}
}
