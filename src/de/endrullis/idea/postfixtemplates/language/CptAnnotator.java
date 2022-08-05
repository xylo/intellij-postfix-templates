package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import org.jetbrains.annotations.NotNull;

/**
 * Action to open the java templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptAnnotator implements Annotator {

	@Override
	public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof final LeafPsiElement psiElement) {

			if (psiElement.getElementType().equals(CptTypes.CLASS_NAME)) {
				final String className = element.getText();

				SupportedLanguages.getCptLang(element).ifPresent(lang -> {
					final CptLangAnnotator annotator = lang.getAnnotator();

					if (!annotator.isMatchingType(psiElement, className)) {
						holder.newAnnotation(HighlightSeverity.ERROR, "Class not found").range(element.getTextRange()).create();
					}
				});
			}
		}
	}

}
