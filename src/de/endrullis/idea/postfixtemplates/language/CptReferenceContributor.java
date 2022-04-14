package de.endrullis.idea.postfixtemplates.language;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CptReferenceContributor extends PsiReferenceContributor {
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		// TODO
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
			new PsiReferenceProvider() {
				@NotNull
				@Override
				public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
				                                                       @NotNull ProcessingContext
					                                                     context) {
					PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
					String value = literalExpression.getValue() instanceof String ?
						(String) literalExpression.getValue() : null;
					if (value != null && value.startsWith("simple" + ":")) {
						return new PsiReference[]{
							new CptReference(element, new TextRange(8, value.length() + 1))};
					}
					return PsiReference.EMPTY_ARRAY;
				}
			});
	}
}
