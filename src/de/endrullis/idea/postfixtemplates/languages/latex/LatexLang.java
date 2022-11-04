package de.endrullis.idea.postfixtemplates.languages.latex;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Latex.
 *
 */
public class LatexLang extends CptLang {

	public LatexLang() {
		super("Latex", LatexAnnotator.class);
	}

}
