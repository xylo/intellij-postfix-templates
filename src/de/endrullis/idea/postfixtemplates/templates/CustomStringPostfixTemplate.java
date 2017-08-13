/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.templates.MyJavaPostfixTemplatesUtils.IS_DECIMAL_NUMBER;
import static de.endrullis.idea.postfixtemplates.templates.MyJavaPostfixTemplatesUtils.isCertainNumberType;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

public class CustomStringPostfixTemplate extends StringBasedPostfixTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ARRAY.name(), IS_ARRAY);
		put(SpecialType.BOOLEAN.name(), IS_BOOLEAN);
		put(SpecialType.ITERABLE_OR_ARRAY.name(), IS_ITERABLE_OR_ARRAY);
		put(SpecialType.NON_VOID.name(), IS_NON_VOID);
		put(SpecialType.NOT_PRIMITIVE.name(), IS_NOT_PRIMITIVE);
		put(SpecialType.NUMBER.name(), IS_DECIMAL_NUMBER);
		put(SpecialType.BYTE.name(), isCertainNumberType(PsiType.BYTE));
		put(SpecialType.SHORT.name(), isCertainNumberType(PsiType.SHORT));
		put(SpecialType.CHAR.name(), isCertainNumberType(PsiType.CHAR));
		put(SpecialType.INT.name(), isCertainNumberType(PsiType.INT));
		put(SpecialType.LONG.name(), isCertainNumberType(PsiType.LONG));
		put(SpecialType.FLOAT.name(), isCertainNumberType(PsiType.FLOAT));
		put(SpecialType.DOUBLE.name(), isCertainNumberType(PsiType.DOUBLE));
	}};

	private final String          template;
	private final Set<MyVariable> variables = new OrderedSet<>();

	public CustomStringPostfixTemplate(String clazz, String name, String example, String template) {
		super(name, example, selectorTopmost(getCondition(clazz)));

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

	@NotNull
	private static Condition<PsiElement> getCondition(String clazz) {
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

			if (this.skipOnStart != that.skipOnStart) return false;
			if (this.no != that.no) return false;

			return true;
		}
	}

}
