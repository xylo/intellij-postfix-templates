package de.endrullis.idea.postfixtemplates.languages.dart;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class DartLang extends CptLang {

	public DartLang() {
		super("Dart", DartAnnotator.class);
	}

}
