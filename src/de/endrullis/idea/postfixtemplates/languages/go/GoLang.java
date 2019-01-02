package de.endrullis.idea.postfixtemplates.languages.go;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Philip Griggs (philipgriggs@gmail.com)
 */
public class GoLang extends CptLang {

	public GoLang() {
		super("Go", GoAnnotator.class);
	}

}
