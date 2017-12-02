package de.endrullis.idea.postfixtemplates.languages.kotlin;

import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.languages.java.JavaAnnotator;

/**
 * Language definition for Java.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class KotlinLang extends CptLang {

	public KotlinLang() {
		super("kotlin", KotlinAnnotator.class);
	}

}
