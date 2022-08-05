package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;
import org.jetbrains.annotations.NotNull;

public class CptTodoIndexer extends LexerBasedTodoIndexer {
	@Override
	public @NotNull Lexer createLexer(@NotNull OccurrenceConsumer consumer) {
		return CptIndexer.createIndexingLexer(consumer);
	}
}
