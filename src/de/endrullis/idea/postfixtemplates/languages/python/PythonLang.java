package de.endrullis.idea.postfixtemplates.languages.python;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Python.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class PythonLang extends CptLang {

	public PythonLang() {
		super("Python", PythonAnnotator.class);
	}

}
