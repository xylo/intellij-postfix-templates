package de.endrullis.idea.postfixtemplates.languages.scala;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.OrderedSet;
import de.endrullis.idea.postfixtemplates.settings.CustomPostfixTemplates;
import de.endrullis.idea.postfixtemplates.templates.MyVariable;
import de.endrullis.idea.postfixtemplates.templates.NavigatableTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.ScalaStringBasedPostfixTemplate;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.AncestorSelector;

import java.util.*;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.languages.java.CustomJavaStringPostfixTemplate.withProjectClassCondition;
import static de.endrullis.idea.postfixtemplates.languages.scala.ScalaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariables;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.removeVariableValues;

/**
 * Custom postfix template for Scala.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("WeakerAccess")
public class CustomScalaStringPostfixTemplate extends ScalaStringBasedPostfixTemplate implements NavigatableTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = CustomPostfixTemplates.PREDEFINED_VARIABLES;

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), e -> true);
		put(SpecialType.VOID.name(), VOID);
		put(SpecialType.NON_VOID.name(), NON_VOID);
		//put(SpecialType.ARRAY.name(), IS_ARRAY);
		put(SpecialType.BOOLEAN.name(), BOOLEAN);
		//put(SpecialType.ITERABLE_OR_ARRAY.name(), IS_ITERABLE_OR_ARRAY);
		put(SpecialType.NUMBER.name(), DECIMAL_NUMBER);
		put(SpecialType.BYTE.name(), BYTE);
		put(SpecialType.SHORT.name(), SHORT);
		put(SpecialType.CHAR.name(), CHAR);
		put(SpecialType.INT.name(), INT);
		put(SpecialType.LONG.name(), LONG);
		put(SpecialType.FLOAT.name(), FLOAT);
		put(SpecialType.DOUBLE.name(), DOUBLE);
		/*
		put(SpecialType.BYTE_LITERAL.name(), isCertainNumberLiteral(PsiType.BYTE));
		put(SpecialType.SHORT_LITERAL.name(), isCertainNumberLiteral(PsiType.SHORT));
		put(SpecialType.CHAR_LITERAL.name(), isCertainNumberLiteral(PsiType.CHAR));
		put(SpecialType.INT_LITERAL.name(), isCertainNumberLiteral(PsiType.INT));
		put(SpecialType.LONG_LITERAL.name(), isCertainNumberLiteral(PsiType.LONG));
		put(SpecialType.FLOAT_LITERAL.name(), isCertainNumberLiteral(PsiType.FLOAT));
		put(SpecialType.DOUBLE_LITERAL.name(), isCertainNumberLiteral(PsiType.DOUBLE));
		put(SpecialType.NUMBER_LITERAL.name(), IS_DECIMAL_NUMBER_LITERAL);
		put(SpecialType.STRING_LITERAL.name(), STRING_LITERAL);
		put(SpecialType.CLASS.name(), IS_CLASS);
		/*
		put(SpecialType.FIELD.name(), IS_FIELD);
		put(SpecialType.LOCAL_VARIABLE.name(), IS_LOCAL_VARIABLE);
		put(SpecialType.VARIABLE.name(), IS_VARIABLE);
		put(SpecialType.ASSIGNMENT.name(), IS_ASSIGNMENT);
		*/
	}};

	private final String template;
	private final Set<MyVariable> variables = new OrderedSet<>();
	private final PsiElement psiElement;

	public CustomScalaStringPostfixTemplate(String matchingClass, String conditionClass, String templateName, String example, String template, PsiElement psiElement) {
		super(templateName.substring(1), example, new AncestorSelector(getCondition(matchingClass, conditionClass), SelectorType.All()));
		this.psiElement = psiElement;

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

	/**
	 * Returns a function that returns true if
	 * <ul>
	 *   <li>the PSI element satisfies the type condition regarding {@code matchingClass} and</li>
	 *   <li>{@code conditionClass} is either {@code null} or available in the current module.</li>
	 * </ul>
	 *
	 * @param matchingClass  required type of the psi element to satisfy this condition
	 * @param conditionClass required class in the current module to satisfy this condition, or {@code null}
	 * @return PSI element condition
	 */
	@NotNull
	private static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition == null) {
			psiElementCondition = SelectorConditions.isDescendantCondition(matchingClass);
		}

		return withProjectClassCondition(conditionClass, psiElementCondition);
	}


	@Nullable
	@Override
	public String getTemplateString(@NotNull PsiElement psiElement) {
		return template;
	}

	@Override
	public PsiElement getNavigationElement() {
		return psiElement;
	}

}
