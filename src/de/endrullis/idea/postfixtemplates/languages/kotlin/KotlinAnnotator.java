package de.endrullis.idea.postfixtemplates.languages.kotlin;

import com.intellij.psi.impl.source.tree.LeafPsiElement;
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
public class KotlinAnnotator implements CptLangAnnotator {

	private final Map<String, Boolean> className2exists = new HashMap<String, Boolean>() {{
		put(SpecialType.ANY.name(), true);
	}};

	@Override
	public boolean isMatchingType(@NotNull final LeafPsiElement element, final String className) {
		return className2exists.containsKey(className);
	}

}
