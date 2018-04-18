package de.endrullis.idea.postfixtemplates.language;

/**
 * Abstract CPT language definition.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public abstract class CptLang {

	private final String                            niceName;
	private final String                            language;
	private final Class<? extends CptLangAnnotator> annotatorClass;
	/** Lazily instantiated CptLangAnnotator. */
	private CptLangAnnotator annotator;

	public CptLang(String niceName, Class<? extends CptLangAnnotator> annotatorClass) {
		this.niceName = niceName;
		this.language = niceName.toLowerCase();
		this.annotatorClass = annotatorClass;
	}

	public String getNiceName() {
		return niceName;
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

	@Override
	public int hashCode() {
		return language.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CptLang) {
			CptLang that = (CptLang) obj;
			return this.language.equals(that.language);
		}
		return false;
	}

	@Override
	public String toString() {
		return niceName;
	}

}
