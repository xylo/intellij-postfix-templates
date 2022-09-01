package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.FlexAdapter;

public class CptLexerAdapter extends FlexAdapter {

	public CptLexerAdapter() {
		super(new CptLexer(null));
	}

}
