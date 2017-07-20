// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import de.endrullis.idea.postfixtemplates.language.psi.impl.*;

public interface CptTypes {

  IElementType MAPPING = new CptElementType("MAPPING");
  IElementType MAPPINGS = new CptElementType("MAPPINGS");
  IElementType REPLACEMENT = new CptElementType("REPLACEMENT");
  IElementType TEMPLATE = new CptElementType("TEMPLATE");

  IElementType CLASS_NAME = new CptTokenType("CLASS_NAME");
  IElementType COMMENT = new CptTokenType("COMMENT");
  IElementType MAP = new CptTokenType("MAP");
  IElementType SEPARATOR = new CptTokenType("SEPARATOR");
  IElementType TEMPLATE_CODE = new CptTokenType("TEMPLATE_CODE");
  IElementType TEMPLATE_DESCRIPTION = new CptTokenType("TEMPLATE_DESCRIPTION");
  IElementType TEMPLATE_NAME = new CptTokenType("TEMPLATE_NAME");
  IElementType TEMPLATE_VARIABLE = new CptTokenType("TEMPLATE_VARIABLE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == MAPPING) {
        return new CptMappingImpl(node);
      }
      else if (type == MAPPINGS) {
        return new CptMappingsImpl(node);
      }
      else if (type == REPLACEMENT) {
        return new CptReplacementImpl(node);
      }
      else if (type == TEMPLATE) {
        return new CptTemplateImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
