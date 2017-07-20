package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lang.Language;

public class CptLanguage extends Language {
  public static final CptLanguage INSTANCE = new CptLanguage();

  private CptLanguage() {
    super("CPT");
  }
}