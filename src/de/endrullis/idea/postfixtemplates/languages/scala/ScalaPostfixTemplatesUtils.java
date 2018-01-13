package de.endrullis.idea.postfixtemplates.languages.scala;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.SelectorConditions;

import java.util.stream.Stream;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class ScalaPostfixTemplatesUtils {

	public static final Condition<PsiElement> BYTE     = isDescendant("scala.Byte", "java.lang.Byte");
	public static final Condition<PsiElement> CHAR     = isDescendant("scala.Char", "java.lang.Character");
	public static final Condition<PsiElement> DOUBLE   = isDescendant("scala.Double", "java.lang.Double");
	public static final Condition<PsiElement> FLOAT    = isDescendant("scala.Float", "java.lang.Float");
	public static final Condition<PsiElement> INT      = isDescendant("scala.Int", "java.lang.Integer");
	public static final Condition<PsiElement> LONG     = isDescendant("scala.Long", "java.lang.Long");
	public static final Condition<PsiElement> SHORT    = isDescendant("scala.Short", "java.lang.Short");
	public static final Condition<PsiElement> BOOLEAN  = isDescendant("scala.Boolean", "java.lang.Boolean");
	public static final Condition<PsiElement> VOID     = isDescendant("scala.Unit", "java.lang.Void");
	public static final Condition<PsiElement> NON_VOID = e -> !VOID.value(e);
	public static final Condition<PsiElement> DECIMAL_NUMBER = e ->
		Stream.of(BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT).anyMatch(t -> t.value(e));

	private static Condition<PsiElement> isDescendant(String class1, String class2) {
		final Condition<PsiElement> class1Condition = SelectorConditions.isDescendantCondition(class1);
		final Condition<PsiElement> class2Condition = SelectorConditions.isDescendantCondition(class2);

		return psiElement -> class1Condition.value(psiElement) || class2Condition.value(psiElement);
	}

}
