package de.endrullis.idea.postfixtemplates.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import de.endrullis.idea.postfixtemplates.language.psi.CptNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class CptNamedElementImpl extends ASTWrapperPsiElement implements CptNamedElement {
	protected CptNamedElementImpl(@NotNull ASTNode node) {
		super(node);
	}
}
