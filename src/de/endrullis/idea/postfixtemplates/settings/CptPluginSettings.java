package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import org.jetbrains.annotations.NotNull;

public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Attribute("VarLambdaStyle")
	private boolean varLambdaStyle;

	private CptPluginSettings() {
		this(true);
	}

	public CptPluginSettings(boolean varLambdaStyle) {
		this.varLambdaStyle = varLambdaStyle;
	}

	public boolean isVarLambdaStyle() {
		return varLambdaStyle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CptPluginSettings that = (CptPluginSettings) o;

		return varLambdaStyle == that.varLambdaStyle;
	}

	@Override
	public int hashCode() {
		return varLambdaStyle ? 1 : 0;
	}

	public interface Holder {
		void setPluginSettings(@NotNull CptPluginSettings settings);

		@NotNull
		CptPluginSettings getPluginSettings();
	}
}
