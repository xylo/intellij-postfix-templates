package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.template.impl.TemplateColors;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

public class CptSyntaxHighlighter extends SyntaxHighlighterBase {
	public static final TextAttributesKey SEPARATOR =
		createTextAttributesKey("CPT_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final TextAttributesKey TEMPLATE_NAME =
		createTextAttributesKey("CPT_TEMPLATE_NAME", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey CLASS_NAME =
		createTextAttributesKey("CPT_CLASS_NAME", DefaultLanguageHighlighterColors.CLASS_NAME);
	public static final TextAttributesKey TEMPLATE_DESCRIPTION =
		createTextAttributesKey("CPT_TEMPLATE_DESCRIPTION", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey TEMPLATE_CODE =
		createTextAttributesKey("CPT_TEMPLATE_CODE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
	public static final TextAttributesKey TEMPLATE_ESCAPE =
		createTextAttributesKey("CPT_TEMPLATE_ESCAPE", DefaultLanguageHighlighterColors.METADATA);
	public static final TextAttributesKey TEMPLATE_VARIABLE_HOLDER =
		createTextAttributesKey("CPT_PLACE_HOLDER", TemplateColors.TEMPLATE_VARIABLE_ATTRIBUTES);
	public static final TextAttributesKey COMMENT =
		createTextAttributesKey("CPT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey BAD_CHARACTER =
		createTextAttributesKey("CPT_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

	private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
	private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
	private static final TextAttributesKey[] TEMPLATE_NAME_KEYS = new TextAttributesKey[]{TEMPLATE_NAME};
	private static final TextAttributesKey[] CLASS_NAME_KEYS = new TextAttributesKey[]{CLASS_NAME};
	private static final TextAttributesKey[] TEMPLATE_DESCRIPTION_KEYS = new TextAttributesKey[]{TEMPLATE_DESCRIPTION};
	private static final TextAttributesKey[] TEMPLATE_CODE_KEYS = new TextAttributesKey[]{TEMPLATE_CODE};
	private static final TextAttributesKey[] TEMPLATE_ESCAPE_KEYS = new TextAttributesKey[]{TEMPLATE_ESCAPE};
	private static final TextAttributesKey[] TEMPLATE_VARIABLE_KEYS = new TextAttributesKey[]{TEMPLATE_CODE, TEMPLATE_VARIABLE_HOLDER};
	private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
	private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
	private static final java.util.Set<IElementType> TEMPLATE_VARIABLE_PARTS = _Set(CptTypes.TEMPLATE_VARIABLE_START,
		CptTypes.TEMPLATE_VARIABLE_END, CptTypes.TEMPLATE_VARIABLE_NAME, CptTypes.TEMPLATE_VARIABLE_EXPRESSION,
		CptTypes.TEMPLATE_VARIABLE_VALUE, CptTypes.TEMPLATE_VARIABLE_SEPARATOR);

	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		return new CptLexerAdapter();
	}

	@NotNull
	@Override
	public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
		if (tokenType.equals(CptTypes.SEPARATOR) || tokenType.equals(CptTypes.MAP)) {
			return SEPARATOR_KEYS;
		} else if (tokenType.equals(CptTypes.TEMPLATE_NAME)) {
			return TEMPLATE_NAME_KEYS;
		} else if (tokenType.equals(CptTypes.CLASS_NAME)) {
			return CLASS_NAME_KEYS;
		} else if (tokenType.equals(CptTypes.TEMPLATE_DESCRIPTION)) {
			return TEMPLATE_DESCRIPTION_KEYS;
		} else if (tokenType.equals(CptTypes.TEMPLATE_CODE)) {
			return TEMPLATE_CODE_KEYS;
		} else if (TEMPLATE_VARIABLE_PARTS.contains(tokenType)) {
			return TEMPLATE_VARIABLE_KEYS;
		} else if (tokenType.equals(CptTypes.TEMPLATE_ESCAPE)) {
			return TEMPLATE_ESCAPE_KEYS;
		} else if (tokenType.equals(CptTypes.COMMENT)) {
			return COMMENT_KEYS;
		} else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
			return BAD_CHAR_KEYS;
		} else {
			return EMPTY_KEYS;
		}
	}
}