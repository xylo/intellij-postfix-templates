package de.endrullis.idea.postfixtemplates.language;

import com.intellij.lang.Language;

/**
 * The CPT language.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptLanguage extends Language {

	public static final CptLanguage INSTANCE = new CptLanguage();

	private CptLanguage() {
		super("CPT");
	}

}
