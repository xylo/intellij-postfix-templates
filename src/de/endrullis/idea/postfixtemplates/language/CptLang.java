package de.endrullis.idea.postfixtemplates.language;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

/**
 * Abstract CPT language definition.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@Getter
public abstract class CptLang {

	private final String                            niceName;
	private final String                            language;
	private final Class<? extends CptLangAnnotator> annotatorClass;
	/** Lazily instantiated CptLangAnnotator. */
	private CptLangAnnotator annotator;

	protected CptLang(String niceName, Class<? extends CptLangAnnotator> annotatorClass) {
		this.niceName = niceName;
		this.language = niceName.toLowerCase();
		this.annotatorClass = annotatorClass;
	}

	public CptLangAnnotator getAnnotator() {
		// instantiate annotator if not yet done
		if (annotator == null) {
			try {
				annotator = annotatorClass.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
		if (obj instanceof CptLang that) {
			return this.language.equals(that.language);
		}
		return false;
	}

	@Override
	public String toString() {
		return niceName;
	}

}
