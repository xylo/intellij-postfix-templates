package de.endrullis.idea.postfixtemplates.language;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CptStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
	private final PsiElement element;

	public CptStructureViewElement(PsiElement element) {
		this.element = element;
	}

	@Override
	public Object getValue() {
		return element;
	}

	@Override
	public void navigate(boolean requestFocus) {
		if (element instanceof NavigationItem) {
			((NavigationItem) element).navigate(requestFocus);
		}
	}

	@Override
	public boolean canNavigate() {
		return element instanceof NavigationItem &&
			((NavigationItem) element).canNavigate();
	}

	@Override
	public boolean canNavigateToSource() {
		return element instanceof NavigationItem &&
			((NavigationItem) element).canNavigateToSource();
	}

	@Override
	public @NotNull String getAlphaSortKey() {
		return Objects.requireNonNull(element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null);
	}

	@Override
	public @NotNull ItemPresentation getPresentation() {
		return Objects.requireNonNull(element instanceof NavigationItem ? ((NavigationItem) element).getPresentation() : null);
	}

	@Override
	public TreeElement @NotNull [] getChildren() {
		if (element instanceof CptFile) {
			CptTemplate[] templates = PsiTreeUtil.getChildrenOfType(element, CptTemplate.class);
			assert templates != null;
			List<TreeElement> treeElements = new ArrayList<>(templates.length);
			for (CptTemplate template : templates) {
				treeElements.add(new CptStructureViewElement(template));
			}
			return treeElements.toArray(new TreeElement[0]);
		} else {
			return EMPTY_ARRAY;
		}
	}
}