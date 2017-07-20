// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface CptTemplate extends CptNamedElement {

  @NotNull
  CptMappings getMappings();

  String getTemplateName();

  String getTemplateDescription();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

  ItemPresentation getPresentation();

}
