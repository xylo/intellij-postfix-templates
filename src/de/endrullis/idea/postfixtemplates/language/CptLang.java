package de.endrullis.idea.postfixtemplates.language;

/**
 * Abstract CPT language definition.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public abstract class CptLang {

	private final String                            language;
	private final Class<? extends CptLangAnnotator> annotatorClass;
	/** Lazily instantiated CptLangAnnotator. */
	private CptLangAnnotator annotator;

	public CptLang(String language, Class<? extends CptLangAnnotator> annotatorClass) {
		this.language = language;
		this.annotatorClass = annotatorClass;
	}

	public String getLanguage() {
		return language;
	}

	public CptLangAnnotator getAnnotator() {
		// instantiate annotator if not yet done
		if (annotator == null) {
			try {
				annotator = annotatorClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}

		return annotator;
	}

}
