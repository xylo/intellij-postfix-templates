package de.endrullis.idea.postfixtemplates.languages.python;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for Python CPTs.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class PythonAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		put(SpecialType.ANY.name(), true);
		for (String pyType : PythonPostfixTemplatesUtils.PYTHON_TYPES) {
			put(pyType, true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull final LeafPsiElement element, @NotNull final String className) {
		return className2exists.containsKey(className);
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		resultSet.addElement(LookupElementBuilder.create(SpecialType.ANY.name()));
		for (String pyType : PythonPostfixTemplatesUtils.PYTHON_TYPES) {
			resultSet.addElement(LookupElementBuilder.create(pyType));
		}
	}

}
