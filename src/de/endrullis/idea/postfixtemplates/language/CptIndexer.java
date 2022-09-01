package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer;
import org.jetbrains.annotations.NotNull;

public class CptIndexer extends LexerBasedIdIndexer {

	public static Lexer createIndexingLexer(OccurrenceConsumer consumer) {
		return new CptFilterLexer(new CptLexerAdapter(), consumer);
	}

	@Override
	public @NotNull Lexer createLexer(final @NotNull OccurrenceConsumer consumer) {
		return createIndexingLexer(consumer);
	}

}
