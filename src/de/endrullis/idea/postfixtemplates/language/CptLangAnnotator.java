package de.endrullis.idea.postfixtemplates.language;

import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract CPT language annotator.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public interface CptLangAnnotator {

	boolean isMatchingType(@NotNull final LeafPsiElement element, @NotNull final String className);

}
