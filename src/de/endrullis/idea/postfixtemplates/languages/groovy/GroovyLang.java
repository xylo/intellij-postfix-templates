package de.endrullis.idea.postfixtemplates.languages.groovy;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Groovy.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class GroovyLang extends CptLang {

	public GroovyLang() {
		super("Groovy", GroovyAnnotator.class);
	}

}
