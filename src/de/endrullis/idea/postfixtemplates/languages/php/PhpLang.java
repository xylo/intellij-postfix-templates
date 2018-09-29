package de.endrullis.idea.postfixtemplates.languages.php;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for PHP.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class PhpLang extends CptLang {

	public PhpLang() {
		super("PHP", PhpAnnotator.class);
	}

}
