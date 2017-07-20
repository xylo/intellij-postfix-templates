package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import com.intellij.psi.TokenType;

%%

%class CptLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \t]
TEMPLATE_DESCRIPTION_FIRST_CHARACTER=[^ \n\f\\] | "\\"{CRLF} | "\\".
TEMPLATE_DESCRIPTION_CHARACTER=[^\n\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT=("#")[^\r\n]*
SEPARATOR=[:=]
DOT="."
MAP=("->"|"→")
NAME_CHARACTER=[a-zA-Z0-9]
CLASS_NAME_CHARACTER=[a-zA-Z0-9.]
TEMPLATE_VARIABLE=("$END$"|"$EXPR$")
TEMPLATE_CODE_FIRST_CHARACTER=[^ $\r\n]
TEMPLATE_CODE_CHARACTER=[^$\r\n]

%state WAITING_DESCRIPTION
%state WAITING_TEMPLATE_CODE

%%

<YYINITIAL> {END_OF_LINE_COMMENT}                           { yybegin(YYINITIAL); return CptTypes.COMMENT; }

<YYINITIAL> {DOT}{NAME_CHARACTER}+                          { yybegin(YYINITIAL); return CptTypes.TEMPLATE_NAME; }

<YYINITIAL> {CLASS_NAME_CHARACTER}+                         { yybegin(YYINITIAL); return CptTypes.CLASS_NAME; }

<YYINITIAL> {MAP}                                           { yybegin(WAITING_TEMPLATE_CODE); return CptTypes.MAP; }

<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_DESCRIPTION); return CptTypes.SEPARATOR; }

<YYINITIAL> {TEMPLATE_VARIABLE}                             { yybegin(WAITING_DESCRIPTION); return CptTypes.TEMPLATE_VARIABLE; }


<WAITING_DESCRIPTION> {CRLF}({CRLF}|{WHITE_SPACE})+         { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_DESCRIPTION> {WHITE_SPACE}+                        { yybegin(WAITING_DESCRIPTION); return TokenType.WHITE_SPACE; }

<WAITING_DESCRIPTION> {TEMPLATE_DESCRIPTION_FIRST_CHARACTER}{TEMPLATE_DESCRIPTION_CHARACTER}* { yybegin(YYINITIAL); return CptTypes.TEMPLATE_DESCRIPTION; }


<WAITING_TEMPLATE_CODE> {CRLF}({CRLF}|{WHITE_SPACE})+       { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_TEMPLATE_CODE> {WHITE_SPACE}+                      { yybegin(WAITING_TEMPLATE_CODE); return TokenType.WHITE_SPACE; }

<WAITING_TEMPLATE_CODE> {TEMPLATE_CODE_FIRST_CHARACTER}{TEMPLATE_CODE_CHARACTER}* { yybegin(WAITING_TEMPLATE_CODE); return CptTypes.TEMPLATE_CODE; }

<WAITING_TEMPLATE_CODE> {TEMPLATE_VARIABLE}                 { yybegin(WAITING_TEMPLATE_CODE); return CptTypes.TEMPLATE_VARIABLE; }


({CRLF}|{WHITE_SPACE})+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

.                                                           { return TokenType.BAD_CHARACTER; }
