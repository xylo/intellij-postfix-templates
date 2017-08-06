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
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

public class CustomStringPostfixTemplate extends StringBasedPostfixTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private final String template;
	private final Set<MyVariable> variables;

	public CustomStringPostfixTemplate(String clazz, String name, String example, String template) {
		super(name, example, selectorTopmost(getCondition(clazz)));

		variables = parseVariables(template).stream().filter(v -> {
			return !PREDEFINED_VARIABLES.contains(v.getName());
		}).collect(Collectors.toSet());

		this.template = removeVariableValues(template, variables);
	}

	@Override
	public void setVariables(@NotNull Template template, @NotNull PsiElement psiElement) {
		super.setVariables(template, psiElement);

		for (Variable variable : variables) {
			template.addVariable(variable.getName(), variable.getExpressionString(), variable.getDefaultValueString(), variable.isAlwaysStopAt());
			/*
			List<Macro> macros = MacroFactory.getMacros(variable.getDefaultValueString());

			if (!macros.isEmpty()) {
				Macro macro = macros.get(0);

				MacroCallNode index = new MacroCallNode(macro);

				template.addVariable(variable.getName(), index, index, true);
				//template.addVariable(variable.getName(), variable.getDefaultValueString(), variable.getDefaultValueString(), variable.isAlwaysStopAt());
			} else {
				template.addVariable(variable.getName(), "", "", true);
			}
			*/
		}
	}

	@NotNull
	private static Condition<PsiElement> getCondition(String clazz) {
		if (clazz.equals(SpecialType.ARRAY.name())) {
			return IS_ARRAY;
		}
		if (clazz.equals(SpecialType.BOOLEAN.name())) {
			return IS_BOOLEAN;
		}
		if (clazz.equals(SpecialType.ITERABLE_OR_ARRAY.name())) {
			return IS_ITERABLE_OR_ARRAY;
		}
		if (clazz.equals(SpecialType.NON_VOID.name())) {
			return IS_NON_VOID;
		}
		if (clazz.equals(SpecialType.NOT_PRIMITIVE.name())) {
			return IS_NOT_PRIMITIVE;
		}
		if (clazz.equals(SpecialType.NUMBER.name())) {
			return MyJavaPostfixTemplatesUtils.IS_DECIMAL_NUMBER;
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
			} else
			if (c == '$') {
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
	static Set<MyVariable> parseVariables(@NotNull String templateText) {
		Set<String> varNames = parseVariableNames(templateText);

		return varNames.stream().map(varName -> {
			String[] parts = varName.split(":", 3);

			if (parts.length == 3) {
				return new MyVariable(parts[0], parts[1], parts[2], true, varName);
			} else
			if (parts.length == 2) {
				return new MyVariable(parts[0], parts[1], "", true, varName);
			} else {
				return new MyVariable(varName, "", "", true, varName);
			}
		}).collect(Collectors.toSet());
	}

	/**
	 * Returns the template text without the variable default values.
	 *
	 * @param templateText template text
	 * @param variables variables that may have default values
	 * @return the template text without the variable default values
	 */
	static String removeVariableValues(@NotNull String templateText, Set<MyVariable> variables) {
		final String[] newTemplateText = {templateText};

		variables.stream().filter(v -> !v.getExpressionString().isEmpty()).forEach(variable -> {
			String varPattern = "$" + variable.getVarCode() + "$";
			String replacement = "$" + variable.getName() + "$";
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
		private final String varCode;

		public MyVariable(@NotNull String s, @Nullable String s1, @Nullable String s2, boolean b, String varCode) {
			super(s, s1, s2, b);
			this.varCode = varCode;
		}

		public String getVarCode() {
			return varCode;
		}
	}

}
