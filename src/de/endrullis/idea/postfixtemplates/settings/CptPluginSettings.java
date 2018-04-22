package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Attribute("VarLambdaStyle")
	private boolean varLambdaStyle;

	@Attribute("TemplateSuffix")
	@NotNull
	private String templateSuffix;

	private CptPluginSettings() {
		this(true, "→");
	}

	public CptPluginSettings(boolean varLambdaStyle, 	@NotNull String templateSuffix) {
		this.varLambdaStyle = varLambdaStyle;
		this.templateSuffix = templateSuffix;
	}

	public boolean isVarLambdaStyle() {
		return varLambdaStyle;
	}

	@NotNull
	public String getTemplateSuffix() {
		return templateSuffix;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CptPluginSettings that = (CptPluginSettings) o;
		
		return varLambdaStyle == that.varLambdaStyle &&
			Objects.equals(templateSuffix, that.templateSuffix);
	}

	@Override
	public int hashCode() {

		return Objects.hash(varLambdaStyle, templateSuffix);
	}

	public interface Holder {
		void setPluginSettings(@NotNull CptPluginSettings settings);

		@NotNull
		CptPluginSettings getPluginSettings();
	}
}
