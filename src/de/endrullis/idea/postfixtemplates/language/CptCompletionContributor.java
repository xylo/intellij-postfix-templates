package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Macro;
import com.intellij.codeInsight.template.macro.MacroFactory;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import static de.endrullis.idea.postfixtemplates.languages.java.CustomJavaStringPostfixTemplate.PREDEFINED_VARIABLES;

public class CptCompletionContributor extends CompletionContributor {
	public CptCompletionContributor() {
		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement(CptTypes.CLASS_NAME).withLanguage(CptLanguage.INSTANCE),
			new CompletionProvider<CompletionParameters>() {
				public void addCompletions(@NotNull CompletionParameters parameters,
				                           ProcessingContext context,
				                           @NotNull CompletionResultSet resultSet) {
					PsiElement psiElement = parameters.getPosition();

					if (isUserTemplateFile(psiElement)) {
						SupportedLanguages.getCptLang(psiElement).ifPresent(lang -> {
							final CptLangAnnotator annotator = lang.getAnnotator();

							annotator.completeMatchingType(parameters, resultSet);
						});
					}
				}
			}
		);

		extend(CompletionType.BASIC,
			PlatformPatterns.psiElement(CptTypes.TEMPLATE_VARIABLE_NAME).withLanguage(CptLanguage.INSTANCE),
			new CompletionProvider<CompletionParameters>() {
				public void addCompletions(@NotNull CompletionParameters parameters,
				                           ProcessingContext context,
				                           @NotNull CompletionResultSet resultSet) {
					if (isUserTemplateFile(parameters.getPosition())) {
						for (String name : PREDEFINED_VARIABLES) {
							resultSet.addElement(LookupElementBuilder.create(name));
						}
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
					if (isUserTemplateFile(parameters.getPosition())) {
						for (Macro macro : MacroFactory.getMacros()) {
							resultSet.addElement(LookupElementBuilder.create(macro.getPresentableName()));
						}
					}
				}
			}
		);
	}

	/**
	 * Returns true if the given file is a user template file (editable).
	 *
	 * @param psiElement PSI element of the file
	 * @return true if the given file is a user template file (editable)
	 */
	private boolean isUserTemplateFile(PsiElement psiElement) {
		val virtualFile = CptUtil.getVirtualFile(psiElement);
		val langAndVFile = CptUtil.getLangAndVFile(virtualFile);

		return langAndVFile == null || langAndVFile._2.id == null;
	}
}
