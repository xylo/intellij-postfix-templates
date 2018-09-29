package de.endrullis.idea.postfixtemplates.languages.rust;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Rust.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class RustLang extends CptLang {

	public RustLang() {
		super("Rust", RustAnnotator.class);
	}

}
