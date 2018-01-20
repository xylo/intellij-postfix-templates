package de.endrullis.idea.postfixtemplates.languages.kotlin;

import de.endrullis.idea.postfixtemplates.language.CptLang;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class KotlinLang extends CptLang {

	public KotlinLang() {
		super("Kotlin", KotlinAnnotator.class);
	}

}
