package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class CptLexerAdapter extends FlexAdapter {
	public CptLexerAdapter() {
		super(new CptLexer((Reader) null));
	}
}
