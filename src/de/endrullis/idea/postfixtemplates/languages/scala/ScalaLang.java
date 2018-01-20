package de.endrullis.idea.postfixtemplates.languages.scala;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class ScalaLang extends CptLang {

	public ScalaLang() {
		super("Scala", ScalaAnnotator.class);
	}

}
