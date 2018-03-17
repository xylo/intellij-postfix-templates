// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import de.endrullis.idea.postfixtemplates.language.psi.CptEscape;
import de.endrullis.idea.postfixtemplates.language.psi.CptVisitor;
import org.jetbrains.annotations.NotNull;

public class CptEscapeImpl extends ASTWrapperPsiElement implements CptEscape {

  public CptEscapeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CptVisitor visitor) {
    visitor.visitEscape(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CptVisitor) accept((CptVisitor)visitor);
    else super.accept(visitor);
  }

}
