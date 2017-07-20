package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;

public class CptTodoIndexer extends LexerBasedTodoIndexer {
  @Override
  public Lexer createLexer(OccurrenceConsumer consumer) {
    return CptIndexer.createIndexingLexer(consumer);
  }
}
