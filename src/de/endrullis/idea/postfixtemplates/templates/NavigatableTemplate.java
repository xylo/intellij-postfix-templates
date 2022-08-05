package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public interface NavigatableTemplate extends Navigatable {

	PsiElement getNavigationElement();

	@Override
	default void navigate(boolean b) {
		final PsiElement navigationElement = getNavigationElement();
		if (navigationElement instanceof final Navigatable navigatable) {
			if (navigatable.canNavigate()) {
				navigatable.navigate(true);
			}
		}
	}

	@Override
	default boolean canNavigate() {
		return true;
	}

	@Override
	default boolean canNavigateToSource() {
		return true;
	}

}
