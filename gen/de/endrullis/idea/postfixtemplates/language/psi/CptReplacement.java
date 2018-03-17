// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CptReplacement extends PsiElement {

  @NotNull
  List<CptEscape> getEscapeList();

  @NotNull
  List<CptTemplateCodeG> getTemplateCodeGList();

  @NotNull
  List<CptTemplateVariable> getTemplateVariableList();

}
