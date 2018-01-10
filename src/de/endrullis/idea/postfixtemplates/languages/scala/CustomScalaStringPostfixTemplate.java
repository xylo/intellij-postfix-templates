package de.endrullis.idea.postfixtemplates.languages.scala;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.OrderedSet;
import de.endrullis.idea.postfixtemplates.templates.MyVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.ScalaStringBasedPostfixTemplate;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.AncestorSelector;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.SelectorType;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariables;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.removeVariableValues;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Custom postfix template for Scala.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("WeakerAccess")
public class CustomScalaStringPostfixTemplate extends ScalaStringBasedPostfixTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private final String template;
	private final Set<MyVariable> variables = new OrderedSet<>();

	public CustomScalaStringPostfixTemplate(String className, String templateName, String description, String template) {
		super(templateName, description, new AncestorSelector(getCondition(className), SelectorType.All()));

		List<MyVariable> allVariables = parseVariables(template).stream().filter(v -> {
			return !PREDEFINED_VARIABLES.contains(v.getName());
		}).collect(Collectors.toList());

		this.template = removeVariableValues(template, allVariables);

		// filter out variable duplicates
		Set<String> foundVarNames = new HashSet<>();
		for (MyVariable variable : allVariables) {
			if (!foundVarNames.contains(variable.getName())) {
				variables.add(variable);
				foundVarNames.add(variable.getName());
			}
		}
	}

	@Override
	public void setVariables(@NotNull Template template, @NotNull PsiElement psiElement) {
		super.setVariables(template, psiElement);

		List<MyVariable> sortedVars = variables.stream().sorted(Comparator.comparing(s -> s.getNo())).collect(Collectors.toList());

		for (Variable variable : sortedVars) {
			template.addVariable(variable.getName(), variable.getExpression(), variable.getDefaultValueExpression(),
				variable.isAlwaysStopAt(), variable.skipOnStart());
		}
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
