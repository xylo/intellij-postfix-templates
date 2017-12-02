package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Action to open the java templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptAnnotator implements Annotator {

	@Override
	public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof LeafPsiElement) {
			final LeafPsiElement psiElement = (LeafPsiElement) element;

			if (psiElement.getElementType().equals(CptTypes.CLASS_NAME)) {
				final String className = element.getText();

				final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
				if (document == null) return;
				final VirtualFile vFile = Objects.requireNonNull(FileDocumentManager.getInstance().getFile(document)).getCanonicalFile();
				if (vFile == null) return;
				final String language = CptUtil.getLanguageOfTemplateFile(vFile);

				final CptLangAnnotator annotator = SupportedLanguages.getCptLang(language).getAnnotator();

				if (!annotator.isMatchingType(psiElement, className)) {
					holder.createErrorAnnotation(element.getTextRange(), "Class not found");
				}
			}
		}
	}

}
