package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CptAnnotator implements Annotator {
	private Map<String, Boolean> classCache = new HashMap<>();

	{
		for (SpecialType specialType : SpecialType.values()) {
			classCache.put(specialType.name(), true);
		}
	}

	@Override
	public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof LeafPsiElement) {
			LeafPsiElement psiElement = (LeafPsiElement) element;

			if (psiElement.getElementType().equals(CptTypes.CLASS_NAME)) {
				String className = psiElement.getText();

				boolean isClass = classCache.computeIfAbsent(className, name -> {
					Project project = element.getProject();
					return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project)) != null;
				});

				if (!isClass) {
					holder.createErrorAnnotation(psiElement.getTextRange(), "Class not found");
				}
			}
		}
	}
}
