package de.endrullis.idea.postfixtemplates.languages.php;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for PHP CPTs.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class PhpAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		put(SpecialType.ANY.name(), true);
		for (PhpType phpType : PhpPostfixTemplatesUtils.PHP_TYPES) {
			put(phpType.toString(), true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull final LeafPsiElement element, @NotNull final String className) {
		return className2exists.computeIfAbsent(className, name -> {
			val project = element.getProject();
			return PhpIndex.getInstance(project).getClassByName(className) != null;
		});
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		resultSet.addElement(LookupElementBuilder.create(SpecialType.ANY.name()));
		for (PhpType phpType : PhpPostfixTemplatesUtils.PHP_TYPES) {
			resultSet.addElement(LookupElementBuilder.create(phpType.toString()));
		}

		PhpPostfixTemplatesUtils.addCompletions(parameters, resultSet);
	}

}
