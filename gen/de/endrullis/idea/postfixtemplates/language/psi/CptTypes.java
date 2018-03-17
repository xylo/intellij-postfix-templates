// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import de.endrullis.idea.postfixtemplates.language.psi.impl.*;

public interface CptTypes {

  IElementType ESCAPE = new CptElementType("ESCAPE");
  IElementType MAPPING = new CptElementType("MAPPING");
  IElementType MAPPINGS = new CptElementType("MAPPINGS");
  IElementType REPLACEMENT = new CptElementType("REPLACEMENT");
  IElementType TEMPLATE = new CptElementType("TEMPLATE");
  IElementType TEMPLATE_CODE_G = new CptElementType("TEMPLATE_CODE_G");
  IElementType TEMPLATE_VARIABLE = new CptElementType("TEMPLATE_VARIABLE");
  IElementType TEMPLATE_VARIABLE_EXPRESSION_G = new CptElementType("TEMPLATE_VARIABLE_EXPRESSION_G");
  IElementType TEMPLATE_VARIABLE_NAME_G = new CptElementType("TEMPLATE_VARIABLE_NAME_G");
  IElementType TEMPLATE_VARIABLE_VALUE_G = new CptElementType("TEMPLATE_VARIABLE_VALUE_G");

  IElementType BRACKET_CLOSE = new CptTokenType("BRACKET_CLOSE");
  IElementType BRACKET_OPEN = new CptTokenType("BRACKET_OPEN");
  IElementType CLASS_NAME = new CptTokenType("CLASS_NAME");
  IElementType COMMENT = new CptTokenType("COMMENT");
  IElementType MAP = new CptTokenType("MAP");
  IElementType SEPARATOR = new CptTokenType("SEPARATOR");
  IElementType TEMPLATE_CODE = new CptTokenType("TEMPLATE_CODE");
  IElementType TEMPLATE_DESCRIPTION = new CptTokenType("TEMPLATE_DESCRIPTION");
  IElementType TEMPLATE_ESCAPE = new CptTokenType("TEMPLATE_ESCAPE");
  IElementType TEMPLATE_NAME = new CptTokenType("TEMPLATE_NAME");
  IElementType TEMPLATE_VARIABLE_END = new CptTokenType("TEMPLATE_VARIABLE_END");
  IElementType TEMPLATE_VARIABLE_EXPRESSION = new CptTokenType("TEMPLATE_VARIABLE_EXPRESSION");
  IElementType TEMPLATE_VARIABLE_NAME = new CptTokenType("TEMPLATE_VARIABLE_NAME");
  IElementType TEMPLATE_VARIABLE_SEPARATOR = new CptTokenType("TEMPLATE_VARIABLE_SEPARATOR");
  IElementType TEMPLATE_VARIABLE_START = new CptTokenType("TEMPLATE_VARIABLE_START");
  IElementType TEMPLATE_VARIABLE_VALUE = new CptTokenType("TEMPLATE_VARIABLE_VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ESCAPE) {
        return new CptEscapeImpl(node);
      }
      else if (type == MAPPING) {
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
      else if (type == TEMPLATE_CODE_G) {
        return new CptTemplateCodeGImpl(node);
      }
      else if (type == TEMPLATE_VARIABLE) {
        return new CptTemplateVariableImpl(node);
      }
      else if (type == TEMPLATE_VARIABLE_EXPRESSION_G) {
        return new CptTemplateVariableExpressionGImpl(node);
      }
      else if (type == TEMPLATE_VARIABLE_NAME_G) {
        return new CptTemplateVariableNameGImpl(node);
      }
      else if (type == TEMPLATE_VARIABLE_VALUE_G) {
        return new CptTemplateVariableValueGImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
