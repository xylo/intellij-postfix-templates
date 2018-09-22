package de.endrullis.idea.postfixtemplates.languages.sql;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class SqlLang extends CptLang {

	public SqlLang() {
		super("SQL", SqlAnnotator.class);
	}

}
