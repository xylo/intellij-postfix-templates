package de.endrullis.idea.postfixtemplates.languages.java;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import de.endrullis.idea.postfixtemplates.language.CptLangAnnotator;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Code annotator for Java CPT.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class JavaAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<String, Boolean>() {{
		for (SpecialType specialType : SpecialType.values()) {
			put(specialType.name(), true);
		}
	}};

	@Override
	public boolean isMatchingType(@NotNull LeafPsiElement element, String className) {
		return className2exists.computeIfAbsent(className, name -> {
			final Project project = element.getProject();
			return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project)) != null;
		});
	}

}
