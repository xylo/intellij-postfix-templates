package de.endrullis.idea.postfixtemplates.languages.swift;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class SwiftLang extends CptLang {

	public SwiftLang() {
		super("Swift", SwiftAnnotator.class);
	}

}
