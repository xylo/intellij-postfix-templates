package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CptReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
	private final String key;

	public CptReference(@NotNull PsiElement element, TextRange textRange) {
		super(element, textRange);
		key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
	}

	@NotNull
	@Override
	public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
		Project project = myElement.getProject();
		final List<CptMapping> properties = CptUtil.findMappings(project, key);
		List<ResolveResult> results = new ArrayList<>();
		for (CptMapping property : properties) {
			results.add(new PsiElementResolveResult(property));
		}
		return results.toArray(new ResolveResult[0]);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		ResolveResult[] resolveResults = multiResolve(false);
		return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
	}

	@NotNull
	@Override
	public Object @NotNull [] getVariants() {
		Project project = myElement.getProject();
		List<CptMapping> properties = CptUtil.findMappings(project);
		List<LookupElement> variants = new ArrayList<>();
		for (final CptMapping property : properties) {
			if (property.getMatchingClassName() != null && !property.getMatchingClassName().isEmpty()) {
				variants.add(LookupElementBuilder.create(property).
					withIcon(CptIcons.FILE).
					withTypeText(property.getContainingFile().getName())
				);
			}
		}
		return variants.toArray();
	}
}