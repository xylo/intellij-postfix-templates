package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@Getter
public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Attribute("VarLambdaStyle")
	private final boolean varLambdaStyle;

	@Attribute("LangName2virtualFile")
	private final Map<String, List<CptVirtualFile>> langName2virtualFile = new HashMap<>();

	@Attribute("TemplateSuffix")
	@NotNull
	private String templateSuffix;

	@Attribute("TemplateSuffixVersion")
	private int templateSuffixVersion = 0;

	private CptPluginSettings() {
			this(true, "");
	}

	public CptPluginSettings(boolean varLambdaStyle, @NotNull String templateSuffix) {
		this.varLambdaStyle = varLambdaStyle;
		this.templateSuffix = templateSuffix;
		this.templateSuffixVersion = 1;
	}

	void upgrade() {
		if (templateSuffixVersion == 0 && templateSuffix.equals("→")) {
			templateSuffix = "";
		}
		templateSuffixVersion = 1;
	}

	public interface Holder {
		void setPluginSettings(@NotNull CptPluginSettings settings);

		@NotNull
		CptPluginSettings getPluginSettings();
	}
}
