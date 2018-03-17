// This is a generated file. Not intended for manual editing.
package de.endrullis.idea.postfixtemplates.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static de.endrullis.idea.postfixtemplates.language.psi.CptTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class CptParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ESCAPE) {
      r = escape(b, 0);
    }
    else if (t == MAPPING) {
      r = mapping(b, 0);
    }
    else if (t == MAPPINGS) {
      r = mappings(b, 0);
    }
    else if (t == REPLACEMENT) {
      r = replacement(b, 0);
    }
    else if (t == TEMPLATE) {
      r = template(b, 0);
    }
    else if (t == TEMPLATE_CODE_G) {
      r = templateCodeG(b, 0);
    }
    else if (t == TEMPLATE_VARIABLE) {
      r = templateVariable(b, 0);
    }
    else if (t == TEMPLATE_VARIABLE_EXPRESSION_G) {
      r = templateVariableExpressionG(b, 0);
    }
    else if (t == TEMPLATE_VARIABLE_NAME_G) {
      r = templateVariableNameG(b, 0);
    }
    else if (t == TEMPLATE_VARIABLE_VALUE_G) {
      r = templateVariableValueG(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return CptFile(b, l + 1);
  }

  /* ********************************************************** */
  // (template|COMMENT)*
  static boolean CptFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CptFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!CptFile_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "CptFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // template|COMMENT
  private static boolean CptFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CptFile_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = template(b, l + 1);
    if (!r) r = consumeToken(b, COMMENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_ESCAPE
  public static boolean escape(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escape")) return false;
    if (!nextTokenIs(b, TEMPLATE_ESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_ESCAPE);
    exit_section_(b, m, ESCAPE, r);
    return r;
  }

  /* ********************************************************** */
  // CLASS_NAME (BRACKET_OPEN CLASS_NAME BRACKET_CLOSE)? MAP replacement
  public static boolean mapping(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapping")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAPPING, "<mapping>");
    r = consumeToken(b, CLASS_NAME);
    r = r && mapping_1(b, l + 1);
    r = r && consumeToken(b, MAP);
    r = r && replacement(b, l + 1);
    exit_section_(b, l, m, r, false, recover_parser_parser_);
    return r;
  }

  // (BRACKET_OPEN CLASS_NAME BRACKET_CLOSE)?
  private static boolean mapping_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapping_1")) return false;
    mapping_1_0(b, l + 1);
    return true;
  }

  // BRACKET_OPEN CLASS_NAME BRACKET_CLOSE
  private static boolean mapping_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapping_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, BRACKET_OPEN, CLASS_NAME, BRACKET_CLOSE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // mapping*
  public static boolean mappings(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappings")) return false;
    Marker m = enter_section_(b, l, _NONE_, MAPPINGS, "<mappings>");
    int c = current_position_(b);
    while (true) {
      if (!mapping(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mappings", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // !(TEMPLATE_NAME|CLASS_NAME|COMMENT)
  static boolean recover_parser(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_parser")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_parser_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // TEMPLATE_NAME|CLASS_NAME|COMMENT
  private static boolean recover_parser_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_parser_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_NAME);
    if (!r) r = consumeToken(b, CLASS_NAME);
    if (!r) r = consumeToken(b, COMMENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (templateCodeG|escape|templateVariable)+
  public static boolean replacement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "replacement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, REPLACEMENT, "<replacement>");
    r = replacement_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!replacement_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "replacement", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // templateCodeG|escape|templateVariable
  private static boolean replacement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "replacement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = templateCodeG(b, l + 1);
    if (!r) r = escape(b, l + 1);
    if (!r) r = templateVariable(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (TEMPLATE_NAME SEPARATOR TEMPLATE_DESCRIPTION) mappings
  public static boolean template(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE, "<template>");
    r = template_0(b, l + 1);
    r = r && mappings(b, l + 1);
    exit_section_(b, l, m, r, false, recover_parser_parser_);
    return r;
  }

  // TEMPLATE_NAME SEPARATOR TEMPLATE_DESCRIPTION
  private static boolean template_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TEMPLATE_NAME, SEPARATOR, TEMPLATE_DESCRIPTION);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_CODE
  public static boolean templateCodeG(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateCodeG")) return false;
    if (!nextTokenIs(b, TEMPLATE_CODE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_CODE);
    exit_section_(b, m, TEMPLATE_CODE_G, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_VARIABLE_START templateVariableNameG
  // 										(TEMPLATE_VARIABLE_SEPARATOR templateVariableExpressionG
  // 										(TEMPLATE_VARIABLE_SEPARATOR templateVariableValueG)?)? TEMPLATE_VARIABLE_END
  public static boolean templateVariable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariable")) return false;
    if (!nextTokenIs(b, TEMPLATE_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_VARIABLE_START);
    r = r && templateVariableNameG(b, l + 1);
    r = r && templateVariable_2(b, l + 1);
    r = r && consumeToken(b, TEMPLATE_VARIABLE_END);
    exit_section_(b, m, TEMPLATE_VARIABLE, r);
    return r;
  }

  // (TEMPLATE_VARIABLE_SEPARATOR templateVariableExpressionG
  // 										(TEMPLATE_VARIABLE_SEPARATOR templateVariableValueG)?)?
  private static boolean templateVariable_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariable_2")) return false;
    templateVariable_2_0(b, l + 1);
    return true;
  }

  // TEMPLATE_VARIABLE_SEPARATOR templateVariableExpressionG
  // 										(TEMPLATE_VARIABLE_SEPARATOR templateVariableValueG)?
  private static boolean templateVariable_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariable_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_VARIABLE_SEPARATOR);
    r = r && templateVariableExpressionG(b, l + 1);
    r = r && templateVariable_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TEMPLATE_VARIABLE_SEPARATOR templateVariableValueG)?
  private static boolean templateVariable_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariable_2_0_2")) return false;
    templateVariable_2_0_2_0(b, l + 1);
    return true;
  }

  // TEMPLATE_VARIABLE_SEPARATOR templateVariableValueG
  private static boolean templateVariable_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariable_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_VARIABLE_SEPARATOR);
    r = r && templateVariableValueG(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_VARIABLE_EXPRESSION*
  public static boolean templateVariableExpressionG(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariableExpressionG")) return false;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_VARIABLE_EXPRESSION_G, "<template variable expression g>");
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, TEMPLATE_VARIABLE_EXPRESSION)) break;
      if (!empty_element_parsed_guard_(b, "templateVariableExpressionG", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // TEMPLATE_VARIABLE_NAME
  public static boolean templateVariableNameG(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariableNameG")) return false;
    if (!nextTokenIs(b, TEMPLATE_VARIABLE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_VARIABLE_NAME);
    exit_section_(b, m, TEMPLATE_VARIABLE_NAME_G, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_VARIABLE_VALUE+
  public static boolean templateVariableValueG(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "templateVariableValueG")) return false;
    if (!nextTokenIs(b, TEMPLATE_VARIABLE_VALUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_VARIABLE_VALUE);
    int c = current_position_(b);
    while (r) {
      if (!consumeToken(b, TEMPLATE_VARIABLE_VALUE)) break;
      if (!empty_element_parsed_guard_(b, "templateVariableValueG", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, TEMPLATE_VARIABLE_VALUE_G, r);
    return r;
  }

  final static Parser recover_parser_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return recover_parser(b, l + 1);
    }
  };
}
