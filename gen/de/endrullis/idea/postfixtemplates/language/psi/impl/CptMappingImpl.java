// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.language.psi.CptReplacement;
import de.endrullis.idea.postfixtemplates.language.psi.CptVisitor;
import org.jetbrains.annotations.NotNull;

public class CptMappingImpl extends CptNamedElementImpl implements CptMapping {

  public CptMappingImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CptVisitor visitor) {
    visitor.visitMapping(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CptVisitor) accept((CptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CptReplacement getReplacement() {
    return findNotNullChildByClass(CptReplacement.class);
  }

  public String getMatchingClassName() {
    return CptPsiImplUtil.getMatchingClassName(this);
  }

  public String getConditionClassName() {
    return CptPsiImplUtil.getConditionClassName(this);
  }

  public String getReplacementString() {
    return CptPsiImplUtil.getReplacementString(this);
  }

  public String getName() {
    return CptPsiImplUtil.getName(this);
  }

  public PsiElement setName(String newName) {
    return CptPsiImplUtil.setName(this, newName);
  }

  public PsiElement getNameIdentifier() {
    return CptPsiImplUtil.getNameIdentifier(this);
  }

  public ItemPresentation getPresentation() {
    return CptPsiImplUtil.getPresentation(this);
  }

}
