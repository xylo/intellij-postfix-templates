package de.endrullis.idea.postfixtemplates.languages.csharp;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for C#.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CsharpLang extends CptLang {

	public CsharpLang() {
		super("C#", CsharpAnnotator.class);
	}

}
