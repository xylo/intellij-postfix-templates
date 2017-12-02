package de.endrullis.idea.postfixtemplates.languages;

import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.languages.java.JavaLang;
import de.endrullis.idea.postfixtemplates.languages.javascript.JavaScriptLang;
import de.endrullis.idea.postfixtemplates.languages.kotlin.KotlinLang;

import java.util.HashMap;
import java.util.List;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

/**
 * All supported languages of this plugin.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class SupportedLanguages {

	private static final List<CptLang> supportedLanguages = _List(
		new JavaLang(),
		new JavaScriptLang(),
		new KotlinLang()
	);

	private static final HashMap<String, CptLang> languageToCptLang = new HashMap<String, CptLang>() {{
		supportedLanguages.forEach(lang -> put(lang.getLanguage(), lang));
	}};

	public static CptLang getCptLang(String language) {
		return languageToCptLang.get(language);
	}

}
