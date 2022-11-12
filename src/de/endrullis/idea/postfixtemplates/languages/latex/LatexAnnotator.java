package de.endrullis.idea.postfixtemplates.languages.latex;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.endrullis.idea.postfixtemplates.language.CptCompletionUtil;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for JavaScript CPTs.
 *
 */
public class LatexAnnotator implements CptLangAnnotator {

	private final static SpecialType[] supportedTypes = new SpecialType[]{
			SpecialType.ANY,
			SpecialType.MATH,
			SpecialType.TEXT,
	};

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		for (SpecialType specialType : supportedTypes) {
			put(specialType.name(), true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull LeafPsiElement element, @NotNull String className) {
		return className2exists.containsKey(className);
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		for (SpecialType specialType : supportedTypes) {
			resultSet.addElement(LookupElementBuilder.create(specialType.name()));
		}

		CptCompletionUtil.addCompletions(parameters, resultSet);
	}
}
