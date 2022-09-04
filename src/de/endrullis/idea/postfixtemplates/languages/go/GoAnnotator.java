package de.endrullis.idea.postfixtemplates.languages.go;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for Go CPTs.
 *
 * @author Philip Griggs (philipgriggs@gmail.com)
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class GoAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		for (String key : CustomGoStringPostfixTemplate.type2psiCondition.keySet()) {
			put(key, true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull LeafPsiElement element, @NotNull String className) {
		return className2exists.containsKey(className);
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		for (String key : CustomGoStringPostfixTemplate.type2psiCondition.keySet()) {
			resultSet.addElement(LookupElementBuilder.create(key));
		}
	}

}
