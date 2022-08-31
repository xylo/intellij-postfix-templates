package de.endrullis.idea.postfixtemplates.languages.groovy;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import de.endrullis.idea.postfixtemplates.language.CptCompletionUtil;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for Groovy CPTs.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class GroovyAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		for (GroovyType value : GroovyType.values()) {
			put(value.name(), true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull final LeafPsiElement element, @NotNull final String className) {
		return className2exists.computeIfAbsent(className, name -> {
			val project = element.getProject();
			return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project)) != null;
		});
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		for (GroovyType value : GroovyType.values()) {
			resultSet.addElement(LookupElementBuilder.create(value.name()));
		}

		CptCompletionUtil.addCompletions(parameters, resultSet);
	}

}
