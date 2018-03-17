// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class CptVisitor extends PsiElementVisitor {

  public void visitEscape(@NotNull CptEscape o) {
    visitPsiElement(o);
  }

  public void visitMapping(@NotNull CptMapping o) {
    visitNamedElement(o);
  }

  public void visitMappings(@NotNull CptMappings o) {
    visitPsiElement(o);
  }

  public void visitReplacement(@NotNull CptReplacement o) {
    visitPsiElement(o);
  }

  public void visitTemplate(@NotNull CptTemplate o) {
    visitNamedElement(o);
  }

  public void visitTemplateCodeG(@NotNull CptTemplateCodeG o) {
    visitPsiElement(o);
  }

  public void visitTemplateVariable(@NotNull CptTemplateVariable o) {
    visitPsiElement(o);
  }

  public void visitTemplateVariableExpressionG(@NotNull CptTemplateVariableExpressionG o) {
    visitPsiElement(o);
  }

  public void visitTemplateVariableNameG(@NotNull CptTemplateVariableNameG o) {
    visitPsiElement(o);
  }

  public void visitTemplateVariableValueG(@NotNull CptTemplateVariableValueG o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull CptNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
