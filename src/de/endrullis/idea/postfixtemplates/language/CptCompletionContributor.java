package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Macro;
import com.intellij.codeInsight.template.macro.MacroFactory;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import static de.endrullis.idea.postfixtemplates.templates.CustomJavaStringPostfixTemplate.PREDEFINED_VARIABLES;

public class CptCompletionContributor extends CompletionContributor {
	public CptCompletionContributor() {
		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement(CptTypes.CLASS_NAME).withLanguage(CptLanguage.INSTANCE),
			new CompletionProvider<CompletionParameters>() {
				public void addCompletions(@NotNull CompletionParameters parameters,
				                           ProcessingContext context,
				                           @NotNull CompletionResultSet resultSet) {
					for (SpecialType specialType : SpecialType.values()) {
						resultSet.addElement(LookupElementBuilder.create(specialType.name()));
					}

					CptCompletionUtil.addCompletions(parameters, resultSet);
				}
			}
		);

		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement(CptTypes.TEMPLATE_VARIABLE_NAME).withLanguage(CptLanguage.INSTANCE),
			new CompletionProvider<CompletionParameters>() {
				public void addCompletions(@NotNull CompletionParameters parameters,
				                           ProcessingContext context,
				                           @NotNull CompletionResultSet resultSet) {
					for (String name : PREDEFINED_VARIABLES) {
						resultSet.addElement(LookupElementBuilder.create(name));
					}
				}
			}
		);

		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement(CptTypes.TEMPLATE_VARIABLE_EXPRESSION).withLanguage(CptLanguage.INSTANCE),
			new CompletionProvider<CompletionParameters>() {
				public void addCompletions(@NotNull CompletionParameters parameters,
				                           ProcessingContext context,
				                           @NotNull CompletionResultSet resultSet) {
					for (Macro macro : MacroFactory.getMacros()) {
						resultSet.addElement(LookupElementBuilder.create(macro.getPresentableName()));
					}
				}
			}
		);
	}
}
