package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

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
	}

	/*
	@Override
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		//super.fillCompletionVariants(parameters, resultSet);

		DataBindingCompletionUtil.addCompletions(parameters, resultSet);
	}
	*/
}
