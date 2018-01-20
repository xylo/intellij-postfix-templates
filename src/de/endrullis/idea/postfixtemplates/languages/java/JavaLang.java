package de.endrullis.idea.postfixtemplates.languages.java;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class JavaLang extends CptLang {

	public JavaLang() {
		super("Java", JavaAnnotator.class);
	}

}
