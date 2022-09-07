package de.endrullis.idea.postfixtemplates.languages.scala;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import de.endrullis.idea.postfixtemplates.language.CptCompletionUtil;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for Java CPTs.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class ScalaAnnotator implements CptLangAnnotator {

	private final static SpecialType[] supportedTypes = new SpecialType[]{
		SpecialType.ANY,
		SpecialType.VOID,
		SpecialType.NON_VOID,
		SpecialType.BOOLEAN,
		SpecialType.NUMBER,
		SpecialType.BYTE,
		SpecialType.SHORT,
		SpecialType.CHAR,
		SpecialType.INT,
		SpecialType.LONG,
		SpecialType.FLOAT,
		SpecialType.DOUBLE,
	};

	private final Map<String, Boolean> className2exists = new HashMap<>() {{
		for (SpecialType specialType : supportedTypes) {
			put(specialType.name(), true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull LeafPsiElement element, @NotNull String className) {
		return className2exists.computeIfAbsent(className, name -> {
			final Project project = element.getProject();
			return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project)) != null;
		});
	}

	@Override
	public void completeMatchingType(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
		for (SpecialType specialType : supportedTypes) {
			resultSet.addElement(LookupElementBuilder.create(specialType.name()));
		}

		CptCompletionUtil.addCompletions(parameters, resultSet);
	}

}
