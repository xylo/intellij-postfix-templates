package de.endrullis.idea.postfixtemplates.languages.java;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import de.endrullis.idea.postfixtemplates.language.CptCompletionUtil;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for Java CPTs.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class JavaAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		for (SpecialType specialType : SpecialType.values()) {
			put(specialType.name(), true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull LeafPsiElement element, @NotNull String className) {
		return className2exists.computeIfAbsent(className, name -> {
			val project = element.getProject();
			return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project)) != null;
		});
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		for (SpecialType specialType : SpecialType.values()) {
			resultSet.addElement(LookupElementBuilder.create(specialType.name()));
		}

		CptCompletionUtil.addCompletions(parameters, resultSet);
	}

}
