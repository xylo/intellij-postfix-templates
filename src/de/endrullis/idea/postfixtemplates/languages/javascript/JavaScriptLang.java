package de.endrullis.idea.postfixtemplates.languages.javascript;

import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.languages.kotlin.KotlinAnnotator;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class JavaScriptLang extends CptLang {

	public JavaScriptLang() {
		super("javascript", JavaScriptAnnotator.class);
	}

}
