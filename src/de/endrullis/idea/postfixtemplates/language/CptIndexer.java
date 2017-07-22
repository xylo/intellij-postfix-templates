package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer;

public class CptIndexer extends LexerBasedIdIndexer {

	public static Lexer createIndexingLexer(OccurrenceConsumer consumer) {
		return new CptFilterLexer(new CptLexerAdapter(), consumer);
	}

	@Override
	public Lexer createLexer(final OccurrenceConsumer consumer) {
		return createIndexingLexer(consumer);
	}
}
