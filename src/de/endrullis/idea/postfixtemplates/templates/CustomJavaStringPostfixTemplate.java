package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.templates.MyJavaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Custom postfix template for Java.
 */
@SuppressWarnings("WeakerAccess")
public class CustomJavaStringPostfixTemplate extends StringBasedPostfixTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), IS_ANY);
		put(SpecialType.VOID.name(), IS_VOID);
		put(SpecialType.NON_VOID.name(), IS_NON_VOID);
		put(SpecialType.ARRAY.name(), IS_ARRAY);
		put(SpecialType.BOOLEAN.name(), IS_BOOLEAN);
		put(SpecialType.ITERABLE_OR_ARRAY.name(), IS_ITERABLE_OR_ARRAY);
		put(SpecialType.NOT_PRIMITIVE.name(), IS_NOT_PRIMITIVE);
		put(SpecialType.NUMBER.name(), IS_DECIMAL_NUMBER);
		put(SpecialType.BYTE.name(), isCertainNumberType(PsiType.BYTE));
		put(SpecialType.SHORT.name(), isCertainNumberType(PsiType.SHORT));
		put(SpecialType.CHAR.name(), isCertainNumberType(PsiType.CHAR));
		put(SpecialType.INT.name(), isCertainNumberType(PsiType.INT));
		put(SpecialType.LONG.name(), isCertainNumberType(PsiType.LONG));
		put(SpecialType.FLOAT.name(), isCertainNumberType(PsiType.FLOAT));
		put(SpecialType.DOUBLE.name(), isCertainNumberType(PsiType.DOUBLE));
		put(SpecialType.CLASS.name(), IS_CLASS);
		/*
		put(SpecialType.FIELD.name(), IS_FIELD);
		put(SpecialType.LOCAL_VARIABLE.name(), IS_LOCAL_VARIABLE);
		put(SpecialType.VARIABLE.name(), IS_VARIABLE);
		put(SpecialType.ASSIGNMENT.name(), IS_ASSIGNMENT);
		*/
	}};

	private final String          template;
	private final Set<MyVariable> variables = new OrderedSet<>();

	public static List<PsiExpression> collectExpressions(final PsiFile file,
		                                                   final Document document,
		                                                   final int offset,
		                                                   boolean acceptVoid) {
		CharSequence text = document.getCharsSequence();
		int correctedOffset = offset;
		int textLength = document.getTextLength();
		if (offset >= textLength) {
			correctedOffset = textLength - 1;
		} else if (!Character.isJavaIdentifierPart(text.charAt(offset))) {
			correctedOffset--;
		}
		if (correctedOffset < 0) {
			correctedOffset = offset;
		} else if (!Character.isJavaIdentifierPart(text.charAt(correctedOffset))) {
			if (text.charAt(correctedOffset) == ';') {//initially caret on the end of line
				correctedOffset--;
			}
			if (correctedOffset < 0 || text.charAt(correctedOffset) != ')') {
				correctedOffset = offset;
			}
		}
		final PsiElement elementAtCaret = file.findElementAt(correctedOffset);
		final List<PsiExpression> expressions = new ArrayList<>();
	 /*for (PsiElement element : statementsInRange) {
     if (element instanceof PsiExpressionStatement) {
       final PsiExpression expression = ((PsiExpressionStatement)element).getExpression();
       if (expression.getType() != PsiType.VOID) {
         expressions.add(expression);
       }
     }
   }*/
		PsiExpression expression = PsiTreeUtil.getParentOfType(elementAtCaret, PsiExpression.class);
		while (expression != null) {
			if (!expressions.contains(expression) && !(expression instanceof PsiParenthesizedExpression) && !(expression instanceof PsiSuperExpression) &&
				(acceptVoid || !PsiType.VOID.equals(expression.getType()))) {
				if (expression instanceof PsiMethodReferenceExpression) {
					expressions.add(expression);
				} else if (!(expression instanceof PsiAssignmentExpression)) {
					if (!(expression instanceof PsiReferenceExpression)) {
						expressions.add(expression);
					} else {
						if (!(expression.getParent() instanceof PsiMethodCallExpression)) {
							final PsiElement resolve = ((PsiReferenceExpression) expression).resolve();
							if (!(resolve instanceof PsiClass) && !(resolve instanceof PsiPackage)) {
								expressions.add(expression);
							}
						}
					}
				}
			}
			expression = PsiTreeUtil.getParentOfType(expression, PsiExpression.class);
		}
		return expressions;
	}

	public static PostfixTemplateExpressionSelector selectorAllExpressionsWithCurrentOffset(final Condition<PsiElement> additionalFilter) {
		return new PostfixTemplateExpressionSelectorBase(additionalFilter) {
			@Override
			protected List<PsiElement> getNonFilteredExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				return ContainerUtil.newArrayList(collectExpressions(context.getContainingFile(), document,
					Math.max(offset - 1, 0), false));
			}

			@NotNull
			@Override
			public List<PsiElement> getExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				if (DumbService.getInstance(context.getProject()).isDumb()) return Collections.emptyList();

				List<PsiElement> expressions = super.getExpressions(context, document, offset);
				if (!expressions.isEmpty()) return expressions;

				return ContainerUtil.filter(ContainerUtil.<PsiElement>createMaybeSingletonList(getTopmostExpression(context)), getFilters(offset));
			}

			@NotNull
			@Override
			public Function<PsiElement, String> getRenderer() {
				return JavaPostfixTemplatesUtils.getRenderer();
			}
		};
	}

	public CustomJavaStringPostfixTemplate(String clazz, String name, String example, String template) {
		super(name.substring(1), example, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));

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
	protected PsiElement getElementToRemove(PsiElement expr) {
		return expr;
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

	@NotNull
	public static Condition<PsiElement> getCondition(String clazz) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(clazz);

		if (psiElementCondition != null) {
			return psiElementCondition;
		} else {
			return MyJavaPostfixTemplatesUtils.isCustomClass(clazz);
		}
	}

	/**
	 * Returns the variable names used in the template.
	 *
	 * @param templateText template text
	 * @return the variable names used in the template
	 */
	@NotNull
	static Set<String> parseVariableNames(@NotNull String templateText) {
		Set<String> variableNames = new OrderedSet<>();

		int varStart = -1;
		int i = 0;

		while (i < templateText.length()) {
			char c = templateText.charAt(i);

			if (c == '\\') {
				i++;
			} else if (c == '$') {
				if (varStart == -1) {
					varStart = i;
				} else {
					String varName = templateText.substring(varStart + 1, i);
					variableNames.add(varName);
					varStart = -1;
				}
			}

			i++;
		}

		return variableNames;
	}

	/**
	 * Returns the variables used in the template.
	 *
	 * @param templateText template text
	 * @return the variables used in the template
	 */
	static List<MyVariable> parseVariables(@NotNull String templateText) {
		Set<String> varNames = parseVariableNames(templateText);

		final int[] autoNo = {0};

		return varNames.stream().map(variable -> {
			String[] parts = variable.split(":", 3);

			String[] nameParts = parts[0].split("#", 2);

			boolean skipIfDefined = nameParts[0].endsWith("*");
			String varName = nameParts[0].replaceFirst("\\*$", "");

			int no;
			try {
				no = nameParts.length == 2 ? Integer.parseInt(nameParts[1]) : autoNo[0];
			} catch (NumberFormatException e) {
				no = autoNo[0];
			}

			autoNo[0]++;

			if (parts.length == 3) {
				return new MyVariable(varName, parts[1], parts[2], true, skipIfDefined, no, variable);
			} else if (parts.length == 2) {
				return new MyVariable(varName, parts[1], "", true, skipIfDefined, no, variable);
			} else {
				return new MyVariable(varName, "", "", true, skipIfDefined, no, variable);
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Returns the template text without the variable default values.
	 *
	 * @param templateText template text
	 * @param variables    variables that may have default values
	 * @return the template text without the variable default values
	 */
	static String removeVariableValues(@NotNull String templateText, Collection<MyVariable> variables) {
		final String[] newTemplateText = {templateText};

		variables.forEach(variable -> {
			String varPattern = "$" + variable.getVarCode() + "$";
			String replacement = "$" + variable.getName().replaceFirst("\\*$", "") + "$";
			newTemplateText[0] = newTemplateText[0].replaceAll(Pattern.quote(varPattern), Matcher.quoteReplacement(replacement));
		});

		return newTemplateText[0];
	}

	@Nullable
	@Override
	public String getTemplateString(@NotNull PsiElement element) {
		return template;
	}

	public static class MyVariable extends Variable {
		private final String  varCode;
		private final boolean skipOnStart;
		private final int     no;

		public MyVariable(@NotNull String name, @Nullable String expression, @Nullable String defaultValue,
		                  boolean alwaysStopAt, boolean skipOnStart, int no, String varCode) {
			super(name, expression, defaultValue, alwaysStopAt);
			this.skipOnStart = skipOnStart;
			this.no = no;
			this.varCode = varCode;
		}

		@Override
		public boolean skipOnStart() {
			return skipOnStart;
		}

		public int getNo() {
			return no;
		}

		public String getVarCode() {
			return varCode;
		}

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = 29 * result + (skipOnStart ? 1 : 0);
			result = 29 * result + no;
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (!super.equals(o)) {
				return false;
			}

			MyVariable that = (MyVariable) o;

			return
				this.skipOnStart == that.skipOnStart &&
				this.no == that.no;
		}
	}

}
